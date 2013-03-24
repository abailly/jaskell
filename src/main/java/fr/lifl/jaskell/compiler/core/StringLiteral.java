package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: StringLiteral.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class StringLiteral extends Literal {

	/** the String */
	private String string;
	
	public StringLiteral(String s) {
		string = s;
	}
	
	
	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return Primitives.STRING;
	}

	/**
	 * Returns the string.
	 * @return String
	 */
	public String getString() {
		return string;
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
		return string;
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof StringLiteral))
			return false;
		StringLiteral i = (StringLiteral)obj;
		return i.getString().equals(this.getString());	
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return '"' + string + '"';
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		StringLiteral sl = new StringLiteral(string);
		sl.setParent(getParent());
		return sl;
	}

}
