package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.Types;
import fr.lifl.jaskell.parser.Yyparser;
import junit.framework.TestCase;

import java.io.StringReader;

/**
 * @author abailly
 * @version $Id: StrictnessAnalyzerTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class StrictnessAnalyzerTest extends TestCase {

    /**
     * Constructor for StrictnessAnalyzerTest.
     *
     * @param arg0
     */
    public StrictnessAnalyzerTest(String arg0) {
        super(arg0);
    }

    @Override
    public void setUp() throws Exception {
        Module.getToplevels().clear();
        // register Prelude using side-effect of ctor        
        Module.getToplevels().put("Prelude",Module.PRELUDE);
    }
    

    public void testFac() throws Exception {
        Class<Primitives> primitivesClass = Primitives.class;
        String text = "module Main where {fac x = if x == 0 then 1 else x * (fac (x+ -1))}";
        StringReader sr = new StringReader(text);
        Yyparser p = new Yyparser(false);
        p.parse(sr);
        Module m = (Module) Module.getToplevels().get("Main");
        /* typecheck module */
        TypeChecker tc = new TypeChecker();
        m.visit(tc);
        System.err.println(m);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        m.visit(sal);
        Abstraction abs = (Abstraction) m.lookup("fac");
        assertTrue(abs.isStrict(0));
    }

    public void testPropagate() {
        Module m = new Module("Main", null);
        Type f1 =
                Types.fun(
                        Primitives.INT,
                        Types.fun(
                                Primitives.INT,
                                Types.fun(
                                        Primitives.INT,
                                        Primitives.INT)));
        // definition de g	
        Abstraction a1 = new Abstraction();
        a1.setType(f1);
        a1.bind(new LocalBinding("x", Primitives.INT));
        a1.bind(new LocalBinding("y", Primitives.INT));
        a1.bind(new LocalBinding("z", Primitives.INT));
        //  g y x ...
        Application ap1 = new Application();
        QualifiedVariable qv = new QualifiedVariable("g", f1);
        ap1.setFunction(qv);
        ap1.addArgument(new Variable("y", Primitives.INT));
        ap1.addArgument(new Variable("x", Primitives.INT));
        // (z-1)
        Application ap2 = new Application();
        QualifiedVariable qv1 =
                new QualifiedVariable("(-)", Primitives.INT_INT_INT);
        ap2.setFunction(qv1);
        ap2.addArgument(new Variable("z", Primitives.INT));
        ap2.addArgument(new Variable("1", Primitives.INT));
        ap1.addArgument(ap2);
        // if (z == 0) then x else <ap1>
        Alternative if1 = new Alternative();
        if1.setBinding(new LocalBinding("t", Primitives.INT));
        if1.setExpression(new Variable("z", Primitives.INT));
        if1.addPattern(
                new IntegerLiteral(0),
                new Variable("x", Primitives.INT));
        if1.setWildcard(ap1);
        a1.setBody(if1);
        // definition de f
        m.bind("safe_div", a1);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        m.visit(sal);
        assertTrue(a1.isStrict(0));
        assertTrue(!a1.isStrict(1));
        assertTrue(a1.isStrict(2));
    }

    public void testPartialFunctionApplication() {
        Module m = new Module("Main", null);
        // definitions des Types
        Type f1 = Types.fun(Primitives.INT, Primitives.INT);
        // definition de g	
        Abstraction a1 = new Abstraction();
        a1.setType(f1);
        a1.bind(new LocalBinding("x", Primitives.INT));
        Application ap2 = new Application();
        Application ap1 = new Application();
        QualifiedVariable qv =
                new QualifiedVariable("(+)", Primitives.INT_INT_INT);
        qv.addPathElement("Prelude");
        ap1.setFunction(qv);
        ap1.addArgument(new IntegerLiteral(1));
        ap2.setFunction(ap1);
        ap2.addArgument(new Variable("x", Primitives.INT));
        a1.setBody(ap2);
        Application ap5 = new Application();
        QualifiedVariable qv1 = new QualifiedVariable("primPutInt");
        qv1.addPathElement("Prelude");
        ap5.setFunction(qv1);
        ap5.addArgument(new Variable("plus1", f1));
        ap5.addArgument(new IntegerLiteral(3));
        m.bind("plus1", a1);
        m.bind("main", ap5);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        m.visit(sal);
        assertTrue(!a1.isStrict(0));
    }

    public void testMap() {
        String text =
                "module Main where {map f [] = [] ;" + " map f (x:xs) = f x : map f xs}";
        StringReader sr = new StringReader(text);
        Yyparser p = new Yyparser();
        p.parse(sr);
        Module m = (Module) Module.getToplevels().get("Main");
		/* typecheck module */
        //		TypeChecker tc = new TypeChecker();
        //		m.visit(tc);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        m.visit(sal);
        Abstraction abs = (Abstraction) m.lookup("map");
        assertFalse(abs.isStrict(0));
        System.err.println(m);
    }

    public void testAppend() {
        String text =
                "module Main where {x ++ [] = x;" + " [] ++ x  = x; (x:xs) ++ y = x : (xs ++ y)}";
        StringReader sr = new StringReader(text);
        Yyparser p = new Yyparser();
        p.parse(sr);
        Module m = (Module) Module.getToplevels().get("Main");
		/* typecheck module */
        //		TypeChecker tc = new TypeChecker();
        //		m.visit(tc);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        m.visit(sal);
        Abstraction abs = (Abstraction) m.lookup("(++)");
        assertTrue(abs.isStrict(0));
        assertTrue(abs.isStrict(1));
        System.err.println(m);
    }

}