/*
 * Created on Jun 4, 2003 by Arnaud Bailly - bailly@lifl.fr
 * Copyright 2003 - Arnaud Bailly 
 */
package fr.lifl.jaskell.runtime.types;


/** a private class for handling the nil constant */
public class _5b_5d extends JList implements JFunction {
	
	/** The nil object */
	static public final JList _instance = new _5b_5d();

	public _5b_5d() {}

	/* (non-Javadoc)
	 * @see jaskell.runtime.types.JFunction#apply(jaskell.runtime.types.JObject)
	 */
	public JObject apply(JObject obj) {
		throw new JError("Invalid application of "+obj +" to Cons");
	}

	
	/* (non-Javadoc)
	 * @see jaskell.runtime.types.JFunction#init()
	 */
	public JFunction init() {
		return (JFunction)_instance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[]";
	}

}