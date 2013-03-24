/*
 * Created on Mar 9, 2004
 * 
 * $Log: TypeComparator.java,v $
 * Revision 1.2  2004/09/07 10:04:04  bailly
 * cleared imports
 *
 * Revision 1.1  2004/03/10 20:31:56  bailly
 * added type comparison classes
 *
 */
package fr.lifl.jaskell.compiler.types;


/**
 * Compares two @see{Type} objects.
 * <p>
 * This class is used to compara two type objects constructed with 
 * the same type constructor. Each instance is normally associated with a
 * TypeConstructor object. The default behavior is covariant : a type t1 is
 *  a subtype of t2 of all componentns of t1 are subtypes of all component
 * of t2.
 * 
 * @author bailly
 * @version $Id: TypeComparator.java 1153 2005-11-24 20:47:55Z nono $
 */
public abstract class TypeComparator  {

	/**
	 * This method compares the two type objects and returns an integer denoting the relation
	 * between the two objects :
	 * <ul>
	 * <li>0 if the two objects are equals;</li>
	 * <li>&lt; 0 if <code>o1</code> is subtype of <code>o2</code>;</li>
	 * <li>&gt; 0 if <code>o1</code> is supertype of <code>o2</code>;</li>
	 * </ul>
	 * If the two objects are uncomparable, this class throws an exception. 
	 * 
	 * @param o1 first type to compare
	 * @param o2 second type to compare
	 * @return an integer denoting a subtype relation between the two objects
	 * @throws UncomparableException if the two objects are uncomparable
	 */
	public abstract int compare(Type o1, Type o2) throws UncomparableException ;

	/**
	 * Returns true if the two objects are equal.
	 * This method is equivalent to the following code fragment :
	 * <code>
	 * try {
	 * 	   return compare(o1,o2) == 0;
	 * } catch(UncomparableException ex) {
	 * 	   return false;
	 * }
	 * </code>
	 * 
	 * @param o1 first object to compare
	 * @param o2 second object to compare
	 * @return true if the two objects are equal according to this relation
	 */
	public boolean equals(Type o1, Type o2) {
		try {
			return compare(o1, o2) == 0;
		} catch (UncomparableException ex) {
			return false;
		}

	}

}
