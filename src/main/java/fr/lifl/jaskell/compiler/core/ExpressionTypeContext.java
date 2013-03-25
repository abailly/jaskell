/*
 * Created on 11 f�vr. 2004
 * 
 * $Log: ExpressionTypeContext.java,v $
 * Revision 1.3  2004/02/19 14:55:01  nono
 * Integrated jaskell to FIDL
 * Added rules in grammar to handle messages
 *
 * Revision 1.2  2004/02/18 17:20:07  nono
 * suppressed type classes use and definitions
 *
 * Revision 1.1  2004/02/11 16:46:51  nono
 * Repair ClassMethod missing
 *
 */
package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.TypeApplication;
import fr.lifl.jaskell.compiler.types.TypeConstructor;
import fr.lifl.jaskell.compiler.types.TypeContext;

/**
 * @author bailly
 * @version $Id: ExpressionTypeContext.java 1154 2005-11-24 21:43:37Z nono $
 */
public class ExpressionTypeContext implements TypeContext {

	private Expression expr;

	/**
	 * @param base
	 */
	public ExpressionTypeContext(Expression base) {
		this.expr = base;
	}


	/* (non-Javadoc)
	 * @see jaskell.compiler.types.TypeContext#resolveType(jaskell.compiler.types.Type)
	 */
	public Definition resolveType(Type t) {
		Expression par = null;
		Module mod = null;
		/* lookup enclosing namespace */
		for (par = expr.getParent();
			!(par instanceof Module) && (par != null);
			par = par.getParent());
		if (par == null)
			mod = Module.PRELUDE; /* should lookup all toplevel modules */
		else
			mod = (Module) par;
		if (t instanceof TypeApplication)
			return mod.resolveType(
				((TypeConstructor) t.getConstructor())
					.getName());
		else if (t instanceof TypeConstructor)
			return mod.resolveType(((TypeConstructor) t).getName());
		return null;
	}

}
