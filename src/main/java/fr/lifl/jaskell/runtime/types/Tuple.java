package fr.lifl.jaskell.runtime.types;

/**
 * This class is the super class of all tuple objects.
 * 
 * The Tuple class is an abstract  class which only stores
 * the size of the tuple. Concrete subclass must be created for
 * each type instance.
 * 
 * @author bailly
 * @version $Id: Tuple.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public abstract class Tuple extends JValue {
	
		/** size of tuple */
		private int size;
		
		protected Tuple(int sz) {
			this.size = sz;
		}
		
}
