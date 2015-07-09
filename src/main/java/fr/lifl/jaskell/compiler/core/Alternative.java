/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.lifl.jaskell.compiler.JaskellVisitor;


/**
 * Class representing 'case' expressions. This class encapsulates an alternative expression, built from :
 *
 * <ul>
 * <li>an expression which is evaluated and checked for,</li>
 * <li>a binding variable to which the result of the evaluation is bound,</li>
 * <li>a list of case statements made of patterns-expression couples.</li>
 * </ul>
 *
 * @author  bailly
 * @version $Id: Alternative.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Alternative extends ExpressionBase {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /** the evaluated expression */
    private Expression expression;

    /** the binding for this alternative */
    private LocalBinding binding;

    /** the list of patterns and expressions */
    private LinkedHashMap patterns = new LinkedHashMap();

    /** the catch-all pattern */
    private Expression wildcard;

    /** count of maximal size of local bindings in patterns */
    private int maxCountBindings = 0;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
     */
    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * Adds a new pattern-expression couple to this alternative. In the processe, the local bindings are reindexed
     * starting from 0.
     *
     * @param pat  a Pattern object
     * @param body the expression for this alternative
     */
    public void addPattern(Pattern pat, Expression body) {
        /* update maximum count of bindings */
        int b = pat.countBindings();
        maxCountBindings = (b > maxCountBindings) ? b : maxCountBindings;
        patterns.put(pat, body);
        /* update indices of bindings in pat */
        Iterator it = pat.getBindings().iterator();
        int i = 0;
        while (it.hasNext()) {
            LocalBinding bp = (LocalBinding) it.next();
            bp.setIndex(i++);
        }
        /* update parent relationship */
        pat.setParent(this);
        body.setParent(pat);
    }

    /**
     * Returns the binding.
     *
     * @return LocalBinding
     */
    public LocalBinding getBinding() {
        return binding;
    }

    /**
     * Returns the expression.
     *
     * @return Expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Returns the patterns.
     *
     * @return List
     */
    public Iterator getPatterns() {
        return patterns.keySet().iterator();
    }

    /**
     * Returns an iterator over all the pattern/expressions couples in this alternative. The returned Iterator objetc
     * ranges over <code>Map.Entry</codE> objects.
     *
     * @return an Iterator object
     */
    public Iterator getChoices() {
        return patterns.entrySet().iterator();
    }

    /**
     * Returns an iterator over the set of all body of choices made in this alternative.
     *
     * @return an Iterator over all expressions
     */
    public Iterator getBodies() {
        return patterns.values().iterator();
    }

    /**
     * Returns the wildcard.
     *
     * @return Expression
     */
    public Expression getWildcard() {
        if (wildcard == null) { /* set wildcard to error */
            Application app = new Application();
            QualifiedVariable qv = new QualifiedVariable("error");
            qv.addPathElement("Prelude");
            app.setFunction(qv);
            app.addArgument(new StringLiteral("no match"));
            wildcard = app;
        }
        return wildcard;
    }

    /**
     * Sets the binding.
     *
     * @param binding The binding to set
     */
    public void setBinding(LocalBinding binding) {
        this.binding = binding;
        binding.setParent(this);
    }

    /**
     * Sets the expression.
     *
     * @param expression The expression to set
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
        expression.setParent(this);
    }

    /**
     * Sets the wildcard.
     *
     * @param wildcard The wildcard to set
     */
    public void setWildcard(Expression wildcard) {
        this.wildcard = wildcard;
        wildcard.setParent(this);
    }

    /**
     * Returns the maxCountBindings.
     *
     * @return int
     */
    public int getMaxCountBindings() {
        return maxCountBindings;
    }

    /**
     * Returns a body given a pattern
     *
     * @param  pat a Pattern
     *
     * @return the body of given pattern or null
     */
    public Expression getBody(Pattern pat) {
        return (Expression) patterns.get(pat);
    }

    /**
     * @see jaskell.compiler.core.Expression#lookup(String)
     */
    public Expression lookup(String vname) {
        if (binding != null)
            if (vname.equals(binding.getName()))
                return binding;
        return parent.lookup(vname);
    }

    /**
     * Method setBody.
     *
     * @param pat
     * @param object
     */
    public void setBody(Pattern pat, Expression expr) {
        patterns.put(pat, expr);
        expr.setParent(pat);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("case ");
        if (binding != null)
            sb.append(binding).append("@(").append(expression).append(")");
        else
            sb.append(expression);
        sb.append(" of {");
        Iterator it = patterns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append(';');
        }
        sb.append("_ -> ").append(wildcard).append('}');
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        Alternative alt = new Alternative();
        alt.setExpression((Expression) ((ExpressionBase) expression).clone());
        alt.setBinding((LocalBinding) binding.clone());
        alt.setWildcard((Expression) ((ExpressionBase) wildcard).clone());
        alt.setType(getType());
        Iterator it = patterns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            alt.addPattern((Pattern) ((Pattern) entry.getKey()).clone(), (Expression) ((ExpressionBase) entry.getValue()).clone());
        }
        alt.setParent(getParent());
        return alt;
    }

}
