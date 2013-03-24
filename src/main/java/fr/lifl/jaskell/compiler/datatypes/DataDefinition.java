package fr.lifl.jaskell.compiler.datatypes;

import fr.lifl.jaskell.compiler.CompilerException;
import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.types.Kind;
import fr.lifl.jaskell.compiler.types.SimpleKind;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.TypeApplication;
import fr.lifl.jaskell.compiler.types.TypeConstructor;
import fr.lifl.jaskell.compiler.types.TypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bailly
 * @version $Id: DataDefinition.java 1154 2005-11-24 21:43:37Z nono $
 */
public class DataDefinition extends Definition {

	/* list of constructors for data type */
	private List constructors;

	/* list of type paremeters */
	private List parameters;

	/* type constructor expression */
	private Type type;

	/* kind of this constructor */
	private Kind kind;

	/**
	 * Constructor for DataDefinition.
	 */
	public DataDefinition(String name, Module mod) {
		this(name, TypeFactory.makeTycon(name), mod);
	}

	/**
	 * Constructs a data definition with a simple type.
	 * The given type argument must be application of a type constructor
	 * to zero or more type variables
	 * 
	 * @param t a Type 
	 * @param mod Module where this definition is stored
	 */
	public DataDefinition(String name, Type t, Module mod) {
		super(name, null, null, mod);
		if (t instanceof TypeApplication) {
			TypeConstructor tc =
				(TypeConstructor) ((TypeApplication) t).getConstructor();
			/* construct kind */
			this.kind = tc.getKind();
		} else if (t instanceof TypeConstructor) {
			this.kind = SimpleKind.K;
		} else
			throw new CompilerException(
				"Invalid type argument for data definition :" + t.makeString() +" type = "+t.getClass());
		this.type = t;
		this.constructors = new ArrayList();
		this.parameters = new ArrayList();
		mod.addTypeDefinition(this);
		//mod.bind(name, this);
	}

	/**
	 * Adds a new constructor for this data type
	 * 
	 * @param cdef a Constructor definition object
	 */
	public void addConstructor(ConstructorDefinition cdef) {
		constructors.add(cdef);
		cdef.setParent(this);
	}

	/**
	 * Return the set of constructors for this data type
	 * 
	 * @return a - possibly empty - list 
	 */
	public List getConstructors() {
		return constructors;
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see jaskell.compiler.core.Expression#getType()
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return type.makeString();
	}



	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof DataDefinition))
			return false;
		return getName().equals(((DataDefinition) obj).getName());
	}


}
