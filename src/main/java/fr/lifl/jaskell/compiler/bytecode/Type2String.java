/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.bytecode;

import fr.lifl.jaskell.compiler.types.*;

import oqube.bytes.TypeHelper;


/**
 * @author  bailly
 * @version $Id: Type2String.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Type2String implements TypeVisitor {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* store the set of constraints against which variables may be resolved */
    // private Set context;

    /**
     * Constructor for Type2String.
     */
    public Type2String( /*Set context*/) {
        // this.context = context;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Object visit(TypeVariable t) {
//              /* try to find constraint for variable t */
//              Iterator it = context.iterator();
//              /* a list to store matching constraints */
//              List clss = new ArrayList();
//              while (it.hasNext()) {
//                      TypeConstraint tc = (TypeConstraint) it.next();
//                      TypeClass cls = tc.typeClass;
//                      Type ta = tc.typeExpression;
//                      if (t.equals(ta)
//                              || (ta instanceof TypeApplication
//                                      && ((TypeApplication) ta).getConstructor().equals(t)))
//                              clss.add(cls);
//              }
//              if (!clss.isEmpty())
//                      /* TODO - try to find a suitable constraint */
//                      return "L"+BytecodeGenerator.encodeName2Java(((TypeClass) clss.get(0)).getName())+";";
//              else
        return "Ljaskell/runtime/types/JObject;";
    }

    public Object visit(PrimitiveType primitiveType) {
        return TypeHelper.getInternalName(primitiveType.getJavaClass());
    }

    public Object visit(TypeApplication typeApplication) {
        return typeApplication.getConstructor().visit(this);
    }

    public Object visit(TypeConstructor typeConstructor) {
        return "L" + BytecodeGenerator.encodeName2Java(typeConstructor.getName()) + ";";
    }

    @Override
    public Object visit(ConstrainedType constrainedType) {
        return "Ljaskell/runtime/types/JObject;";
    }

}
