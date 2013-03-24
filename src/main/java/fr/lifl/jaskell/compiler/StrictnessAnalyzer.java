package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Binder;
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
import fr.lifl.jaskell.compiler.core.Let;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.PrimitiveFunction;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.StringLiteral;
import fr.lifl.jaskell.compiler.core.Variable;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.lifl.parsing.Namespace;

/**
 * @author bailly
 * @version $Id: StrictnessAnalyzer.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class StrictnessAnalyzer extends CompilerPass {

	private static Logger log =
		Logger.getLogger(StrictnessAnalyzer.class.getName());

	/**
	 * Constructor for StrictnessAnalyzer.
	 */
	public StrictnessAnalyzer() {
		super();
	}

	/** already analyzed abstraction objects */
	private Set analyzed = new HashSet();

	private static final Set emptySet = new HashSet();

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Abstraction)
	 */
	public Object visit(Abstraction a) {
		analyzed.add(a);
		/* define set of strict variables as all variables in abstraction */
		Map m = a.getBindings();
		Set str = m.keySet();
		setAllStrict(a, str);
		Set cur, old;
		cur = old = str;
		do {
			old = cur;
			/* calculate set of strict variables from body */
			cur = (Set) a.getBody().visit(this);
			setAllStrict(a, cur);
		} while (!cur.equals(old));
		return null;
	}

	private void setAllStrict(Abstraction a, Set set) {
		Iterator it = a.getBindings().keySet().iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
			if (set.contains(s))
				a.setStrict(s);
			else
				a.setNonStrict(s);
		}
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Alternative)
	 */
	public Object visit(Alternative a) {
		// visit condition to be evaluated
		Set s1 = (Set) a.getExpression().visit(this);
		// visit each alternative and retains intersection of all strict sets
		Set s2 = null;
		Iterator it = a.getBodies();
		while (it.hasNext()) {
			Expression expr = (Expression) it.next();
			if (s2 == null)
				s2 = (Set) expr.visit(this);
			else
				s2.retainAll((Set) expr.visit(this));
		}
		s1.addAll(s2);
		return s1;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Application)
	 */
	public Object visit(Application a) {
		/* evaluate functional term */
		Expression fun = a.getFunction();
		/* if fun is not a function name, nothing can be said and we return the strict set for this term only*/
		if (!(fun instanceof Variable))
			return fun.visit(this);
		/* we return the union of all strict sets from the  application parameters */
		Variable v = (Variable) fun;
		Expression e = v.lookup(v.getName());
		Binder abs = null;
		try {
			abs = (Binder) e;
			if (abs == null)
				return emptySet;
		} catch (ClassCastException ccex) {
			return emptySet;
		}
		log.finest("Visiting application with function " + v.getName());
		if (!analyzed.contains(abs) && abs instanceof Abstraction)
			 ((Abstraction) abs).visit(this);
		Set s = new HashSet();
		Iterator it = a.getArgs().iterator();
		int i = 0;
		while (it.hasNext()) {
			Expression arg = (Expression) it.next();
			if (abs.isStrict(i++))
				s.addAll((Set) arg.visit(this));
		}
		return s;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(BooleanLiteral)
	 */
	public Object visit(BooleanLiteral a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(CharLiteral)
	 */
	public Object visit(CharLiteral a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Constructor)
	 */
	public Object visit(Constructor a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(ConstructorDefinition)
	 */
	public Object visit(ConstructorDefinition a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Definition)
	 */
	public Object visit(Definition a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(DoubleLiteral)
	 */
	public Object visit(DoubleLiteral a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(FloatLiteral)
	 */
	public Object visit(FloatLiteral a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(IntegerLiteral)
	 */
	public Object visit(IntegerLiteral a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Module)
	 */
	public Object visit(Namespace a) {
		Iterator it = a.getAllBindings().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			Expression def = (Expression) entry.getValue();
			log.finest(
				"In module " + a.getName() + ", visiting symbol " + name);
			def.visit(this);
		}
		/* return compiled class*/
		return null;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(StringLiteral)
	 */
	public Object visit(StringLiteral a) {
		return emptySet;
	}

	/**
	 * 	return definition of variable
	 * @see jaskell.compiler.JaskellVisitor#visit(Variable)
	 */
	public Object visit(Variable a) {
		Set s = new HashSet();
		Expression b = a.lookup(a.getName());
		if (b instanceof LocalBinding) {
			s.add(a.getName());
		}
		return s;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(ConstructorPattern)
	 */
	public Object visit(ConstructorPattern a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(LocalBinding)
	 */
	public Object visit(LocalBinding a) {
		return emptySet;
	}

	/**
	 * Returns the definition for the variable
	 * 
	 * @see jaskell.compiler.JaskellVisitor#visit(QualifiedVariable)
	 */
	public Object visit(QualifiedVariable a) {
		return emptySet;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Conditional)
	 */
	public Object visit(Conditional a) {
		// visit condition to be evaluated
		Set s1 = (Set) a.getCondition().visit(this);
		// visit each alternative and retains intersection of all strict sets

		Set s2 = (Set) a.getIfTrue().visit(this);
		s2.retainAll((Set) a.getIfFalse().visit(this));
		s1.addAll(s2);
		return s1;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(PrimitiveFunction)
	 */
	public Object visit(PrimitiveFunction f) {
		analyzed.add(f);
		return null;
	}

	/**
	 * @see jaskell.compiler.JaskellVisitor#visit(Let)
	 */
	public Object visit(Let a) {
		Iterator it = a.getBindings().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			Expression def = (Expression) entry.getValue();
			// analyze expression 
			def.visit(this);
		}
		// analyze body 
		return a.getBody().visit(this);
	}

}
