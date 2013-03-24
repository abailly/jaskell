package fr.lifl.jaskell.compiler.types;

/**
 * 
 * @author bailly
 * @version $Id: SimpleKind.java 1153 2005-11-24 20:47:55Z nono $
 */
public class SimpleKind implements Kind {
	
	/* the one and only instance of SimpleKind */
	public static final SimpleKind K = new SimpleKind();
	
	/* constructor is made private to prevent construction of
	 * other simple kind
	 */
	private SimpleKind() {}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "*";
	}

	/**
	 * @see jaskell.compiler.types.Kind#apply(Kind)
	 */
	public Kind apply(Kind kind) {
		throw new TypeError("Invalid appllication to kind *");
	}

}
