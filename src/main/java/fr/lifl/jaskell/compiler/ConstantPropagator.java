/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.compiler;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;


/**
 * The constant propagator reduces the program complexity by precomputing constant expressions and propagating them. The
 * ConstantPropagator visitor may change the structure of JAskell-Core tree by eliminating unuseful instructions.
 *
 * @author  bailly
 * @version $Id: ConstantPropagator.java 1154 2005-11-24 21:43:37Z nono $
 */
public class ConstantPropagator extends CompilerPass {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /** current module */
    private Module module;

    private boolean inConst;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
     */
    public Object visit(Abstraction a) {
        Expression body = (Expression) a.getBody().visit(this);
        a.setBody(body);
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
     */
    public Object visit(Alternative a) {
        // simplify alternatives
        Iterator it = a.getPatterns();
        while (it.hasNext()) {
            Pattern p = (Pattern) it.next();
            Expression e = a.getBody(p);
            Expression e2 = (Expression) e.visit(this);
            // rebind pattern
            if (e2 != e)
                a.addPattern(p, e2);
        }
        // simplify expression
        Expression expr = (Expression) a.getExpression().visit(this);
        a.setExpression(expr);
        // checks the case where the evaluated expression is a literal
        // we replace the case expression with the sub expression corresponding
        // to the constant
        if (expr instanceof Literal) {
            it = a.getPatterns();
            while (it.hasNext()) {
                Pattern p = (Pattern) it.next();
                if (staticPatternMatch(p, (Literal) expr))
                    return a.getBody(p);
            }
            // if no pattern matches, return catchall expressino
            return a.getWildcard();
        }
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Application)
     */
    public Object visit(Application a) {
        boolean allConsts = true;
        // propagate constants in arguments
        ListIterator it = a.getArgs().listIterator();
        while (it.hasNext()) {
            Expression expr = (Expression) it.next();
            Expression expr2 = (Expression) expr.visit(this);
            // replace argument if folded
            if (expr != expr2)
                it.set(expr2);
            if (!(expr2 instanceof Literal))
                allConsts = false;
        }
        Expression fun = a.getFunction();
        Expression fun2 = (Expression) fun.visit(this);
        if (fun2 != fun)
            a.setFunction(fun2);
        // try to fold if all arguments are constant and function
        // is known by name rather than being the result of a computation
        if (allConsts && (fun2 instanceof Variable)) {
            return tryApply(a);
        }
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
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorDefinition)
     */
    public Object visit(ConstructorDefinition a) {
        // does not propagate constant in ctor definition as
        // it cannot contains other definitions
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Definition)
     */
    public Object visit(Definition a) {
        Expression def = a.getDefinition();
        def = (Expression) def.visit(this);
        a.setDefinition(def);
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
     * Module visit is the root of ConstantPropagator
     *
     * @see jaskell.compiler.JaskellVisitor#visit(Module)
     */
    public Object visit(Module a) {
        Map map = a.getSymbols();
        // visit all symbols
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String sname = (String) it.next();
            Expression ex = (Expression) map.get(sname);
            ex = (Expression) ex.visit(this);
            // rebind symbol to returned expression
            a.rebind(sname, ex);
        }
        return a;
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
     * @see jaskell.compiler.JaskellVisitor#visit(QualifiedVariable)
     */
    public Object visit(QualifiedVariable a) {
        return a;
    }

}
