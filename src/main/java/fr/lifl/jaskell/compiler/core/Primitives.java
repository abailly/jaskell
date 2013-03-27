/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.jaskell.compiler.types.*;
import fr.lifl.jaskell.runtime.modules.Prelude;
import fr.lifl.jaskell.runtime.types.*;


/**
 * This class stores all primitive objects defined in Jaskell useable by the compiler.
 *
 * @author  bailly
 * @version $Id: Primitives.java 1154 2005-11-24 21:43:37Z nono $
 */
public interface Primitives {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /** primitive types */
    /** Type constants for builtin atomic types */
    PrimitiveType BOOL = new PrimitiveType("Bool", boolean.class);

    PrimitiveType INT = new PrimitiveType("Int", int.class) {
        /*
         * (non-Javadoc)
         *
         * @see jaskell.compiler.types.TypeConstructor#compare(jaskell.compiler.types.Type)
         */
        public int compare(Type other) throws UncomparableException {
            if (other == INT)
                return 0;
            if ((other == FLOAT) || (other == DOUBLE))
                return -1;
            return -other.compare(this);
        }
    };

    PrimitiveType FLOAT = new PrimitiveType("Float", float.class) {
        /*
         * (non-Javadoc)
         *
         * @see jaskell.compiler.types.TypeConstructor#compare(jaskell.compiler.types.Type)
         */
        public int compare(Type other) throws UncomparableException {
            if (other == FLOAT)
                return 0;
            if (other == INT)
                return 1;
            if (other == DOUBLE)
                return -1;
            return -other.compare(this);
        }
    };

    PrimitiveType DOUBLE = new PrimitiveType("Double", double.class) {
        /*
         * (non-Javadoc)
         *
         * @see jaskell.compiler.types.TypeConstructor#compare(jaskell.compiler.types.Type)
         */
        public int compare(Type other) throws UncomparableException {
            if ((other == FLOAT) || (other == INT))
                return 1;
            if (other == DOUBLE)
                return 0;
            return -other.compare(this);
        }
    };

    PrimitiveType STRING = new PrimitiveType("String", java.lang.String.class);

    PrimitiveType CHAR = new PrimitiveType("Char", char.class);

    PrimitiveData BOOL_DATA = new PrimitiveData("Bool", BOOL, boolean.class, Module.PRELUDE);

    PrimitiveData INT_DATA = new PrimitiveData("Int", INT, int.class, Module.PRELUDE);

    PrimitiveData FLOAT_DATA = new PrimitiveData("Float", FLOAT, float.class, Module.PRELUDE);

    PrimitiveData DOUBLE_DATA = new PrimitiveData("Double", DOUBLE, double.class, Module.PRELUDE);

    PrimitiveData STRING_DATA = new PrimitiveData("String", STRING, java.lang.String.class, Module.PRELUDE);

    PrimitiveData CHAR_DATA = new PrimitiveData("Char", CHAR, char.class, Module.PRELUDE);

    /** type constructors for builtin aggregate types */
    PrimitiveType MESSAGE = new PrimitiveType("Message", JMessage.class);

    PrimitiveType EVENT = new PrimitiveType("Event", JEvent.class);

    PrimitiveData EVENT_DATA = new PrimitiveData("Event", EVENT, JEvent.class, Module.PRELUDE);

    PrimitiveType LIST = new PrimitiveType("([])", JList.class, FunctionKind.K_K, new TypeApplicationFormat() {
                public String formatApply(Type d, Type r) {
                    return "[" + r + "]";
                }
            }, new CovariantComparator());

    PrimitiveType FUNCTION = new PrimitiveType("(->)", Closure.class, new FunctionKind(SimpleKind.K, FunctionKind.K_K), new TypeApplicationFormat() {
                public String formatApply(Type d, Type r) {
                    return r + " ->";
                }
            }, new FunctionComparator());

    PrimitiveType UNIT = new PrimitiveType("(())", Unit.class);

    PrimitiveType TUPLE_2 = new PrimitiveType("((,))", Tuple_2.class, new FunctionKind(SimpleKind.K, FunctionKind.K_K), new TypeApplicationFormat() {
                public String formatApply(Type d, Type r) {
                    return r + ",";
                }
            }, new CovariantComparator());

    Type VAR_A = TypeFactory.freshBinding();

    Type VAR_1 = TypeFactory.freshBinding();

    Type VAR_2 = TypeFactory.freshBinding();

    /* useful function types */
    Type INT_INT = PrimitiveType.makeFunction(INT, INT);

    Type INT_INT_INT = PrimitiveType.makeFunction(INT, PrimitiveType.makeFunction(INT, INT));

    Type FLOAT_FLOAT_FLOAT = PrimitiveType.makeFunction(FLOAT, PrimitiveType.makeFunction(FLOAT, FLOAT));

    Type FLOAT_FLOAT = PrimitiveType.makeFunction(FLOAT, FLOAT);

    Type BOOL_BOOL = PrimitiveType.makeFunction(BOOL, BOOL);

    Type BOOL_BOOL_BOOL = PrimitiveType.makeFunction(BOOL, PrimitiveType.makeFunction(BOOL, BOOL));

    /*
     * public static final Type FLOAT_FLOAT = PrimitiveType.makeFunction(FLOAT,
     * FLOAT); public static final Type FLOAT_FLOAT_FLOAT =
     * PrimitiveType.makeFunction(FLOAT, PrimitiveType.makeFunction(FLOAT,
     * FLOAT));
     */
    Type INT_INT_BOOL = PrimitiveType.makeFunction(INT, PrimitiveType.makeFunction(INT, BOOL));

    Type FLOAT_FLOAT_BOOL = PrimitiveType.makeFunction(FLOAT, PrimitiveType.makeFunction(FLOAT, BOOL));

    /** primitive data definitions */
    DataDefinition DATA_LIST = new PrimitiveData("([])", Types.apply(LIST, VAR_A), JList.class, Module.PRELUDE);

    ConstructorDefinition CONS = new PrimitiveConstructor("(:)", DATA_LIST, new Type[] {
            VAR_A,
            Types.apply(LIST, VAR_A)
        }, _3a.class, Module.PRELUDE);

    ConstructorDefinition NIL = new PrimitiveConstructor("([])", DATA_LIST, new Type[0], _5b_5d.class, Module.PRELUDE);

    ConstructorDefinition MSG = new PrimitiveConstructor("Event", EVENT_DATA, new Type[] {
            STRING, /* port name */
            MESSAGE /* message */
        }, Event.class, Module.PRELUDE, new int[] { 0, 1 });

    /** primitive functions */
    PrimitiveFunction ERROR = new PrimitiveFunction("error", Module.PRELUDE, Types.apply(Types.apply(FUNCTION, STRING), TypeFactory.freshBinding()), null);

    PrimitiveFunction ADD_INT_INT = new PrimitiveFunction("(+)", Module.PRELUDE, INT_INT_INT, null);

    PrimitiveFunction ADD_FLOAT_FLOAT = new PrimitiveFunction("(+.)", Module.PRELUDE, FLOAT_FLOAT_FLOAT, null);

    /*
     * public static final PrimitiveFunction ADD_FLOAT_FLOAT = new
     * PrimitiveFunction("(+)", Module.PRELUDE, FLOAT_FLOAT_FLOAT, null);
     */
    PrimitiveFunction SUB_INT_INT = new PrimitiveFunction("(-)", Module.PRELUDE, INT_INT_INT, null);

    PrimitiveFunction MUL_INT_INT = new PrimitiveFunction("(*)", Module.PRELUDE, INT_INT_INT, null);

    PrimitiveFunction EQ_INT_INT = new PrimitiveFunction("(==)", Module.PRELUDE, INT_INT_BOOL, null);

    PrimitiveFunction LE_INT_INT = new PrimitiveFunction("(<=)", Module.PRELUDE, INT_INT_BOOL, null);

    PrimitiveFunction GE_INT_INT = new PrimitiveFunction("(>=)", Module.PRELUDE, INT_INT_BOOL, null);

    PrimitiveFunction LT_INT_INT = new PrimitiveFunction("(<)", Module.PRELUDE, INT_INT_BOOL, null);

    PrimitiveFunction GT_INT_INT = new PrimitiveFunction("(>)", Module.PRELUDE, INT_INT_BOOL, null);

    PrimitiveFunction LE_FLOAT_FLOAT = new PrimitiveFunction("(<=.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

    PrimitiveFunction GE_FLOAT_FLOAT = new PrimitiveFunction("(>=.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

    PrimitiveFunction LT_FLOAT_FLOAT = new PrimitiveFunction("(<.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

    PrimitiveFunction GT_FLOAT_FLOAT = new PrimitiveFunction("(>.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

    PrimitiveFunction EQ_FLOAT_FLOAT = new PrimitiveFunction("(=.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

    PrimitiveFunction MAX_INT_INT = new PrimitiveFunction("max", Module.PRELUDE, INT_INT_INT, null);

    PrimitiveFunction XOR = new PrimitiveFunction("(^^)", Module.PRELUDE, BOOL_BOOL_BOOL, null);

    PrimitiveFunction NOT = new PrimitiveFunction("not", Module.PRELUDE, BOOL_BOOL, null);

    PrimitiveFunction SEQ = new PrimitiveFunction("seq", Module.PRELUDE, PrimitiveType.makeFunction(TypeFactory.freshBinding(), PrimitiveType.makeFunction(VAR_1, VAR_1)), Prelude.class);

    PrimitiveFunction NEG_INT = new PrimitiveFunction("negate", Module.PRELUDE, INT_INT, null);

    PrimitiveFunction PRIM_ERROR = new PrimitiveFunction("primError", Module.PRELUDE, Types.apply(Types.apply(FUNCTION, STRING), TypeFactory.freshBinding()), Prelude.class);

    PrimitiveFunction PRIM_PUT_INT = new PrimitiveFunction("primPutInt", Module.PRELUDE, Types.apply(Types.apply(FUNCTION, INT), TypeFactory.freshBinding()), Prelude.class);

    /* primitive constructors */
    //    public static final PrimitiveData LIST_DATA =
    //            new PrimitiveData("([])", new TypeApplication(LIST,VAR_A),
    // JList.class,Module.PRELUDE);
    //
    //    public static final PrimitiveConstructor NIL =
    //            new PrimitiveConstructor(
    //                    "([])",
    //                    LIST_DATA,
    //                    new Type[0],NilList.class,Module.PRELUDE);
    //
    //    public static final PrimitiveConstructor CONS =
    //            new PrimitiveConstructor(
    //                    "(:)",
    //                    LIST_DATA,
    //                    new Type[] { VAR_A, new TypeApplication(LIST, VAR_A)},
    //                    ConsList.class,Module.PRELUDE);
}
