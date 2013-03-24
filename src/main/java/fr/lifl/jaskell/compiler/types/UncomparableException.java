/*
 * Created on Mar 9, 2004
 * 
 * $Log: UncomparableException.java,v $
 * Revision 1.1  2004/03/10 20:31:56  bailly
 * added type comparison classes
 *
 */
package fr.lifl.jaskell.compiler.types;

/**
 * This exception is thrown by @see{PartialComparator} implementations to 
 * notify caller that the compared objects are indeed uncomparable.
 * 
 * @author bailly
 * @version $Id: UncomparableException.java 1153 2005-11-24 20:47:55Z nono $
 */
public class UncomparableException extends Exception {

	/**
	 * 
	 */
	public UncomparableException() {
		super();
	}

	/**
	 * @param message
	 */
	public UncomparableException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UncomparableException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UncomparableException(String message, Throwable cause) {
		super(message, cause);
	}

}
