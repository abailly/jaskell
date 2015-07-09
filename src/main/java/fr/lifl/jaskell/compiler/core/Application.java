/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.lifl.jaskell.compiler.JaskellVisitor;


/**
 * This class represents applicative forms. An application is basically just a sequence of expressions which can later
 * be evaluated through a function.
 *
 * @author  bailly
 * @version $Id: Application.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Application extends ExpressionBase {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /** list of expressions in the application */
    private List args = new ArrayList();

    /** functional expression for this application */
    private Expression function;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Adds an argument to this application
     *
     * @param expr Expression object to add
     */
    public void addArgument(Expression expr) {
        args.add(expr);
        expr.setParent(this);
    }

    public void setArgument(Expression expr, int pos) {
        args.set(pos, expr);
        expr.setParent(this);
    }

    public Expression getArgument(int pos) {
        return (Expression) args.get(pos);
    }

    /**
     * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
     */
    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * Returns the args.
     *
     * @return List
     */
    public List getArgs() {
        return args;
    }

    /**
     * Returns the function.
     *
     * @return Expression
     */
    public Expression getFunction() {
        return function;
    }

    /**
     * Sets the function.
     *
     * @param function The function to set
     */
    public void setFunction(Expression function) {
        this.function = function;
        function.setParent(this);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("(");
        sb.append(function);
        Iterator it = args.iterator();
        while (it.hasNext())
            sb.append(' ').append(it.next());
        sb.append(')');
        return sb.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        Application app = new Application();
        Iterator it = args.iterator();
        while (it.hasNext())
            app.addArgument((Expression) ((ExpressionBase) it.next()).clone());

        app.setFunction((Expression) ((ExpressionBase) function).clone());
        app.setType(getType());
        return app;
    }

}
