package fr.lifl.jaskell.compiler.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A tagging interface implemented by all kind of expressions usable
 * in a pattern
 * 
 * This interface is implementd by all class of expression usable as
 * a pattern in an alternative. 
 * 
 * @author bailly
 * @version $Id: Pattern.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public abstract class Pattern extends ExpressionBase {

	/**
	 * This method returns sub-patterns of this pattern as
	 * an iterator. 
	 * 
	 * @return an Iterator object over all sub-patterns of this
	 * pattern
	 * @exception IllegalStateException if this pattern is not valid
	 */
	public abstract Iterator getSubPatterns();

	/**
	 * This method returns the count of bindings for this
	 * pattern
	 * 
	 * @return an integer giving the number of variables bind in
	 * this pattern
	 */
	public int countBindings() {
		int count = 0;
		Iterator it = getSubPatterns();
		while (it.hasNext())
			count += ((Pattern) it.next()).countBindings();
		return count;
	}

	/**
	 * This method returns all bindings in this pattern
	 * 
	 * @return an Iterator over all bindings in this pattern
	 */
	public List getBindings() {
		List l = new ArrayList();
		Iterator it = getSubPatterns();
		while (it.hasNext())
			l.addAll(((Pattern) it.next()).getBindings());
		return l;
	}

}
