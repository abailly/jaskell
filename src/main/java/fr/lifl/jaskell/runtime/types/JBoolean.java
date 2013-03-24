package fr.lifl.jaskell.runtime.types;

/**
 * Wrapper class for boolean values
 * 
 * @author bailly
 * @version $Id: JBoolean.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class JBoolean extends JValue {

	/** the boolean contained in this JInt */
	private boolean f;

	/**
	 * constructs a Jboolean from a boolean
	 * 
	 * @param f a boolean
	 */
	public JBoolean(boolean f) {
		this.f = f;
	}


	/**
	 * @see jaskell.runtime.modules.JFloat.javatypes.JObject#evalAsBoolean()
	 */
	public boolean asBool() {
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
		JBoolean jbo = (JBoolean)obj;
		return jbo.f == f;
	}

}
