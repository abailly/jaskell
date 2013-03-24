/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.lifl.jaskell.compiler.core.*;


/**
 * A class that collects a Set of captured variables
 *
 * @author  bailly
 * @version $Id: CaptureCollector.java 1154 2005-11-24 21:43:37Z nono $
 */
class CaptureCollector extends JaskellVisitorAdapter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Set emptySet = new HashSet();

    /* the set of captured variables */
    private Set captured;

    /* the set of locally bound variables */
    private Set locals;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     */
    public CaptureCollector() {
        this.captured = new HashSet();
        this.locals = new HashSet();
    }

    /**
     * @param captured
     */
    public CaptureCollector(Set captured) {
        this.captured = captured;
        this.locals = new HashSet();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Abstraction)
     */
    public Object visit(Abstraction a) {
        locals.addAll(a.getBindings().values());
        a.getBody().visit(this);
        return null;
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Alternative)
     */
    public Object visit(Alternative a) {
        /* add all bindings to locals */
        locals.add(a.getBinding());
        Iterator it = a.getPatterns();
        while (it.hasNext()) {
            locals.addAll(((Pattern) it.next()).getBindings());
        }
        /* visit all bodies of alternative */
        it = a.getBodies();
        while (it.hasNext()) {
            Expression e = (Expression) it.next();
            e.visit(this);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Application)
     */

    public Object visit(Application a) {
        a.getFunction().visit(this);
        /* visit all bodies of alternative */
        Iterator it = a.getArgs().iterator();
        while (it.hasNext()) {
            Expression e = (Expression) it.next();
            e.visit(this);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Conditional)
     */
    public Object visit(Conditional conditional) {
        conditional.getCondition().visit(this);
        conditional.getIfFalse().visit(this);
        conditional.getIfTrue().visit(this);
        return null;
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Definition)
     */
    public Object visit(Definition a) {
        return a.getDefinition().visit(this);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Let)
     */
    public Object visit(Let let) {
        Iterator it = let.getBindings().values().iterator();
        while (it.hasNext()) {
            Expression e = (Expression) it.next();
            e.visit(this);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Variable)
     */
    public Object visit(Variable a) {
        /* resolve variable */
        Expression e = a.lookup(a.getName());
        /* simple case */
        if (locals.contains(e) || !(e instanceof LocalBinding)) {
            return emptySet;
        }
        /* e is defined in outer scope */
        captured.add(e);
        return null;
    }

}
