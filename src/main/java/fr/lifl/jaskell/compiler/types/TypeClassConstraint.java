package fr.lifl.jaskell.compiler.types;

public class TypeClassConstraint extends TypeConstraint {
    private final TypeClass typeClass;
    private final TypeVariable typeVariable;

    public TypeClassConstraint(TypeClass typeClass, TypeVariable typeVariable) {
        this.typeClass = typeClass;
        this.typeVariable = typeVariable;
    }

    @Override
    public boolean containsVariable(TypeVariable variableType) {
        return typeVariable.equals(variableType);
    }

    @Override
    public String toString() {
        return typeClass + " " + typeVariable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeClassConstraint that = (TypeClassConstraint) o;

        if (!typeClass.equals(that.typeClass)) return false;
        if (!typeVariable.equals(that.typeVariable)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = typeClass.hashCode();
        result = 31 * result + typeVariable.hashCode();
        return result;
    }
}
