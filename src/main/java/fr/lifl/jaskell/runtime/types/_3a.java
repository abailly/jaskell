/*
 * Created on Jun 4, 2003 by Arnaud Bailly - bailly@lifl.fr
 * Copyright 2003 - Arnaud Bailly 
 */
package fr.lifl.jaskell.runtime.types;

/**
 * @author bailly
 * @version $Id: _3a.java 1153 2005-11-24 20:47:55Z nono $
 */
public class _3a extends JList implements JFunction {

	public JObject _0;
	
	public JObject _1;
	
	int _nargs;
	
	public _3a(JObject obj,JObject list) {
		this._0 = obj;
		this._1 = list;
		this._nargs = 2;
	}
	
	public _3a() {
	}
	
	/* (non-Javadoc)
	 * @see jaskell.runtime.types.JFunction#apply(jaskell.runtime.types.JObject)
	 */
	public JObject apply(JObject obj) {
		if(this._nargs == 2)
			throw new JError("Invalid application of "+obj +" to Cons");
		switch(this._nargs++) {
			case 0:
				_0 = obj;
				break;
			case 1:
				_1 = obj;
				break;
		}
		return this;			
	}

	/* (non-Javadoc)
	 * @see jaskell.runtime.types.JFunction#init()
	 */
	public JFunction init() {
		return new _3a();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return _0 +" : " +_1;
	}

}
