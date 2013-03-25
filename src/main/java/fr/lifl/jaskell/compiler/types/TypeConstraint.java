package fr.lifl.jaskell.compiler.types;

public abstract class TypeConstraint {
    public abstract boolean containsVariable(TypeVariable variableType);
}
