package fr.lifl.jaskell.compiler.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bailly
 * @version $Id: TypeSubstitution.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeSubstitution implements TypeVisitor {

	private Map map;

	private boolean incontext = false;

	/**
	 * Constructor for TypeSubstitution.
	 * This constructs builds a default - empty - Type substitution
	 * 
	 */
	public TypeSubstitution() {
		this.map = new HashMap();
	}

	/**
	 * Constructor for TypeSubstitution.
	 * This constructs builds a Type Substitution backed by given
	 * Map
	 * 
	 */
	public TypeSubstitution(Map map) {
		this.map = map;
	}

	/**
	 * Applies m as a substitution for type variables
	 * occuring in t
	 * 
	 * @param t the Type to substitute
	 * @param map the map to apply
	 * @return a new Type whose all occurences of mapping in m 
	 * have been replaced
	 */
	public Type substitute(Type t) {
		Type ret = (Type) t.visit(this);
		return ret;
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeVariable)
	 */
	public Object visit(TypeVariable t) {
		Type m = (Type) map.get(t);
		if (m != null) {
			return m.visit(this);
		}
		return t;
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(PrimitiveType)
	 */
	public Object visit(PrimitiveType primitiveType) {
		return primitiveType;
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeApplication)
	 */
	public Object visit(TypeApplication typeApplication) {
		Type t = TypeFactory.makeApplication(
			(Type) typeApplication.getDomain().visit(this),
			(Type) typeApplication.getRange().visit(this));
		return t;
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeConstructor)
	 */
	public Object visit(TypeConstructor typeConstructor) {
		return typeConstructor;
	}


}
