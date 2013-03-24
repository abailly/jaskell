package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.BooleanLiteral;
import fr.lifl.jaskell.compiler.core.CharLiteral;
import fr.lifl.jaskell.compiler.core.Conditional;
import fr.lifl.jaskell.compiler.core.Constructor;
import fr.lifl.jaskell.compiler.core.ConstructorPattern;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.DoubleLiteral;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.FloatLiteral;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.Pattern;
import fr.lifl.jaskell.compiler.core.PrimitiveFunction;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.StringLiteral;
import fr.lifl.jaskell.compiler.core.Variable;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.types.TypeSubstitution;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A general substitution class for replacing free type variables occuring in an expression
 * 
 * This substitution algorithms replaces free type variables in an expression according
 * to a given map. 
 * 
 * @author bailly
 * @version $Id: TyvarSubstitution.java 1154 2005-11-24 21:43:37Z nono $
 */
public class TyvarSubstitution extends JaskellVisitorAdapter {


	/* the substitution object */
	private TypeSubstitution subst;
	
	/**
	 * Constructor for Substitution.
	 */
	public TyvarSubstitution(HashMap subst) {
		this.subst = new TypeSubstitution(subst);
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
	 */
	public Object visit(Abstraction a) {
		/* replace type of bindings */
		Iterator it = a.getBindings().values().iterator();
		while(it.hasNext()) {
			Pattern pat =(Pattern)it.next();
			pat.visit(this);
		}
		/* substitute body */
		a.getBody().visit(this);		
		/* substitute this type */
		a.setType(subst.substitute(a.getType()));
		return a;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
	 */
	public Object visit(Alternative a) {
		/* apply substitution to evaluated expression */
		a.getExpression().visit(this);
		LocalBinding lb = a.getBinding();
		if(lb != null)
			lb.visit(this);
		/* visit patterns to hide variables */
		Iterator it = a.getPatterns();
		while(it.hasNext()) {
			Pattern pat = (Pattern)it.next();
			pat.visit(this);
			a.getBody(pat).visit(this);
		}
		/* apply substiution to default expression */
		a.getWildcard().visit(this);
		a.setType(subst.substitute(a.getType()));
		return a;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Application)
	 */
	public Object visit(Application a) {
		/* substitute all elements of a */
		a.getFunction().visit(this);
		Iterator it = a.getArgs().iterator();
		while(it.hasNext()) 
			((Expression)it.next()).visit(this);
		a.setType(subst.substitute(a.getType()));
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
	 * @see jaskell.compiler.JaskellVisitor#visit(ConstructorDefinition)
	 */
	public Object visit(ConstructorDefinition a) {
		return null;
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
		a.setType(subst.substitute(a.getType()));
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
		while(it.hasNext()) {
			Pattern pat = (Pattern)it.next();
			pat.visit(this);
		}
		a.setType(subst.substitute(a.getType()));
		return a;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(LocalBinding)
	 */
	public Object visit(LocalBinding a) {
		a.setType(subst.substitute(a.getType()));
		return a;
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
		conditional.getCondition().visit(this);
		conditional.getIfFalse().visit(this);
		conditional.getIfTrue().visit(this);
		conditional.setType(subst.substitute(conditional.getType()));
		return conditional;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(DataDefinition)
	 */
	public Object visit(DataDefinition def) {
		return null;
	}

}
