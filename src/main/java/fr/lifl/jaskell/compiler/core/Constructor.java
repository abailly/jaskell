package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * A class representing data constructors references.
 * 
 * This class is a specialization of Variable for identifying 
 * constructors occurences. In Haskell, Constructors are normally
 * identifiable by naming conventions : either an initiali upper-case letter
 * or an initial colon symbol for constructor operators.<p>
 * The type of a Constructor occurence is the type of data it constructs.
 * 
 * @author bailly
 * @version $Id: Constructor.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class Constructor extends Variable {

	/**
	 * Constructor for Constructor.
	 * @param name
	 */
	public Constructor(String name) {
		super(name);
	}

	/**
	 *Constructs a Constructor with given type
	 * 
	 * @param name name of constructor
	 * @param type type of constructed objects
	 */
	public Constructor(String name, Type type) {
		super(name, type);
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Constructor))
			return false;
		Constructor v = (Constructor) obj;
		return v.getName().equals(getName());
	}

}
