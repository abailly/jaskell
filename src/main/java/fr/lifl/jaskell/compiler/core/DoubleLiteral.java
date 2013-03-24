package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: DoubleLiteral.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class DoubleLiteral extends Literal {
	
		/** the double */
		private double fl;
		
		public DoubleLiteral(double fl) {
			this.fl = fl;
		}
		
		/**
		 *  Gets double represented by this literal
		 * 
		 * @return a double value
		 */
		public double getDouble() {
			return fl;
		}
		
		
	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return Primitives.DOUBLE;
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
		return new Double(fl);
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof DoubleLiteral))
			return false;
		DoubleLiteral i = (DoubleLiteral)obj;
		return i.getDouble() == this.getDouble();	
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
		DoubleLiteral il = new DoubleLiteral(fl);
		il.setParent(getParent());
		return il;
	}

}
