/*
 * Created on Jun 9, 2003
 * Copyright 2003 Arnaud Bailly
  */
package fr.lifl.jaskell.compiler.core;

/**
 * Tag instances are used to annotate the abstract syntax tree with 
 * semantic information like strictness status or type.
 * 
 * @author bailly
 * @version $Id: Tag.java 1153 2005-11-24 20:47:55Z nono $
 */
public interface Tag {

	/**
	 * Returns the name identifying this tag. A name must
	 * be unique among all tags stored in a node as it is used
	 * as a key to a hashtable.
	 * 
	 * @return
	 */
	public String getName();
	
	
	
}
