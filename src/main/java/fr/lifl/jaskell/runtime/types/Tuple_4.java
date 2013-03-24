package fr.lifl.jaskell.runtime.types;

/**
 * A generic class for 4-tuples.
 * 
 * @author bailly
 * @version $Id: Tuple_4.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class Tuple_4 extends Tuple {

	private JObject fst,snd,thd,fth;
	
	/**
	 * Unboxed - generic - constructor for 4-tuples
	 * 
	 */
	protected Tuple_4() {
		super(4);
	}
	
	/**
	 * Boxed constructor for Tuple_2.
	 * @param sz
	 */
	public Tuple_4(JObject a, JObject b,JObject c, JObject d) {
		super(4);
		fst = a;
		snd = b;
		thd = c;
		fth = d;
	}

}
