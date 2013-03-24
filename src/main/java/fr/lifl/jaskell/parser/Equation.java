package fr.lifl.jaskell.parser;

import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.ExpressionBase;

import java.util.Iterator;

/**
 * @author bailly
 * @version $Id: Equation.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Equation extends ExpressionBase{

	private ExpressionList lhs;
	private Expression rhs;
	

	/**
	 * Returns the lhs.
	 * @return Expression
	 */
	public ExpressionList getLhs() {
		return lhs;
	}

	/**
	 * Returns the rhs.
	 * @return Expression
	 */
	public Expression getRhs() {
		return rhs;
	}

	/**
	 * Sets the lhs.
	 * @param lhs The lhs to set
	 */
	public void setLhs(Expression lhs) {
		if(lhs instanceof ExpressionList)
		this.lhs = (ExpressionList)lhs;
		else  {
			this.lhs = new ExpressionList();
			this.lhs.add(lhs);
		}
	}

	/**
	 * Sets the rhs.
	 * @param rhs The rhs to set
	 */
	public void setRhs(Expression rhs) {
		this.rhs = rhs;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("");
		Iterator it = lhs.iterator();
		while(it.hasNext()) 
			sb.append(it.next()).append(" ");
		sb.append(" = ").append(rhs);
		return sb.toString();
	}
		
}
