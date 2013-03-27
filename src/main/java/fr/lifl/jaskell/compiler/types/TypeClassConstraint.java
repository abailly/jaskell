package fr.lifl.jaskell.compiler.types;

import java.util.Set;

public class TypeClassConstraint implements TypeConstraint {
    private final TypeClass typeClass;
    private final Type type;

    public TypeClassConstraint(TypeClass typeClass, Type type) {
        this.typeClass = typeClass;
        this.type = type;
    }

    @Override
    public boolean containsVariable(TypeVariable variableType) {
        return type.equals(variableType);
    }

    @Override
    public void collectTo(Set<TypeConstraint> constraints) {
        constraints.add(this);
    }

    @Override
    public TypeConstraint substitute(TypeSubstitution map) {
        return new TypeClassConstraint(typeClass,map.substitute(type));
    }

    @Override
    public String toString() {
        return typeClass + " " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeClassConstraint that = (TypeClassConstraint) o;

        return typeClass.equals(that.typeClass) && type.equals(that.type);

    }

    @Override
    public int hashCode() {
        int result = typeClass.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
