/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.types;

public class Visitors {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static class IdentityTypeVisitor implements TypeVisitor<Type> {
        @Override
        public Type visit(TypeVariable t) {
            return t;
        }

        @Override
        public Type visit(PrimitiveType primitiveType) {
            return primitiveType;
        }

        @Override
        public Type visit(TypeApplication typeApplication) {
            return typeApplication;
        }

        @Override
        public Type visit(TypeConstructor typeConstructor) {
            return typeConstructor;
        }

        @Override
        public Type visit(ConstrainedType constrainedType) {
            return constrainedType;
        }
    }

    public static class ConstantTypeVisitor<T> implements TypeVisitor<T> {
        private final T constant;

        public ConstantTypeVisitor(T constant) {
            this.constant = constant;
        }

        @Override
        public T visit(TypeVariable t) {
            return constant;
        }

        @Override
        public T visit(PrimitiveType primitiveType) {
            return constant;
        }

        @Override
        public T visit(TypeApplication typeApplication) {
            return constant;
        }

        @Override
        public T visit(TypeConstructor typeConstructor) {
            return constant;
        }

        @Override
        public T visit(ConstrainedType constrainedType) {
            return constant;
        }
    }
}
