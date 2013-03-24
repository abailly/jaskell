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
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.parsing.Namespace;

/**
 * A skeletal implementation of JaskellVisitor.
 * <p>
 * This implementation of @see{JaskellVisitor} is provided as an 
 * alternative to full implementation of visitor. Allt he <code>visit</code> methods
 * return the passed object.
 * 
 * @author bailly
 * @version $Id: JaskellVisitorAdapter.java 1154 2005-11-24 21:43:37Z nono $
 */
public class JaskellVisitorAdapter implements JaskellVisitor {

	/**
	 * Constructor for JaskellVisitorAdapter.
	 */
	public JaskellVisitorAdapter() {
		super();
	}

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
		return visit((Namespace)a);
	}

	/**
	 * @param namespace
	 * @return
	 */
	protected Object visit(Namespace namespace) {
		return namespace;
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
		return visit((Namespace)a);
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveConstructor)
	 */
	public Object visit(PrimitiveConstructor a) {
		return 	visit((ConstructorDefinition)a);
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.datatypes.PrimitiveData)
	 */
	public Object visit(PrimitiveData a) {
		return visit((DataDefinition)a);
	}


}
