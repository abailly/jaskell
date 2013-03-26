package fr.lifl.jaskell.compiler.types;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class TypeConstraints implements TypeConstraint {

    private final List<TypeConstraint> constraints;

    public TypeConstraints(TypeConstraint constraint, TypeConstraint... constraints) {
        this.constraints = newArrayList();
        this.constraints.add(constraint);
        Collections.addAll(this.constraints, constraints);
    }

    public TypeConstraints(List<TypeConstraint> constraints) {
        this.constraints = Lists.newArrayList(constraints);
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
    public void collectTo(List<TypeConstraint> constraints) {
        for (TypeConstraint constraint : this.constraints) {
            constraint.collectTo(constraints);
        }
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
