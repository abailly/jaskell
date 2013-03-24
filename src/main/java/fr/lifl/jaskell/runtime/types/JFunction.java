package fr.lifl.jaskell.runtime.types;

/**
 * Base interface for function objects 
 * 
 * @author bailly
 * @version $Id: JFunction.java 1153 2005-11-24 20:47:55Z nono $
 */
public interface JFunction extends JObject {
	
	/**
	 * Partial application of arguments
	 * 
	 * @param obj a JObject
	 * @return a JObject
	 */
	public JObject apply(JObject obj);
	
	/**
	 * Create a new instance of this function 
	 * 
	 * @return a copy of this function
	 */
	public JFunction init();
		
}
