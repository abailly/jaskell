/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.lang.reflect.Method;

import fr.lifl.jaskell.compiler.bytecode.BytecodeGenerator;
import fr.lifl.jaskell.compiler.core.Primitives;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.runtime.modules.Prelude;
import fr.lifl.jaskell.runtime.types.Closure;

import junit.framework.TestCase;


/**
 * @author  bailly
 * @version $Id: CompilerPassTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class CompilerPassTest extends TestCase {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for PreludeTest.
     *
     * @param arg0
     */
    public CompilerPassTest(String arg0) {
        super(arg0);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void testEncodeName2Java() {
        String hname = "Toto.tutu.(..)$(++)";
        String jname = "Toto/tutu/_2e_2e$_2b_2b";
        String res = BytecodeGenerator.encodeName2Java(hname);
        assertEquals(jname, res);
    }

    public void testEncodeName2Java2() {
        String hname = "(++)";
        String jname = "_2b_2b";
        String res = BytecodeGenerator.encodeName2Java(hname);
        assertEquals(jname, res);
    }

    // simple case
    public void testEncodeFunctionName1() {
        Type t = PrimitiveType.makeFunction(Primitives.INT, Primitives.BOOL);
        String res = BytecodeGenerator.encodeName2Java(t.toString());
        String jname = "Int_2d_3eBool";
        assertEquals("Type " + t, jname, res);
    }

    public void testEncodeFunctionName2() {
        Type t = PrimitiveType.makeFunction(PrimitiveType.makeFunction(Primitives.INT, Primitives.INT), Primitives.BOOL);
        String res = BytecodeGenerator.encodeName2Java(t.toString());
        String jname = "_28Int_2d_3eInt_29_2d_3eBool";
        assertEquals("Type " + t, jname, res);
    }

    public void testEncodeFunctionName3() {
        Type t = PrimitiveType.makeFunction(Primitives.INT, PrimitiveType.makeFunction(PrimitiveType.makeFunction(Primitives.INT, Primitives.INT), Primitives.BOOL));
        String res = BytecodeGenerator.encodeName2Java(t.toString());
        String jname = "Int_2d_3e_28_28Int_2d_3eInt_29_2d_3eBool_29";
        assertEquals("Type " + t, jname, res);
    }

    public void testEncodeType1() {
        Class[] cls1 = new Class[] { int.class };
        Type f = PrimitiveType.makeFunction(Primitives.INT, Primitives.BOOL);
        Class[] cls2 = BytecodeGenerator.encodeType2Java(f);
        assertEquals(2, cls2.length);
        assertEquals(cls1[0], cls2[0]);
    }

    public void testEncodeType2() {
        Class[] cls1 = new Class[] { java.lang.String.class, Closure.class };
        Type f = PrimitiveType.makeFunction(Primitives.STRING, PrimitiveType.makeFunction(PrimitiveType.makeFunction(Primitives.INT, Primitives.BOOL), Primitives.FLOAT));
        Class[] cls2 = BytecodeGenerator.encodeType2Java(f);
        assertEquals(3, cls2.length);
        assertEquals(cls1[0], cls2[0]);
        assertEquals(cls1[1], cls2[1]);
    }

    // resolve addition on integers
    public void testResolve() throws Exception {
        String fname = "(+)";
        Type f = PrimitiveType.makeFunction(Primitives.INT, PrimitiveType.makeFunction(Primitives.INT, Primitives.INT));
        Method m = CompilerPass.resolvePrimitive(fname, f);
        Method expect = Prelude.class.getMethod("_2b", new Class[] { int.class, int.class });
        assertEquals(expect, m);
    }

}
