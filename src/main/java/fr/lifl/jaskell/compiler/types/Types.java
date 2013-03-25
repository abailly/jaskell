package fr.lifl.jaskell.compiler.types;

public class Types {

    public static Type apply(Type domain, Type range) {
        TypeConstraint constraint = collectConstraint(domain);
        if (constraint != null) {
            return new ConstrainedType(new TypeApplication(ripOffConstraint(domain), range), constraint);
        } else {
            return new TypeApplication(domain, range);
        }
    }

    private static Type ripOffConstraint(Type domain) {
        TypeVisitor<Type> constraintRipper = new BaseTypeVisitor<Type>() {
            @Override
            public Type visit(TypeVariable t) {
                return t;
            }

            @Override
            public Type visit(PrimitiveType primitiveType) {
                return primitiveType;
            }

            @Override
            public Type visit(TypeConstructor typeConstructor) {
                return typeConstructor;
            }

            @Override
            public Type visit(TypeApplication typeApplication) {
                return new TypeApplication(typeApplication.getDomain().visit(this),typeApplication.getRange().visit(this));    
                
            }

            @Override
            public Type visit(ConstrainedType constrainedType) {
                return constrainedType.getBaseType();
            }
        };

        return domain.visit(constraintRipper);
    }

    private static TypeConstraint collectConstraint(Type domain) {
        TypeVisitor<TypeConstraint> constraintCollector = new BaseTypeVisitor<TypeConstraint>() {

            @Override
            public TypeConstraint visit(TypeApplication typeApplication) {
                return typeApplication.getRange().visit(this);
            }

            @Override
            public TypeConstraint visit(ConstrainedType constrainedType) {
                return constrainedType.getTypeConstraint();
            }
        };
        
        return domain.visit(constraintCollector);
    }

    public static TypeVariable var(String variableName) {
        return new TypeVariable(variableName);
    }
}
