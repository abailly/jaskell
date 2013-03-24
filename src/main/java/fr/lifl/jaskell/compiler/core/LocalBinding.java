package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * This class contains name and type bindings
 * 
 * @author bailly
 * @version $Id: LocalBinding.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class LocalBinding extends Pattern implements Binding {

	private static final String freshPrefix = "v";
		
	private static int freshIndex = 0;

	/* map of all bindings already defined - to create fresh variables */
	private static WeakHashMap bounds = new WeakHashMap();

	/** catch-all binding */
	public static final LocalBinding wildcard = new LocalBinding("_");

	/** name of variable */
	private String name;

	/** index of variable */
	private int index;

	/** strictness of variable */
	private boolean strict;

	/**
	 * Constructs a new binding given a name and a type
	 * 
	 * @param name name of this binding
	 * @param type type of this binding. May be null
	 */
	public LocalBinding(String name, Type type) {
		this(name);
		this.type = type;
	}

	/**
	 * Constructor LocalBinding.
	 * @param string
	 */
	public LocalBinding(String name) {
		this.name = name;
		bounds.put(name,this);
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}


	/**
	 * Returns the index.
	 * @return int
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * @param index The index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the strict.
	 * @return boolean
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Sets the strict.
	 * @param strict The strict to set
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * @see jaskell.compiler.core.Pattern#getSubPatterns()
	 */
	public Iterator getSubPatterns() {
		return new EmptyIterator();
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see jaskell.compiler.core.Pattern#countBindings()
	 */
	public int countBindings() {
		return 1;
	}

	/**
	 * @see jaskell.compiler.core.Pattern#getBindings()
	 */
	public List getBindings() {
		List l = new ArrayList();
		l.add(this);
		return l;
	}

	/**
	 * @see jaskell.compiler.core.Expression#lookup(String)
	 */
	public Expression lookup(String vname) {
		if (vname.equals(name))
			return this;
		return getParent().lookup(vname);
	}

	/**
	 * @see jaskell.compiler.core.Binding#getDefinition()
	 */
	public Expression getDefinition() {
		return null;
	}

	/**
	 * Method freshBinding.
	 * @return LocalBinding
	 */
	public static LocalBinding freshBinding() {
		String fresh = freshPrefix + freshIndex;
		while(bounds.get(fresh) != null) fresh =  freshPrefix + ++freshIndex;
		return new LocalBinding(fresh);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		LocalBinding lb =new LocalBinding(name,getType());
		lb.setIndex(index);
		lb.setStrict(strict);
		return lb;
	}

}
