package fr.lifl.jaskell.compiler.datatypes;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.Primitives;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author nono
 *
 */
public class PrimitiveConstructor
	extends ConstructorDefinition
	implements Primitives {

	private Type dataType;

	/* class name of instances  of this constructor */
	private Class klass;
	
	/**
	 * Constructor for PrimitiveConstructor.
	 * @param name
	 * @param type
	 */
	public PrimitiveConstructor(String name, DataDefinition ddef, Type[] args,Class klass,Module module) {
		super(name, null, module, args);
		this.dataType = ddef.getType();
		this.klass = klass;
		setParent(ddef);
	}

	/**
	 * Constructor for PrimitiveConstructor with strictness information
	 * @param name
	 * @param type
	 */
	public PrimitiveConstructor(String name, DataDefinition ddef, Type[] args,Class klass,Module module,int[]strictArgs) {
		super(name, null, module, args,strictArgs);
		this.dataType = ddef.getType();
		this.klass = klass;
		setParent(ddef);
	}


	/**
	 * @see jaskell.compiler.core.Expression#lookup(String)
	 */
	public Expression lookup(String vname) {
		return null;
	}

	/**
	 * @see jaskell.compiler.core.ConstructorDefinition#getDataType()
	 */
	public Type getDataType() {
		return dataType;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Expression#visit(jaskell.compiler.JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	public Class getJavaClass() {
		return klass;
	}
}
