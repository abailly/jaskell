package fr.lifl.jaskell.compiler;

/**
 * 
 * @author bailly
 * @version $Id: CompilerException.java 1153 2005-11-24 20:47:55Z nono $
 */
public class CompilerException extends RuntimeException {
	
	public CompilerException (String reason) {
		super(reason);
	}

}
