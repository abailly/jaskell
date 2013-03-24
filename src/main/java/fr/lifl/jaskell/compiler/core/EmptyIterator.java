package fr.lifl.jaskell.compiler.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An empty iterator class.
 * 
 * @author bailly
 * @version $Id: EmptyIterator.java 1153 2005-11-24 20:47:55Z nono $
 *  */
class EmptyIterator implements Iterator {

	public boolean hasNext() {
		return false;
	}

	public Object next() {
		throw new NoSuchElementException("Cannot retrieve elements from an empty iterator");
	}

	public void remove() {
		throw new UnsupportedOperationException("Cannot remove elements from an empty iterator");
	}

}
