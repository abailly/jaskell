/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler.types;

import java.util.HashMap;
import java.util.Map;

import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Primitives;

import junit.framework.TestCase;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author  bailly
 * @version $Id: TypeTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class TypeTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static final PrimitiveType LIST = new PrimitiveType("([])", fr.lifl.jaskell.runtime.types.JList.class, FunctionKind.K_K, new TypeApplicationFormat() {
                public String formatApply(Type d, Type r) {
                    return "[" + r + "]";
                }
            }, new CovariantComparator());

    public static final PrimitiveType TUPLE_2 = new PrimitiveType("((,))", fr.lifl.jaskell.runtime.types.Tuple_2.class, new FunctionKind(SimpleKind.K, FunctionKind.K_K), new TypeApplicationFormat() {
                public String formatApply(Type d, Type r) {
                    return r + ",";
                }
            }, new CovariantComparator());

    public static final PrimitiveType FUNCTION = new PrimitiveType("(->)", fr.lifl.jaskell.runtime.types.Closure.class, new FunctionKind(SimpleKind.K, FunctionKind.K_K), new TypeApplicationFormat() {
                public String formatApply(Type d, Type r) {
                    return r + " ->";
                }
            }, new FunctionComparator());

    public static final PrimitiveType INT = new PrimitiveType("Int", int.class);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Test
    public void testApply() {
        Type v = new TypeVariable("a");
        Type v2 = new TypeVariable("b");
        Type t1 = new TypeApplication(new TypeApplication(FUNCTION, v), v2);
        assertEquals("(a -> b)", t1.toString());
    }

    @Test
    public void testTuple2() {
        Type a = new TypeVariable("a");
        Type b = new TypeVariable("b");
        Type t = new TypeApplication(new TypeApplication(TUPLE_2, a), b);
        assertEquals("(a, b)", t.makeString());
    }

    //      public void testTuple3() {
    //              Type a = new TypeVariable("a");
    //              Type b = new TypeVariable("b");
    //              Type c = new TypeVariable("c");
    //              Type t =
    //                      new TypeApplication(
    //                              new TypeApplication(
    //                                      new TypeApplication(Primitives.TUPLE_3, a),
    //                                      b),
    //                              c);
    //              assertEquals("(a,b,c)", t.makeString());
    //      }

    @Test
    public void testMakeFunction() {
        Type a = new TypeVariable("a");
        Type b = new TypeVariable("b");
        Type c = new TypeVariable("c");
        Type t = PrimitiveType.makeFunction(PrimitiveType.makeFunction(a, b), c);
        assertEquals("((a -> b) -> c)", t.makeString());
    }

    @Test
    public void testGetConstructor() {
        TypeVariable v1 = new TypeVariable("m");
        Type v2 = new TypeVariable("a");
        Type v3 = new TypeVariable("b");
        Type t = new TypeApplication(new TypeApplication(FUNCTION, v2), new TypeApplication(v1, v3));
        t = new TypeApplication(new TypeApplication(FUNCTION, new TypeApplication(v1, v2)), t);
        t = new TypeApplication(new TypeApplication(FUNCTION, t), new TypeApplication(v1, v3));
        assertEquals(Primitives.FUNCTION, t.getConstructor());
    }

    @Test
    public void testUnification() {
        Type a = new TypeVariable("a");
        Type b = new TypeVariable("b");
        Type c = new TypeVariable("c");
        Type t = PrimitiveType.makeFunction(PrimitiveType.makeFunction(a, b), c);
        Type i = INT;
        Type t1 = PrimitiveType.makeFunction(PrimitiveType.makeFunction(i, i), i);
        Type t3 = new TypeUnifier().unify(t, t1, new HashMap());
        assertEquals(t1, t3);
    }

    @Test
    public void testUnification2() {
        Type a = new TypeVariable("a");
        Type b = new TypeVariable("b");
        Type t = PrimitiveType.makeFunction(a, b);
        Type i = INT;
        Map m = new HashMap();
        Type t1 = PrimitiveType.makeFunction(PrimitiveType.makeFunction(i, i), i);
        Type t3 = new TypeUnifier().unify(t, t1, m);
        assertEquals(t1, t3);
        System.err.println("subst : " + m);
    }

    @Test
    public void testConstraintUnification() {
        TypeContext tctx = new TypeContext() {

            public Definition resolveType(Type t) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        Map m = new HashMap();
        TypeVariable v = new TypeVariable("a");
        Type v2 = new TypeVariable("b");
        Type t1 = new TypeApplication(new TypeApplication(FUNCTION, v), v2);
        /* (Eq a) => (a -> b) */
        t1.setContext(tctx);
        /* unify with a = INT */
        Type t2 = PrimitiveType.makeFunction(INT, INT);
        t2.setContext(tctx);
        Type t3 = new TypeUnifier().unify(t1, t2, m);
        System.err.println("t3 : " + t3 + ", subst : " + m);
    }

    @Ignore("constraints with typeclasses do not work")
    @Test
    public void testFailedConstraintUnification() {
        TypeContext tctx = new TypeContext() {
            public Definition resolveType(Type t) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        Map m = new HashMap();
        TypeVariable v = new TypeVariable("a");
        Type v2 = new TypeVariable("b");
        Type t1 = new TypeApplication(new TypeApplication(FUNCTION, v), v2);
        /* (Eq a) => (a -> b) */
        t1.setContext(tctx);
        /* unify with a = INT */
        Type t2 = PrimitiveType.makeFunction(INT, INT);
        t2.setContext(tctx);
        try {
            Type t3 = new TypeUnifier().unify(t1, t2, m);
            fail();
        } catch (TypeError te) {
        }
    }

    @Test
    public void testReducedConstraintUnification() {
        TypeContext tctx = new TypeContext() {

            public Definition resolveType(Type t) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        Map m = new HashMap();
        TypeVariable v = new TypeVariable("a");
        Type v2 = new TypeVariable("b");
        Type t1 = new TypeApplication(new TypeApplication(FUNCTION, v), v2);
        /* (Eq a) => (a -> b) */
        t1.setContext(tctx);
        /* unify with a = [ t0 ] */
        Type t2 = PrimitiveType.makeFunction(TypeFactory.makeApplication(LIST, TypeFactory.freshBinding()), INT);
        t2.setContext(tctx);
        Type t3 = new TypeUnifier().unify(t1, t2, m);
        System.err.println("t3 : " + t3 + ", subst : " + m);
    }
}
