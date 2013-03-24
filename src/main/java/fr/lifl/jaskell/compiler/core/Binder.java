package fr.lifl.jaskell.compiler.core;

/**
 * An interface for expressions that bind  variables
 * 
 * This interface is implemented by expressions which bind variables. It is mainly used
 * to do strictness analysis and retrieve parameters by positions
 * 
 * @author bailly
 * @version $Id: Binder.java 1153 2005-11-24 20:47:55Z nono $
 */
public interface Binder {

	/**
	 * Returns the strictness status of a parameter 
	 * given by its index
	 * 
	 * @param i index of parameter
	 */
	public boolean isStrict(int i);

	/**
	 * Sets the strictness status of paremeter given by name
	 * 
	 * @param s the name of variable to set strict
	 */
	public void setStrict(String s);
}
