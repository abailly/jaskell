package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * A class representing variable references.
 * 
 * This class represents occurences of variable references. The
 * name of the variable may be any valid identifier, either qualified or
 * unqualified.
 * 
 * @author bailly
 * @version $Id: Variable.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class Variable extends ExpressionBase {

	
	/** name of this variable */
	private String name;
			
	/**
	 * Constructor for Variable.
	 * @param name
	 * @param type
	 */
	public Variable(String name) {
		this.name = name;
	}
	
	/**
	 *Constructs a variable with given type
	 * 
	 * @param name name of variable
	 * @param type type of constructed objects
	 */
	public Variable(String name, Type type) {
		this.name = name;
		this.type = type;
	}


	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof Variable))
		return false;
		Variable v = (Variable)obj;
		return v.getName().equals(name);
	}

	public Object clone() {
		Variable var = new Variable(name,getType());
		var.setParent(parent);
		return var;
	}
}
