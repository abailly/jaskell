/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler.datatypes;

import java.util.ArrayList;
import java.util.List;

import fr.lifl.jaskell.compiler.CompilerException;
import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.types.*;


/**
 * @author  bailly
 * @version $Id: DataDefinition.java 1154 2005-11-24 21:43:37Z nono $
 */
public class DataDefinition extends Definition {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* list of constructors for data type */
    private List constructors;

    /* list of type paremeters */
    private List parameters;

    /* type constructor expression */
    private Type type;

    /* kind of this constructor */
    private Kind kind;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for DataDefinition.
     */
    public DataDefinition(String name, Module mod) {
        this(name, Types.makeTycon(name), mod);
    }

    /**
     * Constructs a data definition with a simple type. The given type argument must be application of a type
     * constructor to zero or more type variables
     *
     * @param t   a Type
     * @param mod Module where this definition is stored
     */
    public DataDefinition(String name, Type t, Module mod) {
        super(name, null, null, mod);
        if (t instanceof TypeApplication) {
            TypeConstructor tc = (TypeConstructor) t.getConstructor();
            /* construct kind */
            this.kind = tc.getKind();
        } else if (t instanceof TypeConstructor) {
            this.kind = SimpleKind.K;
        } else
            throw new CompilerException("Invalid type argument for data definition :" + t.makeString() + " type = " + t.getClass());
        this.type = t;
        this.constructors = new ArrayList();
        this.parameters = new ArrayList();
        mod.addTypeDefinition(this);
        //mod.bind(name, this);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Adds a new constructor for this data type
     *
     * @param cdef a Constructor definition object
     */
    public void addConstructor(ConstructorDefinition cdef) {
        constructors.add(cdef);
        cdef.setParent(this);
    }

    /**
     * Return the set of constructors for this data type
     *
     * @return a - possibly empty - list
     */
    public List getConstructors() {
        return constructors;
    }

    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    public Type getType() {
        return type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return type.makeString();
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof DataDefinition))
            return false;
        return getName().equals(((DataDefinition) obj).getName());
    }

}
