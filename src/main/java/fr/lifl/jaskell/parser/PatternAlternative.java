package fr.lifl.jaskell.parser;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.Pattern;
import fr.lifl.jaskell.compiler.core.Tag;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: PatternAlternative.java 1154 2005-11-24 21:43:37Z nono $
 */
public class PatternAlternative implements Expression {

	private Pattern pat ;
	private Expression expr;
	
	/**
	 * Constructor for PatternAlternative.
	 */
	public PatternAlternative(Pattern pat, Expression expr) {
		this.pat = pat;
		this.expr = expr;	
	}

	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return null;
	}

	/**
	 * @see jaskell.compiler.core.Expression#setType(Type)
	 */
	public void setType(Type type) {
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return null;
	}

	/**
	 * @see jaskell.compiler.core.Expression#getParent()
	 */
	public Expression getParent() {
		return null;
	}

	/**
	 * @see jaskell.compiler.core.Expression#setParent(Expression)
	 */
	public void setParent(Expression parent) {
	}

	/**
	 * @see jaskell.compiler.core.Expression#lookup(String)
	 */
	public Expression lookup(String vname) {
		return null;
	}

	/**
	 * Returns the expr.
	 * @return Expression
	 */
	public Expression getExpr() {
		return expr;
	}

	/**
	 * Returns the pat.
	 * @return Pattern
	 */
	public Pattern getPattern() {
		return pat;
	}

	/**
	 * Sets the expr.
	 * @param expr The expr to set
	 */
	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	/**
	 * Sets the pat.
	 * @param pat The pat to set
	 */
	public void setPattern(Pattern pat) {
		this.pat = pat;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Expression#getTag(java.lang.String)
	 */
	public Tag getTag(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Expression#putTag(jaskell.compiler.core.Tag)
	 */
	public void putTag(Tag tag) {
		// TODO Auto-generated method stub

	}

}
