/*
 * Created on May 27, 2003
 * Copyright 2003 Arnaud Bailly
  */
package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Conditional;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.Let;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Pattern;
import fr.lifl.jaskell.compiler.core.Variable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A class that collects a Set of captured variables
 * 
 * @author bailly
 * @version $Id: CaptureCollector.java 1154 2005-11-24 21:43:37Z nono $
 */
class CaptureCollector extends JaskellVisitorAdapter {

	private final Set emptySet = new HashSet();

	/* the set of captured variables */
	private Set captured;

	/* the set of locally bound variables */
	private Set locals;

	/**
	 * 
	 */
	public CaptureCollector() {
		this.captured = new HashSet();
		this.locals = new HashSet();
	}

	/**
	 * @param captured
	 */
	public CaptureCollector(Set captured) {
		this.captured = captured;
		this.locals = new HashSet();
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Abstraction)
	 */
	public Object visit(Abstraction a) {
		locals.addAll(a.getBindings().values());
		a.getBody().visit(this);
		return null;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Alternative)
	 */
	public Object visit(Alternative a) {
		/* add all bindings to locals */
		locals.add(a.getBinding());
		Iterator it = a.getPatterns();
		while (it.hasNext()) {
			locals.addAll(((Pattern) it.next()).getBindings());
		}
		/* visit all bodies of alternative */
		it = a.getBodies();
		while (it.hasNext()) {
			Expression e = (Expression) it.next();
			e.visit(this);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Application)
	 */

	public Object visit(Application a) {
		a.getFunction().visit(this);
		/* visit all bodies of alternative */
		Iterator it = a.getArgs().iterator();
		while (it.hasNext()) {
			Expression e = (Expression) it.next();
			e.visit(this);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Conditional)
	 */
	public Object visit(Conditional conditional) {
		conditional.getCondition().visit(this);
		conditional.getIfFalse().visit(this);
		conditional.getIfTrue().visit(this);
		return null;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Definition)
	 */
	public Object visit(Definition a) {
		return a.getDefinition().visit(this);
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Let)
	 */
	public Object visit(Let let) {
		Iterator it = let.getBindings().values().iterator();
		while (it.hasNext()) {
			Expression e = (Expression) it.next();
			e.visit(this);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.Variable)
	 */
	public Object visit(Variable a) {
		/* resolve variable */
		Expression e = a.lookup(a.getName());
		/* simple case */
		if (locals.contains(e) || !(e instanceof LocalBinding)) {
			return emptySet;
		}
		/* e is defined in outer scope */
		captured.add(e);
		return null;
	}

}