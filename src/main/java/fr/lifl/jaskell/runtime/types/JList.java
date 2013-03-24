package fr.lifl.jaskell.runtime.types;

/**
 * Abstract base class for all objects of type list. Each subclass
 * of lists handles a specific kind of list.
 * 
 * @author bailly
 * @version $Id: JList.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public abstract class JList extends JValue {

	public static JList _5b_5d() {
		return _5b_5d._instance;
	}

	public static JList _3a(JObject obj, JObject list) {
		return new _3a(obj, list);
	}

}
