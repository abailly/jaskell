package fr.lifl.jaskell.runtime.types;

/**
 * A generic class for 3-tuples.
 * 
 * @author bailly
 * @version $Id: Tuple_3.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class Tuple_3 extends Tuple {

	private JObject fst,snd,thd;
	
	/**
	 * Unboxed - generic - constructor for 3-tuples
	 * 
	 */
	protected Tuple_3() {
		super(3);
	}
	
	/**
	 * Boxed constructor for Tuple_3.
	 * @param sz
	 */
	public Tuple_3(JObject a, JObject b,JObject c) {
		super(3);
		fst = a;
		snd = b;
		thd = c;
	}

}
