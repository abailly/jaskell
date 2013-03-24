package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.bytecode.PrimitivesCodeGenerator;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author Arnaud Bailly
 * @version $Id: PrimitiveFunction.java 1154 2005-11-24 21:43:37Z nono $
 *
 */
public class PrimitiveFunction extends Abstraction implements Primitives {

	/** name of this primitive function */
	private String name;

	/** number of arguments */
	private int args;
	
	/** class implementing this function */
	private Class klass;

	/**
	 * Constructs a PrimitiveFunction with given name, module and type
	 */
	public PrimitiveFunction(String name, Module module, Type type,Class cls) {
		setParent(module);
		this.name = name;
		setType(type);
		this.args = PrimitiveType.getArgsCount(type);
		this.klass = cls;
		/* register function with bytecode generator if cls not null */
		if(cls !=null)
			PrimitivesCodeGenerator.registerStaticPrimitive(this);
		/* bind definition in module */
		module.bind(name,this);
	}

	/**
	 * @see jaskell.compiler.core.Binding#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see jaskell.compiler.core.Binding#isStrict()
	 */
	public boolean isStrict() {
		return true;
	}

	/**
	 * @see jaskell.compiler.core.Abstraction#getBody()
	 */
	public Expression getBody() {
		return null;
	}

	/**
	 * @see jaskell.compiler.core.Abstraction#setBody(Expression)
	 */
	public void setBody(Expression body) {
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see jaskell.compiler.core.Abstraction#getCount()
	 */
	public int getCount() {
		return args;
	}
	
	/**
	 * All arguments are strict in a primitive function
	 * 
	 * @see jaskell.compiler.core.Abstraction#isStrict(int)
	 */
	public boolean isStrict(int i) {
		if(i < args) 
			return true;
		else 
			return false;
	}

	/**
	 * @see jaskell.compiler.core.Binding#getDefinition()
	 */
	public Expression getDefinition() {
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "native: "+name;
	}

	/**
	 * @return
	 */
	public Class getKlass() {
		return klass;
	}

	/**
	 * @param class1
	 */
	public void setKlass(Class class1) {
		klass = class1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

}