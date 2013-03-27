package fr.lifl.jaskell.compiler.types;

import com.google.common.collect.Sets;

import java.util.Set;

public class TypeConstraintsBuilder {
    
    final Set<TypeConstraint> constraints = Sets.newHashSet();

    public TypeConstraintsBuilder collectConstraints(Type type) {
        type.visit(new Visitors.ConstantTypeVisitor<Set<TypeConstraint>>(constraints) {

            @Override
            public Set<TypeConstraint> visit(TypeApplication typeApplication) {
                typeApplication.getRange().visit(this);
                typeApplication.getDomain().visit(this);
                return constraints;
            }

            @Override
            public Set<TypeConstraint> visit(ConstrainedType constrainedType) {
                constraints.add(constrainedType.getTypeConstraint());
                return constraints;
            }
        });
        
        return this;
    }

    public boolean isEmpty() {
        return constraints.isEmpty();
    }

    public TypeConstraint build() {
        if(constraints.size() == 1) {
            return constraints.iterator().next();
        }

        return new TypeConstraints(flatten(constraints));
    }

    private Set<TypeConstraint> flatten(Set<TypeConstraint> constraints) {
        Set<TypeConstraint> flattened = Sets.newHashSet();
        for (TypeConstraint constraint : constraints) {
            constraint.collectTo(flattened);
        }
        return flattened;
    }
}
