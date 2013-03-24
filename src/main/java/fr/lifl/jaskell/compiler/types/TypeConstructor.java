package fr.lifl.jaskell.compiler.types;


/**
 * A class representing type constructors, both builtin and user defined.
 * <p>
 * This class represents type constructors which are used to construct types.
 * A type constructor is itself a type. Instances of this class include builtin
 * type constructors like (->) for function types, [] for list types, () for 
 * unit type, (,),(,,),(,,,)... for tuple types.<p>
 * It also includes user defined type constructors or simple primitive types
 * like Int or Char.
 * 
 * @author bailly
 * @version $Id: TypeConstructor.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeConstructor extends Type {

	/* name of this constructor */
	private String name;

	/* kind of this constructor */
	private Kind kind;

	/**
	 * Constructor for TypeConstructor.
	 * 
	 * @param name the name of this type constructor. Usually starts
	 * with upper case for user defined data 
	 */
	TypeConstructor(String name) {
		this(name, null);
	}

	TypeConstructor(String name, Kind kind) {
		this.name = name;
		this.kind = kind;
	}

	/**
	 * @see jaskell.compiler.types.Type#contains(TypeVariable)
	*/
	public boolean contains(TypeVariable variableType) {
		return false;
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
	 * @param k a Kind
	 */
	public void setKind(Kind k) {
		this.kind = k;
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
		if (obj == null || !(obj instanceof TypeConstructor))
			return false;
		TypeConstructor at = (TypeConstructor) obj;
		return at.name.equals(name);
	}

	/**
	 * @see jaskell.compiler.types.Type#visit(TypeVisitor)
	 */
	public Object visit(TypeVisitor v) {
		return v.visit(this);
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.types.Type#compare(jaskell.compiler.types.Type)
	 */
	public int compare(Type other) throws UncomparableException {
		if(this.equals(other))
			return 0;
		else if(!(other instanceof TypeConstructor))
			return -other.compare(this);
		else
			throw new UncomparableException("Type constructors are uncomparable : "+this+" <> "+other);
	}

}
