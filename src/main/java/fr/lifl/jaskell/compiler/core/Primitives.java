package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.jaskell.compiler.types.CovariantComparator;
import fr.lifl.jaskell.compiler.types.FunctionComparator;
import fr.lifl.jaskell.compiler.types.FunctionKind;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.SimpleKind;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.TypeApplicationFormat;
import fr.lifl.jaskell.compiler.types.TypeFactory;
import fr.lifl.jaskell.compiler.types.UncomparableException;
import fr.lifl.jaskell.runtime.modules.Prelude;
import fr.lifl.jaskell.runtime.types.Closure;
import fr.lifl.jaskell.runtime.types.Event;
import fr.lifl.jaskell.runtime.types.JEvent;
import fr.lifl.jaskell.runtime.types.JList;
import fr.lifl.jaskell.runtime.types.JMessage;
import fr.lifl.jaskell.runtime.types.Tuple_2;
import fr.lifl.jaskell.runtime.types.Unit;
import fr.lifl.jaskell.runtime.types._3a;
import fr.lifl.jaskell.runtime.types._5b_5d;

/**
 * This class stores all primitive objects defined in Jaskell useable by the
 * compiler.
 * 
 * @author bailly
 * @version $Id: Primitives.java 1154 2005-11-24 21:43:37Z nono $
 */
public interface Primitives {

  /** primitive types */
  /** Type constants for builtin atomic types */
  public static final PrimitiveType BOOL = new PrimitiveType("Bool",
      boolean.class);

  public static final PrimitiveType INT = new PrimitiveType("Int", int.class) {
    /*
     * (non-Javadoc)
     * 
     * @see jaskell.compiler.types.TypeConstructor#compare(jaskell.compiler.types.Type)
     */
    public int compare(Type other) throws UncomparableException {
      if (other == INT)
        return 0;
      if (other == FLOAT || other == DOUBLE)
        return -1;
      return -other.compare(this);
    }
  };

  public static final PrimitiveType FLOAT = new PrimitiveType("Float",
      float.class) {
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

  public static final PrimitiveType DOUBLE = new PrimitiveType("Double",
      double.class) {
    /*
     * (non-Javadoc)
     * 
     * @see jaskell.compiler.types.TypeConstructor#compare(jaskell.compiler.types.Type)
     */
    public int compare(Type other) throws UncomparableException {
      if (other == FLOAT || other == INT)
        return 1;
      if (other == DOUBLE)
        return 0;
      return -other.compare(this);
    }
  };

  public static final PrimitiveType STRING = new PrimitiveType("String",
      java.lang.String.class);

  public static final PrimitiveType CHAR = new PrimitiveType("Char", char.class);

  static final PrimitiveData BOOL_DATA = new PrimitiveData("Bool", BOOL,
      boolean.class, Module.PRELUDE);

  public static final PrimitiveData INT_DATA = new PrimitiveData("Int", INT,
      int.class, Module.PRELUDE);

  public static final PrimitiveData FLOAT_DATA = new PrimitiveData("Float",
      FLOAT, float.class, Module.PRELUDE);

  public static final PrimitiveData DOUBLE_DATA = new PrimitiveData("Double",
      DOUBLE, double.class, Module.PRELUDE);

  public static final PrimitiveData STRING_DATA = new PrimitiveData("String",
      STRING, java.lang.String.class, Module.PRELUDE);

  public static final PrimitiveData CHAR_DATA = new PrimitiveData("Char", CHAR,
      char.class, Module.PRELUDE);

  /** type constructors for builtin aggregate types */
  public static final PrimitiveType MESSAGE = new PrimitiveType("Message",
      JMessage.class);

  public static final PrimitiveType EVENT = new PrimitiveType("Event",
      JEvent.class);

  public static final PrimitiveData EVENT_DATA = new PrimitiveData("Event",
      EVENT, JEvent.class, Module.PRELUDE);

  public static final PrimitiveType LIST = new PrimitiveType("([])",
      JList.class, FunctionKind.K_K,
      new TypeApplicationFormat() {
        public String formatApply(Type d, Type r) {
          return "[" + r + "]";
        }
      }, new CovariantComparator());

  public static final PrimitiveType FUNCTION = new PrimitiveType("(->)",
      Closure.class, new FunctionKind(SimpleKind.K,
          FunctionKind.K_K), new TypeApplicationFormat() {
        public String formatApply(Type d, Type r) {
          return r + " ->";
        }
      }, new FunctionComparator());

  public static final PrimitiveType UNIT = new PrimitiveType("(())",
      Unit.class);

  public static final PrimitiveType TUPLE_2 = new PrimitiveType("((,))",
      Tuple_2.class, new FunctionKind(SimpleKind.K,
          FunctionKind.K_K), new TypeApplicationFormat() {
        public String formatApply(Type d, Type r) {
          return r + ",";
        }
      }, new CovariantComparator());

  public static final Type VAR_A = TypeFactory.freshBinding();

  public static final Type VAR_1 = TypeFactory.freshBinding();

  public static final Type VAR_2 = TypeFactory.freshBinding();

  /* useful function types */
  public static final Type INT_INT = PrimitiveType.makeFunction(INT, INT);

  public static final Type INT_INT_INT = PrimitiveType.makeFunction(INT,
      PrimitiveType.makeFunction(INT, INT));

  public static final Type FLOAT_FLOAT_FLOAT = PrimitiveType.makeFunction(
      FLOAT, PrimitiveType.makeFunction(FLOAT, FLOAT));

  public static final Type FLOAT_FLOAT = PrimitiveType.makeFunction(FLOAT,
      FLOAT);

  public static final Type BOOL_BOOL = PrimitiveType.makeFunction(BOOL, BOOL);

  public static final Type BOOL_BOOL_BOOL = PrimitiveType.makeFunction(BOOL,
      PrimitiveType.makeFunction(BOOL, BOOL));

  /*
   * public static final Type FLOAT_FLOAT = PrimitiveType.makeFunction(FLOAT,
   * FLOAT); public static final Type FLOAT_FLOAT_FLOAT =
   * PrimitiveType.makeFunction(FLOAT, PrimitiveType.makeFunction(FLOAT,
   * FLOAT));
   */
  public static final Type INT_INT_BOOL = PrimitiveType.makeFunction(INT,
      PrimitiveType.makeFunction(INT, BOOL));

  public static final Type FLOAT_FLOAT_BOOL = PrimitiveType.makeFunction(FLOAT,
      PrimitiveType.makeFunction(FLOAT, BOOL));

  /** primitive data definitions */
  public static final DataDefinition DATA_LIST = new PrimitiveData("([])",
      TypeFactory.makeApplication(LIST, VAR_A), JList.class, Module.PRELUDE);

  public static final ConstructorDefinition CONS = new PrimitiveConstructor(
      "(:)", DATA_LIST, new Type[] { VAR_A,
          TypeFactory.makeApplication(LIST, VAR_A) }, _3a.class, Module.PRELUDE);

  public static final ConstructorDefinition NIL = new PrimitiveConstructor(
      "([])", DATA_LIST, new Type[0], _5b_5d.class, Module.PRELUDE);

  public static final ConstructorDefinition MSG = new PrimitiveConstructor(
      "Event", EVENT_DATA, new Type[] { STRING, /* port name */
      MESSAGE /* message */
      }, Event.class, Module.PRELUDE, new int[] { 0, 1 });

  /** primitive functions */
  public static final PrimitiveFunction ERROR = new PrimitiveFunction("error",
      Module.PRELUDE, TypeFactory.makeApplication(TypeFactory.makeApplication(
          FUNCTION, STRING), TypeFactory.freshBinding()), null);

  public static final PrimitiveFunction ADD_INT_INT = new PrimitiveFunction(
      "(+)", Module.PRELUDE, INT_INT_INT, null);

  public static final PrimitiveFunction ADD_FLOAT_FLOAT = new PrimitiveFunction(
      "(+.)", Module.PRELUDE, FLOAT_FLOAT_FLOAT, null);

  /*
   * public static final PrimitiveFunction ADD_FLOAT_FLOAT = new
   * PrimitiveFunction("(+)", Module.PRELUDE, FLOAT_FLOAT_FLOAT, null);
   */
  public static final PrimitiveFunction SUB_INT_INT = new PrimitiveFunction(
      "(-)", Module.PRELUDE, INT_INT_INT, null);

  public static final PrimitiveFunction MUL_INT_INT = new PrimitiveFunction(
      "(*)", Module.PRELUDE, INT_INT_INT, null);

  public static final PrimitiveFunction EQ_INT_INT = new PrimitiveFunction(
      "(==)", Module.PRELUDE, INT_INT_BOOL, null);

  public static final PrimitiveFunction LE_INT_INT = new PrimitiveFunction(
      "(<=)", Module.PRELUDE, INT_INT_BOOL, null);

  public static final PrimitiveFunction GE_INT_INT = new PrimitiveFunction(
      "(>=)", Module.PRELUDE, INT_INT_BOOL, null);

  public static final PrimitiveFunction LT_INT_INT = new PrimitiveFunction(
      "(<)", Module.PRELUDE, INT_INT_BOOL, null);

  public static final PrimitiveFunction GT_INT_INT = new PrimitiveFunction(
      "(>)", Module.PRELUDE, INT_INT_BOOL, null);

  public static final PrimitiveFunction LE_FLOAT_FLOAT = new PrimitiveFunction(
      "(<=.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

  public static final PrimitiveFunction GE_FLOAT_FLOAT = new PrimitiveFunction(
      "(>=.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

  public static final PrimitiveFunction LT_FLOAT_FLOAT = new PrimitiveFunction(
      "(<.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

  public static final PrimitiveFunction GT_FLOAT_FLOAT = new PrimitiveFunction(
      "(>.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

  public static final PrimitiveFunction EQ_FLOAT_FLOAT = new PrimitiveFunction(
      "(=.)", Module.PRELUDE, FLOAT_FLOAT_BOOL, null);

  public static final PrimitiveFunction MAX_INT_INT = new PrimitiveFunction(
      "max", Module.PRELUDE, INT_INT_INT, null);

  public static final PrimitiveFunction XOR = new PrimitiveFunction("(^^)",
      Module.PRELUDE, BOOL_BOOL_BOOL, null);

  public static final PrimitiveFunction NOT = new PrimitiveFunction("not",
      Module.PRELUDE, BOOL_BOOL, null);

  public static final PrimitiveFunction SEQ = new PrimitiveFunction("seq",
      Module.PRELUDE, PrimitiveType.makeFunction(TypeFactory.freshBinding(),
          PrimitiveType.makeFunction(VAR_1, VAR_1)),
      Prelude.class);

  public static final PrimitiveFunction NEG_INT = new PrimitiveFunction(
      "negate", Module.PRELUDE, INT_INT, null);

  public static final PrimitiveFunction PRIM_ERROR = new PrimitiveFunction(
      "primError", Module.PRELUDE, TypeFactory.makeApplication(TypeFactory
          .makeApplication(FUNCTION, STRING), TypeFactory.freshBinding()),
      Prelude.class);

  /* primitive constructors */
  //	public static final PrimitiveData LIST_DATA =
  //		new PrimitiveData("([])", new TypeApplication(LIST,VAR_A),
  // JList.class,Module.PRELUDE);
  //
  //	public static final PrimitiveConstructor NIL =
  //		new PrimitiveConstructor(
  //			"([])",
  //			LIST_DATA,
  //			new Type[0],NilList.class,Module.PRELUDE);
  //
  //	public static final PrimitiveConstructor CONS =
  //		new PrimitiveConstructor(
  //			"(:)",
  //			LIST_DATA,
  //			new Type[] { VAR_A, new TypeApplication(LIST, VAR_A)},
  //			ConsList.class,Module.PRELUDE);
}