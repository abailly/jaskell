/*
 * Created on Jun 9, 2003
 * Copyright 2003 Arnaud Bailly
  */
package fr.lifl.jaskell.compiler.core;

/**
 * @author bailly
 * @version $Id: JaskellException.java 1153 2005-11-24 20:47:55Z nono $
 */
public class JaskellException extends RuntimeException {

	/**
	 * 
	 */
	public JaskellException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public JaskellException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JaskellException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public JaskellException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
