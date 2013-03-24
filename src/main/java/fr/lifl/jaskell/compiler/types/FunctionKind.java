package fr.lifl.jaskell.compiler.types;

/**
 * 
 * @author bailly
 * @version $Id: FunctionKind.java 1153 2005-11-24 20:47:55Z nono $
 */
public class FunctionKind implements Kind {

	private Kind from;
	private Kind to;
	
		public static final Kind K_K = new FunctionKind(SimpleKind.K, SimpleKind.K);
	/**
	 * Constructor for FunctionKind.
	 */
	public FunctionKind(Kind from,Kind to) {
		this.from = from;
		this.to  = to;
	}

	/**
	 * Create a function kind with given number of arguments 
	 * 
	 * @param i an integer
	 * @return a functionkind 
	 */
	public static Kind makeKind(int i) {
		Kind k = K_K;
		for(int j= 1;j<i;j++) 
			k = new FunctionKind(SimpleKind.K,k);
		return k;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + from +" -> " + to +")";
	}

	/**
	 * Method getRange.
	 * @return Kind
	 */
	public Kind getRange() {
		return to;
	}


	/**
	 * Method getDomain.
	 */
	public Kind  getDomain() {
		return from;
	}

	/**
	 * Method apply.
	 * @param kind
	 */
	public Kind apply(Kind kind) {
		if(kind.equals(from))
			return to;
		throw new TypeError("Cannot apply "+kind +" to "+from);
	}

}
