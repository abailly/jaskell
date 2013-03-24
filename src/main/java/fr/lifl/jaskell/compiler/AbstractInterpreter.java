/*
 * Created on Jun 16, 2003
 * Copyright 2003 Arnaud Bailly
 * $Log: AbstractInterpreter.java,v $
 * Revision 1.2  2004/02/19 14:55:01  nono
 * Integrated jaskell to FIDL
 * Added rules in grammar to handle messages
 *
 * Revision 1.1  2003/06/18 19:59:16  bailly
 * Added abstractInterpreter and StrictnessInterpreter classes
 * to compute strictness information both for arguments
 * and  internal nodes - applications of combinator bodies
 *
  */
package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.BooleanLiteral;
import fr.lifl.jaskell.compiler.core.CharLiteral;
import fr.lifl.jaskell.compiler.core.Conditional;
import fr.lifl.jaskell.compiler.core.Constructor;
import fr.lifl.jaskell.compiler.core.ConstructorPattern;
import fr.lifl.jaskell.compiler.core.DoubleLiteral;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.FloatLiteral;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.Let;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.PrimitiveFunction;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.StringLiteral;
import fr.lifl.jaskell.compiler.core.Variable;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.jaskell.runtime.types.JFunction;
import fr.lifl.jaskell.runtime.types.JObject;

import java.util.Iterator;

/**
 * An asbtract interpreter implementing an evaluation loop but without 
 * primitive functions or litteral constants definition;
 * 
 * This class implements the skeleton of an abstract interpreter. Its main
 * method is the <code>eval(Expression)</code> method which runs an infinite loop 
 * through the given code fragment, recursively visiting its constructs. 
 *  
 * @author bailly
 * @version $Id: AbstractInterpreter.java 1154 2005-11-24 21:43:37Z nono $
 */
public abstract class AbstractInterpreter extends JaskellVisitorAdapter {

	/**
	 * Main evaluation loop of interpreter
	 * 
	 * @param expr the expression to evaluate
	 * @return an evaluated expression. This method may
	 * loop forever if code does not terminate.
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
			throw new CompilerException(
				"Cannot interpret "
					+ a
					+ " : functional argument is not a function");
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
