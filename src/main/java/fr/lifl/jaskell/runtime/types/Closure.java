package fr.lifl.jaskell.runtime.types;

/**
 * @author bailly
 * @version $Id: Closure.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public abstract class Closure implements JFunction {

	/** Array of JObjects containing collected arguments for this closure */
	protected JObject args[];

	/** number of arguments */
	protected int nargs;

	/** maximum number of arguments */
	protected int maxargs;

	/**
	 * Creates a closure with n arguments
	 * 
	 * @param n number of arguments of Closure. Must be > 0
	 */
	protected Closure(int n) {
		args = new JObject[n];
		nargs = 0;
		maxargs = n;
	}

	/**
	 * This method handles boxed - non strict - partial application 
	 * for closure objects. The passed argument is the stored in the 
	 * args array at index nargs. This method assumed the closure is
	 * not fully applied
	 * 
	 * @param arg argument to this CLosure
	 * @return if nargs < args.length, this method returns this closure,
	 * else it returns the result of evaluating this closure's code 
	 * with given arguments array
	 */
	public JObject apply(JObject arg) {
		if (nargs == maxargs)
			throw new IllegalArgumentException(
				"Too many arguments to function " + this);
		args[nargs++] = arg;
		return this;
	}

	/**
	 * Evaluation of Closure object
	 * 
	 * If number of applied arguments is less than number of 
	 * required arguments, this method yields the closure object itself. 
	 * Otherwise, it calls the abstract <code>eval0()</code> method which 
	 * generally recursively evaluates the applied arguments and 
	 * calls a type specific eval function which 
	 * contains the real code of the method.
	 *
	 * @see JObject#eval()
	 */
	public JObject eval() {
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<function>";
	}

}
