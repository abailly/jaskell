package fr.lifl.jaskell.compiler.types;

import fr.lifl.jaskell.compiler.core.Tag;

/**
 * @author abailly
 * @version $Id: TypeError.java 1154 2005-11-24 21:43:37Z nono $
 */
public class TypeError extends RuntimeException {

    private Tag tag;

	/**
     * Constructor for TypeError.
     */
    public TypeError() {
        super();
    }

    /**
     * Constructor for TypeError.
     * @param message
     */
    public TypeError(String message) {
        super(message);
    }

    /**
     * Constructor for TypeError.
     * @param message
     * @param cause
     */
    public TypeError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for TypeError.
     * @param cause
     */
    public TypeError(Throwable cause) {
        super(cause);
    }

	/**
	 * @param tag
	 */
	public void setLineCol(Tag tag) {
		this.tag  = tag;
	}
	
	public Tag getLineCol() {
		return tag;
	}

}
