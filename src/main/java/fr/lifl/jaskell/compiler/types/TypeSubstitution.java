package fr.lifl.jaskell.compiler.types;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author bailly
 */
public class TypeSubstitution implements TypeVisitor<Type> {

    private final Map<TypeVariable, Type> map;

    public TypeSubstitution() {
        this.map = Maps.newHashMap();
    }

    public TypeSubstitution(Map<TypeVariable,Type> map) {
        this.map = map;
    }

    /**
     * Applies m as a substitution for type variables
     * occuring in t
     *
     * @param t the Type to substitute
     * @return a new Type whose all occurrences of mapping in m
     *         have been replaced
     */
    public Type substitute(Type t) {
        return t.visit(this);
    }

    public Type visit(TypeVariable t) {
        Type m = map.get(t);
        if (m != null) {
            return m.visit(this);
        }
        return t;
    }

    public Type visit(PrimitiveType primitiveType) {
        return primitiveType;
    }

    public Type visit(TypeApplication typeApplication) {
        return Types.apply(
                typeApplication.getDomain().visit(this),
                typeApplication.getRange().visit(this));
    }

    public Type visit(TypeConstructor typeConstructor) {
        return typeConstructor;
    }

    @Override
    public Type visit(ConstrainedType constrainedType) {
        return constrainedType;
    }


}
