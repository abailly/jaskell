package fr.lifl.jaskell.parser;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.Tag;
import fr.lifl.jaskell.compiler.types.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author bailly
 * @version $Id: ExpressionList.java 1154 2005-11-24 21:43:37Z nono $
 */
class ExpressionList extends ArrayList implements Expression {

	/**
	 * Constructor for ExpressionList.
	 * @param initialCapacity
	 */
	public ExpressionList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructor for ExpressionList.
	 */
	public ExpressionList() {
		super();
	}

	/**
	 * Constructor for ExpressionList.
	 * @param c
	 */
	public ExpressionList(Collection c) {
		super(c);
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
		throw new UnsupportedOperationException("Cannot visit an ExpressionList");
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
	

	protected HashMap tags;

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Expression#getTag(java.lang.String)
	 */
	public Tag getTag(String name) {
		if (tags == null)
			return null;
		else
			return (Tag) tags.get(name);
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Expression#putTag(jaskell.compiler.core.Tag)
	 */
	public void putTag(Tag tag) {
		if (tags == null)
			tags = new HashMap();
		tags.put(tag.getName(), tag);
	}


}
