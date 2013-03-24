package fr.lifl.jaskell.compiler.types;

import java.util.HashMap;
import java.util.Map;


class DefaultTypeApplicationFormat implements TypeApplicationFormat {

	/**
	 * @see jaskell.compiler.types.TypeApplicationFormat#formatApply(Type, Type)
	 */
	public String formatApply(Type dom, Type range) {
		return dom + " " + range;
	}

}

/**
 * Base class for all types defined in FIDL. This class is subclassed by 
 * builtin types and user-defined types
 *
 * @author Arnaud Bailly
 * @version $Id: Type.java 1153 2005-11-24 20:47:55Z nono $
 */
public abstract class Type {

	private static final DefaultTypeApplicationFormat def =
		new DefaultTypeApplicationFormat();

	/* map from type constructors to type comparators */
	private static Map comparators = new HashMap();
	
	private TypeApplicationFormat applyFormatter = def;

	/*
	 * The expression this type is attached to 
	 */
	private TypeContext context;

	////////////////////////////////////////////////////:
	// CONSTRUCTORS
	////////////////////////////////////////////////////

	/**
	 * Default constructor
	 * Constructs a Definition with TypeDefType as type
	 */
	protected Type() {
		applyFormatter = def;
	}

	////////////////////////////////////////////////////:
	// PUBLIC METHODS
	/////////////////////////////////////////////////////

	/**
	 * Visit this type object with given TypeVisitor
	 * 
	 * @param v a TypeVisitor implementation
	 * @return a visitor dependant object
	 */
	public abstract Object visit(TypeVisitor v);

	/**
	 * Method contains.
	 * @param variableType
	 * @return boolean
	 */
	public abstract boolean contains(TypeVariable variableType);

	/**
	 * Returns the kind for this type
	 * 
	 * Type returns by default SimpleKind.K
	 * 
	 * @return a Kind object
	 * */
	public Kind getKind() {
		return SimpleKind.K;
	}

	/**
	 * Returns the applyFormatter.
	 * @return TypeApplicationFormat
	 */
	public TypeApplicationFormat getApplyFormatter() {
		return applyFormatter;
	}

	/**
	 * Sets the applyFormatter.
	 * @param applyFormatter The applyFormatter to set
	 */
	public void setApplyFormatter(TypeApplicationFormat applyFormatter) {
		this.applyFormatter = applyFormatter;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String makeString() {
		StringBuffer sb = new StringBuffer("");
		/* call formatter for this type */
		sb.append(toString());
		return sb.toString();
	}

	/**
	* This method returns the root constructor for this 
	* type application 
	* 
	* This method is type analog to getFunction() method in Applicaiton.
	* It travels down the graph to find the TypeConstructor. This is the rightmost
	* type in the spine
	* 
	* @return a Type
	*/
	public Type getConstructor() {
		return this;
	}

	/**
	 * This method returns the context in which this type is used. This allows 
	 * specialized code to use context when necessary - e.g. to perform a lookup for a name
	 * 
	 * @return the context in which this type is used
	 */
	public TypeContext getContext() {
		return context;
	}

	/**
	 * @param expression
	 */
	public void setContext(TypeContext expression) {
		context = expression;
	}

	/**
	 * @param kind
	 */
	public abstract void setKind(Kind kind);

	/**
	 * Compares this type with type <code>other</code> and returns an integer value
	 * denoting this type'ss relationship to other w.r.t subtyping relation.
	 * <p>
	 * This method should be overloaded by concrete subtypes.
	 * 
	 * @param other the other part of subtyping
	 * @return 0 if types are equals, -1 if <code>this</code> is subtype of <code>other</code>,
	 * +1 if   <code>other</code> is subtype of <code>this</code>
	 * @exception UncomparableException if types cannot be compared
		 */
	public int compare(Type other) throws UncomparableException {
		throw new UncomparableException("Cannot compare bare types");
	}

	/**
	 * Retrieve the TypeComparator associated with given type. 
	 * The given type must be a TypeConstructor or a TypeApplication ferom
	 * which a TypeConstructor is extracted.
	 * 
	 * @param type a TypeConstructor or TypeApplication
	 * @exception ClassCastException if type is not one of these
	 */
	public static TypeComparator getComparatorFor(Type type) {
		TypeConstructor tycon = null;
		try {
			tycon = (TypeConstructor)type;
		}	catch(ClassCastException ccex) {
			tycon = (TypeConstructor)((TypeApplication)type).getConstructor();
		}
		return (TypeComparator)comparators.get(tycon);
	}

	/**
	 * sets the TypeComparator object used for a given type constructor.
	 * 
	 * @param tycon 
	 * @param tycompar
	 */
	public static void setComparatorFor(TypeConstructor tycon, TypeComparator tycompar){
		comparators.put(tycon,tycompar);
	}
}
