package fr.lifl.jaskell.compiler.types;

import java.util.List;

public interface TypeConstraint {
    boolean containsVariable(TypeVariable variableType);

    void collectTo(List<TypeConstraint> constraints);
}
