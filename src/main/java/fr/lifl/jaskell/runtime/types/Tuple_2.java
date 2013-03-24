package fr.lifl.jaskell.runtime.types;

/**
 * A generic class for 2-tuples.
 * 
 * @author bailly
 * @version $Id: Tuple_2.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class Tuple_2 extends Tuple {

	private JObject fst,snd;
	
	/**
	 * Unboxed - generic - constructor for two tuples
	 * 
	 */
	protected Tuple_2() {
		super(2);
	}
	
	/**
	 * Boxed constructor for Tuple_2.
	 * @param sz
	 */
	public Tuple_2(JObject a, JObject b) {
		super(2);
		fst = a;
		snd = b;
	}

	/**
	 * Return boxed first component of tuple
	 * 
	 * @return a JObject representing first component
	 */
	public JObject fst() {
		return fst;
	}
	
	/**
	 * Return boxed seconde component of tuple
	 * 
	 * @return a JObject representing second component
	 */
	public JObject snd() {
		return snd;
	}
	
}
