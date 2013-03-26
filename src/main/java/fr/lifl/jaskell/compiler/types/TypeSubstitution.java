package fr.lifl.jaskell.compiler.types;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bailly
 * @version $Id: TypeSubstitution.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeSubstitution implements TypeVisitor<Type> {

    private final Map<TypeVariable, Type> map;

    public TypeSubstitution() {
        this.map = Maps.newHashMap();
    }

    public TypeSubstitution(Map<TypeVariable,Type> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    /**
     * Applies m as a substitution for type variables
     * occuring in t
     *
     * @param t the Type to substitute
     * @return a new Type whose all occurences of mapping in m
     *         have been replaced
     */
    public Type substitute(Type t) {
        Type ret = t.visit(this);
        return ret;
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
        return TypeFactory.makeApplication(
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
