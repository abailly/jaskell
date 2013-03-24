/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.parsing.Namespace;


/**
 * A skeletal implementation of JaskellVisitor.
 *
 * <p>This implementation of @see{JaskellVisitor} is provided as an alternative to full implementation of visitor. Allt
 * he <code>visit</code> methods return the passed object.
 *
 * @author  bailly
 * @version $Id: JaskellVisitorAdapter.java 1154 2005-11-24 21:43:37Z nono $
 */
public class JaskellVisitorAdapter implements JaskellVisitor {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for JaskellVisitorAdapter.
     */
    public JaskellVisitorAdapter() {
        super();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
     */
    public Object visit(Abstraction a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
     */
    public Object visit(Alternative a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Application)
     */
    public Object visit(Application a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(BooleanLiteral)
     */
    public Object visit(BooleanLiteral a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(CharLiteral)
     */
    public Object visit(CharLiteral a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Constructor)
     */
    public Object visit(Constructor a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Definition)
     */
    public Object visit(Definition a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(DoubleLiteral)
     */
    public Object visit(DoubleLiteral a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(FloatLiteral)
     */
    public Object visit(FloatLiteral a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(IntegerLiteral)
     */
    public Object visit(IntegerLiteral a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Module)
     */
    public Object visit(Module a) {
        return visit((Namespace) a);
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(StringLiteral)
     */
    public Object visit(StringLiteral a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Variable)
     */
    public Object visit(Variable a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(QualifiedVariable)
     */
    public Object visit(QualifiedVariable a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorPattern)
     */
    public Object visit(ConstructorPattern a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(LocalBinding)
     */
    public Object visit(LocalBinding a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(PrimitiveFunction)
     */
    public Object visit(PrimitiveFunction a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Conditional)
     */
    public Object visit(Conditional a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorDefinition)
     */
    public Object visit(ConstructorDefinition a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(DataDefinition)
     */
    public Object visit(DataDefinition a) {
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Let)
     */
    public Object visit(Let a) {
        return visit((Namespace) a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveConstructor)
     */
    public Object visit(PrimitiveConstructor a) {
        return visit((ConstructorDefinition) a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveData)
     */
    public Object visit(PrimitiveData a) {
        return visit((DataDefinition) a);
    }

    /**
     * @param  namespace
     *
     * @return
     */
    protected Object visit(Namespace namespace) {
        return namespace;
    }

}
