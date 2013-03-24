package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * This class encapsulates an int in a IntegerLiteral
 * 
 * @author bailly
 * @version $Id: IntegerLiteral.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class IntegerLiteral extends Literal {

	/** the integer */
	private int integer;

	
	public IntegerLiteral(int i) {
		this.integer = i;
	}
	
	
	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return Primitives.INT;
	}

	/**
	 * Returns the integer.
	 * @return int
	 */
	public int getInteger() {
		return integer;
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see jaskell.compiler.core.Literal#unpack()
	 */
	public Object unpack() {
		return new Integer(integer);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof IntegerLiteral))
			return false;
		IntegerLiteral i = (IntegerLiteral)obj;
		return i.getInteger() == this.getInteger();	
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + integer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		IntegerLiteral il = new IntegerLiteral(integer);
		il.setParent(getParent());
		return il;
	}

}
