/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.lang.reflect.Method;

import fr.lifl.jaskell.compiler.bytecode.BytecodeGenerator;
import fr.lifl.jaskell.compiler.bytecode.ClassFileWriter;
import fr.lifl.jaskell.compiler.bytecode.CodeGenerator;
import fr.lifl.jaskell.compiler.bytecode.Type2Class;
import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Conditional;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.Primitives;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.Variable;
import fr.lifl.jaskell.compiler.types.Type;

import junit.framework.TestCase;

import oqube.bytes.loading.ClassFileLoader;


/**
 * @author  bailly
 * @version $Id: CompilerTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class CompilerTest extends TestCase {

    static {
        BytecodeGenerator.setLoader(new ClassFileLoader(ClassLoader.getSystemClassLoader()));
    }


    /**
     * Constructor for CompilerTest.
     *
     * @param arg0
     */
    public CompilerTest(String arg0) {
        super(arg0);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void testFac() throws Exception {
        Module m = new Module("Main", null);
        Type t = Primitives.INT; /* for loading primitives */
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
        m.visit(tc);
        m.visit(sal);
        m.visit(gen);
        ClassFileLoader writer = (ClassFileLoader) BytecodeGenerator.getLoader();
        BytecodeGenerator.cleanupClassFiles(writer);
        /* resolve Main class and call myMax */
        Class cls = writer.loadClass("Main$Module");
        assertNotNull(cls);
        Method met = cls.getMethod("fac", null);
        assertNotNull(met);
        Object obj = met.invoke(null, null);
        Class<?> facClass = obj.getClass();
        met = facClass.getMethod("eval", new Class[]{int.class});
        obj = met.invoke(obj, new Object[] { new Integer(4) });
        assertEquals(24, ((Integer) obj).intValue());
    }

}
