/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler;

import java.util.*;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;


/**
 * A general substitution class for replacing free variables in an expression This substitution algorithms replaces free
 * variables in an expression according to a given map. Variables occuring hidden beneath a lambda abstraction or a case
 * construction are not substituted
 *
 * @author  bailly
 * @version $Id: Substitution.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Substitution extends JaskellVisitorAdapter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* the mapping */
    private HashMap substitution;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for Substitution.
     */
    public Substitution(HashMap subst) {
        this.substitution = subst;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
     */
    public Object visit(Abstraction a) {
        HashMap subst = (HashMap) substitution.clone();
        HashMap old = substitution;
        substitution = subst;
        /* remove bound variables from map */
        Iterator it = a.getBindings().values().iterator();
        while (it.hasNext()) {
            Pattern pat = (Pattern) it.next();
            pat.visit(this);
        }
        /* substitute sub expressions */
        a.setBody((Expression) a.getBody().visit(this));
        substitution = old;
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
     */
    public Object visit(Alternative a) {
        HashMap subst = (HashMap) substitution.clone();
        HashMap old = substitution;
        /* apply substitution to evaluated expression */
        a.setExpression((Expression) a.getExpression().visit(this));
        /* apply substitution to all subexpressions */
        substitution = subst;
        /* remove bound variables from map */
        if (a.getBinding() != null)
            substitution.remove(a.getBinding().getName());
        /* visit patterns to hide variables */
        Iterator it = a.getPatterns();
        while (it.hasNext()) {
            Pattern pat = (Pattern) it.next();
            HashMap psubst = new HashMap(substitution);
            HashMap hidden = substitution;
            substitution = psubst;
            /* visit patterns to remove bound variables from substitution */
            pat.visit(this);
            a.setBody(pat, (Expression) a.getBody(pat).visit(this));
            substitution = hidden;
        }
        /* apply substiution to default expression */
        a.setWildcard((Expression) a.getWildcard().visit(this));
        substitution = old;
        return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Application)
     */
    public Object visit(Application a) {
        /* substitute all elements of a */
        a.setFunction((Expression) a.getFunction().visit(this));
        Iterator it = a.getArgs().iterator();
        List l = new ArrayList();
        while (it.hasNext())
            l.add((Expression) ((Expression) it.next()).visit(this));
        /* replace arguments in a */
        for (int i = 0; i < l.size(); i++)
            a.setArgument((Expression) l.get(i), i);
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
        Object nn = substitution.get(a.getName());
        if (nn != null)
            return nn;
        else
            return a;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorDefinition)
     */
    public Object visit(ConstructorDefinition a) {
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Definition)
     */
    public Object visit(Definition a) {
        HashMap nh = new HashMap(substitution);
        nh.remove(a.getName());
        HashMap old = substitution;
        substitution = nh;
        a.setDefinition((Expression) a.getDefinition().visit(this));
        substitution = old;
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
        return null;
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
        Object nn = substitution.get(a.getName());
        if (nn != null)
            return nn;
        else
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
        Iterator it = a.getSubPatterns();
        while (it.hasNext()) {
            Pattern pat = (Pattern) it.next();
            pat.visit(this);
        }
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(LocalBinding)
     */
    public Object visit(LocalBinding a) {
        substitution.remove(a.getName());
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(PrimitiveFunction)
     */
    public Object visit(PrimitiveFunction f) {
        return f;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Conditional)
     */
    public Object visit(Conditional conditional) {
        conditional.setCondition((Expression) conditional.getCondition().visit(this));
        conditional.setIfFalse((Expression) conditional.getIfFalse().visit(this));
        conditional.setIfTrue((Expression) conditional.getIfTrue().visit(this));
        return conditional;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Let)
     */
    public Object visit(Let a) {
        HashMap subst = (HashMap) substitution.clone();
        HashMap old = substitution;
        substitution = subst;
        /* remove bound variables from map */
        Iterator it = a.getBindings().keySet().iterator();
        while (it.hasNext()) {
            substitution.remove(it.next());
        }
        /* substitute rhs of definitions */
        it = a.getBindings().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            entry.setValue(((Expression) entry.getValue()).visit(this));
        }
        /* substitute sub expressions */
        a.setBody((Expression) a.getBody().visit(this));
        substitution = old;
        return a;
    }

}
