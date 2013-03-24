package fr.lifl.jaskell.compiler.bytecode;

import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.TypeApplication;
import fr.lifl.jaskell.compiler.types.TypeConstructor;
import fr.lifl.jaskell.compiler.types.TypeVariable;
import fr.lifl.jaskell.compiler.types.TypeVisitor;
import fr.norsys.klass.bytes.TypeHelper;

/**
 * @author bailly
 * @version $Id: Type2String.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Type2String implements TypeVisitor {

	/* store the set of constraints against which variables may be resolved */
	 // private Set context;

	/**
	 * Constructor for Type2String.
	 */
	public Type2String(/*Set context*/) {
		// this.context = context;
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeVariable)
	 */
	public Object visit(TypeVariable t) {
//		/* try to find constraint for variable t */
//		Iterator it = context.iterator();
//		/* a list to store matching constraints */
//		List clss = new ArrayList();
//		while (it.hasNext()) {
//			TypeConstraint tc = (TypeConstraint) it.next();
//			TypeClass cls = tc.typeClass;
//			Type ta = tc.typeExpression;
//			if (t.equals(ta)
//				|| (ta instanceof TypeApplication
//					&& ((TypeApplication) ta).getConstructor().equals(t)))
//				clss.add(cls);
//		}
//		if (!clss.isEmpty())
//			/* TODO - try to find a suitable constraint */
//			return "L"+BytecodeGenerator.encodeName2Java(((TypeClass) clss.get(0)).getName())+";";
//		else
			return "Ljaskell/runtime/types/JObject;";
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(PrimitiveType)
	 */
	public Object visit(PrimitiveType primitiveType) {
		return TypeHelper.getInternalName(primitiveType.getJavaClass());
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeApplication)
	 */
	public Object visit(TypeApplication typeApplication) {
		return typeApplication.getConstructor().visit(this);
	}

	/**
	 * @see jaskell.compiler.types.TypeVisitor#visit(TypeConstructor)
	 */
	public Object visit(TypeConstructor typeConstructor) {
		return "L" +BytecodeGenerator.encodeName2Java(typeConstructor.getName())+";";
	}

}
