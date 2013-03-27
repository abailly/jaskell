package fr.lifl.jaskell.compiler.types;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static fr.lifl.jaskell.compiler.core.Primitives.BOOL;
import static fr.lifl.jaskell.compiler.types.Types.*;
import static fr.lifl.jaskell.compiler.types.Types.apply;
import static org.fest.assertions.Assertions.assertThat;

public class TypeUnifierTest {

    TypeUnifier unifier = new TypeUnifier();
    final Type a = var("a");
    final Type b = var("b");
    final Type c = var("c");
    final Type f = fun(b, c);

    @Test
    public void unifiesAVariableAndPrimitiveTypeYieldsAPrimitiveType() throws Exception {

        HashMap<Object, Object> map = newHashMap();
        assertThat(unifier.unify(a, BOOL, map)).isEqualTo(BOOL);
        assertThat(map.get(a)).isEqualTo(BOOL);

        assertThat(unifier.unify(BOOL, a, newHashMap())).isEqualTo(BOOL);
    }

    @Test
    public void unifiesAVariableAndAVariableYieldsAVariable() throws Exception {

        HashMap<Object, Object> map = newHashMap();
        assertThat(unifier.unify(a, b, map)).isEqualTo(b);
        assertThat(map.get(a)).isEqualTo(b);

        assertThat(unifier.unify(b, a, newHashMap())).isEqualTo(a);
    }

    @Test
    public void unifiesAVariableAndAFunctionTypeYieldsFunctionType() throws Exception {
        HashMap<Type, Type> map = newHashMap();
        assertThat(unifier.unify(a, f, map)).isEqualTo(f);
        assertThat(map.get(a)).isEqualTo(f);

        assertThat(unifier.unify(f, a, map)).isEqualTo(f);
        assertThat(map.get(a)).isEqualTo(f);
    }

    @Test
    public void unifiesTwoFunctionsMapsVariablesToPrimitives() throws Exception {
        Map<Type, Type> map = newHashMap();
        assertThat(unifier.unify(fun(a, b), fun(BOOL, BOOL), map)).isEqualTo(fun(BOOL, BOOL));
        assertThat(map.get(a)).isEqualTo(BOOL);
        assertThat(map.get(b)).isEqualTo(BOOL);
    }

    @Test(expected = TypeError.class)
    public void cannotUnifyAVariableAndAFunctionWithSameVariable() throws Exception {
        unifier.unify(a, fun(a, b), newHashMap());
    }

    @Test
    public void unifiesConstrainedVariableToVariableYieldsConstrainedVariable() throws Exception {
        Map<Type, Type> map = newHashMap();
        Type eqB = constraint(b, typeClass("Eq", b));
        Type eqA = constraint(a, typeClass("Eq", a));

        assertThat(unifier.unify(a, eqB, map)).isEqualTo(eqB);
        assertThat(map.get(a)).isEqualTo(eqB);

        map = newHashMap();
        assertThat(unifier.unify(eqB, a, map)).isEqualTo(eqA);
        assertThat(map.get(b)).isEqualTo(a);
    }

    @Test
    public void propagatesRangeSubstitutionToDomainWhenUnifyingTypeApplication() throws Exception {
        assertThat(unifier.unify(fun(a, a), fun(b, BOOL),newHashMap())).isEqualTo(fun(BOOL, BOOL));
    }

    @Test
    public void unifiesConstrainedTypeWithPrimitiveTypes() throws Exception {
        Type t = var("t");
        Type eqTA = constraint(fun(apply(t, a), a), typeClass("Eq", apply(t, a)));
        Type f = fun(b, BOOL);
        
        assertThat(unifier.unify(eqTA,f,newHashMap())).isEqualTo(constraint(fun(apply(t, BOOL), BOOL), typeClass("Eq", apply(t, BOOL))));
    }
}
