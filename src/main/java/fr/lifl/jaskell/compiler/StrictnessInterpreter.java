/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.util.*;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.runtime.types.Closure;
import fr.lifl.jaskell.runtime.types.JFunction;
import fr.lifl.jaskell.runtime.types.JObject;
import fr.lifl.parsing.Namespace;
import fr.lifl.parsing.SymbolException;


/**
 * @author  bailly
 * @version $Id: StrictnessInterpreter.java 1154 2005-11-24 21:43:37Z nono $
 */
public class StrictnessInterpreter extends AbstractInterpreter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* constant value denoting a terminating computation */
    private static JObject one = new JObject() {
        public JObject eval() {
            return this;
        }
    };

    /* constant value denoting a divergent computation */
    private static JObject zero = new JObject() {
        public JObject eval() {
            return this;
        }
    };

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* current namespace to resolve variables */
    private Namespace module;

    /* store mapping from abstractions to values */
    private Map abstractions = new HashMap();

    /* current context for variable resolution - initially empty */
    private Map context = new HashMap();

    /* stack of contexts */
    private Stack stack;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Alternative)
     */
    public Object visit(Alternative a) {
        JObject eval = (JObject) a.getExpression().visit(this);
        if (eval == zero)
            return zero;
        return one;
    }

    /**
     * Restore context after function return
     */
    public void popContext() {
        if (stack.isEmpty())
            throw new IllegalStateException("Stack underflow");
        context = (Map) stack.pop();
    }

    /**
     * Install a new context - name to values mapping - and save the old one.
     *
     * @param vars
     */
    public void pushContext(Map vars) {
        stack.push(context);
        context = vars;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.BooleanLiteral)
     */
    public Object visit(BooleanLiteral a) {
        return one;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.CharLiteral)
     */
    public Object visit(CharLiteral a) {
        return one;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Conditional)
     */
    public Object visit(Conditional a) {
        /* return zero if condition is zero or both branch of alternatives are zero */
        JObject obj = (JObject) a.getCondition().visit(this);
        JObject ift = (JObject) a.getIfTrue().visit(this);
        JObject iff = (JObject) a.getIfFalse().visit(this);
        return (obj == zero) ? zero : (((ift == zero) && (iff == zero)) ? zero : one);
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.DoubleLiteral)
     */
    public Object visit(DoubleLiteral a) {
        return one;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.FloatLiteral)
     */
    public Object visit(FloatLiteral a) {
        return one;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.IntegerLiteral)
     */
    public Object visit(IntegerLiteral a) {
        return one;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.QualifiedVariable)
     */
    public Object visit(QualifiedVariable a) {
        /* resolve variable */
        Expression expr = a.lookup(a.getName());
        return (JObject) expr.visit(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.StringLiteral)
     */
    public Object visit(StringLiteral a) {
        return one;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Variable)
     */
    public Object visit(Variable a) {
        /* try to find variable in current context */
        JObject obj = (JObject) context.get(a.getName());
        if (obj != null)
            return obj;
        /* explore stack */
        if (!stack.isEmpty())
            for (int i = stack.size() - 1; i >= 0; i--) {
                Map m = (Map) stack.get(i);
                if ((obj = (JObject) m.get(a.getName())) != null)
                    return obj;
            }

        try {
            /* resolve variable at toplevel */
            Expression expr = (Expression) module.resolve(a.getName());
            return (JObject) expr.visit(this);
        } catch (SymbolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalStateException("Unresolved variable " + a);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.PrimitiveFunction)
     */
    public Object visit(PrimitiveFunction a) {
        /*
         * primitive functions are always strict - if any argument fails, the
         * function fail
         */
        return new StrictFunction(a.getCount(), a);
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Abstraction)
     */
    public Object visit(Abstraction a) {
        JObject cur = (JObject) abstractions.get(a);
        if (cur == null) {
            cur = new Combinator(a.getCount(), a, this);
            abstractions.put(a, cur);
        }
        return cur;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Constructor)
     */
    public Object visit(Constructor a) {
        ConstructorDefinition def;
        try {
            def = (ConstructorDefinition) module.resolve(a.getName());
            return new StrictFunction(def.getParameters().size(), def);
        } catch (SymbolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CompilerException("Unknown constructor " + a.getName());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Module)
     */
    public Object visit(Namespace a) {
        /* set current namespace to module */
        module = a;
        /* visit definitions */
        Iterator it = a.getAllBindings().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String functionName = (String) entry.getKey();
            Expression def = (Expression) entry.getValue();
            JObject cur, res = null;
            do {
                cur = res;
                res = eval(def);
            } while (cur != res);
        }
        return null;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* a function that is strict in all its arguments */
    class StrictFunction extends Closure {

        Binder binder;

        /**
         * @param n
         */
        public StrictFunction(int n, Binder def) {
            super(n);
            this.binder = def;
        }

        /*
         * (non-Javadoc)
         *
         * @see jaskell.runtime.types.JObject#eval()
         */
        public JObject eval() {
            if (nargs < maxargs)
                return this;
            else {
                for (int i = 0; i < maxargs; i++)
                    if ((args[i] == zero) && binder.isStrict(i))
                        return zero;
                return one;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see jaskell.runtime.types.JFunction#init()
         */
        public JFunction init() {
            return new StrictFunction(maxargs, binder);
        }

    }

    /* standard function */
    class Combinator extends Closure {

        Abstraction abs;

        StrictnessInterpreter interp;

        /**
         * @param n
         */
        public Combinator(int n, Abstraction abs, StrictnessInterpreter interp) {
            super(n);
            this.abs = abs;
            this.interp = interp;
        }

        /*
         * (non-Javadoc)
         *
         * @see jaskell.runtime.types.JObject#eval()
         */
        public JObject eval() {
            if (nargs < maxargs)
                return this;
            else {
                /* gives value to bindings */
                int i = 0;
                Map vars = new LinkedHashMap(abs.getBindings());
                Iterator it = vars.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    entry.setValue(args[i++]);
                }
                interp.pushContext(vars);
                /* instantiate abs body and call eval on it */
                JObject val = (JObject) abs.getBody().visit(interp);
                interp.popContext();
                return val;
            }

        }

        /*
         * (non-Javadoc)
         *
         * @see jaskell.runtime.types.JFunction#init()
         */
        public JFunction init() {
            return new Combinator(maxargs, abs, interp);
        }

    }
}
