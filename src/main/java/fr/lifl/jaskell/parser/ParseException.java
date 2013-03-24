package fr.lifl.jaskell.parser;

/**
 * @author bailly
 * @version $Id: ParseException.java 1153 2005-11-24 20:47:55Z nono $
 */
public class ParseException extends RuntimeException {

	/**
	 * Constructor for ParseException.
	 */
	public ParseException() {
		super();
	}

	/**
	 * Constructor for ParseException.
	 * @param message
	 */
	public ParseException(String message) {
		super(message);
	}

	/**
	 * Constructor for ParseException.
	 * @param message
	 * @param cause
	 */
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for ParseException.
	 * @param cause
	 */
	public ParseException(Throwable cause) {
		super(cause);
	}

}
