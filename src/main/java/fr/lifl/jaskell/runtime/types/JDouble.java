package fr.lifl.jaskell.runtime.types;

/**
 * Wrapper class for double values
 * 
 * @author bailly
 * @version $Id: JDouble.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class JDouble extends JValue {

	/** the double contained in this JInt */
	private double f;

	/**
	 * constructs a Jdouble from a double
	 * 
	 * @param f a double
	 */
	public JDouble(double f) {
		this.f = f;
	}


	/**
	 * @see jaskell.runtime.modules.types.JObject#evalAsDouble()
	 */
	public double asDouble() {
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
		JDouble jdouble = (JDouble)obj;
		return jdouble.f == f;
	}

}
