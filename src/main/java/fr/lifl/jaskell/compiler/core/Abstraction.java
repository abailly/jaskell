/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.core;

import java.util.*;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.Types;


/**
 * A class representing lambda expressions. An abstraction is used to define functions and bind variables. It is assumed
 * that every abstraction is defined at the module level and may not be nested inside another abstraction. This means
 * that abstraction namespace is flat.
 *
 * @author  bailly
 * @version $Id: Abstraction.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Abstraction extends ExpressionBase implements Binder {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private short maxlocals;

    /** map of bindings in this abstraction */
    private Map bindings = new LinkedHashMap();

    /** indexed list of bindings */
    private List indices = new ArrayList();

    /** body of this abstraction */
    private Expression body;

    /** count of bindings in this abstraction */
    private int count = 0;

    /** Name of class file where this abstraction is compiled to */
    private String className;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Adds a new binding to this Abstraction. This method sets the index of the binding object and returns it
     *
     * @param  bind a LocalBinding object
     *
     * @return index of this binding object
     */
    public int bind(LocalBinding bind) {
        Object b = bindings.get(bind.getName());
        if (b != null)
            throw new IllegalArgumentException("Variable " +
                bind.getName() +
                " is already bound in Abstraction " + this);
        bindings.put(bind.getName(), bind);
        /* update index */
        bind.setIndex(count);
        indices.add(count, bind);
        bind.setParent(this);
        maxlocals++;
        return count++;
    }

    /**
     * Retrieves binding for a given index
     *
     * @param  i the index of binding to retrieve
     *
     * @return a LocalBinding object or null if no binding is defined at that index
     */
    public LocalBinding getByIndex(int i) {
        return (LocalBinding) indices.get(i);
    }

    /**
     * Retrieves bindings for a given declaration name
     *
     * @param  name the name of the variable
     *
     * @return a LocalBinding object or null if no binding is defined
     */
    public LocalBinding getByName(String name) {
        return (LocalBinding) bindings.get(name);
    }

    /**
     * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
     */
    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * Returns the body.
     *
     * @return Expression
     */
    public Expression getBody() {
        return body;
    }

    /**
     * Sets the body.
     *
     * @param body The body to set
     */
    public void setBody(Expression body) {
        this.body = body;
        body.setParent(this);
    }

    /**
     * Returns the bindings.
     *
     * @return Map
     */
    public Map getBindings() {
        return bindings;
    }

    /**
     * Returns the count.
     *
     * @return int
     */
    public int getCount() {
        return count;
    }

    /**
     * Computes the type of this expression from the type of its bindings and body. getType does not verify the validity
     * of the computed type.
     *
     * @see jaskell.compiler.core.Expression#getType()
     */
    public Type getType() {
        Type type;
        // return precalculated type if available
        if ((type = super.getType()) != null)
            return type;
        type = body.getType();
        if (type == null)
            return null;
        LocalBinding[] binds = (LocalBinding[]) indices.toArray(new LocalBinding[0]);
        for (int i = binds.length; i > 0; i--) {
            type = Types.apply(Types.apply(Primitives.FUNCTION, binds[i - 1].getType()), type);
        }
        setType(type);
        return type;
    }

    /**
     * Method resolve.
     *
     * @param  vname
     *
     * @return Binding
     */
    public Expression lookup(String vname) {
        LocalBinding bind = getByName(vname);
        if (bind == null) { // try module
            Expression parent = getParent();
            if (parent != null)
                return parent.lookup(vname);
        }
        return bind;
    }

    /**
     * Method setStrict. Sets the given variable name to strict
     *
     * @param s
     */
    public void setStrict(String s) {
        LocalBinding b = (LocalBinding) bindings.get(s);
        b.setStrict(true);
    }

    /**
     * Returns the strictness status of a parameter given by its index
     *
     * @param i index of parameter
     */
    public boolean isStrict(int i) {
        return getByIndex(i).isStrict();
    }

    /**
     * Method setNonStrict.
     *
     * @param s
     */
    public void setNonStrict(String s) {
        LocalBinding b = (LocalBinding) bindings.get(s);
        b.setStrict(false);
    }

    /**
     * Returns the class this abstraction was compiled in
     *
     * @return an Stirng representing a class name in internal form
     */
    public String getClassName() {
        return className;
    }

    /**
     * Defines the class name where this abstraction is compiled to
     *
     * @param className internal name for class
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("\\");
        Iterator it = indices.iterator();
        while (it.hasNext())
            sb.append(' ').append(it.next());
        sb.append(" -> ").append(body);
        return sb.toString();

    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        Abstraction abs = new Abstraction();
        Iterator it = getBindings().values().iterator();
        while (it.hasNext()) {
            LocalBinding lb = (LocalBinding) it.next();
            abs.bind(lb);
        }
        abs.setBody((Expression) ((ExpressionBase) body).clone());
        abs.setType(getType());
        abs.setClassName(getClassName());
        abs.setParent(getParent());
        return abs;
    }

    /**
     * @return
     */
    public short getMaxLocals() {
        return maxlocals;
    }

    /**
     * @param s
     */
    public void setMaxlocals(short s) {
        maxlocals = s;
    }

}
