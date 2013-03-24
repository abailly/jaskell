/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.util.Iterator;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.jaskell.runtime.types.JFunction;
import fr.lifl.jaskell.runtime.types.JObject;


/**
 * An asbtract interpreter implementing an evaluation loop but without primitive functions or litteral constants
 * definition; This class implements the skeleton of an abstract interpreter. Its main method is the <code>
 * eval(Expression)</code> method which runs an infinite loop through the given code fragment, recursively visiting its
 * constructs.
 *
 * @author  bailly
 * @version $Id: AbstractInterpreter.java 1154 2005-11-24 21:43:37Z nono $
 */
public abstract class AbstractInterpreter extends JaskellVisitorAdapter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Main evaluation loop of interpreter
     *
     * @param  expr the expression to evaluate
     *
     * @return an evaluated expression. This method may loop forever if code does not terminate.
     */
    public JObject eval(Expression expr) {
        return (JObject) expr.visit(this);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Application)
     */
    public Object visit(Application a) {
        try {
            JFunction fun = (JFunction) a.getFunction().visit(this);
            Iterator it = a.getArgs().iterator();
            while (it.hasNext())
                fun.apply((JObject) ((Expression) it.next()).visit(this));
            return fun.eval();
        } catch (ClassCastException ccex) {
            throw new CompilerException("Cannot interpret " +
                a +
                " : functional argument is not a function");
        }
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Abstraction)
     */
    public Object visit(Abstraction a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Alternative)
     */
    public Object visit(Alternative a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.BooleanLiteral)
     */
    public Object visit(BooleanLiteral a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.CharLiteral)
     */
    public Object visit(CharLiteral a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Conditional)
     */
    public Object visit(Conditional a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Constructor)
     */
    public Object visit(Constructor a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.ConstructorPattern)
     */
    public Object visit(ConstructorPattern a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.DoubleLiteral)
     */
    public Object visit(DoubleLiteral a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.FloatLiteral)
     */
    public Object visit(FloatLiteral a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.IntegerLiteral)
     */
    public Object visit(IntegerLiteral a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Let)
     */
    public Object visit(Let a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.LocalBinding)
     */
    public Object visit(LocalBinding a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveConstructor)
     */
    public Object visit(PrimitiveConstructor a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveData)
     */
    public Object visit(PrimitiveData a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.PrimitiveFunction)
     */
    public Object visit(PrimitiveFunction a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.QualifiedVariable)
     */
    public Object visit(QualifiedVariable a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.StringLiteral)
     */
    public Object visit(StringLiteral a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Variable)
     */
    public Object visit(Variable a) {
        // TODO Auto-generated method stub
        return super.visit(a);
    }

}
