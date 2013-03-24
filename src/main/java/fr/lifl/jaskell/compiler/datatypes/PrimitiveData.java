package fr.lifl.jaskell.compiler.datatypes;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: PrimitiveData.java 1154 2005-11-24 21:43:37Z nono $
 */
public class PrimitiveData extends DataDefinition {

	/** java class for this type */
	private Class klass;

	/**
	 * Constructor for PrimitiveData.
	 * @param name
	 * @param mod
	 */
	public PrimitiveData(String name, Type type,Class cls,Module module) {
		super(name, type,module);
		this.klass = cls;
	}

	/**
	 * Returns the klass.
	 * @return Class
	 */
	public Class getKlass() {
		return klass;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Expression#visit(jaskell.compiler.JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

}
