package fr.lifl.jaskell.compiler.bytecode;

import java.io.StringReader;

import fr.lifl.jaskell.compiler.LambdaLifter;
import fr.lifl.jaskell.compiler.StrictnessAnalyzer;
import fr.lifl.jaskell.compiler.TypeChecker;
import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.parser.Yyparser;

import junit.framework.TestCase;

import oqube.bytes.loading.ClassFileLoader;


public class CodeGeneratorTest extends TestCase {

    static {
        BytecodeGenerator.setLoader(new ClassFileLoader(ClassLoader.getSystemClassLoader()));
    }

    public CodeGeneratorTest(String arg) {
        super(arg);
    }

    public void testAbstraction() {
        Module m = new Module("Test", null);
        Abstraction abs = new Abstraction();
        abs.bind(new LocalBinding("x", Primitives.INT));
        abs.bind(new LocalBinding("y", Primitives.BOOL));
        abs.setBody(new StringLiteral("toto"));
        m.bind("consttoto", abs);
        CodeGenerator gen = new CodeGenerator();
        m.visit(gen);
        ClassFileWriter writer = new ClassFileWriter("bin");
        BytecodeGenerator.cleanupClassFiles(writer);
        Type2Class.cleanupClassFiles(writer);
    }

    public void testApplication() {
        Module m = new Module("Main", null);
        // definitions des Types
        Type f1 = PrimitiveType.makeFunction(Primitives.INT, Primitives.INT);
        Type f2 = PrimitiveType.makeFunction(f1, f1);
        // definition de g
        Abstraction a1 = new Abstraction();
        a1.setType(Primitives.INT_INT_INT);
        a1.bind(new LocalBinding("x", Primitives.INT));
        a1.bind(new LocalBinding("y", Primitives.INT));
        Application ap1 = new Application();
        QualifiedVariable qv = new QualifiedVariable("(+)", Primitives.INT_INT_INT);
        qv.addPathElement("Prelude");
        ap1.setFunction(qv);
        ap1.addArgument(new Variable("x", Primitives.INT));
        ap1.addArgument(new Variable("y", Primitives.INT));
        a1.setBody(ap1);
        // definition de f
        Abstraction a2 = new Abstraction();
        a2.setType(f2);
        a2.bind(new LocalBinding("g", f1));
        a2.bind(new LocalBinding("x", Primitives.INT));
        Application ap3 = new Application();
        ap3.setFunction(qv);
        ap3.addArgument(new Variable("x", Primitives.INT));
        ap3.addArgument(new IntegerLiteral(1));
        a2.setBody(ap3);
        // definition de main
        Application ap6 = new Application();
        ap6.setFunction(new Variable("g", Primitives.INT_INT_INT));
        ap6.addArgument(new IntegerLiteral(1));

        Application ap4 = new Application();
        ap4.setFunction(new Variable("f", f2));
        ap4.addArgument(ap6);
        Application ap7 = new Application();
        ap7.setFunction(ap4);
        ap7.addArgument(new IntegerLiteral(2));
        Application ap5 = new Application();
        QualifiedVariable qv1 = new QualifiedVariable("primPutInt");
        qv1.addPathElement("Prelude");
        ap5.setFunction(qv1);
        ap5.addArgument(ap7);
        m.bind("f", a2);
        m.bind("g", a1);
        m.bind("main", ap5);
        TypeChecker tc = new TypeChecker();
        CodeGenerator gen = new CodeGenerator();
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        m.visit(tc);
        m.visit(sal);
        m.visit(gen);
        ClassFileWriter writer = new ClassFileWriter("bin");
        BytecodeGenerator.cleanupClassFiles(writer);
        Type2Class.cleanupClassFiles(writer);
    }

    //      public void testPartialFunctionApplication() {
    //              Module m = new Module("Main",null);
    //              // definitions des Types
    //              FunctionType f1 = new FunctionType(Primitives.INT, Primitives.INT);
    //              // definition de g
    //              Abstraction a1 = new Abstraction();
    //              a1.setType(f1);
    //              a1.bind(new LocalBinding("x", Primitives.INT));
    //              Application ap2 = new Application();
    //              Application ap1 = new Application();
    //              QualifiedVariable qv = new QualifiedVariable("(+)",Primitives.INT_INT_INT);
    //              qv.addPathElement("Prelude");
    //              ap1.setFunction(qv);
    //              ap1.addArgument(new IntegerLiteral(1));
    //              ap2.setFunction(ap1);
    //              ap2.addArgument(new Variable("x",Primitives.INT));
    //              a1.setBody(ap2);
    //              Application ap5 = new Application();
    //              QualifiedVariable qv1 = new QualifiedVariable("primPutInt",new FunctionType(Primitives.INT,Primitives.IO));
    //              qv1.addPathElement("Prelude");
    //              ap5.setFunction (qv1);
    //              Application ap6 = new Application();
    //              ap6.setFunction(new Variable("plus1",f1));
    //              ap6.addArgument(new IntegerLiteral(1));
    //              ap5.addArgument(ap6);
    //              m.bind("plus1",  a1);
    //              m.bind("main", ap5);
    //              CodeGenerator gen = new CodeGenerator();
    //              StrictnessAnalyzer sal = new StrictnessAnalyzer();
    //              m.visit(sal);
    //              m.visit(gen);
    //              ClassFileWriter writer = new ClassFileWriter("test");
    //              gen.cleanupClassFiles(writer);
    //              Type2ClassVisitor.cleanupClassFiles(writer);
    //      }

    public void testFac() {
        Module m = new Module("Main", null);
        Type f1 = PrimitiveType.makeFunction(Primitives.INT, Primitives.INT);
        // definition de g
        Abstraction a1 = new Abstraction();
        a1.bind(new LocalBinding("x"));

        Application ap0 = new Application();
        QualifiedVariable qv = new QualifiedVariable("(*)");
        qv.addPathElement("Prelude");
        ap0.setFunction(qv);
        ap0.addArgument(new Variable("x"));

        Application ap1 = new Application();
        Variable v = new Variable("fac");
        ap1.setFunction(v);

        Application ap2 = new Application();
        qv = new QualifiedVariable("(-)");
        qv.addPathElement("Prelude");
        ap2.setFunction(qv);
        ap2.addArgument(new Variable("x"));
        ap2.addArgument(new IntegerLiteral(1));

        ap1.addArgument(ap2);
        ap0.addArgument(ap1);

        Application ap3 = new Application();
        qv = new QualifiedVariable("(==)");
        qv.addPathElement("Prelude");
        ap3.setFunction(qv);
        ap3.addArgument(new Variable("x"));
        ap3.addArgument(new IntegerLiteral(0));

        Conditional if1 = new Conditional();
        if1.setCondition(ap3);
        if1.setIfTrue(new IntegerLiteral(1));
        if1.setIfFalse(ap0);
        a1.setBody(if1);
        // definition de f
        m.bind("fac", a1);
        Application ap4 = new Application();
        ap4.setFunction(new Variable("fac"));
        ap4.addArgument(new IntegerLiteral(5));
        Application ap5 = new Application();
        QualifiedVariable qv1 = new QualifiedVariable("primPutInt");
        qv1.addPathElement("Prelude");
        ap5.setFunction(qv1);
        ap5.addArgument(ap4);
        m.bind("main", ap5);

        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        TypeChecker tc = new TypeChecker();
        CodeGenerator gen = new CodeGenerator();
        m.visit(sal);
        m.visit(tc);
        assertEquals(Primitives.BOOL, ap3.getType());
        m.visit(gen);
        ClassFileWriter writer = new ClassFileWriter("bin");
        BytecodeGenerator.cleanupClassFiles(writer);
        Type2Class.cleanupClassFiles(writer);
    }

    //      public void testMax() throws Exception {
    //              Module m = new Module("Main", null);
    //              // definition de g
    //              Abstraction a1 = new Abstraction();
    //              a1.setType(Primitives.INT_INT_INT);
    //              a1.bind(new LocalBinding("x", Primitives.INT));
    //              a1.bind(new LocalBinding("y", Primitives.INT));
    //
    //              Application ap0 = new Application();
    //              QualifiedVariable qv =
    //                      new QualifiedVariable("max", Primitives.INT_INT_INT);
    //              qv.addPathElement("Prelude");
    //              ap0.setFunction(qv);
    //              ap0.addArgument(new Variable("x", Primitives.INT));
    //              ap0.addArgument(new Variable("y", Primitives.INT));
    //              a1.setBody(ap0);
    //              // definition de f
    //              m.bind("myMax", a1);
    //              Application ap4 = new Application();
    //              ap4.setFunction(new Variable("myMax", Primitives.INT_INT_INT));
    //              ap4.addArgument(new IntegerLiteral(5));
    //              ap4.addArgument(new IntegerLiteral(6));
    //              Application ap5 = new Application();
    //              QualifiedVariable qv1 =
    //                      new QualifiedVariable(
    //                              "primPutInt");
    //              qv1.addPathElement("Prelude");
    //              ap5.setFunction(qv1);
    //              ap5.addArgument(ap4);
    //              m.bind("main", ap5);
    //              TypeChecker tc = new TypeChecker();
    //              StrictnessAnalyzer sal = new StrictnessAnalyzer();
    //              CodeGenerator gen = new CodeGenerator();
    //              m.visit(tc);
    //              sal.visit(m);
    //              m.visit(gen);
    //              ClassFileLoader writer = new ClassFileLoader(BytecodeGenerator.getLoader());
    //              gen.cleanupClassFiles(writer);
    //              Type2Class.cleanupClassFiles(writer);
    //              /* resolve Main class and call myMax */
    //              Class cls = writer.loadClass("Main");
    //              assertNotNull(cls);
    //              Method met = cls.getMethod("myMax", null);
    //              assertNotNull(met);
    //              Object obj = met.invoke(null, null);
    //              met =
    //                      obj.getClass().getMethod(
    //                              "eval",
    //                              new Class[] { int.class, int.class });
    //              obj = met.invoke(obj, new Object[] { new Integer(4), new Integer(5)});
    //              assertEquals(5, ((Integer) obj).intValue());
    //      }

    public void testRecursiveLet() {
        String text = "{f x  = let { even 0 = true; even x = odd (x  - 1); odd 0  =false; odd x = even (x - 1) } in odd x; main = primPutBool (f 11)}";
        StringReader sr = new StringReader(text);
        Yyparser p = new Yyparser();
        p.parse(sr);
        Module m = (Module) Module.getToplevels().get("Main");
        System.out.println(m);
        /* typecheck module */
        LambdaLifter ll = new LambdaLifter(m);
        m.visit(ll);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        TypeChecker tc = new TypeChecker();
        CodeGenerator gen = new CodeGenerator();
        System.out.println(m);
        m.visit(sal);
        m.visit(tc);
        m.visit(gen);
        ClassFileWriter writer = new ClassFileWriter("bin");
        BytecodeGenerator.cleanupClassFiles(writer);
        Type2Class.cleanupClassFiles(writer);
        System.out.println(m);
    }

    public void testAppend() {
        String text = "module MyList where {" +
            "{-# NATIVE data ([]) a \"fr.lifl.jaskell.runtime.types.JList\" #-};" +
            "{-# NATIVE ([]) = (:) a [a] \"fr.lifl.jaskell.runtime.types._3a\" #-};" +
            "{-# NATIVE ([]) = ([]) \"fr.lifl.jaskell.runtime.types._5b_5d\" #-};" +
            "addHead x [] = [x]; addHead x xs = x:xs}";
        StringReader sr = new StringReader(text);
        Yyparser p = new Yyparser();
        p.parse(sr);
        Module m = (Module) Module.getToplevels().get("MyList");
        /* typecheck module */
        LambdaLifter ll = new LambdaLifter(m);
        m.visit(ll);
        StrictnessAnalyzer sal = new StrictnessAnalyzer();
        TypeChecker tc = new TypeChecker();
        CodeGenerator gen = new CodeGenerator();
        m.visit(sal);
        m.visit(tc);
        m.visit(gen);
        ClassFileWriter writer = new ClassFileWriter("bin");
        BytecodeGenerator.cleanupClassFiles(writer);
        System.out.println(m);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
