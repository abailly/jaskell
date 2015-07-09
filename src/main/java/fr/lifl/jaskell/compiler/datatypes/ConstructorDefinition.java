/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.datatypes;

import java.util.*;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Binder;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.Types;


/**
 * @author  bailly
 * @version $Id: ConstructorDefinition.java 1154 2005-11-24 21:43:37Z nono $
 */
public class ConstructorDefinition extends Definition implements Binder {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private List parameters;

    private BitSet strict;

    private int argCount = 0;

    private Type dataType;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for ConstructorDefinition.
     *
     * @param name
     * @param type
     * @param expr
     * @param module
     */
    public ConstructorDefinition(String name, Type type, Module module) {
        super(name, type, null, module);
        this.dataType = type;
        parameters = new ArrayList();
        strict = new BitSet();
        module.bind(name, this);
//              module.addTypeDefinition(this);
    }

    public ConstructorDefinition(String name, Type type, Module module, Type[] args) {
        this(name, type, module);
        this.dataType = type;
        parameters.addAll(Arrays.asList(args));
        argCount = args.length;
        strict = new BitSet(args.length);
    }

    /**
     * Constructor with strictness information
     *
     * @param name
     * @param type
     * @param module
     * @param args
     */
    public ConstructorDefinition(String name, Type type, Module module, Type[] args, int[] strictArgs) {
        this(name, type, module);
        this.dataType = type;
        parameters.addAll(Arrays.asList(args));
        argCount = args.length;
        strict = new BitSet(args.length);
        for (int i = 0; i < strictArgs.length; i++)
            strict.set(strictArgs[i]);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Adds an argument to this constructor definition
     *
     * @param at a Type parameter
     */
    public void addParameter(Type at) {
        parameters.add(at);
        strict.clear(argCount++);
    }

    public void addStrictParameter(Type at) {
        parameters.add(at);
        System.out.println("Adding strict parameter of type " + at + " to constructor " + getName());
        strict.set(argCount++);
    }

    public List getParameters() {
        return parameters;
    }

    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * @see jaskell.compiler.core.Expression#lookup(String)
     */

    public Expression lookup(String vname) {
        if (vname.equals(getName()))
            return this;
        return getParent().lookup(vname);
    }

    /**
     * A cosntructor definition is its data definition
     *
     * @see jaskell.compiler.core.Binding#getDefinition()
     */
    public Expression getDefinition() {
        return getParent();
    }

    /**
     * @see jaskell.compiler.core.Definition#setDefinition(Expression)
     */
    public void setDefinition(Expression definition) {
    }

    public Type getType() {
        // return precalculated type if available
        if (super.getType() != null)
            return super.getType();
        Type ret = getDataType();
        if (ret == null)
            return null;
        Type[] binds = (Type[]) parameters.toArray(new Type[0]);
        for (int i = binds.length; i > 0; i--)
            ret = Types.fun(binds[i - 1], ret);
        setType(ret);
        return ret;
    }

    /**
     * Returns the type of data this constructor builds This method defaults to returning the type stored into parent
     * DataDefinition
     *
     * @return a Type object
     */
    public Type getDataType() {
        if (dataType != null)
            return dataType;
        return getParent().getType();
    }

    /**
     * @see jaskell.compiler.core.Binder#isStrict(int)
     */
    public boolean isStrict(int i) {
        return strict.get(i);
    }

    public void setStrict(int i) {
        strict.set(i);
    }

    /**
     * @see jaskell.compiler.core.Binder#setStrict(String)
     */
    public void setStrict(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return
     */
    public String getInstanceName() {
        return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("").append(getName());
        Iterator it = parameters.iterator();
        while (it.hasNext())
            sb.append(' ').append(it.next());
        return sb.toString();
    }

}
