package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;

/**
 * A class representing simple if-then-else conditions
 * 
 * This class represents simple conditional constructs whihc may be used
 * instead of the more general and complicated Alternative construct. 
 * 
 * @author bailly
 * @version $Id: Conditional.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Conditional extends ExpressionBase {

	/**
	 *  conditional expression
	 */
	private Expression condition;
	
	/**
	 * true branch
	 */
	private Expression ifTrue;
	
	/**
	 * false branch
	 */
	private Expression ifFalse;


	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}
	

	/**
	 * Returns the condition.
	 * @return Expression
	 */
	public Expression getCondition() {
		return condition;
	}

	/**
	 * Returns the ifFalse.
	 * @return Expression
	 */
	public Expression getIfFalse() {
		return ifFalse;
	}

	/**
	 * Returns the ifTrue.
	 * @return Expression
	 */
	public Expression getIfTrue() {
		return ifTrue;
	}

	/**
	 * Sets the condition.
	 * @param condition The condition to set
	 */
	public void setCondition(Expression condition) {
		this.condition = condition;
		condition.setParent(this);
	}

	/**
	 * Sets the ifFalse.
	 * @param ifFalse The ifFalse to set
	 */
	public void setIfFalse(Expression ifFalse) {
		this.ifFalse = ifFalse;
		ifFalse.setParent(this);
	}

	/**
	 * Sets the ifTrue.
	 * @param ifTrue The ifTrue to set
	 */
	public void setIfTrue(Expression ifTrue) {
		this.ifTrue = ifTrue;
		ifTrue.setParent(this);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("if ");
		sb.append(condition);
		sb.append(" then ").append(ifTrue);
		sb.append(" else ").append(ifFalse);
		return sb.toString();
		
	}

	public Object clone() throws CloneNotSupportedException{
		Conditional cond = new Conditional();
		cond.setCondition((Expression)((ExpressionBase)condition).clone());
		cond.setIfFalse((Expression)((ExpressionBase)ifFalse).clone());
		cond.setIfTrue((Expression)((ExpressionBase)ifTrue).clone());
		cond.setType(getType());
		cond.setParent(getParent());
		return cond;
	}

}
