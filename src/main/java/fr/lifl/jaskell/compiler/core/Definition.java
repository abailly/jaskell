package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * A class for named definitions of objects in a module
 * 
 * This class stores all data pertaining to a definition in a module :
 * the name of the definition, its type, and the expression it references. 
 * 
 * @author bailly
 * @version $Id: Definition.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class Definition extends ExpressionBase implements Binding {

	/** name of variable */
	private String name;
	
 	/** definition of this object */
	private Expression definition;

	/* module where this definition is located */
	private Module module;
	
	/**
	 * Constructor for Definition.
	 */
	public Definition(String name, Type type, Expression expr, Module module) {
		this.name = module.getName() + "." + name;
		this.definition = expr;
		this.module = module;
		setType(type);
		setParent(module);
	}

	public Definition() {
	}
	
	/**
	 * Returns the definition.
	 * @return Expression
	 */
	public Expression getDefinition() {
		return definition;
	}

	/**
	 * Returns the module.
	 * @return Module
	 */
	public Module getModule() {
		return module;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the definition.
	 * @param definition The definition to set
	 */
	public void setDefinition(Expression definition) {
		this.definition = definition;
	}

	/**
	 * Sets the module.
	 * @param module The module to set
	 */
	public void setModule(Module module) {
		this.module = module;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}
	/**
	 * @see jaskell.compiler.core.Binding#isStrict()
	 */
	public boolean isStrict() {
		return false;
	}

	/**
	 * @see jaskell.compiler.core.Expression#lookup(String)
	 */
	public Expression lookup(String vname) {
		return parent.lookup(vname);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name +"::"+getType();
	}

}
