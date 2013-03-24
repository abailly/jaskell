/*
 * Created on Mar 9, 2004
 * 
 * $Log: FunctionComparator.java,v $
 * Revision 1.1  2004/03/10 20:31:56  bailly
 * added type comparison classes
 *
 */
package fr.lifl.jaskell.compiler.types;

/**
 * A class that compares function types. 
 * <p>
 * Function types are covariant in their domain and contravariant on their range.
 * This class compare method assumes given type objects are constructed from function 
 * type constructor.
 * 
 * @author bailly
 * @version $Id: FunctionComparator.java 1153 2005-11-24 20:47:55Z nono $
 */
public class FunctionComparator extends TypeComparator {

	/* (non-Javadoc)
	 * @see jaskell.compiler.types.TypeComparator#compare(jaskell.compiler.types.Type, jaskell.compiler.types.Type)
	 */
	public int compare(Type o1, Type o2) throws UncomparableException {
		int ret = 0;
		Type r1 = ((TypeApplication) o1).getRange();
		Type r2 = ((TypeApplication) o2).getRange();
		Type d1 = ((TypeApplication)((TypeApplication) o1).getDomain()).getRange();
		Type d2 = ((TypeApplication)((TypeApplication) o2).getDomain()).getRange();
		/* compare */
		int rcomp = r1.compare(r2);
		int dcomp = d1.compare(d2);
		/* equality case */
		if((dcomp == 0) && (rcomp == 0))
			return 0;
			
		if((dcomp <= 0) && (rcomp >=0))
			return -1;
		if((dcomp >= 0) && (rcomp <= 0))
			return 1;
		/* default case */
		throw new UncomparableException("Type "+o1 + " is not comparable with "+o2);
	}

}
