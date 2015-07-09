/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.types;

import fr.lifl.jaskell.compiler.core.Primitives;

import java.util.Iterator;
import java.util.List;

public class Types {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static Type apply(Type domain, Type range) {
        TypeConstraintsBuilder typeConstraintsBuilder = new TypeConstraintsBuilder();
        typeConstraintsBuilder.collectConstraints(domain).collectConstraints(range);
        if (!typeConstraintsBuilder.isEmpty()) {
            return new ConstrainedType(new TypeApplication(ripOffConstraint(domain), ripOffConstraint(range)), typeConstraintsBuilder.build());
        }
        return new TypeApplication(domain, range);
    }

    public static TypeVariable var(String variableName) {
        return new TypeVariable(variableName);
    }

    public static TypeClassConstraint typeClass(String className, Type type) {
        return new TypeClassConstraint(new TypeClass(className), type);
    }

    public static Type constraint(Type type, TypeConstraint constraint, TypeClassConstraint... constraints) {
        return new ConstrainedType(type, new TypeConstraintsBuilder().add(constraint).add(constraints).build());
    }

    private static Type ripOffConstraint(Type domain) {
        Visitors.IdentityTypeVisitor constraintRipper = new Visitors.IdentityTypeVisitor() {

            @Override
            public Type visit(TypeApplication typeApplication) {
                return new TypeApplication(typeApplication.getDomain().visit(this), typeApplication.getRange().visit(this));

            }

            @Override
            public Type visit(ConstrainedType constrainedType) {
                return constrainedType.getBaseType();
            }
        };

        return domain.visit(constraintRipper);
    }

    /** 
     * A factory method to properly handle primitive type names
     * 
     * @param name the name of TypeConstructor to build
     * @return a Type
     */
    public static Type makeTycon(String name, Kind kind) {
        Type type = (Type) PrimitiveType.primitives.get(name);
        if (type == null)
            return new TypeConstructor(name, kind);
        else
            return type;
    }

    /** 
	 * A factory method to properly handle primitive type names
	 * 
	 * @param name the name of TypeConstructor to build
	 * @return a Type
	 */
	public static Type makeTycon(String name) {
		return makeTycon(name, null);
	}

    /**
     * Create a new Type application with given type constructor and arguments
     *
     * @param tycon a Type used as TyepConstrcutor
     * @param args  a List of arguments 
     */
    public static Type apply(Type tycon, List args) {
        if (args == null || args.size() == 0)
            throw new TypeError("Cannot construct application with empty arguments");
        Iterator it = args.iterator();
        Type st = apply(tycon, (Type) it.next());
        while (it.hasNext()) {
            st = apply(st, (Type) it.next());
        }
        return st;
    }

    /**
     * A method to create simply function types This factory method is used to create function types from an domain and
     * range types
     *
     * @param domain   the domain of function
     * @param range the range of function
     */
    public static Type fun(Type domain, Type range) {
        return apply(apply(Primitives.FUNCTION, domain), range);
    }

    /**
     * Create a type constructor for given tuple size
     *
     * @param  n the size of the tuple type to create
     *
     * @return a type constructor for this tuple size
     */
    public static TypeConstructor tuple(int n) {
        if (n < 2)
            throw new TypeError("Cannot make tuples with less than two elements");
        StringBuffer sb = new StringBuffer("Prelude.((");
        Kind k = SimpleKind.K;
        for (int i = 0; i < n; i++) {
            k = new FunctionKind(SimpleKind.K, k);
            if (i < (n - 1))
                sb.append(',');
        }
        sb.append("))");
        return (TypeConstructor) makeTycon(sb.toString(), k);
    }

    /**
     * Creates a function type from a list of arguments and a return type
     *
     * @param args a list of type arguments
     * @param ret  type of return
     */
    public static Type fun(List args, Type ret) {
        Iterator it = args.iterator();
        Type t = ret;
        for (int i = args.size() - 1; i >= 0; i--)
            t = fun((Type) args.get(i), t);
        return t;
    }
}
