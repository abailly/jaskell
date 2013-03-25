package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.Literal;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import junit.framework.TestCase;

/**
 * @author bailly
 * @version $Id: ConstantPropagatorTest.java 1183 2005-12-07 22:45:19Z nono $
 *  */
public class ConstantPropagatorTest extends TestCase {

	/**
	 * Constructor for ConstantPropagatorTest.
	 * @param arg0
	 */
	public ConstantPropagatorTest(String arg0) {
		super(arg0);
	}

	public void testSimpleConstant() {
		// build expression
		Application a = new Application();
		a.addArgument(new IntegerLiteral(1));
		a.addArgument(new IntegerLiteral(1));
		QualifiedVariable qv = new QualifiedVariable("(+)");
		qv.addPathElement("Prelude");
		a.setFunction(qv);
		// apply constant propagator
		TypeChecker tc=  new TypeChecker();
		a.visit(tc);
		ConstantPropagator cp = new ConstantPropagator();
		Expression e = (Expression) a.visit(cp);
		// compare results
		Literal res = new IntegerLiteral(2);
		assertEquals(res, e);
	}

}
