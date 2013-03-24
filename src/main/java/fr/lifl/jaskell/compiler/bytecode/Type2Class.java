/*
 * $Log: Type2Class.java,v $
 * Revision 1.29  2004/10/17 15:05:07  bailly
 * corrected calss to BET API
 * removed OverloadDefinition
 *
 * Revision 1.28  2004/09/07 10:04:04  bailly
 * cleared imports
 *
 * Revision 1.27  2004/07/23 06:58:31  bailly
 * changed classloader management to external definition
 *
 * Revision 1.26  2004/07/03 20:34:22  bailly
 * correction pattern matching sur les litt?aux
 * modification r?solution nom dans Module pour prendre en compte
 * les modules import?s renomm?s
 * correction dans le parseur
 *
 * Revision 1.25  2004/07/01 15:57:41  bailly
 * suppression de l'interface Namespace au profit de fr.lifl.parsing.Namespace
 * modification de la g?n?ration de code pour les constructeurs et les types de donnees
 * creation d'un type JEvent et d'un constructeur Event
 * modification du parser pour creer des Event lors de l'analyse syntaxique
 *
 * Revision 1.24  2004/03/11 22:16:24  bailly
 * Modif Type2Class to generate CDef directly
 * Modif CodeGenerator and all visitors to handle properly
 * recursive namespaces
 *
 * Revision 1.23  2004/02/18 17:20:07  nono
 * suppressed type classes use and definitions
 *
 * Revision 1.22  2004/02/09 20:40:02  nono
 * mise a jour repository central
 *
 * Revision 1.21  2003/06/27 06:24:52  bailly
 * *** empty log message ***
 *
 * Revision 1.19  2003/06/23 06:33:31  bailly
 * Debugging
 * Added JError primitive type to raise runtime exceptions
 * when no match is found
 *
 * Revision 1.18  2003/06/18 19:59:16  bailly
 * Added abstractInterpreter and StrictnessInterpreter classes
 * to compute strictness information both for arguments
 * and  internal nodes - applications of combinator bodies
 *
 * Revision 1.17  2003/06/16 20:07:54  bailly
 * *** empty log message ***
 *
 * Revision 1.16  2003/06/10 19:05:47  bailly
 * Debugging parser - continued adding error handling and
 * recovery
 *
 * Revision 1.15  2003/06/10 12:58:10  bailly
 * Start work on error recovery and error messages in parsing
 *
 * 
 */

package fr.lifl.jaskell.compiler.bytecode;

import fr.lifl.jaskell.compiler.CompilerException;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.TypeApplication;
import fr.lifl.jaskell.compiler.types.TypeConstructor;
import fr.lifl.jaskell.compiler.types.TypeVariable;
import fr.lifl.jaskell.compiler.types.TypeVisitor;
import fr.lifl.jaskell.runtime.types.JBoolean;
import fr.lifl.jaskell.runtime.types.JChar;
import fr.lifl.jaskell.runtime.types.JDouble;
import fr.lifl.jaskell.runtime.types.JFloat;
import fr.lifl.jaskell.runtime.types.JInt;
import fr.lifl.jaskell.runtime.types.JObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import fr.norsys.klass.bytes.ByteArrayClassLoader;
import fr.norsys.klass.bytes.ClassData;
import fr.norsys.klass.bytes.ClassFile;
import fr.norsys.klass.bytes.ClassFileInfo;
import fr.norsys.klass.bytes.CodeAttribute;
import fr.norsys.klass.bytes.Constants;
import fr.norsys.klass.bytes.FieldFileInfo;
import fr.norsys.klass.bytes.FieldRefData;
import fr.norsys.klass.bytes.Instruction;
import fr.norsys.klass.bytes.MethodFileInfo;
import fr.norsys.klass.bytes.MethodRefData;
import fr.norsys.klass.bytes.Sequence;
import fr.norsys.klass.bytes.TypeHelper;
import fr.norsys.klass.bytes.TypedInst;

/**
 * A class offering methods for generating java classes from haskell type
 * information
 * 
 * This class offers several methods for generating java class objects from
 * haskell type information, either type expressions or type definitions.
 * 
 * @author bailly
 * @version $Id: Type2Class.java 1207 2006-05-26 08:55:33Z nono $
 */
public class Type2Class extends BytecodeGenerator implements TypeVisitor {

  private static Logger log = Logger.getLogger(Type2Class.class.getName());

  /* store the set of constraints against which variables may be resolved */
  private Set context;

  /** current module */
  private Module module;

  /** a te?mporary class loader for partial class definitions */
  ByteArrayClassLoader temploader = new ByteArrayClassLoader(this.getClass()
      .getClassLoader());

  /**
   * Construct a Type2Class object with an explicit context given.
   * 
   * @param context
   *          a Set containing instances of TypeConstraint
   */
  public Type2Class(Set context, Module mod) {
    this.context = context;
    this.module = mod;
  }

  /**
   * Constructs a generic Type2Class object
   */
  public Type2Class(Module mod) {
    this.context = new HashSet();
    this.module = mod;
  }

  /**
   * Constructs a generic Type2Class object
   */
  public Type2Class() {
    this.context = new HashSet();
  }

  /**
   * 
   * returns a sequence of instruction to encapsulate an object of type type in
   * an Object reference. The data to encapsulate is assumed to lay on top of
   * the stack Needs a ClassFile to generate constant entries
   */
  public static Instruction box(ClassFile cf, Class c) {
    Sequence seq = new Sequence(cf);
    short d, m;
    if (c.isPrimitive()) {
      switch (TypeHelper.getInternalName(c).charAt(0)) {
      case 'I':
        d = ClassData
            .create(cf.getConstantPool(), "fr/lifl/jaskell/runtime/types/JInt");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JInt", "<init>", "(I)V");
        seq._new(d)._dup_x1()._swap()._invokespecial(m, cf);
        return seq;
      // we've got an initialized integer on the stack
      case 'Z':
        d = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JBoolean");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JBoolean", "<init>", "(Z)V");
        seq._new(d)._dup_x1()._swap()._invokespecial(m, cf);
        return seq;
      case 'F':
        d = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JFloat");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JFloat", "<init>", "(F)V");
        seq._new(d)._dup_x1()._swap()._invokespecial(m, cf);
        return seq;
      case 'D':
        d = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JDouble");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JDouble", "<init>", "(D)V");
        seq._new(d)._dup_x2()._dup_x2()._pop()._invokespecial(m, cf);
        return seq;
      //  	    case 'J':
      //  		d =
      // ClassData.create(cf.getConstantPool(),"fr/lifl/jaskell/runtime/types/Long");
      //  		m =
      // MethodRefData.create(cf.getConstantPool(),"fr/lifl/jaskell/runtime/types/Long","<init>","(J)V");
      //  		seq.add(new TwoArgInst(opc_new,d));
      //  		seq.add(new ZeroArgInst(opc_dup_x2));
      //  		seq.add(new ZeroArgInst(opc_dup_x2));
      //  		seq.add(new ZeroArgInst(opc_pop));
      //  		seq.add(new TwoArgInst(opc_invokespecial,m));
      //  		return seq;
      case 'S':
        d = ClassData
            .create(cf.getConstantPool(), "fr/lifl/jaskell/runtime/types/JInt");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/Jint", "<init>", "(I)V");
        seq._new(d)._dup_x1()._swap()._invokespecial(m, cf);
        return seq;
      case 'C':
        d = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JChar");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JChar", "<init>", "(C)V");
        seq._new(d)._dup_x1()._swap()._invokespecial(m, cf);
        return seq;
      case 'B':
        d = ClassData
            .create(cf.getConstantPool(), "fr/lifl/jaskell/runtime/types/JInt");
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JInt", "<init>", "(I)V");
        seq._new(d)._dup_x1()._swap()._invokespecial(m, cf);
        return seq;
      }
    } else
      // nothing to do. The value on top of stack is already a reference
      seq._nop();
    return seq;
  }

  /**
   * returns a sequence of instruction to decapsulate a JObject to the type
   * given by c. There is a problem only for primitive data types. The data to
   * encapsulate is assumed to lay on top of the stack Needs a ClassFile to
   * generate constant entries
   */
  public static Instruction unbox(ClassFile cf, Class c) {
    Sequence seq = new Sequence(cf);
    short d, m, cli;
    cli = ClassData
        .create(cf.getConstantPool(), "fr/lifl/jaskell/runtime/types/JValue");
    if (c.isPrimitive()) {
      switch (TypeHelper.getInternalName(c).charAt(0)) {
      case 'I':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asInt", "()I");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      // we've got an initialized integer on the stack
      case 'Z':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asBool", "()Z");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      case 'F':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asFloat", "()F");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      case 'D':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asDouble", "()D");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      //  	    case 'J':
      //  		d =
      // ClassData.create(cf.getConstantPool(),"fr/lifl/jaskell/runtime/types/Long");
      //  		m =
      // MethodRefData.create(cf.getConstantPool(),"fr/lifl/jaskell/runtime/types/Long","<init>","(J)V");
      //  		seq.add(new TwoArgInst(opc_new,d));
      //  		seq.add(new ZeroArgInst(opc_dup_x2));
      //  		seq.add(new ZeroArgInst(opc_dup_x2));
      //  		seq.add(new ZeroArgInst(opc_pop));
      //  		seq.add(new TwoArgInst(opc_invokespecial,m));
      //  		return seq;
      case 'S':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asInt", "()I");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      case 'C':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asChar", "()C");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      case 'B':
        m = MethodRefData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JValue", "asInt", "()I");
        seq._checkcast(cli)._invokevirtual(m, cf); // unpakc
        return seq;
      }
    } else { // just checkcast
      cli = ClassData.create(cf.getConstantPool(), c.getName()
          .replace('.', '/'));
      seq._checkcast(cli); // cast
    }
    return seq;
  }

  /**
   * @see jaskell.compiler.TypeVisitor#visit(TypeVariable)
   */
  public Object visit(TypeVariable t) {
    return JObject.class;
  }

  /**
   * @see jaskell.compiler.TypeVisitor#visit(PrimitiveType)
   */
  public Object visit(PrimitiveType primitiveType) {
    return primitiveType.getJavaClass();
  }

  /**
   * Method cast.
   * 
   * This method returns checkcast instruction according to wrapper class of
   * class c
   * 
   * @param lambda
   * @param c
   * @return Instruction
   */
  public Instruction cast(ClassFile cf, Class c) {
    Sequence seq = new Sequence(cf);
    short d, m, cli = 0;
    if (c.isPrimitive()) {
      switch (TypeHelper.getInternalName(c).charAt(0)) {
      case 'I':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JInt");
        break;
      // we've got an initialized integer on the stack
      case 'Z':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JBoolean");
        break;
      case 'F':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JFloat");
        break;
      case 'D':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JDouble");
        break;
      case 'S':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JInt");
        break;
      case 'C':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JChar");
        break;
      case 'B':
        cli = ClassData.create(cf.getConstantPool(),
            "fr/lifl/jaskell/runtime/types/JInt");
        break;
      }
    } else
      // nothing to do. The value on top of stack is already a reference
      cli = ClassData.create(cf.getConstantPool(), c.getName()
          .replace('.', '/'));
    seq._checkcast(cli); // cast
    return seq;
  }

  /**
   * @see jaskell.compiler.TypeVisitor#visit(TypeApplication)
   */
  public Object visit(TypeApplication typeApplication) {
    /*
     * Set old = context; if (old == null) this.context =
     * typeApplication.getAssumptions(); else { context =
     * typeApplication.getAssumptions(); context.addAll(old); }
     */
    Class ret = (Class) typeApplication.getDomain().visit(this);
    /* restore contexet */
    // context = old;
    return ret;
  }

  /**
   * @see jaskell.compiler.types.TypeVisitor#visit(TypeConstructor)
   */
  public Object visit(TypeConstructor t) {
    String dname;
    if (module != null)
      dname = BytecodeGenerator.encodeName2Java(((Definition) module
          .resolveType(t.toString())).getName());
    else {
      /*
       * convert a type name to its fullpath form by locating its definition
       */
      String tname = t.getContext().resolveType(t).getName();
      if (tname == null)
        throw new CompilerException("Cannot resolve type " + t
            + " : bad context");
      dname = BytecodeGenerator.encodeName2Java(tname);

    }
    /* name resolved - find class for it */
    try {
      return Class.forName(dname);
    } catch (Exception ex) {
      try {
        return BytecodeGenerator.getLoader().loadClass(dname);
      } catch (Exception eex) {
        try {
          /*
           * class may stored in temporary class loader, to allow constructors
           * generation without circulary references. is it OK ??
           */
          return temploader.loadClass(dname);
        } catch (Exception eeex) {
          return generateClassFromTycon(dname);
        }
      }
    }
  }

  /**
   * Method generateClassFromTycon.
   * 
   * @param t
   * @return Object
   */
  private Object generateClassFromTycon(String tname) {
    // create a new ClassFile
    ClassFile cf = new ClassFile();
    ClassFileInfo cfi = new ClassFileInfo(cf, tname);
    cfi.setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_ABSTRACT));
    cf.setClassFileInfo(cfi);
    cfi.setParent("fr/lifl/jaskell/runtime/types/JValue");
    // define class in current class loader
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      cf.write(dos);
    } catch (java.io.IOException ex) {
      return null;
    }
    return temploader.buildClass(tname, bos.toByteArray());
  }

  private Class generateClassFromCtor(String dataname, ClassFile datafile,
      ConstructorDefinition type) {
    /* compute class name */
    String tname = BytecodeGenerator.encodeName2Java(type.getName());
    /* retrieve all parameters type classes */
    List l = new ArrayList();
    Iterator it = type.getParameters().iterator();
    while (it.hasNext())
      l.add(((Type) it.next()).visit(this));
    /* create a new ClassFile */
    ClassFile cf = new ClassFile();
    ClassFileInfo cfi = new ClassFileInfo(cf, tname);
    cfi.setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_FINAL));
    cfi.setParent(dataname);
    cf.setClassFileInfo(cfi);
    /* create fields for constructor and compute constructor signature */
    StringBuffer initsig = new StringBuffer("(");
    /* append datatype parameters to constructor */
    String supersig = initsig.toString() + ")V";
    StringBuffer factsigsb = new StringBuffer("(");
    it = l.iterator();
    int i = 0;
    while (it.hasNext()) {
      Class fcls = (Class) it.next();
      /* type of argument is boxed if non strict */
      if (!type.isStrict(i))
        fcls = JObject.class;
      FieldFileInfo ffi = new FieldFileInfo(cf);
      ffi.setName("_" + i++);
      ffi.setFlags((short) (Constants.ACC_PUBLIC));
      String ftname = TypeHelper.getInternalName(fcls);
      initsig.append(ftname);
      factsigsb.append(ftname);
      ffi.setType(ftname);
      cf.add(ffi);
    }
    initsig.append(")V");
    /*
     * create constructor for constructor !!! - if no arguments, generate
     * singleton instance and private constructor - if one or more arguments,
     * simply generate package fr.lifl.constructor
     */
    MethodFileInfo mfi = new MethodFileInfo(cf);
    mfi.setName("<init>");
    if (l.size() == 0)
      mfi.setFlags((short) Constants.ACC_PRIVATE);
    else
      mfi.setFlags((short) Constants.ACC_PUBLIC);
    mfi.setType(initsig.toString());
    short supref = MethodRefData.create(cf.getConstantPool(), dataname,
        "<init>", supersig);
    CodeAttribute code = new CodeAttribute(cf, (short) (l.size() + 1));
    Sequence seq = new Sequence(cf);
    seq._aload_0(); // load this
    /* load type arguments if there are any */
    seq._invokespecial(supref, cf); // call super.<init>
    /* initialize fields */
    it = l.iterator();
    i = 1;
    while (it.hasNext()) {
      Class cls = (Class) it.next();
      /* type of argument is boxed if non strict */
      if (!type.isStrict(i - 1))
        cls = JObject.class;
      short findex = FieldRefData.create(cf.getConstantPool(), tname, "_"
          + (i - 1), TypeHelper.getInternalName(cls));
      seq._aload_0().add(TypedInst.load(cls,cf, i++))._putfield(findex, cf);
    }
    /* initialize nargs and maxargs */
    seq._return();
    code.add(seq);
    mfi.addAttribute(code);
    cf.add(mfi);
    /* create a static instance for singleton non-parametric data types */
    if (l.size() == 0) {
      /* create field instance */
      FieldFileInfo ffi = new FieldFileInfo(cf);
      ffi.setName("_instance");
      ffi
          .setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_FINAL | Constants.ACC_STATIC));
      ffi.setType("L" + dataname + ";");
      cf.add(ffi);
      /* initialize instance in clinit method */
      mfi = new MethodFileInfo(cf);
      mfi.setFlags((short) Constants.ACC_PUBLIC);
      mfi.setName("<clinit>");
      mfi.setType("()V");
      mfi.setFlags((short) (Constants.ACC_STATIC | Constants.ACC_PUBLIC));
      short clref = ClassData.create(cf.getConstantPool(), dataname);
      code = new CodeAttribute(cf, (short) 0);
      seq = new Sequence(cf);
      clref = ClassData.create(cf.getConstantPool(), tname);
      short fref = FieldRefData.create(cf.getConstantPool(), tname,
          "_instance", "L" + dataname + ";");
      supref = MethodRefData.create(cf.getConstantPool(), tname, "<init>",
          initsig.toString());
      seq._new(clref)._dup()._invokespecial(supref, cf)._putstatic(fref, cf)
          ._return();
      code.add(seq);
      mfi.addAttribute(code);
      cf.add(mfi);
    } else {
      /* create argument less constructor */
      mfi = new MethodFileInfo(cf);
      mfi.setFlags((short) Constants.ACC_PUBLIC);
      mfi.setName("<init>");
      mfi.setType("()V");
      code = new CodeAttribute(cf, (short) (1));
      seq = new Sequence(cf);
      seq._aload_0(); // load this
      /* load type arguments if there are any */
      seq._invokespecial(supref, cf); // call super.<init>
      seq._return();
      code.add(seq);
      mfi.addAttribute(code);
      cf.add(mfi);
    }
    // store class
    BytecodeGenerator.generateClass(tname, cf);
    Class klass;
    try {
        klass = BytecodeGenerator.getLoader().loadClass(tname);
   } catch (ClassNotFoundException e) {
        return null;
    }
    // resolve class
    
    return klass;
  }

  /**
   * @see jaskell.compiler.datatypes.DataTypeVisitor#visit(ConstructorDefinition)
   */
  public Object visit(ConstructorDefinition cdef) {
    /* name of definition */
    String dname = BytecodeGenerator.encodeName2Java(cdef.getName());
    try {
      return Class.forName(dname);
    } catch (Exception ex) {
      try {
        return BytecodeGenerator.getLoader().loadClass(dname);
      } catch (Exception eex) {
        return generateClassFromCtor("fr/lifl/jaskell/runtime/types/JMessage", null,
            cdef);
      }
    }
  }

  /**
   * @see jaskell.compiler.datatypes.DataTypeVisitor#visit(DataDefinition)
   */
  public Object visit(DataDefinition ddef) {
    /* name of data definition is translated */
    String dname = BytecodeGenerator.encodeName2Java(ddef.getName());
    try {
      return Class.forName(dname);
    } catch (Exception ex) {
      try {
        return BytecodeGenerator.getLoader().loadClass(dname);
      } catch (Exception eex) {
        return generateClassFromData(ddef);
      }
    }
  }

  /**
   * @param ddef
   * @return
   */
  private Object generateClassFromData(DataDefinition ddef) {
    // type name
    String tname = BytecodeGenerator.encodeName2Java(ddef.getName());
    /* create a new ClassFile */
    ClassFile cf = new ClassFile();
    ClassFileInfo cfi = new ClassFileInfo(cf, tname);
    cfi.setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_ABSTRACT));
    cf.setClassFileInfo(cfi);
    cfi.setParent("fr/lifl/jaskell/runtime/types/JValue");
    generateClass(tname, cf);
    /* generate constructor */
    MethodFileInfo mfi = new MethodFileInfo(cf);
    mfi.setFlags((short) (Constants.ACC_PUBLIC));
    mfi.setName("<init>");
    mfi.setType("()V");
    short supref = MethodRefData.create(cf.getConstantPool(),
        "fr/lifl/jaskell/runtime/types/JValue", "<init>", "()V");
    CodeAttribute code = new CodeAttribute(cf, (short) (1));
    Sequence seq = new Sequence(cf);
    seq._aload_0() // load this
        ._invokespecial(supref, cf)._return(); // call super.<init>
    code.add(seq);
    mfi.addAttribute(code);
    cf.add(mfi);
    /*
     * generate methods and class for constructors Iterator it =
     * ddef.getConstructors().iterator(); while (it.hasNext()) {
     * ConstructorDefinition cdef = (ConstructorDefinition) it.next();
     * generateClassFromCtor(tname, cf, cdef); }
     */
    // define class in current class loader
    Class klass;
    try {
        klass = BytecodeGenerator.getLoader().loadClass(tname);
    } catch (ClassNotFoundException e) {
        return null;
    }
    return klass;
  }

  /**
   * Method generateMethodHeader.
   * 
   * @param cf
   * @param n
   * @param t
   */
  private void generateMethodHeader(ClassFile cf, String n, Type t) {
    MethodFileInfo mfi = new MethodFileInfo(cf);
    mfi.setFlags((short) (Constants.ACC_PUBLIC | Constants.ACC_ABSTRACT));
    mfi.setName(encodeName2Java(n));
    mfi.setType(Type2Class.encodeType2Bytecode(t));
    cf.add(mfi);
  }

  /**
   * Returns the context.
   * 
   * @return Set
   */
  public Set getContext() {
    return context;
  }

  /**
   * Sets the context.
   * 
   * @param context
   *          The context to set
   */
  public void setContext(Set context) {
    this.context = context;
  }

  /**
   * Method encodeType2Bytecode. Returns the signature of the given type as
   * encoded in class files.
   * 
   * @param type
   * @return String
   */
  public static String encodeType2Bytecode(Type type) {
    Type2String tv = new Type2String(/* type.getAssumptions() */
    );
    List l = new ArrayList();
    Type cur = type;
    Class cls;
    StringBuffer tsig = new StringBuffer("");
    log.finest("Trying to encode " + type);

    if (type instanceof TypeApplication) { // iterate over function args
      tsig.append("(");
      Iterator fti = PrimitiveType.functionIterator(type);
      while (fti.hasNext()) {
        cur = (Type) fti.next();
        tsig.append(((TypeApplication) ((TypeApplication) cur).getDomain())
            .getRange().visit(tv));
      }
      tsig.append(")");
      // append return type
      tsig.append(((TypeApplication) cur).getRange().visit(tv));
    } else {
      tsig.append("()").append(cur.visit(tv));
    }
    return tsig.toString();
  }

  /**
   * @see jaskell.compiler.JaskellVisitor#visit(Module)
   */
  public Object visit(Module a) {
    module = a;
    /* visit sub modules */
    Iterator it = a.getModules().iterator();
    while (it.hasNext()) {
      Module mod = (Module) it.next();
      Module tmp = module;
      mod.visit(this);
      module = tmp;
    }
    /* generate code for type classes definitions */
    it = a.getTypeDefinitions().iterator();
    while (it.hasNext()) {
      Definition def = (Definition) it.next();
      try {
        log.finest("In module " + a.getName() + ", visiting symbol "
            + def.getName());
        // compile expression - normally generate a CodeAttribute object
        Class cls = (Class) def.visit(this);
      } catch (Exception ex) {
        log.severe("Error in code generation for definition " + def.getName());
        ex.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Return the boxed Class object for the given primitive class
   * 
   * @param cls
   * @return
   */
  public static Class boxed(Class c) {
    if (c.isPrimitive())
      switch (TypeHelper.getInternalName(c).charAt(0)) {
      case 'I':
        return JInt.class;
      case 'Z':
        return JBoolean.class;
      case 'F':
        return JFloat.class;
      case 'D':
        return JDouble.class;
      case 'S':
        return JInt.class;
      case 'C':
        return JChar.class;
      case 'B':
        return JInt.class;
      default:
        throw new IllegalArgumentException();
      }
    else
      return JObject.class;
  }

  /*
   * (non-Javadoc)
   * 
   * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveData)
   */
  public Object visit(PrimitiveData a) {
    return a.getKlass();
  }

  /*
   * (non-Javadoc)
   * 
   * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveConstructor)
   */
  public Object visit(PrimitiveConstructor a) {
    return a.getJavaClass();
  }

}