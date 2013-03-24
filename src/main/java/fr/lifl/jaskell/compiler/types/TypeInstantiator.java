package fr.lifl.jaskell.compiler.types;

import java.util.HashMap;
import java.util.Map;

/**
 * A visitor implementation for instantiating type variables occurence
 * into a type.
 * 
 * This class works by replacing every occurence of a variable into a given type
 * by a fresh variable or the content of a map. The constraints occuring into the type definition are 
 * also substituted and checked.
 * 
 * @author bailly
 * @version $Id: TypeInstantiator.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeInstantiator implements TypeVisitor {

	/* the base type we work with */
	private Type base;

	/* the substitution we create */
	private Map map = new HashMap();

	/**
	* Constructor TypeInstantiator.
	* @param type
	* 	*/
	public TypeInstantiator(Type type) {
		this.base = type;
	}

	/**
	 * @see jaskell.compiler.TypeVisitor#visit(PrimitiveType)
	 */
	public Object visit(PrimitiveType primitiveType) {
		return primitiveType;
	}

	/**
	 * @see jaskell.compiler.TypeVisitor#visit(TypeVariable)
	 */
	public Object visit(TypeVariable t) {
		Type tv = (Type) map.get(t);
		if (tv == null) {
			tv = TypeFactory.freshBinding();
			map.put(t, tv);
		}
		return tv;
	}

	/**
	 * Method instance.
	 * @return Type
	 */
	public Type instance() {
		Type ret = (Type) base.visit(this);
		return ret;
	}

	/**
	 * Returns the base.
	 * @return Type
	 */
	public Type getBase() {
		return base;
	}

	/**
	 * Returns the mapping created by this instantiator.
	 * 
	 * The substitution is a map from type variables to type variables.
	 * 
	 * @return Map
	 */
	public Map getMap() {
		return map;
	}

	public void reset() {
		map.clear();
	}

	/**
	 * Sets the base.
	 * @param base The base to set
	 */
	public void setBase(Type base) {
		this.base = base;
	}

	/**
	 * @see jaskell.compiler.TypeVisitor#visit(TypeApplication)
	 */
	public Object visit(TypeApplication typeApplication) {
		Type ta = TypeFactory.makeApplication(
			(Type) typeApplication.getDomain().visit(this),
			(Type) typeApplication.getRange().visit(this));
		return ta;
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeConstructor)
	 */
	public Object visit(TypeConstructor typeConstructor) {
		return typeConstructor;
	}

	/**
	 * Sets the map.
	 * @param map The map to set
	 */
	public void setMap(Map map) {
		this.map = map;
	}


}
