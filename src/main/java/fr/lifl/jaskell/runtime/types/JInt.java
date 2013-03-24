package fr.lifl.jaskell.runtime.types;

/**
 * Wrapper class for int values
 * 
 * @author bailly
 * @version $Id: JInt.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class JInt extends JValue {

	/** the integer contained in this JInt */
	private int i;

	/**
	 * constructs a JInt from an int
	 * 
	 * @param i an integer
	 */
	public JInt(int i) {
		this.i = i;
	}

	/**
	 * @see jaskell.runtime.modules.types.JObject#evalAsInt()
	 */
	public int asInt() {
		return i;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ""+i;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		JInt jint = (JInt)obj;
		return jint.i == i;
	}

}
