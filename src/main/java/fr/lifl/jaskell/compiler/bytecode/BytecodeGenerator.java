package fr.lifl.jaskell.compiler.bytecode;

import fr.lifl.jaskell.compiler.CompilerPass;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.TypeApplication;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.norsys.klass.bytes.ByteArrayClassLoader;
import fr.norsys.klass.bytes.ClassFile;
import fr.norsys.klass.bytes.ClassFileHandler;
import fr.norsys.klass.bytes.Opcodes;

/**
 * @author bailly
 * @version $Id: BytecodeGenerator.java 1207 2006-05-26 08:55:33Z nono $
 */
public class BytecodeGenerator extends CompilerPass implements Opcodes {

  /** static map of generated class files indexed by names */
  private static Map generatedClass = new HashMap();

  private static ByteArrayClassLoader loader;

  /**
   * @param loader
   *          The loader to set.
   */
  public static void setLoader(ClassLoader loader) {
    BytecodeGenerator.loader = (ByteArrayClassLoader) loader;
   // System.err.println("Setting class loader to "+loader);
  }

  /**
   * Cleanup generated class files by iterating over the current map and calling
   * a ClassFileHandler
   * 
   * @param cfh
   *          ClassFileHandler
   */
  public static synchronized void cleanupClassFiles(ClassFileHandler cfh) {
    Iterator it = generatedClass.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String name = (String) entry.getKey();
      ClassFile cf = (ClassFile) entry.getValue();
      cfh.handle(name, cf);
      it.remove();
    }
  }

  public static ClassLoader getLoader() {
    return loader;
  }

  /**
   * Method encodeType2Java. Transforms a - function - type into an array of
   * java classes allowing resolution of method.
   * 
   * @param type
   * @return Class[]
   */
  public static Class[] encodeType2Java(Type type) {
    Type2Class tv = new Type2Class();
    List l = new ArrayList();
    Iterator fti = PrimitiveType.functionIterator(type);
    Type cur = type;
    while (fti.hasNext()) {
      cur = (Type) fti.next();
      Type dom = ((TypeApplication) ((TypeApplication) cur).getDomain())
          .getRange();
      l.add(dom.visit(tv));
    }
    l.add(((TypeApplication) cur).getRange().visit(tv));
    return (Class[]) l.toArray(new Class[0]);
  }

  /**
   * Method encodeType2Java. Transforms a - function - type into an array of
   * java classes allowing resolution of method. The return argument is not
   * used.
   * 
   * @param type
   * @return Class[]
   */
  public static Class[] encodeType2JavaArgs(Type type) {
    Type2Class tv = new Type2Class();
    List l = new ArrayList();
    Iterator fti = PrimitiveType.functionIterator(type);
    Type cur = type;
    while (fti.hasNext()) {
      cur = (Type) fti.next();
      Type dom = ((TypeApplication) ((TypeApplication) cur).getDomain())
          .getRange();
      l.add(dom.visit(tv));
    }
    return (Class[]) l.toArray(new Class[0]);
  }

  /**
   * Method encode2java. Transforms a Jaskell function name into a valid java
   * name.
   * 
   * @param fname
   * @return String
   */
  public static String encodeName2Java(String fname) {
    StringBuffer sb = new StringBuffer(fname);
    StringBuffer newsb = new StringBuffer("");
    int i = 0;
    // special case for Prelude
    if (fname.equals("Prelude"))
      return "fr/lifl/jaskell/runtime/modules/Prelude";

    while (i < sb.length()) {
      char c = sb.charAt(i);
      switch (c) {
      case '(': // start of operator name ?? What about tuple ???
        String opname = encodeOperator2java(sb, ++i);
        newsb.append(opname);
        break;
      case '[': // start of list name
        opname = encodeList2java(sb, ++i);
        newsb.append(opname);
        break;
      case ')':
      case ']':
        return newsb.toString();
      case '.':
        i++;
        newsb.append('/');
        break;
      case ' ':
        i++;
        newsb.append('_');
        break;
      default:
        i++;
        newsb.append(c);
      }
    }
    return newsb.toString();
  }

  /**
   * Encode a string until closing parenthesis is found
   */
  public static String encodeList2java(StringBuffer op, int index) {
    StringBuffer sb = new StringBuffer("JList_");
    String content = encodeName2Java(op.substring(index));
    sb.append(content);
    // delete consumed characters
    op.delete(index, index + content.length());
    return sb.toString();
  }

  /**
   * Encode a string until closing parenthesis is found
   */
  public static String encodeOperator2java(StringBuffer op, int index) {
    StringBuffer sb = new StringBuffer("");
    int pardepth = 0;
    int i = 0;
    boolean stop = false;
    for (i = index; !stop && i < op.length(); i++) {
      char c = op.charAt(i);
      switch (c) {
      case '(':
        pardepth++;
        sb.append('_').append(Integer.toHexString(c));
        break;
      case ')': // the end
        if (pardepth-- == 0) {
          stop = true;
          break;
        }
      case '[':
      case ']':
      case ':':
      case '!':
      case '#':
      case '$':
      case '%':
      case '&':
      case '*':
      case '.':
      case ',':
      case '/':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case '\\':
      case '^':
      case '-':
      case '~':
      case '+':
        sb.append('_').append(Integer.toHexString(c));
        break;
      case ' ':
        continue;
      default:
        sb.append(c);
      }
    }
    // delete consumed characters
    op.delete(index, i);
    return sb.toString();
  }

  /**
   * Return java class objects for given type.
   * 
   * @param type
   *          a Type object
   * @return a Class object
   */
  public static Class encodeType2Class(Type type) {
    Type2Class tv = new Type2Class();
    return (Class) type.visit(new Type2Class());
    // get class of return type
  }

  public static ClassFile getGeneratedClass(String clname) {
    return (ClassFile) generatedClass.get(clname);
  }

  /**
   * @param tname
   * @param cf
   */
  public static void generateClass(String tname, ClassFile cf) {
    generatedClass.put(tname, cf);
    // define class in current class loader
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      cf.write(dos);
    } catch (java.io.IOException ex) {
      ex.printStackTrace();
    }
    /* store bytes of class and delay definition and linking */
    loader.putBytes(tname, bos.toByteArray());
  }

}