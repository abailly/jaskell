/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler;

import java.util.*;
import java.util.logging.Logger;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.types.*;
import fr.lifl.parsing.Namespace;


/**
 * A type inference engine for jaskell expressions This visitor tries to verify that typing information is correct for a
 * set of expressions, or that it can be inferred from subexpressions.
 *
 * <p>The visitor recursively checks sub-expressions for type of leaves and then tries to unify leaf types with node
 * types. If unification is possible, then the super-expression type is set and returned.
 *
 * @author  bailly
 * @version $Id: TypeChecker.java 1154 2005-11-24 21:43:37Z nono $
 */
public class TypeChecker extends CompilerPass {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static Logger log = Logger.getLogger(TypeChecker.class.getName());

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private Type argumentsType;

    /* map from current variable set to types */
    private HashMap namesMap;

    /* map from current type variables to types */
    private HashMap typeVariablesMap;

    /* current type substitution object */
    private TypeSubstitution subst;

    /* current unifier */
    private TypeUnifier tu;

    /* stack of names context */
    private Stack nameStack = new Stack();
    private Stack typeStack = new Stack();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for TypeChecker.
     */
    public TypeChecker() {
        typeVariablesMap = new HashMap(); /* default - empty - substitution */
        this.subst = new TypeSubstitution(typeVariablesMap);
        this.tu = new TypeUnifier();
        //TypeVariable.reset();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
     */
    public Object visit(Abstraction a) {
        try {
            Type t = a.getType();
            if (t != null)
                return subst.substitute(t);
            log.finest("Visiting abstraction : " + a);
            Expression body = a.getBody();
            /* duplicate bindings map to assign types to variables */
            pushContext(a.getBindings());
            /* create fresh type variables as type for each bound
             * variable */
            Iterator it = namesMap.values().iterator();
            LinkedList tl = new LinkedList();
            while (it.hasNext()) {
                LocalBinding name = (LocalBinding) it.next();
                Type vt = TypeFactory.freshBinding();
                name.setType(vt);
                tl.add(vt);
            }
            Type tv = TypeFactory.freshBinding();
            /* create type with all variables for function */
            Type ft = Types.fun(tl, tv);
            log.finer("In abstraction, setting type to " + ft);
            a.setType(ft);
            /* analyze body */
            Type bt = (Type) body.visit(this);
            /* unify return type of function with type of body */
            Type ret = tu.unify(PrimitiveType.getReturnType(ft), bt, typeVariablesMap);
            TyvarSubstitution tys = new TyvarSubstitution(typeVariablesMap);
            tys.visit(a);
            log.finer("Done abstraction, setting type from " +
                ft +
                " to " + a.getType());
            popContext();
            return a.getType();
        } catch (TypeError te) {
            if (te.getLineCol() == null)
                te.setLineCol(a.getTag("source"));
            throw te;
        }

    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
     */
    public Object visit(Alternative a) {
        try {
            Type tex = (Type) a.getExpression().visit(this);
            log.finer("In alternative, type of expression " + a.getExpression() + " is " + tex);

            /* set type of bound variable */
            LocalBinding lb = a.getBinding();
            if (lb != null)
                lb.setType(tex);
            /* visit type of alternatives */
            Iterator it = a.getChoices();
            Type ptype = tex;
            Type btype = null;
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                /* checkt type of pattern */
                Type pt = (Type) ((Pattern) entry.getKey()).visit(this);
                if (ptype == null)
                    ptype = pt;
                else
                    ptype = tu.unify(pt, ptype, typeVariablesMap);
                log.finer("In alternative, unifying pattern type " +
                    pt +
                    " to " + ptype);
                Expression expr = (Expression) entry.getValue();
                /* check type of body */
                Type bt = (Type) expr.visit(this);
                //                      /* apply substitution with type variables from pattern */
                //                      TypeSubstitution ts = new TypeSubstitution(typeVariablesMap);
                //                      expr.visit(ts);
                if (btype == null)
                    btype = bt;
                else
                    btype = tu.unify(bt, btype, typeVariablesMap);
                log.finer("In alternative, unifying body type " +
                    bt +
                    " to " + btype);
            }
            /* visit default choice */
            Type deft = (Type) a.getWildcard().visit(this);
            a.setType(btype);
            return btype;
        } catch (TypeError te) {
            if (te.getLineCol() == null)
                te.setLineCol(a.getTag("source"));
            throw te;
        }
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Application)
     */
    public Object visit(Application a) {
        try {
            /* get type of function */
            Expression fun = a.getFunction();
            /* get type deduced from arguments */
            LinkedList l = new LinkedList();
            Iterator it = a.getArgs().iterator();
            while (it.hasNext()) {
                Expression e = (Expression) it.next();
                l.add((Type) e.visit(this));
            }
            Type vt = TypeFactory.freshBinding();
            Type ft = Types.fun(l, vt);
            log.finer("TypeChecker => In application " + a + ", type is " + ft);
            /* apply substitution on both types */
            ft = subst.substitute(ft);
            /* try to unify function type and constructed types */
            Type t = (Type) fun.visit(this);
            log.finer("In application, function " + fun + " :: " + t);
            t = subst.substitute(t);
            log.finer("In application, trying to unify function type " +
                t +
                " with body " + ft);
            /*
             * TODO : problem with unification of constrained types
             */
            TypeApplication uni = (TypeApplication) tu.unify(t, ft, typeVariablesMap);
            /* sets type of functional expression - this allows
             * polymorphic functions to receive several types
             * in the same code */
            //fun.setType(uni);
            /* apply arguments type to compute return type */
            log.finer("Done unify application :" + uni);
            it = PrimitiveType.functionIterator(uni);
            Iterator it2 = l.iterator();
            TypeApplication ut = uni;
            while (it2.hasNext()) {
                /* type of argument */
                Type at = (Type) it2.next();
                ut = (TypeApplication) it.next();
                /* try unification */
                tu.unify(((TypeApplication) ut.getDomain()).getRange(), at, new HashMap(typeVariablesMap));
            }
            ft = subst.substitute(ft);
            fun.setType(ft);
            log.finer("Setting type of functional element [" + fun + "] to :" + ft);
            a.setType(ut.getRange());
            return ut.getRange();
        } catch (TypeError te) {
            if (te.getLineCol() == null)
                te.setLineCol(a.getTag("source"));
            throw te;
        }
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(BooleanLiteral)
     */
    public Object visit(BooleanLiteral a) {
        return PrimitiveType.BOOL;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(CharLiteral)
     */
    public Object visit(CharLiteral a) {
        return PrimitiveType.CHAR;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Constructor)
     */
    public Object visit(Constructor a) {
        Type ret = a.getType();
        if (ret != null)
            return ret;
        String vname = a.getName();
        ConstructorDefinition def = (ConstructorDefinition) a.lookup(vname);
        if (def == null) // unknown symbol
            throw new CompilerException("Unknown constructor " + vname);
        ret = new TypeInstantiator(def.getType()).instance();
        a.setType(ret);
        return ret;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorDefinition)
     */
    public Object visit(ConstructorDefinition a) {
        /* returns a FunctionType */
        return a.getType();
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Definition)
     */
    public Object visit(Definition a) {
        return a.getType();
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(DoubleLiteral)
     */
    public Object visit(DoubleLiteral a) {
        return PrimitiveType.DOUBLE;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(FloatLiteral)
     */
    public Object visit(FloatLiteral a) {
        return PrimitiveType.FLOAT;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(IntegerLiteral)
     */
    public Object visit(IntegerLiteral a) {
        return PrimitiveType.INT;
    }

    /**
     * Returns null or throws a TypeError if type cannot be inferred for any definition found. Verifies that main symbol
     * in Main module is typed as <code>IO ()</code>.
     *
     * @see jaskell.compiler.JaskellVisitor#visit(Module)
     */
    public Object visit(Namespace a) {
        // visit definitions
        Iterator it = a.getAllBindings().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Expression def = (Expression) entry.getValue();
            Type type = (Type) def.visit(this);
            def.setType(type);
            log.finer("TypeChecker => END Visiting " + name);
        }
        log.finer("Substitution map = " + typeVariablesMap);
        return null;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(StringLiteral)
     */
    public Object visit(StringLiteral a) {
        return PrimitiveType.STRING;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Variable)
     */
    public Object visit(Variable a) {
        Type ret = null;
        String vname = a.getName();
        Expression def = a.lookup(vname);
        if (def == null) // unknown symbol
            throw new CompilerException("Unknown variable " + vname);
        else
            ret = (Type) def.visit(this);
        a.setType(ret);
        return ret;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(ConstructorPattern)
     */
    public Object visit(ConstructorPattern a) {
        String cname = a.getConstructor().getName();
        /* retrieve parameter types of constructor */
        ConstructorDefinition ctor = (ConstructorDefinition) a.getConstructor().lookup(cname);
        if (ctor == null)
            throw new TypeError("Undefined constructor pattern  " + a);
        /* type of data constructed by constructor */
        TypeInstantiator ti = new TypeInstantiator(ctor.getDataType());
        Type rtype = ti.instance();
        Map map = ti.getMap();
        TypeSubstitution ts = new TypeSubstitution(map);
        Iterator ittypes = ctor.getParameters().iterator();
        /* retrieve type of patterns */
        Iterator it = a.getSubPatterns();
        while (it.hasNext()) {
            try {
                Pattern p = (Pattern) it.next();
                Type actual = TypeFactory.freshBinding();
                Type formal = ts.substitute((Type) ittypes.next());
                /* unify both types */
                p.setType(tu.unify(formal, actual, typeVariablesMap));
            } catch (NoSuchElementException nex) {
                throw new TypeError("Wrong number of arguments to pattern " + a);
            }
        }
        a.setType(rtype);
        return a.getType();
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
        /* module found */
        if (mod != null) {
            Expression def = mod.lookup(a.getName());
            if (def == null)
                throw new CompilerException("Unknown variable " + a.getName());
            Type t = def.getType();
            if (t == null)
                t = (Type) def.visit(this);

            /* as it is the case for variable, we assume
             * that a defined symbol may be overloaded (only for primitive types)
             * so we return a type variable and defers choice of
             * symbol to a later stage
             */
            a.setType(t);
            return t;
        }
        throw new CompilerException("Unable to find module needed for variable " + a.getName());
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Conditional)
     */
    public Object visit(Conditional conditional) {
        Type iff = (Type) conditional.getIfFalse().visit(this);
        Type ift = (Type) conditional.getIfTrue().visit(this);
        Type tcond = (Type) conditional.getCondition().visit(this);
        tcond = tu.unify(tcond, Primitives.BOOL, typeVariablesMap);
        if (!(tcond.equals(Primitives.BOOL)))
            throw new TypeError("Conditional expression is not of type Bool : " + tcond);
        /* unify false and true parts */
        Type uni = tu.unify(iff, ift, typeVariablesMap);
        conditional.setType(uni);
        return uni;
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(LocalBinding)
     */
    public Object visit(LocalBinding a) {
        return a.getType();
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(PrimitiveFunction)
     */
    public Object visit(PrimitiveFunction f) {
        return new TypeInstantiator(f.getType()).instance();
    }

    /**
     * @see jaskell.compiler.JaskellVisitor#visit(Let)
     */
    public Object visit(Let a) {
        Type ret = a.getType();
        if (ret != null)
            return ret;
        // visit definitions
        Iterator it = a.getBindings().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Expression def = (Expression) entry.getValue();
            log.finer("Visiting " + name);
            def.setType((Type) def.visit(this));
        }
        // visit body
        ret = (Type) a.getBody().visit(this);
        a.setType(ret);
        // return type of body
        return ret;

    }

    /* (non-Javadoc)
     * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Module)
     */
    public Object visit(Module a) {
        try {
            return visit((Namespace) a);
        } catch (TypeError te) {
            if (te.getLineCol() == null)
                te.setLineCol(a.getTag("source"));
            log.severe("Type error at (" + te.getLineCol() + ") : " + te.getMessage());
            throw te;
        }
    }

    /**
     * Save a stack of names and type vars maps. Given argument is list of bindings from which we construct a linked map
     * to store names
     *
     * @param linkedHashMap
     */
    private void pushContext(Map bindings) {
        nameStack.push(namesMap);
        typeStack.push(typeVariablesMap);
        namesMap = new LinkedHashMap(bindings);
        typeVariablesMap = new HashMap();
    }

    private void popContext() {
        if (nameStack.size() > 0)
            namesMap = (LinkedHashMap) nameStack.pop();
        if (typeStack.size() > 0)
            typeVariablesMap = (HashMap) typeStack.pop();
    }

}
