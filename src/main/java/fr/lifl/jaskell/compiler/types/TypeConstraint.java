package fr.lifl.jaskell.compiler.types;

import java.util.Set;

public interface TypeConstraint {
    boolean containsVariable(TypeVariable variableType);

    void collectTo(Set<TypeConstraint> constraints);
}
