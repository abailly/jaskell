/*
 * Created on Mar 9, 2004
 * 
 * $Log: CovariantComparator.java,v $
 * Revision 1.1  2004/03/10 20:31:56  bailly
 * added type comparison classes
 *
 */
package fr.lifl.jaskell.compiler.types;

/**
 * A covariant subtype relation implementation
 * 
 * @author bailly
 * @version $Id: CovariantComparator.java 1153 2005-11-24 20:47:55Z nono $
 */
public class CovariantComparator extends TypeComparator {

	/**
	 * This method assumes both arguments are type applications of 
	 * the same constructor. 
	 * 
	 * @see jaskell.compiler.types.TypeComparator#compare(jaskell.compiler.types.Type, jaskell.compiler.types.Type)
	 */
	public int compare(Type o1, Type o2) throws UncomparableException {
		int ret = 0;
		/* iterate over ranges */
		while ((o1 instanceof TypeApplication)
			&& (o2 instanceof TypeApplication)) {
			Type t1 = ((TypeApplication) o1).getRange();
			Type t2 = ((TypeApplication) o2).getRange();
			/* compare */
			int loc = t1.compare(t2);
			/* oppposite signs -> mismatch */
			if (((loc < 0) && (ret > 0)) || ((loc > 0) && (ret < 0)))
				throw new UncomparableException("Not covariant");
			/* keep current sign */
			if(loc != 0)
				ret = loc;
			/* loop */
			o1 = ((TypeApplication)o1).getDomain();
			o2 = ((TypeApplication)o2).getDomain();
		}
		return ret;
	}

}
