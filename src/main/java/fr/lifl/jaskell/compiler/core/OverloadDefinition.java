/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
/*
 * Created on Mar 9, 2004
 *
 * $Log: OverloadDefinition.java,v $
 * Revision 1.3  2004/09/07 10:04:04  bailly
 * cleared imports
 *
 * Revision 1.2  2004/03/29 15:57:53  bailly
 * Code generation fixes
 *
 * Revision 1.1  2004/03/10 20:06:49  bailly
 * *** empty log message ***
 *
 */
package fr.lifl.jaskell.compiler.core;

import java.util.*;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.UncomparableException;


/**
 * A class that stores definitions for overloaded functions
 *
 * <p>This class is used to store multiple definitions of a function for a single name. The various definitions are
 * distinguished by their types.
 *
 * @author  bailly
 * @version $Id: OverloadDefinition.java 1154 2005-11-24 21:43:37Z nono $
 */
public class OverloadDefinition extends Definition {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* stores untyped definitions which may later be type checked */
    private List untyped = new ArrayList();

    /* maps type to definitions */
    private Map defs = new HashMap();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public OverloadDefinition() {
    }

    /**
     * @param name
     * @param type
     * @param expr
     * @param module
     */
    public OverloadDefinition(String name, Module module) {
        super(name, null, null, module);
        module.bind(name, this);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Adds a new definition for this overloaded function with given type. If <code>type</code> is null, we assumes that
     * it is not yet known and stores the <code>expr</code> part in a waiting list.
     *
     * @param expr
     * @param type
     */
    public void addDefinition(Expression expr, Type type) {
        if (type == null)
            untyped.add(expr);
        else {
            defs.put(type, expr);
        }
        expr.setParent(this);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.core.Expression#visit(jaskell.compiler.JaskellVisitor)
     */
    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * Returns a list of all untyped definitions and remove them from this definition. It is the responsibility of the
     * caller to rebind the definitions into this overload definition object
     *
     * @return a List of expressions
     */
    public List removeUntypedDefinitions() {
        List ret = untyped;
        /* reset untyped list */
        untyped = new ArrayList();
        return ret;
    }

    /**
     * Returns a set view of all definitinos in this object, whether typed or untyped
     *
     * @return a Set containing all definitions in this overload definition
     */
    public Set getAllDefinitions() {
        Set s = new HashSet();
        s.addAll(untyped);
        s.addAll(defs.values());
        return s;
    }

    /**
     * Returns an expression from this overloaded function which is "compatible" with <code>type</code>.
     *
     * <p>This method tries to locate in this object an expression whose type is the equal to or is the least supertype
     * of the given <code>type</code>.
     *
     * @param  type a type requested
     *
     * @return an Expression whose type is a super type of requested type. May return null if no correct definitino is
     *         found
     */
    public Expression getDefinitionFor(Type type) {
        Type rt = null;
        Expression re = null;
        Iterator it = defs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Expression expr = (Expression) entry.getValue();
            Type t = (Type) entry.getKey();
            /* tries to compare with type */
            try {
                if (type.compare(t) > 0)
                    continue;
                /* found a possible match */
                if ((rt == null) || (t.compare(rt) < 0)) {
                    rt = t;
                    re = expr;
                }
            } catch (UncomparableException ex) {
                continue;
            }
        }
        return re;
    }
}
