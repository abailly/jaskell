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

/**
 * A visitor interface for fucntinal expressions
 * 
 * @author Arnaud.Bailly - bailly@lifl.fr
 * @version $Id: JaskellVisitor.java 1154 2005-11-24 21:43:37Z nono $
 */
public interface JaskellVisitor
{
    public Object visit(Abstraction a);
    public Object visit(Alternative a);
    public Object visit(Application a);
    public Object visit(BooleanLiteral a);
    public Object visit(CharLiteral a);
    public Object visit(Constructor a);
    public Object visit(Definition a);
    public Object visit(DoubleLiteral a);
    public Object visit(FloatLiteral a);
    public Object visit(IntegerLiteral a);
    public Object visit(Module a);
    public Object visit(StringLiteral a);
    public Object visit(Variable a);
    public Object visit(QualifiedVariable a);
    public Object visit(ConstructorPattern a);
    public Object visit(LocalBinding a);
	  public Object visit(PrimitiveFunction f);
		
	/**
	 * Method visit.
	 * @param conditional
	 * @return Object
	 */
	Object visit(Conditional conditional);


	/**
	 * Method visit.
	 * @param ddef
	 * @return Object
	 */
	Object visit(DataDefinition ddef);

	/**
	 * Method visit.
	 * @param cdef
	 * @return Object
	 */
	Object visit(ConstructorDefinition cdef);

	/**
	 * Method visit.
	 * @param let
	 * @return Object
	 */
	Object visit(Let let);

	Object visit(PrimitiveConstructor a);
	
	Object visit(PrimitiveData a);
	
}
