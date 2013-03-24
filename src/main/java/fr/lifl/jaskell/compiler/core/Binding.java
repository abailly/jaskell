package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.types.Type;

/**
 * This interface is implemented by all name to objects bindings.
 * 
 * @author bailly
 * @version $Id: Binding.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public interface Binding {
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() ;

	/**
	 * Returns the type.
	 * @return Type
	 */
	public Type getType() ;
	
	/**
	 * Returns the strictness status of this bindings
	 * 
	 * @return true is this binding is strict, false otherwise
	 */
	public boolean isStrict();

	/**
	 * Method getDefinition.
	 * @return Expression
	 */
	Expression getDefinition();

}
