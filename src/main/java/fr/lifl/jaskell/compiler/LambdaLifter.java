/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.util.*;
import java.util.logging.Logger;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.parsing.Namespace;
import fr.lifl.parsing.SymbolException;


/**
 * A class that implements lambda lifting This class is used to implement lambda-lifting. It is used either on Module
 * object to clean-up globally all abstractions in this module or on specific definitions. In the latter case, the
 * parameterized constructor may be used to define the scope where abstractions should be lifted to.
 *
 * <p>The algorithm used is simple : each abstraction which is not at top level is scanned for <code>LocalBinding</code>
 * objects which are defined in an enclosing scope. This bindings are then substituted for bindings local to the
 * abstraction which lifted to the enclosing Namespace and given a name. All occurences of this abstraction is then
 * replaced by calls to the lifted definition.
 *
 * <p>
 *
 * @author  bailly
 * @version $Id: LambdaLifter.java 1154 2005-11-24 21:43:37Z nono $
 */
public class LambdaLifter extends JaskellVisitorAdapter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static Logger log = Logger.getLogger(LambdaLifter.class.getName());

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* the namespace where lifted abstractions should be bound */
    private Namespace ns;

    /* counter for generated lambdas */
    private int counter = 0;

    /* flag for inner abstractions */
    private boolean lift = false;

    /* map from old to new abstractions */
    private Map substMap = new HashMap();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     */
    public LambdaLifter() {
        super();
    }

    public LambdaLifter(Namespace ns) {
        this.ns = ns;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /*
     * If we visit a nested abstraction, we just launch a new lift operation on
     * this abstraction using current context as namespace and returns an
     * Application object
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Abstraction)
     */
    public Object visit(Abstraction a) {
        if (!lift) { /* top level abstractions */
            lift = true;
            a.setBody((Expression) a.getBody().visit(this));
            return a;
        }
        /* first visit body */
        a.setBody((Expression) a.getBody().visit(this));
        /* retrieve outer LocalBindings */
        Set captured = new HashSet();
        CaptureCollector cc = new CaptureCollector(captured);
        a.visit(cc);
        /* return the newly computed abstraction as an application spine */
        String vname;
        try {
            vname = lift(a, captured);
            Expression ex = applyLifted(vname, captured);
            ex.setParent(a.getParent());
            return ex;
        } catch (SymbolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Alternative)
     */
    public Object visit(Alternative a) {
        Iterator it = a.getChoices();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Expression body = (Expression) entry.getValue();
            entry.setValue(body.visit(this));
        }
        return a;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Application)
     */
    public Object visit(Application a) {
        /* visit function */
        a.setFunction((Expression) a.getFunction().visit(this));
        /* visit all bodies of alternative */
        List it = a.getArgs();
        for (int i = 0; i < it.size(); i++) {
            Expression e = (Expression) it.get(i);
            it.set(i, e.visit(this));
        }
        return a;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Conditional)
     */
    public Object visit(Conditional conditional) {
        conditional.setCondition((Expression) conditional.getCondition().visit(this));
        conditional.setIfFalse((Expression) conditional.getIfFalse().visit(this));
        conditional.setIfTrue((Expression) conditional.getIfTrue().visit(this));
        return conditional;
    }

    /*
     * Definitions in the let block are lifted to top-level and the corresponding
     * references in the body are replaced by new definitions.
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Let)
     */
    public Object visit(Let let) {
        HashMap subst = new HashMap(); /* substitution map */
        Set lambdas = new HashSet(); /* newly created toplevels ? */
        Iterator it = let.getBindings().entrySet().iterator();
        /* first lift all definitions in this let */
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Expression e = (Expression) entry.getValue();
            /* reset lift flag */
            lift = false;
            Expression ndef = (Expression) e.visit(this);
            ndef.setParent(let);
            /* lift new definition */
            Set captured = new HashSet();
            CaptureCollector cc = new CaptureCollector(captured);
            ndef.visit(cc);
            String vname;
            try {
                vname = lift(ndef, captured);
                lambdas.add(vname);
                /*
                 * store new application spine in a map for later substitution
                 */
                subst.put(name, applyLifted(vname, captured));
            } catch (SymbolException e1) {
                e1.printStackTrace();
            }
        }
        /* second, replace old occurences in new definitions */
        it = lambdas.iterator();
        while (it.hasNext()) {
            String n = (String) it.next();
            Expression e;
            try {
                e = (Expression) ns.resolve(n);
                ns.rebind(n, (Expression) e.visit(new Substitution(subst)));
            } catch (SymbolException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        /* thirs, replace occurences in body of let and return it */
        return let.getBody().visit(new Substitution(subst));
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Module)
     */
    public Object visit(Namespace a) {
        /* set current namespace */
        ns = a;
        /*
         * visit definitions We set an infinite loop to cope with modifications
         * introduced by visiting sub expressions. When something is modified -
         * through lift - we restart the whole process. TODO : change this to defer
         * registering new bindings at end
         */
        while (true) {
            try {
                Iterator it = a.getAllBindings().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String functionName = (String) entry.getKey();
                    Expression def = (Expression) entry.getValue();
                    if (def instanceof Abstraction)
                        ((Abstraction) def).setClassName(ns.getName() + "." + functionName);
                    log.finest("Analyzing lifting for " + functionName);
                    lift = false;
                    entry.setValue((Expression) def.visit(this));
                }
                break;
            } catch (ConcurrentModificationException cmex) {
                /* restart the process */
            }
        }
        return a;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Variable)
     */
    public Object visit(Variable a) {
        return a;
    }

    /**
     * This method produces a new Abstraction which is the result of adding captured variables as new arguments and bind
     * it in current ns. It returns the name of the bound funciton.
     *
     * @param  a
     * @param  captured
     *
     * @return a String object denoting the name given to this function
     *
     * @throws SymbolException
     */
    private String lift(Abstraction a, Set captured) throws SymbolException {
        Abstraction abs = a;
        if (!captured.isEmpty()) {
            /* recreate abstraction adding captured variables */
            abs = new Abstraction();
            /* bind captured variables */
            Iterator it = captured.iterator();
            while (it.hasNext()) {
                LocalBinding lb = (LocalBinding) it.next();
                LocalBinding nlb = new LocalBinding(lb.getName());
                abs.bind(nlb);
            }
            /* rebind old variables */
            it = a.getBindings().values().iterator();
            while (it.hasNext()) {
                LocalBinding lb = (LocalBinding) it.next();
                LocalBinding nlb = new LocalBinding(lb.getName());
                abs.bind(nlb);
            }
            /* restore body */
            abs.setBody(a.getBody());
        }
        /*
         * bind new abstraction in ns if a is an anonymous abstraction - no class
         * name - then it is renamed lambdaXXX and bound in namespace. Else, it is
         * given a new name and a reference to this new name is stored in substMap.
         */
        String newname = "lambda" + counter++;
        ns.bind(newname, abs);
        abs.setClassName(ns.getName() + "." + newname);
        return newname;
    }

    /**
     * Returns an Abstraction object resulting from lifting the given expression with captured variable set. if <code>f
     * x y</code> is an expression with <code>y</code> a captured variable, then it is transformed into <code>lambdaXXX
     * = \ y -> f x y</code> which can later be used to replace the original expression with <code>lambdaXXX y</code>.
     *
     * @param  e
     * @param  captured
     *
     * @return a String object
     *
     * @throws SymbolException
     */
    private String lift(Expression e, Set captured) throws SymbolException {
        if (e instanceof Abstraction)
            return lift((Abstraction) e, captured);
        Abstraction abs = new Abstraction();
        /* bind captured variables */
        Iterator it = captured.iterator();
        while (it.hasNext()) {
            LocalBinding lb = (LocalBinding) it.next();
            LocalBinding nlb = new LocalBinding(lb.getName());
            abs.bind(nlb);
        }
        /* restore body */
        abs.setBody(e);
        String newname = "lambda" + counter++;
        ns.bind(newname, abs);
        return newname;
    }

    /**
     * This method produces an Expression from an arbitrary Abstraction object which is applied to a list of Variables
     * referencing captured bindings.
     *
     * @param  a        an Abstraction object to partially apply
     * @param  captured
     *
     * @return
     */
    private Expression applyLifted(String s, Set captured) {
        if (captured.isEmpty())
            return new Variable(s);
        Application app = new Application();
        app.setFunction(new Variable(s));
        /* bind captured variables */
        Iterator it = captured.iterator();
        while (it.hasNext()) {
            LocalBinding lb = (LocalBinding) it.next();
            app.addArgument(new Variable(lb.getName()));
        }
        return app;
    }

}
