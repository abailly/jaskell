package fr.lifl.jaskell.compiler.types;

/**
 * 
 * @author bailly
 * @version $Id: Kind.java 1153 2005-11-24 20:47:55Z nono $
 */
public interface Kind {
	public Kind apply(Kind kind);

}
