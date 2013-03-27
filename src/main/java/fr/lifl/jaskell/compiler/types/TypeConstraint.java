package fr.lifl.jaskell.compiler.types;

import java.util.Map;
import java.util.Set;

public interface TypeConstraint {
    boolean containsVariable(TypeVariable variableType);

    void collectTo(Set<TypeConstraint> constraints);

    /**
     * Applies a mapping to this constraint replacing all occurrences of mapped type by their image.
     * 
     *
     * @param map a substitution from Types to Types
     * @return an updated TypeConstraint
     */
    TypeConstraint substitute(TypeSubstitution map);
}
