package fr.lifl.jaskell.runtime.types;

/**
 * Wrapper class for String values
 * 
 * @author bailly
 * @version $Id: JString.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class JString extends JValue {

	/** the String contained in this JInt */
	private java.lang.String f;

	/**
	 * constructs a JString from a String
	 * 
	 * @param f a String
	 */
	public JString(java.lang.String f) {
		this.f = f;
	}


	/**
	 * @see jaskell.runtime.modules.types.JObject#evalAsString()
	 */
	public java.lang.String asString() {
		return f;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return '"' + f + '"';
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		JString js = (JString)obj;
		return js.f.equals(f);
	}

}
