package fr.lifl.jaskell.compiler.types;

import java.util.Map;

/**
 * Unifies two types.
 * <p/>
 * This class implements a type unification algorithm between types A and B. A call
 * to unify returns the least general type which is a unification of both types.
 *
 * @author bailly
 * @version $Id: TypeUnifier.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeUnifier implements TypeVisitor {

    private Type from, to;

    /* store the resutl */
    private Type result;

    /* the mapping */
    private Map map;

    private TypeSubstitution subst;


    /**
     * Tries to unify given types with the type for this unifier
     * storing the result in given map. The user is responsible for
     * applying the substitution to the returned type.
     *
     * @param from the type to unify
     * @param to   the type to unify to
     * @param map  the mapping to store substitutions in
     * @return a new Type
     */
    public Type unify(Type from, Type to, Map map) {
        this.from = from;
        this.to = to;
        this.map = map;
        this.subst = new TypeSubstitution(map);
        log.finest(
                "TypeUnifier -> trying to unify "
                        + from
                        + " against "
                        + to
                        + " with map "
                        + map);
        result = (Type) from.visit(this);
        return result;
    }

    public Object visit(TypeVariable t) {
        /* try to find type variable in map */
        Type m = (Type) map.get(t);
        log.finest(
                "TypeUnifier -> visiting variable  "
                        + t
                        + " against "
                        + to
                        + " with map "
                        + m);
        if (m != null) {
			/* variable t already mapped - recursively unifies 
					m with type and check circularity */
            if (t.equals(map.get(m)))
                return t;
            else
                return unify(m, to, map);
        } else {
			/* t is not mapped yet */
            if (t.equals(map.get(to))) /* circularity check */
                return t;
            else if (t.equals(to))
                return t;
            else {
                Type r = subst.substitute(to);
                if (r.contains(t))
                    throw new TypeError(
                            "Recursive type variable unification between "
                                    + t
                                    + " and "
                                    + to);
                map.put(t, r);
                log.finest("Mapping " + t + " to " + r);
                return r;
            }
        }
    }

    public Object visit(PrimitiveType primitiveType) {
        if (to instanceof PrimitiveType && primitiveType.equals(to))
            return primitiveType;
        else if (to instanceof TypeVariable) {
            return unify(to, primitiveType, map);
        } else {
            throw new TypeError(
                    "Cannot unify " + to + " with " + primitiveType);
        }

    }

    public Object visit(TypeApplication typeApplication) {
        if (to instanceof TypeApplication) {
            TypeApplication ta = (TypeApplication) to;
            Type dom = unify(typeApplication.getDomain(), ta.getDomain(), map);
            Type rge =
                    unify(
                            subst.substitute(typeApplication.getRange()),
                            ta.getRange(),
                            map);
            return new TypeApplication(subst.substitute(dom), rge);
        } else if (to instanceof TypeVariable) {
            return unify(to, typeApplication, map);
        } else {
            throw new TypeError(
                    "Cannot unify " + to + " with " + typeApplication);
        }
    }

    public Object visit(TypeConstructor typeConstructor) {
        if (to instanceof TypeConstructor && typeConstructor.equals(to))
            return typeConstructor;
        else if (to instanceof TypeVariable) {
            return unify(to, typeConstructor, map);
        } else {
            throw new TypeError(
                    "Cannot unify " + to + " with " + typeConstructor);
        }
    }

    @Override
    public Object visit(ConstrainedType constrainedType) {
        Type unified = unify(constrainedType.getBaseType(),to, map);
        TypeConstraint constraint = constrainedType.getTypeConstraint().substitute(subst);
        return Types.constraint(unified,constraint);
    }

}
