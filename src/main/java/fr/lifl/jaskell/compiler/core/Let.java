/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler.core;

import java.util.HashMap;
import java.util.Map;

import fr.lifl.jaskell.compiler.CompilerException;
import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.parsing.Namespace;
import fr.lifl.parsing.SymbolException;


/**
 * A class that represents a <code>let</code> or <code>where</code> construct in Jaskell This class defines a local
 * environment which can have its own definitions
 *
 * @author  bailly
 * @version $Id: Let.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Let extends ExpressionBase implements Namespace {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* the body of let construct */
    private Expression body;

    /* the list of definitions stored in this environnement */
    private Map bindings;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for Let.
     */
    public Let() {
        bindings = new HashMap();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @see jaskell.compiler.core.Expression#getType()
     */
    public Type getType() {
        return body.getType();
    }

    /**
     * @see jaskell.compiler.core.Expression#setType(Type)
     */
    public void setType(Type type) {
        body.setType(type); /* ?? */
    }

    /**
     * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
     */
    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * @see jaskell.compiler.core.Namespace#lookup(String)
     */
    public Expression lookup(String name) {
        Expression ex = (Expression) bindings.get(name);
        if (ex == null)
            return parent.lookup(name);
        else
            return ex;
    }

    /**
     * @see jaskell.compiler.core.Namespace#bind(String, Expression)
     */
    public void bind(String name, Object expr) {
        Expression ex = (Expression) bindings.get(name);
        if (ex == null)
            bindings.put(name, expr);
        else
            throw new CompilerException("Name " + name + " already bound in this let-construct");
    }

    /**
     * @see jaskell.compiler.core.Namespace#rebind(String, Expression)
     */
    public Object rebind(String name, Object value) {
        Expression ex = (Expression) bindings.get(name);
        bindings.put(name, value);
        return ex;
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

    /* (non-Javadoc)
     * @see jaskell.compiler.core.Namespace#getName()
     */
    public String getName() {
        return "_let";
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.core.Namespace#addTypeDefinition(jaskell.compiler.core.Definition)
     */
    public void addTypeDefinition(Definition def) {
        throw new JaskellException("Cannot bind a type definition into a Let construct");
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.core.Namespace#resolveType(java.lang.String)
     */
    public Definition resolveType(String name) {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.lifl.parsing.Namespace#forward(java.lang.String, java.lang.Object)
     */
    public void forward(String name, Object definition) throws SymbolException {
        throw new SymbolException("Cannot forward " + name + " in Let environment");
    }

    /* (non-Javadoc)
     * @see fr.lifl.parsing.Namespace#unbind(java.lang.String)
     */
    public Object unbind(String name) throws SymbolException {
        throw new SymbolException("Cannot unbind " + name + " in Let environment");
    }

    /* (non-Javadoc)
     * @see fr.lifl.parsing.Namespace#getEnclosing()
     */
    public Namespace getEnclosing() {
        Object ex = parent;
        while (!(ex instanceof Namespace))
            ex = ((Expression) ex).getParent();
        return (Namespace) ex;
    }

    /* (non-Javadoc)
     * @see fr.lifl.parsing.Namespace#resolve(java.lang.String)
     */
    public Object resolve(String name) throws SymbolException {
        return lookup(name);
    }

    /* (non-Javadoc)
     * @see fr.lifl.parsing.Namespace#getAllBindings()
     */
    public Map getAllBindings() {
        return new HashMap(bindings);
    }
}
