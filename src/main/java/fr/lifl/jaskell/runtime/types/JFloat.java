package fr.lifl.jaskell.runtime.types;

/**
 * Wrapper class for float values
 * 
 * @author bailly
 * @version $Id: JFloat.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class JFloat extends JValue {

	/** the float contained in this JInt */
	private float f;

	/**
	 * constructs a JFloat from a float
	 * 
	 * @param f a float
	 */
	public JFloat(float f) {
		this.f = f;
	}


	/**
	 * @see jaskell.runtime.modules.types.JObject#evalAsFloat()
	 */
	public float asFloat() {
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
		JFloat jfloat = (JFloat)obj;
		return jfloat.f == f;
	}

}
