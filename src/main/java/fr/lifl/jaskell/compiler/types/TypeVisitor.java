package fr.lifl.jaskell.compiler.types;

import java.util.logging.Logger;


/**
 * An interface to visit type constructs
 * 
 * @author bailly
 * @version $Id: TypeVisitor.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public interface TypeVisitor<T> {

	public static final Logger log = Logger.getLogger(TypeVisitor.class.getName());

	T visit(TypeVariable t);
	
	T visit(PrimitiveType primitiveType);

	T visit(TypeApplication typeApplication);

	T visit(TypeConstructor typeConstructor);

    T visit(ConstrainedType constrainedType);
}
