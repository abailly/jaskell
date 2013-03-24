package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Conditional;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.Primitives;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.Variable;
import fr.lifl.jaskell.compiler.types.TypeError;
import fr.lifl.jaskell.parser.Yyparser;

import java.io.StringReader;

import junit.framework.TestCase;

/**
 * @author nono
 * @version $Id: TypeCheckerTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class TypeCheckerTest extends TestCase {

  /**
   * Constructor for TypeCheckerTest.
   * 
   * @param arg0
   */
  public TypeCheckerTest(String arg0) {
    super(arg0);
  }


  public void testY() throws Exception {
    Module m = new Module("Main", null);
    //Class.forName("jaskell.compiler.core.Primitives");
    // definition de g
    Abstraction a1 = new Abstraction();
    a1.bind(new LocalBinding("x"));

    Application ap0 = new Application();
    ap0.setFunction(new Variable("x"));
    ap0.addArgument(new Variable("x"));
    a1.setBody(ap0);
    // definition de f
    m.bind("Y", a1);
    TypeChecker tc = new TypeChecker();
    try {
      m.visit(tc);
      fail();
    } catch (TypeError te) {
      System.err.println("TCheck =>" + te.getMessage());
    }
  }


  public void testComplexType() {
    Module m = new Module("Main", null);
    // definitions des Types
    QualifiedVariable qv = new QualifiedVariable("(+)");
    qv.addPathElement("Prelude");
    // definition de f
    Abstraction a2 = new Abstraction();
    a2.bind(new LocalBinding("g"));
    a2.bind(new LocalBinding("x"));
    Application ap3 = new Application();
    ap3.setFunction(qv);
    ap3.addArgument(new Variable("x"));
    ap3.addArgument(new IntegerLiteral(1));
    a2.setBody(ap3);
    // definition de main
    Application ap6 = new Application();
    ap6.setFunction(new Variable("g"));
    ap6.addArgument(new IntegerLiteral(1));

    m.bind("f", a2);
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
    assertNotNull("Computed type :" + a2.getType().makeString(), a2.getType());
  }

  public void testComplexType02() {
    Module m = new Module("Main", null);
    // definitions des Types
    QualifiedVariable qv = new QualifiedVariable("(+)");
    qv.addPathElement("Prelude");
    // definition de f
    Abstraction a2 = new Abstraction();
    a2.bind(new LocalBinding("n"));
    a2.bind(new LocalBinding("a"));
    a2.bind(new LocalBinding("b"));
    Application ap3 = new Application();
    ap3.setFunction(new Variable("b"));
    ap3.addArgument(new Variable("n"));
    // definition de main
    Application ap6 = new Application();
    ap6.setFunction(new Variable("n"));
    ap6.addArgument(new Variable("a"));
    ap6.addArgument(new Variable("b"));
    ap3.addArgument(ap6);
    a2.setBody(ap3);
    m.bind("f", a2);
    TypeChecker tc = new TypeChecker();
    try {
      m.visit(tc);
      fail();
    } catch (TypeError te) {
    }
  }

  public void testComplexType03() {
    Module m = new Module("Main", null);
    // definition de f
    Abstraction a2 = new Abstraction();
    a2.bind(new LocalBinding("f"));
    a2.bind(new LocalBinding("a"));
    a2.bind(new LocalBinding("b"));
    a2.bind(new LocalBinding("c"));
    Application ap3 = new Application();
    ap3.setFunction(new Variable("f"));
    ap3.addArgument(new Variable("a"));

    Application ap4 = new Application();
    ap4.setFunction(new Variable("f"));
    ap4.addArgument(new Variable("b"));
    // definition de main
    Application ap6 = new Application();
    ap6.setFunction(new Variable("c"));
    ap6.addArgument(ap3);
    ap6.addArgument(ap4);
    a2.setBody(ap6);
    m.bind("f", a2);
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
  }

  public void testAlternative() {
    String text = "length [] = 0 ;" + "length (x : xs) = length xs + 1";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser();
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
    System.err.println(m);
  }

  public void testMappairs() {
    String text = "	mappairs f [] ys = [] ;" + " mappairs f (x:xs) [] = [];"
        + " mappairs f (x:xs) (y:ys) = f x y : mappairs f xs ys";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser(false);
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    System.out.println(m);
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
  }

  public void testMap() {
    String text = "	map f [] = [] ;" + " map f (x:xs) = f x : map f xs";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser();
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
  }

  public void testFoldl() {
    String text = "foldl f z (x:xs) = foldl f (f z x) xs ; "
        + "	foldl f z [] = [] ";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser();
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
    System.err.println(m);
  }

  public void testFoldr() {
    String text = "foldr f z (x:xs) = f x (foldr f z xs); "
        + "	foldr f z [] = [] ";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser();
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
    System.err.println(m);
  }

  public void testIterate() {
    String text = "iterate f x  = x : iterate f (f x) ";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser();
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
    System.err.println(m);
  }

  public void testOverload() throws ClassNotFoundException {
    Class.forName("jaskell.compiler.core.Primitives");
    String text = "add1 x = x + 1.0";
    StringReader sr = new StringReader(text);
    Yyparser p = new Yyparser();
    p.parse(sr);
    Module m = (Module) Module.getToplevels().get("Main");
    /* typecheck module */
    TypeChecker tc = new TypeChecker();
    m.visit(tc);
    System.err.println(m);
    assertEquals(Primitives.FLOAT_FLOAT, ((Expression) m.resolve("add1"))
        .getType());
  }
}