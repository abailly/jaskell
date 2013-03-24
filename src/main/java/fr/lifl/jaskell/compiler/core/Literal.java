package fr.lifl.jaskell.compiler.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author bailly
 * @version $Id: Literal.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public abstract class Literal extends Pattern {

	/**
	 * @see jaskell.compiler.core.Pattern#getSubPatterns()
	 */
	public Iterator getSubPatterns() {
		return new EmptyIterator();
	}

	/**
	 * @see jaskell.compiler.core.Pattern#countBindings()
	 */
	public int countBindings() {
		return 0;
	}

	/**
	 * @see jaskell.compiler.core.Pattern#getBindings()
	 */
	public List getBindings() {
		return new ArrayList();
	}


	/**
	 * Return a Java representation of this literal as an object
	 * 
	 * @return an Object representing this literal in Java
	 */
	public abstract Object unpack();

	/**
	 * Returns a Literal expression representing the given object
	 * 
	 * @param obj a constant Object which must be a java value compatible
	 * with one of the literal types
	 * @return a Literal expression
	 */
	public static Literal pack(Object obj) {
		if (obj instanceof Integer)
			return new IntegerLiteral(((Integer) obj).intValue());
		else if (obj instanceof Float)
			return new FloatLiteral(((Float) obj).floatValue());
		else if (obj instanceof Double)
			return new DoubleLiteral(((Double) obj).doubleValue());
		else if (obj instanceof Boolean)
			return new BooleanLiteral(((Boolean) obj).booleanValue());
		else if (obj instanceof Character)
			return new CharLiteral(((Character) obj).charValue());
		else if (obj instanceof String)
			return new StringLiteral((String) obj);
		else
			throw new IllegalArgumentException("Unable to pack " + obj);

	}

}
