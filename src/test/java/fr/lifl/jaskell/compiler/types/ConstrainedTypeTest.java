package fr.lifl.jaskell.compiler.types;

import static fr.lifl.jaskell.compiler.types.TypeTest.FUNCTION;

import static fr.lifl.jaskell.compiler.types.Types.apply;
import static fr.lifl.jaskell.compiler.types.Types.var;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.fest.assertions.Assertions;
import org.junit.Ignore;
import org.junit.Test;


public class ConstrainedTypeTest {

    @Test
    public void typeClassConstraintIsPropagatedWhenApplyingType() {
        TypeVariable v = var("a");
        ConstrainedType constrainedType = new ConstrainedType(v,
                new TypeClassConstraint(new TypeClass("Eq"),v));

        Type v2 = var("b");
        Type t1 = apply(apply(FUNCTION, constrainedType), v2);

        Type constrainedFunctionType = new ConstrainedType(apply(apply(FUNCTION, v), v2),
                new TypeClassConstraint(new TypeClass("Eq"),v));

        assertThat(t1).isEqualTo(constrainedFunctionType);
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
