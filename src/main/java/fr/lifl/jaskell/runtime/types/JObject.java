package fr.lifl.jaskell.runtime.types;

/**
 * The root class for all objects manipulated in Jaskell.
 * 
 * All objects manipulated in Jaskell derives from this base class. 
 * 
 * @author bailly
 * @version $Id: JObject.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public interface JObject {
	/**
	 * Evaluates this objects, yielding a supposedly reduced
	 * object. The implementation iin JObject does nothing and
	 * returns this.
	 * 
	 * @return a JObject which is guaranteed to be the same object
	 * or a reduced form of this.
	 */
	public JObject eval();


}
