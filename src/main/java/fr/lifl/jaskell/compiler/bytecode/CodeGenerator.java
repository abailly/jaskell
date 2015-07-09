/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.bytecode;

import java.lang.reflect.Field;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.lifl.jaskell.compiler.CompilerException;
import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.TypeApplication;
import fr.lifl.jaskell.runtime.types.JObject;

import oqube.bytes.*;
import oqube.bytes.attributes.*;
import oqube.bytes.instructions.*;
import oqube.bytes.pool.*;
import oqube.bytes.struct.ClassFileInfo;
import oqube.bytes.struct.MethodFileInfo;


/**
 * A class for generating class files from Jaskell code.
 *
 * @author  bailly
 * @version $Id: CodeGenerator.java 1207 2006-05-26 08:55:33Z nono $
 */
public class CodeGenerator extends BytecodeGenerator {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static Logger log = Logger.getLogger(CodeGenerator.class.getName());

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /** current class file being generated */
    private ClassFile classFile;

    /** current ns being visited */
    private Module ns;

    /** current prefix for generated classes */
    private String nsname;

    /** current closure */
    private Abstraction closure;

    /** counter for name generator */
    private int counter = 0;

    /** current strictness of application evaluation */
    private boolean strictness = false;

    /** saved stack of strictness status */
    private Stack savedStrictness = new Stack();

    /* name of currently analyzed supercombinator */
    private String functionName;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public CodeGenerator() {
    }

    /**
     * This constructor is used to generate code in a context different from a Module which is the standard case.
     *
     * @param cf the root classfile to use for storing static methods
     * @param ns the Root namespace for code generated in this generator
     */
    public CodeGenerator(ClassFile cf, Module ns, String fname) {
        this.classFile = cf;
        this.ns = ns;
        this.functionName = fname;
        Logger.getLogger(CodeGenerator.class.getName()).setLevel(Level.FINEST);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * An abstraction compiles to a new Class in an enclosing context and returns a code sequence corresponding to
     * instanciation of this class. If the <code>innerClass</code> flag is set,
     *
     * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
     */
    public Object visit(Abstraction a) {
        // update closure state
        closure = a;
        //create a new ClassFile to store code
        ClassFile lambda = new ClassFile();
        String fullname = BytecodeGenerator.encodeName2Java(ns.getName() + "/" + functionName);
        nsname = fullname;
        a.setClassName(fullname);
        Type type = a.getType();
        /*
         * save classfile context
         */
        ClassFile save = classFile;
        classFile = lambda;
        /* add base eval method - arguments are taken from the arguments */
        Type range = makeClosure(lambda, a, fullname, type);
        lambda.add(makeEval0Method(lambda, a, type));
        /* eval method encapsulates code corresponding to body of abstraction */
        lambda.add(makeUnboxedEvalMethod(lambda, a, range));
        classFile = save;
        /* store classfile */
        generateClass(fullname, lambda);
        /* generate call sequence for abstraction = create new abstraction object */
        if (classFile != null) {
            Sequence seq = new Sequence(classFile);
            short lambdaindex = ClassData.create(classFile.getConstantPool(), fullname);
            short ctorindex = MethodRefData.create(classFile.getConstantPool(), fullname, "<init>", "()V");
            seq._new(lambdaindex)._dup()._invokespecial(ctorindex, classFile);
            closure = null;
            return seq;
        } else
            /* assuem we don't care about storing a call
             * to create an instance of this abstraction
             */
            return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
     */
    public Object visit(Alternative a) {
        PatternCodeGenerator pcg = new PatternCodeGenerator(this, classFile);
        Sequence seq = new Sequence(classFile);
        /* if binding is not null, store it */
        LocalBinding lb = a.getBinding();
        if (lb == null) {
            lb = LocalBinding.freshBinding();
            a.setBinding(lb);
            lb.setType(a.getExpression().getType());
        }
        /* reindex alternative bindings */
        if (closure != null)
            reindex(a, closure);
        /* first evaluate expression */
        pushStrict(true);
        Expression expr = a.getExpression();
        seq.add((Instruction) expr.visit(this));
        int index = lb.getIndex();
        seq.add(generateStore(index, lb));
        popStrict();
        // generate code for each alternative branch
        Iterator it = a.getChoices();
        List tests = new ArrayList(); /* to store various branches */
        List res = new ArrayList(); /* to store bodies code */
        int len = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Pattern p = (Pattern) entry.getKey();
            Expression e = (Expression) entry.getValue();
            /*
             * generate code for expressions
             * we add a type checking at end
             */
            Instruction i = (Instruction) e.visit(this);
            //                      if(e instanceof Variable) {
            //                              try {
            //                                      lb = (LocalBinding)e.resolve(((Variable)e).getName());
            //                                      if(!lb.isStrict()) {
            //                                              Type2Class tc = new  Type2Class(ns);
            //                                              Type2Class.unbox(classFile,(Class)lb.getType().visit(tc));
            //                                      }
            //                              }catch(ClassCastException ccex) {
            //                                      /* nothing to do */
            //                              }
            //                      }

            len += i.size() + 3; /* unconditional goto to end of alternative */
            res.add(i);
            /*
             * generate code for tests
             * this code must be patched to resolve jump targets
             */
            i = (Instruction) p.visit(pcg);
            tests.add(i);
            len += i.size();
        }
        /* evaluate wildcard */
        pushStrict(true);
        Instruction wc = (Instruction) a.getWildcard().visit(this);
        popStrict();
        return seq.add((Instruction) makeStrictCase(len, tests, res, wc));
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Application)
     */
    public Object visit(Application a) {
        // generated sequence of instructions
        Type2Class tv = new Type2Class();
        /* retrieve function */
        Expression fun = a.getFunction();
        TypeApplication funtype = null;
        try {
            funtype = (TypeApplication) fun.getType();
        } catch (ClassCastException ccex) {
            // functional argument is not a function
            throw new CompilerException("Type of first member of application [" +
                fun +
                "] is not a function " + funtype);
        }
        if (funtype == null)
            throw new CompilerException("Type of function " +
                fun +
                " in application " + a +
                " is null");
        /* start code with evaluation of function */
        Sequence funseq = new Sequence(classFile);
        funseq.add((Instruction) fun.visit(this));
        /*
         * whatever the case, if number of argumetns is not enough
         * generate partial call
         */
        if ((a.getArgs().size() < PrimitiveType.getArgsCount(funtype)))
            return makePartialApplication(a, funseq, funtype);
        /*
         * if strictness is off, we just create an application spine
         */
        if (!strictness)
            return makePartialApplication(a, funseq, funtype);
        /*
         * if there are enough arguments, try to optimize
         * arguments evaluation using strictness information on
         * function
         */
        if (fun instanceof Variable) { /* either variable or Constructor */
            /* get definition of function */
            Variable v = (Variable) fun;
            Expression e = v.lookup(v.getName());
            Binder abs = null;
            try {
                abs = (Binder) e;
            } catch (ClassCastException e1) {
                /*
                 * we did not found a Binder but definition.
                 * we should generate a call to a virtual function, but which function ?
                 */
                return makeStrictCompleteApplicationSpine(a, funseq, funtype);
            }
            log.finer("Making strict complete application for function " +
                v +
                "(" + abs +
                ")");
            return makeStrictCompleteApplication(a, abs, e.getType());
        } else {
            /*
             * dont know anything about function so generate code in non strict environnement
             */
            //            pushStrict(false);
            //            Iterator it = a.getArgs().iterator();
            //            while (it.hasNext()) {
            //                Expression arg = (Expression) it.next();
            //                funseq.add((Instruction) arg.visit(this));
            //            }
            //            popStrict();
            return makeStrictCompleteApplicationSpine(a, funseq, funtype);
        }
    }

    public Object visit(BooleanLiteral a) {
        // generate code
        Instruction inst = null;
        if (a.getBoolean())
            inst = new ZeroArgInst(Opcodes.opc_iconst_1, classFile);
        else
            inst = new ZeroArgInst(Opcodes.opc_iconst_0, classFile);
        // box value if not in strict context
        if (!strictness) {
            Sequence seq = new Sequence(classFile);
            seq.add(inst);
            seq.add(Type2Class.box(classFile, boolean.class));
            return seq;
        } else
            return inst;
    }

    public Object visit(CharLiteral a) {
        Instruction inst = generateConstantInt(a.getChar());
        // box value if not in strict context
        if (!strictness) {
            Sequence seq = new Sequence(classFile);
            seq.add(inst);
            seq.add(Type2Class.box(classFile, char.class));
            return seq;
        } else
            return inst;
    }

    /**
     * A Constructor is visited if it is an application
     *
     * @see jaskell.compiler.JaskellVisitor#visit(Constructor)
     */
    public Object visit(Constructor a) {
        String vname = a.getName();
        // generate invokestatic code on right module
        ConstructorDefinition def = (ConstructorDefinition) ns.lookup(vname);
        if (def == null) // unknown symbol
            throw new CompilerException("Unknown constructor " + vname);
        return def.visit(this);
    }

    public Object visit(ConstructorDefinition def) {
        Sequence seq = new Sequence(classFile);
        /* retrieve type definition for constructor */
        DataDefinition ddef = (DataDefinition) def.getParent();
        // create method data
        log.finest("Generating code for constructor " + def);
        Type2Class tv = new Type2Class(ns);
        String clname = ((Class) def.visit(tv)).getName().replace('.', '/');
        if (def.getParameters().size() > 0) { /* index of ctor class */
            short cref = ClassData.create(classFile.getConstantPool(), clname);
            /* index of <init> */
            short nidx = MethodRefData.create(classFile.getConstantPool(), clname, "<init>", "()V");
            seq._new(cref)._dup()._invokespecial(nidx, classFile);
            return seq;
        } else {
            String dname = BytecodeGenerator.encodeName2Java(((Class) ddef.visit(tv)).getName().replace('.', '/'));
            /* index of <instance field> */
            short fidx = FieldRefData.create(classFile.getConstantPool(), clname, "_instance", "L" + dname + ";");
            seq._getstatic(fidx, classFile);
            return seq;

        }
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Definition)
     */
    public Object visit(Definition a) {
        log.finest("Visiting definition for " + a.getName());
        Expression expr = a.getDefinition();
        return expr.visit(this);
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(DoubleLiteral)
     */
    public Object visit(DoubleLiteral a) {

        Instruction inst = null;
        double f = a.getDouble();
        if (f == 0)
            inst = new ZeroArgInst(Opcodes.opc_dconst_0, classFile);
        else if (f == 1.0)
            inst = new ZeroArgInst(Opcodes.opc_dconst_1, classFile);
        else { // store constant in cpool
            // store string in constant pool
            short idx = DoubleData.create(classFile.getConstantPool(), f);
            // generate code
            inst = new TwoArgInst(Opcodes.opc_ldc2_w, classFile, idx);
        }
        if (!strictness) {
            Sequence seq = new Sequence(classFile);
            seq.add(inst);
            seq.add(Type2Class.box(classFile, double.class));
            return seq;
        } else
            return inst;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(FloatLiteral)
     */
    public Object visit(FloatLiteral a) {
        Instruction inst = generateConstantFloat(a.getFloat(), classFile);
        if (!strictness) {
            Sequence seq = new Sequence(classFile);
            seq.add(inst);
            seq.add(Type2Class.box(classFile, float.class));
            return seq;
        } else
            return inst;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(IntegerLiteral)
     */
    public Object visit(IntegerLiteral a) {
        Instruction inst = generateConstantInt(a.getInteger());
        if (!strictness) {
            Sequence seq = new Sequence(classFile);
            seq.add(inst);
            seq.add(Type2Class.box(classFile, int.class));
            return seq;
        } else
            return inst;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Module)
     */
    public Object visit(Module a) {
        log.finer("Start visiting namespace " + a.getName());
        String clname = encodeName2Java(a.getName().replace('.', '/') + "$Module");
        // set current namespace to module
        ns = a;
        nsname = clname;
        classFile = new ClassFile();
        ClassFileInfo lcfi = new ClassFileInfo(classFile, clname);
        // set parent class
        lcfi.setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_ABSTRACT));
        classFile.setClassFileInfo(lcfi);
        // visit definitions
        Iterator it = a.getBindings().entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry entry = (Map.Entry) it.next();
                functionName = (String) entry.getKey();
                Expression def = (Expression) entry.getValue();
                /* nested module */
                if (def instanceof Module) {
                    def.visit(new CodeGenerator());
                    continue;
                }
                /* skip constructor definitions as they are normally
                 *      taken care of in data definitions
                 */
                if (def instanceof ConstructorDefinition) {
                    Type2Class t2c = new Type2Class(a);
                    t2c.visit((ConstructorDefinition) def);
                    continue;
                }
                log.info("In " +
                    a.getName() +
                    " , Start visiting symbol " + functionName);
                /* special case of main method in Main module */
                if (functionName.equals("main") && a.getName().equals("Main")) {
                    pushStrict(true);
                    Instruction code = (Instruction) def.visit(this);
                    popStrict();
                    classFile.add(makeMain(code));
                } else {
                    /* define a new static method in module */
                    pushStrict(true);
                    Instruction code = (Instruction) def.visit(this);
                    popStrict();
                    if (code == null) // submodules
                        continue;
                    classFile.add(makeMethod(functionName, code, def.getType()));
                }
            } catch (CompilerException cex) {
                cex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            log.info("In " +
                a.getName() +
                " , Stop visiting symbol " + functionName);
        }
        // visit data definitions
        //              it = a.getTypeDefinitions().iterator();
        //              while (it.hasNext())
        //                       ((Expression) it.next()).visit(this);
        // store class file
        generateClass(clname, classFile);
        // return compiled class
        log.finer("Stop visiting namespace " + a.getName());
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(StringLiteral)
     */
    public Object visit(StringLiteral a) {
        // store string in constant pool
        short idx = StringData.create(classFile.getConstantPool(), a.getString());
        // generate code
        Instruction inst = null;
        if (idx < 256)
            inst = new OneArgInst(Opcodes.opc_ldc, classFile, (byte) idx);
        else
            inst = new TwoArgInst(Opcodes.opc_ldc_w, classFile, idx);
        if (!strictness) {
            Sequence seq = new Sequence(classFile);
            seq.add(inst);
            seq.add(Type2Class.box(classFile, String.class));
            return seq;
        } else
            return inst;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Variable)
     */
    public Object visit(Variable a) {
        String vname = a.getName();
        LocalBinding bind = null;
        /*
         * first, we try to find variable a in local variables. If it is not
         * found, a deepLookup is thrown in current module
         */
        try { // try local variable
            bind = (LocalBinding) a.lookup(vname);
            return generateLoad(bind);
        } catch (NullPointerException ex) { // global binding
            //intentionally left blank
        } catch (ClassCastException ccex) {
        }
        // either closure is null or name not found in closure
        // try to resolve it through module
        if (ns != null) {
            Sequence seq = new Sequence(classFile);
            // generate invokestatic code on right module
            Expression def = ns.lookup(vname);
            if (def == null) // unknown symbol
                throw new CompilerException("Unknown variable " + vname);
            // create method data
            log.finest("Generating code for variable " +
                vname +
                " :: " + def.getType());
            String clname = BytecodeGenerator.encodeName2Java(ns.getName());
            String mtname = BytecodeGenerator.encodeName2Java(vname);
            String tstr = "()" +
                TypeHelper.getInternalName(BytecodeGenerator.encodeType2Class(def.getType()));
            /*
             * generate instruction sequence with call to
             * static method in namespace class
             */
            short index = MethodRefData.create(classFile.getConstantPool(), clname, mtname, tstr);
            seq._invokestatic(index, classFile);
            return seq;
        }
        throw new CompilerException("Module may not be null at this stage");
    }

    /**
     * Patterns are visited only during alternative code generation We generate a code for testing if top of stack
     * matches given pattern and store variables
     *
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorPattern)
     */
    public Object visit(ConstructorPattern a) {
        try {
            Constructor ctor = a.getConstructor();
            /* retrieve type of ctor */
            Type2Class tv = new Type2Class((Module) ns);
            Class cls = (Class) ctor.getType().visit(tv);
            /* generate instanceof check */
            short index = ClassData.create(classFile.getConstantPool(), TypeHelper.getInternalName(cls));
            Sequence seq = new Sequence(classFile);
            /* generate code for subpatterns */
            Sequence sub = new Sequence(classFile);
            /* sub patterns ought to be only variables */
            Iterator it = a.getSubPatterns();
            int i = 0;
            while (it.hasNext()) {
                LocalBinding lb = (LocalBinding) it.next();
                String fldn = "_" + i;
                /* get class of ith field */
                Field fld = cls.getField(fldn);
                Class fcls = fld.getType();
                /* extract field from result and store it*/
                short mref = FieldRefData.create(classFile.getConstantPool(), TypeHelper.getInternalName(cls), "_" + i, TypeHelper.getInternalName(fcls));
                sub._getfield(mref, classFile);
                /* generate cast ?? */
                sub.add(generateStore(lb.getIndex(), lb));
            }
            /* we must leave result on stack */
            seq._dup()._instanceof(index)._dup()._ifeq(sub.size() + 6).add(sub);
            return seq;
        } catch (NoSuchFieldException ex) {
            log.severe(ex.getMessage());
            return null;
        }
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(LocalBinding)
     */
    public Object visit(LocalBinding a) {
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(QualifiedVariable)
     */
    public Object visit(QualifiedVariable a) {
        Module mod = null;
        Iterator it = a.getPath().iterator();
        while (it.hasNext()) {
            String mname = (String) it.next();
            if (mod != null)
                mod = (Module) mod.lookup(mname);
            else
                mod = (Module) Module.getToplevels().get(mname);
        }
        // module found - locate and generate instructions
        if (mod != null) {
            // generate invokestatic code on right module
            Expression def = mod.lookup(a.getName());
            if (def instanceof PrimitiveFunction)
                return makePrimitiveClosure(def, a);
            // create method data
            String clname = BytecodeGenerator.encodeName2Java(mod.getName());
            String mtname = BytecodeGenerator.encodeName2Java(a.getName());
            Type t = def.getType();
            String tstr;
            if (!PrimitiveType.checkFunction(t))
                tstr = "()" + Type2Class.encodeType2Bytecode(t);
            else
                tstr = Type2Class.encodeType2Bytecode(t);

            // generate instruction sequence
            short index = MethodRefData.create(classFile.getConstantPool(), clname, mtname, tstr);
            Sequence seq = new Sequence(classFile);
            seq._invokestatic(index, classFile);
            return seq;
        }
        throw new CompilerException("Unable to find module needed for variable " + a.getName());
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(PrimitiveFunction)
     */
    public Object visit(PrimitiveFunction f) {
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Conditional)
     */
    public Object visit(Conditional conditional) {
        /* generate code for condition */
        Expression cond = conditional.getCondition();
        Type t = cond.getType();
        if (!t.equals(Primitives.BOOL))
            throw new CompilerException("Illegal conditional expression, type is " + t);
        /*
         * condition is always  evaluated so set strictness on
         */
        pushStrict(true);
        Instruction inst = (Instruction) cond.visit(this);
        popStrict();
        /* alternatives are evaluated in current strictness context */
        Instruction itrue = (Instruction) conditional.getIfTrue().visit(this);
        Instruction ifalse = (Instruction) conditional.getIfFalse().visit(this);
        if (strictness)
            return makeStrictCondition(inst, itrue, ifalse);
        else
            return makeNonStrictCondition(inst, itrue, ifalse);
    }

    /**
     * @param  f
     *
     * @return
     */
    public Instruction generateConstantFloat(float f, ClassFile cf) {
        Instruction inst = null;
        if (f == 0f)
            inst = new ZeroArgInst(Opcodes.opc_fconst_0, cf);
        else if (f == 1f)
            inst = new ZeroArgInst(Opcodes.opc_fconst_1, cf);
        else if (f == 2f)
            inst = new ZeroArgInst(Opcodes.opc_fconst_2, cf);
        else { // store constant in cpool
            // store string in constant pool
            short idx = FloatData.create(classFile.getConstantPool(), f);
            // generate code
            if (idx < 256)
                inst = new OneArgInst(Opcodes.opc_ldc, cf, (byte) idx);
            else
                inst = new TwoArgInst(Opcodes.opc_ldc_w, cf, idx);
        }
        return inst;
    }

    /**
     */
    public ClassFile getClassFile() {
        return classFile;
    }

    /**
     */
    public Module getNamespace() {
        return ns;
    }

    /**
     * returns the name of eval function to use according to type
     */
    String typedEval(Type type) {
        if (type instanceof PrimitiveType) {
            return "as" + type.toString();
        } else
            return "eval";
    }

    /**
     * Method generateStore.
     *
     * @param  index
     * @param  lb
     *
     * @return Instruction
     */
    Instruction generateStore(int index, LocalBinding lb) {
        try {
            Type2Class tv = new Type2Class();
            Class cls = (Class) lb.getType().visit(tv);
            Sequence seq = new Sequence(classFile);
            if (!strictness) {
                if (lb.isStrict())
                    // store occurs in non strict context
                    seq.add(Type2Class.unbox(classFile, cls)).add(TypedInst.store(cls, classFile, index));
                else
                    seq.add(TypedInst.store(JObject.class, classFile, index));
            } else {
                if (lb.isStrict())
                    // store occurs in non strict context
                    seq.add(TypedInst.store(cls, classFile, index));
                else
                    seq.add(Type2Class.box(classFile, cls)).add(TypedInst.store(JObject.class, classFile, index));
            }
            return seq;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot compute store instruction for variable " + lb);
        }

    }

    Instruction generateConstantInt(int i) {
        // generate code
        Instruction inst = null;
        switch (i) {

        case -1:
            inst = new ZeroArgInst(Opcodes.opc_iconst_m1, classFile);
            break;

        case 0:
            inst = new ZeroArgInst(Opcodes.opc_iconst_0, classFile);
            break;

        case 1:
            inst = new ZeroArgInst(Opcodes.opc_iconst_1, classFile);
            break;

        case 2:
            inst = new ZeroArgInst(Opcodes.opc_iconst_2, classFile);
            break;

        case 3:
            inst = new ZeroArgInst(Opcodes.opc_iconst_3, classFile);
            break;

        case 4:
            inst = new ZeroArgInst(Opcodes.opc_iconst_4, classFile);
            break;

        case 5:
            inst = new ZeroArgInst(Opcodes.opc_iconst_5, classFile);
            break;

        default: { // store constant in cpool
            short idx = IntData.create(classFile.getConstantPool(), i);
            // generate code
            if (idx < 256)
                inst = new OneArgInst(Opcodes.opc_ldc, classFile, (byte) idx);
            else
                inst = new TwoArgInst(Opcodes.opc_ldc_w, classFile, idx);
        }
        }
        return inst;
    }

    /**
     * Method generateLoad.
     *
     * @param  index
     * @param  a
     *
     * @return Object
     */
    Object generateLoad(LocalBinding a) {
        Type2Class tv = new Type2Class(ns);
        Class cls = (Class) a.getType().visit(tv);
        Sequence seq = new Sequence(classFile);
        /* eval index */
        if (!a.isStrict()) {
            // load non strict variable
            seq.add(TypedInst.load(JObject.class, classFile, a.getIndex()));
            if (strictness)
                return seq.add(Type2Class.unbox(classFile, cls));
            else
                return seq;
        } else {
            // load non strict variable
            seq.add(TypedInst.load(cls, classFile, a.getIndex()));
            if (!strictness)
                return seq.add(Type2Class.box(classFile, cls));
            else
                return seq;

        }
    }

    private void pushStrict(boolean strict) {
        savedStrictness.push(new Boolean(strictness));
        strictness = strict;
    }

    private void popStrict() {
        strictness = ((Boolean) savedStrictness.pop()).booleanValue();
    }

    /**
     * Method makeLambdaCtor.
     *
     * @param  lambda
     * @param  a
     *
     * @return FieldFileInfo
     */
    private MethodFileInfo makeLambdaCtor(ClassFile lambda, int argCount) {
        MethodFileInfo mfi = new MethodFileInfo(lambda);
        mfi.setName("<init>");
        mfi.setType("()V");
        mfi.setFlags((short) (Constants.ACC_PUBLIC));
        CodeAttribute code = new CodeAttribute(lambda, (short) 1);
        Sequence seq = new Sequence(lambda);
        short ctor = MethodRefData.create(lambda.getConstantPool(), "fr/lifl/jaskell/runtime/types/Closure", "<init>", "(I)V");
        /*
         * call super(argCount)
         */
        seq._aload_0().add(TypedInst.constInt(lambda, argCount))._invokespecial(ctor, lambda)._return();
        code.add(seq);
        mfi.addAttribute(code);
        return mfi;
    }

    /**
     * Method makeLambdaCtor.
     *
     * @param  lambda
     * @param  a
     *
     * @return FieldFileInfo
     */
    private MethodFileInfo makeInit(ClassFile lambda, int argCount) {
        MethodFileInfo mfi = new MethodFileInfo(lambda);
        mfi.setName("init");
        mfi.setType("()Lfr/lifl/jaskell/runtime/types/JFunction;");
        mfi.setFlags((short) (Constants.ACC_PUBLIC));
        CodeAttribute code = new CodeAttribute(lambda, (short) 1);
        Sequence seq = new Sequence(lambda);
        String clname = lambda.getClassFileInfo().getName();
        short clref = ClassData.create(lambda.getConstantPool(), clname);
        short ctor = MethodRefData.create(lambda.getConstantPool(), clname, "<init>", "()V");

        /*
         * call new <this>(argCount)
         */
        seq._new(clref)._dup()._invokespecial(ctor, lambda)._areturn();
        code.add(seq);
        mfi.addAttribute(code);
        return mfi;
    }

    /**
     * Method makeUnboxedEvalMethod. The unboxed evaluation method assumes all strict arguments are already evaluated on
     * stack and all others are unevaluated.
     *
     * @param  lambda
     * @param  a
     * @param  range
     *
     * @return FieldFileInfo
     */
    private MethodFileInfo makeUnboxedEvalMethod(ClassFile lambda, Abstraction a, Type range) {
        MethodFileInfo mfi = new MethodFileInfo(lambda);
        Type2Class tv = new Type2Class();
        mfi.setName("eval");
        mfi.setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_STATIC));
        String sig = encodeType2Signature(a, range);
        mfi.setType(sig);
        /*
         * generate code for body of abstraction
         */
        CodeAttribute code = new CodeAttribute(lambda, (short) (1 + a.getCount()));
        log.finest("Generating eval unboxed code for " + sig);
        pushStrict(true);
        code.add((Instruction) a.getBody().visit(this));
        popStrict();
        /* reset maximum locals */
        code.setMaxlocals(a.getMaxLocals());
        code.add(TypedInst.returnInst(lambda, (Class) range.visit(tv)));
        mfi.addAttribute(code);
        log.finest("End generating eval unboxed code for " + sig);
        return mfi;
    }

    private String encodeType2Signature(Abstraction a, Type range) {
        /*
         * construct signature of function taking into account
         * strictness of arguments
         */
        StringBuffer sig = new StringBuffer("(");
        Type ft = a.getType();
        Iterator it = PrimitiveType.functionIterator(ft);
        int i = 0;
        TypeApplication ta = null;
        while (it.hasNext()) {
            ta = (TypeApplication) it.next();
            Type t = ((TypeApplication) (ta).getDomain()).getRange();
            if (a.isStrict(i++))
                sig.append(TypeHelper.getInternalName(BytecodeGenerator.encodeType2Class(t)));
            else
                sig.append("Lfr/lifl/jaskell/runtime/types/JObject;");
        }
        sig.append(')').append(TypeHelper.getInternalName(BytecodeGenerator.encodeType2Class(ta.getRange())));
        return sig.toString();
    }

    /**
     * Construct JObject eval() override for closure. The constructed method checks the number of currently applied
     * args. If this number is equal to expected number of arguments, the typed eval method is called else the partially
     * applied closure is returned
     */
    private MethodFileInfo makeEval0Method(ClassFile lambda, Abstraction a, Type type) {
        // create main eval method
        MethodFileInfo mfi = new MethodFileInfo(lambda);
        Type2Class tv = new Type2Class(ns);
        mfi.setName("eval");
        mfi.setFlags((short) (Constants.ACC_PUBLIC));
        mfi.setType("()Lfr/lifl/jaskell/runtime/types/JObject;");
        // create code - all expressiosn underneath an abstraction returns
        // instructions
        CodeAttribute code = new CodeAttribute(lambda, (short) (1));
        Sequence seq = new Sequence(lambda);
        /*
         * the partial evaluation retrieves all arguments already
         * stored in args array, stack all its arguments and calls
         * real eval function
         */
        int arg = a.getCount();
        // number of curried arguments
        Iterator typeit = PrimitiveType.functionIterator(type);
        TypeApplication t = null;
        short argsfield = FieldRefData.create(lambda.getConstantPool(), "fr/lifl/jaskell/runtime/types/Closure", "args", "[Lfr/lifl/jaskell/runtime/types/JObject;");
        short nargsfield = FieldRefData.create(lambda.getConstantPool(), "fr/lifl/jaskell/runtime/types/Closure", "nargs", "I");
        short maxargsfield = FieldRefData.create(lambda.getConstantPool(), "fr/lifl/jaskell/runtime/types/Closure", "maxargs", "I");
        short ieval = InterfaceMethodData.create(lambda.getConstantPool(), "fr/lifl/jaskell/runtime/types/JObject", "eval", "()Lfr/lifl/jaskell/runtime/types/JObject;");
        seq._aload_0();
        // generate sequence for returning this
        Sequence noeval = new Sequence(lambda)._aload_0()._areturn();
        // test if all arguments are present, else return this
        seq._aload_0()._getfield(nargsfield, lambda) // this.nargs
        ._aload_0()._getfield(maxargsfield, lambda) // this.maxargs
        ._if_icmpeq(noeval.size() + 3) // if(maxargs == nargs)
        .add(noeval); // else return this
        int i = 0;
        while (typeit.hasNext()) {
            t = (TypeApplication) typeit.next();
            Class d = (Class) ((TypeApplication) t.getDomain()).getRange().visit(tv);
            seq._aload_0()._getfield(argsfield, lambda).add(TypedInst.constInt(lambda, i))._aaload();
            /* eval and unbox argument if it is strict */
            if (a.isStrict(i))
                seq._invokeinterface(ieval, 1, lambda).add(Type2Class.unbox(lambda, d));
            i++;
        }
        // call real eval
        short evref = MethodRefData.create(lambda.getConstantPool(), lambda.getClassFileInfo().getName(), "eval", encodeType2Signature(a, t.getRange()));
        seq._invokestatic(evref, lambda);
        // retrieve return type and box it
        Class retcls = (Class) t.getRange().visit(tv);
        // return
        seq.add(Type2Class.box(lambda, retcls))._areturn();
        code.add(seq);
        mfi.addAttribute(code);
        return mfi;
    }

    /**
     * Reindexes the bindings in Alternative a to take into account parameters for Closure
     *
     * @param a
     * @param closure
     */
    private void reindex(Alternative a, Abstraction closure) {
        LocalBinding var;
        int argcount = closure.getMaxLocals();
        /* reindexes the binder for expression if it exists */
        if ((var = a.getBinding()) != null) {
            var.setIndex(var.getIndex() + argcount);
            argcount++;
        }
        /*
         * reindexes all bindings in patterns
         */
        Iterator it = a.getPatterns();
        while (it.hasNext()) {
            Pattern pat = (Pattern) it.next();
            Iterator it2 = pat.getBindings().iterator();
            int i = 0;
            /*                      closure.setMaxlocals(
             *                              (short) (closure.getCount() + pat.getBindings().size()));
             */
            while (it2.hasNext()) {
                var = (LocalBinding) it2.next();
                var.setIndex(var.getIndex() + argcount);
                i++;
            }
            argcount += i;
        }
        closure.setMaxlocals((short) (argcount));
        /*
         * set maximum local arguments in abstraction
         */
    }

    /**
     * Method makeNonStrictCase.
     *
     * @param  tests
     * @param  res
     * @param  wc
     *
     * @return Object
     */
    private Object makeNonStrictCase(List tests, List res, Instruction wc) {
        // TODO ??
        return null;
    }

    /**
     * Method makeStrictCase.
     *
     * @param  tests a list of list of tests
     * @param  res
     * @param  wc
     *
     * @return Object
     */
    private Object makeStrictCase(int len, List tests, List res, Instruction wc) {
        Sequence seq = new Sequence(classFile);
        int offset = len;
        /* add code for tests and bodies with appropriate jumps */
        for (int i = 0; i < tests.size(); i++) {
            Sequence ti = (Sequence) tests.get(i);
            Instruction ri = (Instruction) res.get(i);
            int rsz = ri.size(); /* size of expression */
            int tsz = ti.size(); /* size of tests */
            int jumpTo = tsz + rsz + 3;
            /* patch each jump in ti to go at  tsz + rsz + 3 (last goto) */
            Iterator it = ti.iterator();
            while (it.hasNext()) {
                Instruction inst = (Instruction) it.next();
                int op = inst.opcode();
                if ((op >= 153) && (op <= 168)) {
                    TwoArgInst jump = (TwoArgInst) inst;
                    jump.setArgs((short) jumpTo);
                }
                /* decrement jump */
                jumpTo -= inst.size();
            }
            offset -= tsz + rsz;
            /*
             * need to add type check after ri ?
             */
            seq.add(ti).add(ri)._goto(offset + wc.size());
            if (i < (tests.size() - 1))
                offset -= 3;
        }
        /* add wildcard code */
        seq.add(wc);
        return seq;
    }

    /**
     * Finish generating code for an application occuring in strict context This code generate invocation to unboxed
     * evaluation method for given function assuming the closure object and arguments are on the stack. The main
     * difference between this and non strict application is the eval code which is added at the end of sequence.
     *
     * @param  a
     * @param  abs
     *
     * @return Instruction
     */
    private Instruction makeStrictCompleteApplication(Application a, Binder abs, Type abstype) {
        Iterator it = a.getArgs().iterator();
        Sequence seq = new Sequence(classFile);
        int i = 0;
        Iterator typeit = PrimitiveType.functionIterator(abstype);
        while (it.hasNext()) {
            Expression arg = (Expression) it.next();
            Type expect = ((TypeApplication) ((TypeApplication) typeit.next()).getDomain()).getRange();
            if (abs.isStrict(i++)) {
                /* we must generate evaluation of object */
                pushStrict(true & strictness);
            } else
                pushStrict(false);
            seq.add((Instruction) arg.visit(this));
            /* generate cast */
            if (strictness) {
                /* get class for type */
                Type2Class tc = new Type2Class();
                Class acls = (Class) expect.visit(tc);
                if ((!acls.isPrimitive()) && !acls.equals(JObject.class)) {
                    short cr = ClassData.create(classFile.getConstantPool(), TypeHelper.getExternalName(acls).replace('.', '/'));
                    seq._checkcast(cr);
                }
            }
            popStrict();
        }

        /** generate direct call to static method eval */
        short iref = -1;
        if (abs instanceof PrimitiveFunction) {
            PrimitiveFunction pf = (PrimitiveFunction) abs;
            //            iref =
            //                MethodRefData.create(
            //                    classFile.getConstantPool(),
            //                    "fr/lifl/jaskell/runtime/modules/Prelude",
            //                    encodeName2Java(pf.getName()),
            //                    encodeType2Bytecode(pf.getType()));
            //            seq._invokestatic(iref, classFile);
            seq.add(PrimitivesCodeGenerator.emitCode(pf, classFile));
        } else if (abs instanceof Abstraction) {
            Abstraction ab = (Abstraction) abs;
            String clname = ab.getClassName();
            Type funtype = ab.getType();
            iref = MethodRefData.create(classFile.getConstantPool(), encodeName2Java(clname), "eval", encodeType2Signature(ab, funtype));
            seq._invokestatic(iref, classFile);
        } else if (abs instanceof ConstructorDefinition) {
            seq.add(makeStaticConsCall((ConstructorDefinition) abs));
        } else
            throw new CompilerException("Bad function application" + a);
        return seq;
    }

    /**
     * Method makeNonStrictCompleteApplication. Assumes the fun Instruction has done all application and evaluated
     * functional member
     *
     * @param  a
     * @param  fun
     * @param  funtype
     *
     * @return Instruction
     */
    private Instruction makeStrictCompleteApplicationSpine(Application a, Instruction fun, Type funtype) {
        Iterator it = a.getArgs().iterator();
        Iterator it2 = PrimitiveType.functionIterator(funtype);
        Type2Class tv = new Type2Class();
        Sequence seq = new Sequence(classFile);
        /* generate code to retrieve arguments and apply them successively to
         * the closure object which is the function
         */
        short iref = -1;
        seq.add(makePartialApplication(a, fun, funtype));
        iref = InterfaceMethodData.create(classFile.getConstantPool(), "fr/lifl/jaskell/runtime/types/JObject", "eval", "()Lfr/lifl/jaskell/runtime/types/JObject;");
        /* call evaluation on application spine */
        seq._invokeinterface(iref, 1, classFile);
        /* unbox argument as we are in a strict context */
        Type ft = null;
        while (it2.hasNext())
            ft = ((TypeApplication) it2.next()).getRange();
        seq.add(Type2Class.unbox(classFile, (Class) ft.visit(tv)));
        return seq;
    }

    private Instruction makePartialApplication(Application a, Instruction fun, Type funtype) {
        Iterator it = a.getArgs().iterator();
        Iterator it2 = PrimitiveType.functionIterator(funtype);
        Type2Class tv = new Type2Class();
        Sequence seq = new Sequence(classFile);
        short funr = ClassData.create(classFile.getConstantPool(), "fr/lifl/jaskell/runtime/types/JFunction");
        short funinit = InterfaceMethodData.create(classFile.getConstantPool(), "fr/lifl/jaskell/runtime/types/JFunction", "init", "()Lfr/lifl/jaskell/runtime/types/JFunction;");
        /* create new instance of closure */
        seq.add(fun)._checkcast(funr)._invokeinterface(funinit, 1, classFile);
        /* generate code to retrieve arguments and apply them successively to */
        /* the closure object which is the function */
        short apply = InterfaceMethodData.create(classFile.getConstantPool(), "fr/lifl/jaskell/runtime/types/JFunction", "apply", "(Lfr/lifl/jaskell/runtime/types/JObject;)Lfr/lifl/jaskell/runtime/types/JObject;");
        while (it.hasNext()) {
            Expression arg = (Expression) it.next();
            // generate code for argument
            pushStrict(false);
            Instruction arginst = (Instruction) arg.visit(this);
            seq._checkcast(funr);
            seq.add(arginst);
            popStrict();
            // box argument
            //                      seq.add(tv.box(classFile, (Class) arg.getType().visit(tv)));
            // select correct apply method according to type of arguments and range
            seq._invokeinterface(apply, 2, classFile);
        }
        // concatenate and return application code
        return seq;
    }

    private Instruction makeStaticConsCall(ConstructorDefinition def) {
        Sequence seq = new Sequence(classFile);
        /* retrieve type definition for constructor */
        DataDefinition ddef = (DataDefinition) def.getParent();
        // create method data
        log.finest("Generating code for constructor " + def);
        Type2Class tv = new Type2Class(ns);
        String clname = BytecodeGenerator.encodeName2Java(((Class) ddef.visit(tv)).getName().replace('.', '/'));
        String mtname = BytecodeGenerator.encodeName2Java(def.getName().substring(def.getName().lastIndexOf('.') + 1));
        /* encode signature */
        StringBuffer initsig = new StringBuffer("(");
        Iterator it = def.getParameters().iterator();
        int i = 0;
        while (it.hasNext()) {
            Class fcls = (Class) ((Type) it.next()).visit(tv);
            /* type of argument is boxed if non strict */
            if (!def.isStrict(i))
                fcls = Type2Class.boxed(fcls);
            initsig.append(TypeHelper.getInternalName(fcls));
        }
        /* signature for factory method */
        String factsig = initsig.append(")L").append(clname).append(';').toString();
        // generate instruction sequence
        short index = MethodRefData.create(classFile.getConstantPool(), clname, mtname, factsig);
        seq._invokestatic(index, classFile);
        return seq;
    }

    /**
     * Method makeMethod.
     *
     * @param  name
     * @param  def
     *
     * @return MethodFileInfo
     */
    private MethodFileInfo makeMethod(String name, Instruction code, Type type) {
        MethodFileInfo mfi = new MethodFileInfo(classFile);
        Type2Class tv = new Type2Class(ns);
        Class cl = (Class) type.visit(tv); // get class of return type
        String realname = BytecodeGenerator.encodeName2Java(name);
        mfi.setName(realname);
        mfi.setPublic();
        mfi.setStatic();
        CodeAttribute attr = new CodeAttribute(classFile);
        Sequence seq = new Sequence(classFile);
        seq.add(code).add(TypedInst.returnInst(classFile, cl));
        attr.add(seq);
        mfi.addAttribute(attr);
        mfi.setType("()" + TypeHelper.getInternalName(cl));
        return mfi;
    }

    /**
     * Method generate main method in Main ns. The generated class can then be directly executed by java VM.
     *
     * <p>The main function generated is the usual main(String[]) from java and just invokes argument less main and
     * evals it.
     *
     * @param  name
     * @param  def
     *
     * @return MethodFileInfo
     */
    private MethodFileInfo makeMain(Instruction code) {
        MethodFileInfo mfi = new MethodFileInfo(classFile);
        mfi.setName("main");
        mfi.setPublic();
        mfi.setStatic();
        mfi.setType("([Ljava/lang/String;)V");
        CodeAttribute attr = new CodeAttribute(classFile);
        Sequence seq = new Sequence(classFile);
        attr.setMaxlocals((short) 1);
        attr.add(seq.add(code)._return());
        mfi.addAttribute(attr);
        return mfi;
    }

    /**
     * Method isQualified.
     *
     * @param  vname
     *
     * @return boolean
     */
    private boolean isQualified(String vname) {
        return vname.indexOf('.') > 0;
    }

    /**
     * Method makePrimitiveClosure. Create a closure object which encapsulates call to a primitive method.
     *
     * @param  def
     *
     * @return Object
     */
    private Object makePrimitiveClosure(Expression def, QualifiedVariable a) {
        // update closure state
        ClassFile lambda = new ClassFile();
        Abstraction old = closure;
        closure = (Abstraction) def;
        // name of primitive function
        //create a new ClassFile
        String fullname = "";
        Iterator pit = a.getPath().iterator();
        while (pit.hasNext()) {
            String s = (String) pit.next();
            fullname += s + '/';
        }
        fullname += BytecodeGenerator.encodeName2Java(a.getName());
        closure.setClassName(fullname);
        String fname = fullname.substring(fullname.lastIndexOf('/') + 1);
        String modname = fullname.substring(0, fullname.lastIndexOf('/'));
        Type type = def.getType();
        // generate Closure sub-class
        Type range = makeClosure(lambda, closure, fullname, type);
        // eval method contains call to static method
        MethodFileInfo mfi = new MethodFileInfo(lambda);
        Type2Class tv = new Type2Class(ns);
        String sig = Type2Class.encodeType2Bytecode(def.getType());
        // range is the return type of function
        mfi.setName("eval");
        mfi.setFlags((short) (Constants.ACC_PUBLIC));
        mfi.setType(sig);
        // create code
        CodeAttribute code = new CodeAttribute(lambda, (short) (1 + TypeHelper.countArguments(sig)));
        Sequence seq1 = new Sequence(lambda);
        // code to call primitive function
        // generate loading of all argumens with correct type
        Iterator it = PrimitiveType.functionIterator(type);
        int i = 1; // index of local variable slot
        while (it.hasNext()) {
            TypeApplication t = (TypeApplication) it.next();
            Class d = (Class) t.getDomain().visit(tv);
            seq1.add(TypedInst.load(d, lambda, i++));
            if ((d == long.class) || (d == double.class))
                i++;
        }
        // call primitive method - must be a static method defined in a module
        short primindex = MethodRefData.create(lambda.getConstantPool(), BytecodeGenerator.encodeName2Java(modname), fname, sig);
        seq1._invokestatic(primindex, lambda);
        seq1.add(TypedInst.returnInst(lambda, (Class) range.visit(tv)));
        code.add(seq1);
        mfi.addAttribute(code);
        lambda.add(mfi);

        // store classfile
        generateClass(fullname, lambda);
        if (classFile != null) {
            // generate code to make new object - we then have on the
            // stack an object which implements the abstraction and on
            // which we can call apply
            Sequence seq = new Sequence(classFile);
            short lambdaindex = ClassData.create(classFile.getConstantPool(), fullname);
            short ctorindex = MethodRefData.create(classFile.getConstantPool(), fullname, "<init>", "()V");
            seq._new(lambdaindex);
            seq._dup();
            seq._invokespecial(ctorindex, classFile);
            closure = old;
            return seq;
        } else
            return null;
    }

    /**
     * @param  lambda
     * @param  closure
     * @param  fullname
     * @param  type
     *
     * @return
     */
    private Type makeClosure(ClassFile lambda, Abstraction closure, String fullname, Type type) {
        ClassFileInfo lcfi = new ClassFileInfo(lambda, fullname);
        lcfi.setParent("fr/lifl/jaskell/runtime/types/Closure");
        lcfi.setFlags((short) (Constants.ACC_PUBLIC));
        lambda.setClassFileInfo(lcfi);
        int argCount = closure.getCount();
        /*
         * add constructor - this calls constructor for Closure with
         * argument count for storing partially evaluated functions
         */
        lambda.add(makeLambdaCtor(lambda, argCount));
        lambda.add(makeInit(lambda, argCount));
        return PrimitiveType.getReturnType(type);
    }

    /**
     * Method makeNonStrictCondition. Nonstrict conditional boxes the instructions through call to primitive function
     * ifThenElse
     *
     * @param  inst
     * @param  itrue
     * @param  ifalse
     *
     * @return Object
     */
    private Object makeNonStrictCondition(Instruction inst, Instruction itrue, Instruction ifalse) {
        short mref = MethodRefData.create(classFile.getConstantPool(), "fr/lifl/jaskell/runtime/modules/Prelude", "ifThenElse",
                "(Lfr/lifl/jaskell/runtime/types/JObject;Lfr/lifl/jaskell/runtime/types/JObject;Lfr/lifl/jaskell/runtime/types/JObject;)Lfr/lifl/jaskell/runtime/types/JObject;");
        Sequence seq = new Sequence(classFile);
        seq.add(inst).add(itrue).add(ifalse)._invokestatic(mref, classFile);
        return seq;
    }

    /**
     * Method makeStrictCondition. We generate a strict conditional by generating direct comparison between result of
     * condition expression and 0 and juming to code
     *
     * @param  icond
     * @param  itrue
     * @param  ifalse
     *
     * @return Object
     */
    private Object makeStrictCondition(Instruction icond, Instruction itrue, Instruction ifalse) {
        Sequence seq = new Sequence(classFile);
        seq.add(icond)._ifeq(itrue.size() + 6).add(itrue)._goto(ifalse.size() + 3).add(ifalse);
        return seq;
    }

}
