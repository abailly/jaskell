package fr.lifl.jaskell.runtime.classes;

/**
 * Base interface for types defining equality
 * 
 * @author bailly
 * @version $Id: Eq.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public interface Eq {
	
	/** 
	 * (==) :: a -> a -> Bool
	 */
	public boolean _3d_3d(Eq a, Eq b);
	
	/** 
	 * (/=) :: a -> a -> Bool
	 */
	public boolean _2f_3d(Eq a, Eq b);
		
}
