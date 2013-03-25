package fr.lifl.jaskell.compiler.types;

public class BaseTypeVisitor<T> implements TypeVisitor<T> {
    @Override
    public T visit(TypeVariable t) {
        return null;
    }

    @Override
    public T visit(PrimitiveType primitiveType) {
        return null;
    }

    @Override
    public T visit(TypeApplication typeApplication) {
        return null;
    }

    @Override
    public T visit(TypeConstructor typeConstructor) {
        return null;
    }

    @Override
    public T visit(ConstrainedType constrainedType) {
        return null;
    }
}
