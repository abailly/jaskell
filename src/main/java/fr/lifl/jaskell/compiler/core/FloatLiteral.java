package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: FloatLiteral.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class FloatLiteral extends Literal {

	/** the float */
	private float fl;

	public FloatLiteral(float fl) {
		this.fl = fl;
	}

	/**
	 *  Gets float represented by this literal
	 * 
	 * @return a float value
	 */
	public float getFloat() {
		return fl;
	}

	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return Primitives.FLOAT;
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
		return new Float(fl);
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof FloatLiteral))
			return false;
		FloatLiteral i = (FloatLiteral) obj;
		return i.getFloat() == this.getFloat();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + fl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		FloatLiteral il = new FloatLiteral(fl);
		il.setParent(getParent());
		return il;
	}

}
