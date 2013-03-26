/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
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
