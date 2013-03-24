package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: BooleanLiteral.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class BooleanLiteral extends Literal {
	
		/** the boolean */
		private boolean fl;
		
		public BooleanLiteral(boolean fl) {
			this.fl = fl;
		}
		
		/**
		 *  Gets boolean represented by this literal
		 * 
		 * @return a boolean value
		 */
		public boolean getBoolean() {
			return fl;
		}
		
		
	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return Primitives.BOOL;
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
		return Boolean.valueOf(fl);
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof BooleanLiteral))
			return false;
		BooleanLiteral i = (BooleanLiteral)obj;
		return i.getBoolean() == this.getBoolean();	
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(fl)
			return "True";
		else
			return "False";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		BooleanLiteral il = new BooleanLiteral(fl);
		il.setParent(getParent());
		return il;
	}

}
