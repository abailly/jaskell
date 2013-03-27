package fr.lifl.jaskell.compiler.types;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

public class TypeConstraints implements TypeConstraint {

    private final Set<TypeConstraint> constraints;

    public TypeConstraints(TypeConstraint constraint, TypeConstraint... constraints) {
        this.constraints = newHashSet();
        this.constraints.add(constraint);
        Collections.addAll(this.constraints, constraints);
    }

    public TypeConstraints(Set<TypeConstraint> constraints) {
        this.constraints = newHashSet(constraints);
    }

    @Override
    public boolean containsVariable(TypeVariable variableType) {
        boolean containsVariable = false;
        for (TypeConstraint constraint : constraints) {
            containsVariable |= constraint.containsVariable(variableType);
        }
        return containsVariable;
    }

    @Override
    public void collectTo(Set<TypeConstraint> constraints) {
        for (TypeConstraint constraint : this.constraints) {
            constraint.collectTo(constraints);
        }
    }

    @Override
    public TypeConstraint substitute(TypeSubstitution map) {
        return new TypeConstraints(newHashSet(transform(constraints, substituteTypes(map))));
    }

    private Function<TypeConstraint, TypeConstraint> substituteTypes(final TypeSubstitution map) {
        return new Function<TypeConstraint, TypeConstraint>() {
            @Override
            public TypeConstraint apply(@Nullable TypeConstraint input) {
                return input.substitute(map);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeConstraints that = (TypeConstraints) o;

        return !(constraints != null ? !constraints.equals(that.constraints) : that.constraints != null);

    }

    @Override
    public int hashCode() {
        return constraints != null ? constraints.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TypeConstraint constraint : constraints) {
            sb.append(constraint.toString()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
