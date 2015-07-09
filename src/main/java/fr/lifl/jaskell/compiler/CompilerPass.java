/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.lifl.jaskell.compiler.bytecode.BytecodeGenerator;
import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.runtime.modules.Prelude;


/**
 * @author  bailly
 * @version $Id: CompilerPass.java 1154 2005-11-24 21:43:37Z nono $
 */
public abstract class CompilerPass extends JaskellVisitorAdapter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Method for finding primitive methods in builtin modules : Prelude and PreludeBuiltin Returns a method given a
     * function name and arguments types. The function name is given as a valid Jaskell name (if it is an operator, it
     * must be enclosed in parenthesis) and the types as valid Jaskell static type.
     *
     * @param fname function name to resolve
     * @param type  the type of function to find.
     */
    public static Method resolvePrimitive(String fname, Type type) {
        String rname = BytecodeGenerator.encodeName2Java(fname);
        Class[] cls = BytecodeGenerator.encodeType2JavaArgs(type);
        try {
            // try to find method in prelude
            return Prelude.class.getDeclaredMethod(rname, cls);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to resolve " + fname + " :: " + type);
            return null;
        }
    }

    /**
     * Method resolve. Returns a fully qualified name given a simple name
     *
     * @param  string
     *
     * @return String
     */
    public static String resolve(String name) {
        Expression ex = Module.PRELUDE.lookup(name);
        if (ex != null)
            return "Prelude." + name;
        else
            return name;
    }

    /**
     * Generate a call to <code>error</code> function. This method generates a call to the <code>error</code> function
     * yielding a runtime exception with given message.
     *
     * @param  s message to display
     *
     * @return Expression
     */
    public Expression error(String s) {
        Application app = new Application();
        QualifiedVariable qv = new QualifiedVariable("error");
        qv.addPathElement("Prelude");
        app.setFunction(qv);
        app.addArgument(new StringLiteral(s));
        return app;
    }

    /**
     * Method staticPatternMatch. This method compares a Pattern expression and a literal expression. If they can be
     * matched, then return true.
     *
     * @param  p
     * @param  expr
     *
     * @return boolean
     */
    protected boolean staticPatternMatch(Pattern p, Literal expr) {
        if (p instanceof LocalBinding)
            return true;
        else if (p instanceof Literal)
            return p.equals(expr);
        return false;
    }

    /**
     * Method tryApply. Tries to apply the application node to its arguments and evaluates its result. This method is
     * must be called with all arguments from Literal and functional expression from Variable.
     *
     * @param  a
     *
     * @return Expression
     */
    protected Expression tryApply(Application a) {
        // assume fun is a variable
        Variable v = (Variable) a.getFunction();
        String fname = (v).getName();
        // try to find fname in Prelude - assume type of function
        // is correctly defined
        Type t = v.getType();
        try {
            Method m = CompilerPass.resolvePrimitive(fname, t);
            // try to invoke method with arguments
            // unpack arguments to unboxed values
            Object[] objs = unpackArgs(a);
            Object res = m.invoke(null, objs);
            // pack result
            return packArg(res);
        } catch (Exception e) { // if anything goes wrong, log and return a
            System.err.println("Unable to compute value from function " + fname + "::" + t);
            return a;
        }
    }

    /**
     * Method packArg. This method transform a constant object to a Literal expression
     *
     * @param  res
     *
     * @return Expression
     */
    protected Expression packArg(Object res) {
        return Literal.pack(res);
    }

    /**
     * Method unpackArgs. This method takes an application spine and converts literal arguments to an array of Object
     * values.
     *
     * @param  a
     *
     * @return Object[]
     */
    protected Object[] unpackArgs(Application a) {
        List l = new ArrayList();
        Iterator it = a.getArgs().iterator();
        while (it.hasNext()) {
            // assume arguments are literals
            Literal lit = (Literal) it.next();
            l.add(lit.unpack());
        }
        return l.toArray();
    }

}
