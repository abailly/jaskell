package fr.lifl.jaskell.compiler.types;

import com.google.common.collect.Lists;

import java.util.List;

public class TypeConstraintsBuilder {
    
    final List<TypeConstraint> constraints = Lists.newArrayList();

    public TypeConstraintsBuilder collectConstraints(Type type) {
        type.visit(new Visitors.ConstantTypeVisitor<List<TypeConstraint>>(constraints) {

            @Override
            public List<TypeConstraint> visit(TypeApplication typeApplication) {
                typeApplication.getRange().visit(this);
                typeApplication.getDomain().visit(this);
                return constraints;
            }

            @Override
            public List<TypeConstraint> visit(ConstrainedType constrainedType) {
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
            return constraints.get(0);
        }

        return new TypeConstraints(flatten(constraints));
    }

    private List<TypeConstraint> flatten(List<TypeConstraint> constraints) {
        List<TypeConstraint> flattened = Lists.newArrayList();
        for (TypeConstraint constraint : constraints) {
            constraint.collectTo(flattened);
        }
        return flattened;
    }
}
