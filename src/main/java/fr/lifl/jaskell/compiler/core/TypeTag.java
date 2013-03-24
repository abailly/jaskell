/*
 * Created on Jun 9, 2003
 * Copyright 2003 Arnaud Bailly
  */
package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.types.Type;

/**
 * This tag implementation is used to store the type of an 
 * expression. 
 * 
 * @author bailly
 * @version $Id: TypeTag.java 1154 2005-11-24 21:43:37Z nono $
 */
public class TypeTag implements Tag {

	private static final String tagname = "type";

	private Type type;

	/**
	 * 
	 */
	public TypeTag(Type type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Tag#getName()
	 */
	public String getName() {
		return tagname;
	}

	/**
	 * Returns the type stored in this tag
	 * @return a Type instance
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type stored in this tag
	 * @param type a Type instance
	 */
	public void setType(Type type) {
		this.type = type;
	}

}
