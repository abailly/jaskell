package fr.lifl.jaskell.compiler.types;

import static java.lang.String.format;

public class ConstrainedType extends Type {

    private final Type baseType;
    private final TypeConstraint typeConstraint;

    public ConstrainedType(Type baseType, TypeConstraint typeConstraint) {
        this.baseType = baseType;
        this.typeConstraint = typeConstraint;
    }

    @Override
    public <T> T visit(TypeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean contains(TypeVariable variableType) {
        return baseType.contains(variableType) || typeConstraint.containsVariable(variableType);
    }

    @Override
    public void setKind(Kind kind) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public TypeConstraint getTypeConstraint() {
        return typeConstraint;
    }

    public Type getBaseType() {
        return baseType;
    }

    @Override
    public String toString() {
        return format("(%s) => %s", typeConstraint, baseType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstrainedType that = (ConstrainedType) o;

        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (typeConstraint != null ? !typeConstraint.equals(that.typeConstraint) : that.typeConstraint != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = baseType != null ? baseType.hashCode() : 0;
        result = 31 * result + (typeConstraint != null ? typeConstraint.hashCode() : 0);
        return result;
    }
}
