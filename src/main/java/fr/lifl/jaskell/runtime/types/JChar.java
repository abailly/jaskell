package fr.lifl.jaskell.runtime.types;

/**
 * Wrapper class for char values
 * 
 * @author bailly
 * @version $Id: JChar.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class JChar extends JValue {

	/** the char contained in this JInt */
	private char f;

	/**
	 * constructs a Jchar from a char
	 * 
	 * @param f a char
	 */
	public JChar(char f) {
		this.f = f;
	}


	/**
	 * @see jaskell.runtime.modules.types.JObject#evalAsChar()
	 */
	public char asChar() {
		return f;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ""+f;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		JChar jchar = (JChar)obj;
		return jchar.f == f;
	}

}
