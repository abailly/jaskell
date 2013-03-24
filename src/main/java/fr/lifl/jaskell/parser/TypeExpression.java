package fr.lifl.jaskell.parser;

import fr.lifl.jaskell.compiler.core.ExpressionBase;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: TypeExpression.java 1154 2005-11-24 21:43:37Z nono $
 */
public class TypeExpression extends ExpressionBase {

	private boolean strict;
	
	/**
	 * Constructor for TypeDefinition.
	 */
	public TypeExpression(Type type) {
		setType(type);
	}


	/**
	 * @return
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * @param b
	 */
	public void setStrict(boolean b) {
		strict = b;
	}

}
