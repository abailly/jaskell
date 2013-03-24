package fr.lifl.jaskell.compiler.types;

/**
 * An interface that is used by TypeApplication  to format
 * properly type applications
 * 
 * @author bailly
 * @version $Id: TypeApplicationFormat.java 1153 2005-11-24 20:47:55Z nono $
 */
public interface TypeApplicationFormat {

	/**
	 * Format application of dom to range
	 * 
	 * @param dom a Type
	 * @param range a Type
	 * @return string representation of this applicatino
	 */
	String formatApply(Type dom,Type range);
}
