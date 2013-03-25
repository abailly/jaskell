/*
 * Created on 13 f�vr. 2004
 * 
 * $Log: TyvarCollector.java,v $
 * Revision 1.1  2004/02/13 16:53:40  nono
 * 3 fois rien
 *
 */
package fr.lifl.jaskell.compiler.types;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that collects the set of bound variables in a type expression
 * <p>
 * 
 * @author bailly
 * @version $Id: TyvarCollector.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TyvarCollector implements TypeVisitor {

	private final Set emptySet = new HashSet();

	/* (non-Javadoc)
	 * @see jaskell.compiler.types.TypeVisitor#visit(jaskell.compiler.types.TypeVariable)
	 */
	public Object visit(TypeVariable t) {
		HashSet s = new HashSet();
		s.add(t);
		return s;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.types.TypeVisitor#visit(jaskell.compiler.types.PrimitiveType)
	 */
	public Object visit(PrimitiveType primitiveType) {
		return new HashSet();
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.types.TypeVisitor#visit(jaskell.compiler.types.TypeApplication)
	 */
	public Object visit(TypeApplication typeApplication) {
		Set s1 =  (Set)typeApplication.getDomain().visit(this);
		Set s2 =  (Set)typeApplication.getRange().visit(this);
		s1.addAll(s2);
		return s1;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.types.TypeVisitor#visit(jaskell.compiler.types.TypeConstructor)
	 */
	public Object visit(TypeConstructor typeConstructor) {
		return new HashSet();
	}

    @Override
    public Object visit(ConstrainedType constrainedType) {
        return new HashSet();
    }

}
