package fr.lifl.jaskell.compiler.types;

import org.junit.Ignore;
import org.junit.Test;

import static fr.lifl.jaskell.compiler.types.TypeTest.FUNCTION;
import static fr.lifl.jaskell.compiler.types.Types.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * TODO handle constraints identification
 * TODO handle superclass/subclass constraint widening
 * TODO handle constraints unification on variables
 * TODO handle constraints removal with instance
 * TODO multivariate typeclasses constraints
 * TODO type identification constraints (eg. a ~ b)
 */
public class ConstrainedTypeTest {

    private static Type eqA = constraint(var("a"), typeClass("Eq", var("a")));
    private static Type eqB = constraint(var("b"), typeClass("Eq", var("b")));
    private static Type eqC = constraint(var("c"), typeClass("Eq", var("c")));

    @Test
    public void typeClassConstraintIsPropagatedWhenApplyingType() {
        Type functionWithConstrainedDomain = apply(apply(FUNCTION,
                eqA),
                var("b"));

        Type functionWithConstrainedRange = apply(apply(FUNCTION,
                var("b")),
                eqA);

        Type constrainedFunctionType = constraint(
                apply(apply(FUNCTION, var("a")), var("b")), typeClass("Eq", var("a"))
        );

        assertThat(functionWithConstrainedDomain).isEqualTo(constrainedFunctionType);
        assertThat(functionWithConstrainedRange).isEqualTo(constraint(
                apply(apply(FUNCTION, var("b")), var("a")), typeClass("Eq", var("a"))
        ));
    }


    @Test
    public void multipleConstraintsAreComposedWhenApplyingType() throws Exception {

        assertThat(apply(apply(FUNCTION, eqA), eqB)).isEqualTo(constraint(
                apply(apply(FUNCTION, var("a")), var("b")),
                typeClass("Eq", var("a")),
                typeClass("Eq", var("b"))));

        assertThat(apply(apply(FUNCTION, eqC),apply(apply(FUNCTION, eqA), eqB))).isEqualTo(constraint(
                apply(apply(FUNCTION, var("c")), apply(apply(FUNCTION, var("a")), var("b"))),
                typeClass("Eq", var("c")),
                typeClass("Eq", var("a")),
                typeClass("Eq", var("b"))));

    }


    @Test
    public void redundantConstraintsAreIgnored() throws Exception {
        assertThat(apply(apply(FUNCTION, eqA), eqA)).isEqualTo(constraint(
                apply(apply(FUNCTION, var("a")), var("a")),
                typeClass("Eq", var("a"))));
    }

    @Ignore("constraints with typeclasses do not work (yet)")
    @Test
    public void testConstraint2() {
        TypeVariable v1 = new TypeVariable("m");
        Type v2 = new TypeVariable("a");
        Type v3 = new TypeVariable("b");
        Type t = new TypeApplication(new TypeApplication(FUNCTION, v2), new TypeApplication(v1, v3));
        t = new TypeApplication(new TypeApplication(FUNCTION, new TypeApplication(v1, v2)), t);
        t = new TypeApplication(new TypeApplication(FUNCTION, t), new TypeApplication(v1, v3));
        assertEquals("(Monad m) => (((m a) -> (a -> (m b))) -> (m b))", t.makeString());
    }
}
