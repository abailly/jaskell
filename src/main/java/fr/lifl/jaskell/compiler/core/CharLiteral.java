package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: CharLiteral.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class CharLiteral extends Literal {
	
		/** the char */
		private char fl;
		
		public CharLiteral(char fl) {
			this.fl = fl;
		}
		
		/**
		 *  Gets char represented by this literal
		 * 
		 * @return a char value
		 */
		public char getChar() {
			return fl;
		}
		
		
	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return Primitives.CHAR;
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
		return new Character(fl);
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof CharLiteral))
			return false;
		CharLiteral i = (CharLiteral)obj;
		return i.getChar() == this.getChar();	
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
		CharLiteral il = new CharLiteral(fl);
		il.setParent(getParent());
		return il;
	}

}
