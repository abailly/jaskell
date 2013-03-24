/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler.bytecode;

import java.util.HashMap;
import java.util.Map;

import fr.lifl.jaskell.compiler.core.PrimitiveFunction;
import fr.lifl.jaskell.compiler.core.Primitives;

import oqube.bytes.ClassFile;
import oqube.bytes.TypeHelper;
import oqube.bytes.instructions.Instruction;
import oqube.bytes.instructions.Sequence;
import oqube.bytes.pool.ClassData;
import oqube.bytes.pool.MethodRefData;


/*
 * root interface for primitive code registered in code generator
 */
interface PrimitiveCode {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    Instruction emit(ClassFile cf);
}

/*
 * a class for primitive code which is independent of class file
 * in whihc it is located
 */
class AbsolutePrimitiveCode implements PrimitiveCode {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* instruction sequence */
    Instruction inst;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    AbsolutePrimitiveCode(Instruction inst) {
        this.inst = inst;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Instruction emit(ClassFile cf) {
        return (Instruction) inst.clone(cf);
    }
}

/*
 * a class for primitive code which is dependent of class file
 * in whihc it is located
 */
abstract class RelativePrimitiveCode implements PrimitiveCode {
}

/*
 * a class for primitive code which is dependent of the classfile
 * in which it is emitted (e.g. calls to primitive methods defined in
 * Prelude)
 */
class StaticMethodPrimitiveCode implements PrimitiveCode {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* class name */
    String clname;
    /* method name */
    String mname;
    /* method signature */
    String msig;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    StaticMethodPrimitiveCode(String clname, String mname, String msig) {
        this.clname = clname;
        this.mname = mname;
        this.msig = msig;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Instruction emit(ClassFile cf) {
        short mref = MethodRefData.create(cf.getConstantPool(), clname, mname, msig);
        return new Sequence(cf)._invokestatic(mref, cf);
    }
}

/**
 * @author  abailly
 * @version $Id: PrimitivesCodeGenerator.java 1207 2006-05-26 08:55:33Z nono $
 */
public class PrimitivesCodeGenerator {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* the mapping from primitive objects to code */
    private static Map primitives = new HashMap();

    /**
     * This code initializes the primitives map
     */
    static {
        primitives.put(Primitives.ADD_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._iadd()));
        primitives.put(Primitives.MUL_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._imul()));
        primitives.put(Primitives.SUB_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._isub()));
        primitives.put(Primitives.EQ_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._if_icmpeq(7)._iconst_0()._goto(4)._iconst_1()));
        primitives.put(Primitives.LE_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._if_icmple(7)._iconst_0()._goto(4)._iconst_1()));
        primitives.put(Primitives.GE_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._if_icmpge(7)._iconst_0()._goto(4)._iconst_1()));
        primitives.put(Primitives.LT_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._if_icmplt(7)._iconst_0()._goto(4)._iconst_1()));
        primitives.put(Primitives.GT_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._if_icmpgt(7)._iconst_0()._goto(4)._iconst_1()));
        primitives.put(Primitives.MAX_INT_INT, new AbsolutePrimitiveCode(new Sequence(null)._dup2()._if_icmpgt(4)._swap()._pop()));
        primitives.put(Primitives.NEG_INT, new AbsolutePrimitiveCode(new Sequence(null)._ineg()));
        primitives.put(Primitives.XOR, new AbsolutePrimitiveCode(new Sequence(null)._ixor()));

        primitives.put(Primitives.ADD_FLOAT_FLOAT, new AbsolutePrimitiveCode(new Sequence(null)._fadd()));

        primitives.put(Primitives.EQ_FLOAT_FLOAT, new AbsolutePrimitiveCode( /* perform xor on result of fp commparison
                                                                              */new Sequence(null)._fcmpg()._ixor()));

        primitives.put(Primitives.GT_FLOAT_FLOAT, new AbsolutePrimitiveCode(new Sequence(null)._fcmpg()));
        primitives.put(Primitives.LT_FLOAT_FLOAT, new AbsolutePrimitiveCode(new Sequence(null)._swap()._fcmpg()));

        primitives.put(Primitives.GE_FLOAT_FLOAT, new AbsolutePrimitiveCode(new Sequence(null)._fcmpg()._iconst_m1()._if_icmpeq(7)._iconst_1()._goto(4)._iconst_0()));
        primitives.put(Primitives.LE_FLOAT_FLOAT, new AbsolutePrimitiveCode(new Sequence(null)._fcmpg()._iconst_1()._if_icmpeq(7)._iconst_1()._goto(4)._iconst_0()));

        primitives.put(Primitives.NOT, new AbsolutePrimitiveCode(new Sequence(null)._ifeq(7)._iconst_1()._goto(4)._iconst_0()));

        /* generate error code */
        RelativePrimitiveCode error = new RelativePrimitiveCode() {
            public Instruction emit(ClassFile cf) {
                Sequence seq = new Sequence(cf);
                short cref = ClassData.create(cf.getConstantPool(), "fr/lifl/jaskell/runtime/types/JError");
                short mref = MethodRefData.create(cf.getConstantPool(), "fr/lifl/jaskell/runtime/types/JError", "<init>", "(Ljava/lang/String;)V");
                seq._new(cref)._dup_x1()._swap()._invokespecial(mref, cf)._athrow();
                return seq;
            }
        };

        PrimitiveFunction pf = Primitives.ERROR;
        primitives.put(pf, error);

        //              primitives.put(
        //                                      Primitives.NIL,
        //                                      new StaticMethodPrimitiveCode(
        //                                      "fr/lifl/jaskell//runtime/types/JList",
        //                                              "_nil",
        //                                              "()Ljaskell/runtime/types/JList;"));
        //              primitives.put(
        //                                      Primitives.CONS,
        //                                      new StaticMethodPrimitiveCode(
        //                                      "fr/lifl/jaskell//runtime/types/JList",
        //                                              "_cons",
        //                                              "(Ljaskell/runtime/types/JObject;Ljaskell/runtime/types/JList;)Ljaskell/runtime/types/JList;"));
        //
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Method getCode. Returns code sequence associated with given primitive function. The function must have been
     * registered before using <code>registerPrimitiveCode()</code>.
     *
     * @param  pf
     * @param  cf classFile where code is emitted
     *
     * @return Instruction
     */
    public static Instruction emitCode(PrimitiveFunction pf, ClassFile cf) {
        PrimitiveCode pc = (PrimitiveCode) primitives.get(pf);
        return pc.emit(cf);
    }

    /**
     * This method is called during the parsing process to register Native function definitions implemented as static
     * methods in a Java class. This method handles the registration process so that the right function is called with
     * the right arguments.
     *
     * @param pf a PrimitiveFunction object whose implementing class is not null
     */
    public static void registerStaticPrimitive(PrimitiveFunction pf) {
        Class cls = pf.getKlass();
        /* sanity check */
        if (cls == null)
            return;
        /* create static call */
        String clname = TypeHelper.getExternalName(cls).replace('.', '/');
        String fname = BytecodeGenerator.encodeName2Java(pf.getName());
        Class[] args = BytecodeGenerator.encodeType2Java(pf.getType());
        StringBuffer sig = new StringBuffer("(");
        for (int i = 0; i < (args.length - 1); i++)
            sig.append(TypeHelper.getInternalName(args[i]));
        sig.append(')').append(TypeHelper.getInternalName(args[args.length - 1]));
        primitives.put(pf, new StaticMethodPrimitiveCode(clname, fname, sig.toString()));
    }

    /**
     * This method registers a PrimitiveFunction object as generating bytecode which is inserted as is during code
     * generation process.
     *
     * @param pf
     * @param inst
     */
    public static void registerBytecodePrimitive(PrimitiveFunction pf, Instruction inst) {
        primitives.put(pf, new AbsolutePrimitiveCode(inst));
    }
}
