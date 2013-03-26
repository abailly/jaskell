/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler.types;

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

    public static TypeClassConstraint typeClass(String className, TypeVariable typeVariable) {
        return new TypeClassConstraint(new TypeClass(className), typeVariable);
    }

    public static Type constraint(Type type, TypeConstraint constraint, TypeClassConstraint... constraints) {
        return new ConstrainedType(type, new TypeConstraints(constraint,constraints));
    }

    public static Type constraint(Type type, TypeClassConstraint constraint) {
        return new ConstrainedType(type, constraint);
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
}
