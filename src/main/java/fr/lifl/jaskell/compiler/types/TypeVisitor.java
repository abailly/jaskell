package fr.lifl.jaskell.compiler.types;

import java.util.logging.Logger;


/**
 * An interface to visit type constructs
 * 
 * @author bailly
 * @version $Id: TypeVisitor.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public interface TypeVisitor {

	public static final Logger log = Logger.getLogger(TypeVisitor.class.getName());

	public Object visit(TypeVariable t);
	
	/**
	 * Method visit.
	 * @param primitiveType
	 */
	public Object visit(PrimitiveType primitiveType);

	/**
	 * Method visit.
	 * @param typeApplication
	 * @return Object
	 */
	Object visit(TypeApplication typeApplication);

	/**
	 * Method visit.
	 * @param typeConstructor
	 * @return Object
	 */
	Object visit(TypeConstructor typeConstructor);

}
