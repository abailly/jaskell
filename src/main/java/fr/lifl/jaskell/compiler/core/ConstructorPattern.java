package fr.lifl.jaskell.compiler.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.lifl.jaskell.compiler.JaskellVisitor;

/**
 * @author bailly
 * @version $Id: ConstructorPattern.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class ConstructorPattern extends Pattern implements Cloneable {

	/** The constructor reference of this pattern */
	private Constructor constructor;

	/** The list of sub-patterns */
	private List patterns = new ArrayList();


	/**
	 * Constructor for ConstructorPattern.
	 */
	public ConstructorPattern() {
		super();
	}

	/**
	 * @see jaskell.compiler.core.Expression#lookup(String)
	 */
	public Expression lookup(String vname) {
		Iterator it = getBindings().iterator();
		while (it.hasNext()) {
			LocalBinding b = (LocalBinding) it.next();
			if (b.getName().equals(vname))
				return b;
		}
		return getParent().lookup(vname);
	}

	/**
	 *  Adds a new pattern to this pattern 
	 * 
	 * @param pat a Pattern object
	 */
	public void addPattern(Pattern pat) {
		patterns.add(pat);
		pat.setParent(this);
	}

	/**
	 * @see jaskell.compiler.core.Pattern#getSubPatterns()
	 */
	public Iterator getSubPatterns() {
		return patterns.iterator();
	}

	/**
	 * Returns the constructor.
	 * @return Constructor
	 */
	public Constructor getConstructor() {
		return constructor;
	}

	/**
	 * Sets the constructor.
	 * @param constructor The constructor to set
	 */
	public void setConstructor(Constructor constructor) {
		this.constructor = constructor;
		constructor.setParent(this);
	}


	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("(");
		sb.append(constructor);
		Iterator it = patterns.iterator();
		while (it.hasNext())
			sb.append(' ').append(it.next());
		return sb.append(")").toString();

	}

	public Object clone() throws CloneNotSupportedException {
		ConstructorPattern cp = new ConstructorPattern();
		cp.setConstructor((Constructor) constructor.clone());
		Iterator it = patterns.iterator();
		while (it.hasNext()) {
			Pattern pat = (Pattern) it.next();
			cp.addPattern((Pattern) pat.clone());
		}
		cp.setType(getType());
		return cp;
	}
}
