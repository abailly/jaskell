package fr.lifl.jaskell.compiler.types;


/**
 * A class representing type variables in polymorphic types
 * 
* This class represents polymorphic types. It can be subclassed 
* by classes constructed at compile time to represent constrained type 
* variables.
* 
 * @author bailly
 * @version $Id: TypeVariable.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class TypeVariable extends Type {


	/** name assigned to this type variable */
	private String name;

	/* the kind of this type variable */
	private Kind kind = null;

	/**
	* Constructor for TypeVariable.
	*/
	TypeVariable(String name) {
		this.name = name;
	}

	/**
	 * @see jaskell.compiler.core.Type#visit(TypeVisitor)
	 */
	public Object visit(TypeVisitor v) {
		return v.visit(this);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * Method getName.
	 * @return Object
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see jaskell.compiler.types.Type#contains(TypeVariable)
	 */
	public boolean contains(TypeVariable variableType) {
		return this.equals(variableType);
	}

	/**
	 * @see jaskell.compiler.types.Type#getKind()
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * Sets the kind of this type variable
	 * 
	 * If the given kind is different from <code>null</code>, we check that
	 * variable as not already be assigned a kind which would be an error. 
	 * 
	 * @param k a Kind
	 */
	public void setKind(Kind k) {
			this.kind = k;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof TypeVariable))
			return false;
		TypeVariable tv = (TypeVariable) obj;
		return tv.name.equals(name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode();
	}

	/* 
	 * Any type variable is a super type of another type
	 * @see jaskell.compiler.types.Type#compare(jaskell.compiler.types.Type)
	 */
	public int compare(Type other) throws UncomparableException {
			return 0;
	}

}
