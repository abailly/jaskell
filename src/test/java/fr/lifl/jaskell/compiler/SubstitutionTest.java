package fr.lifl.jaskell.compiler;
import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Conditional;
import fr.lifl.jaskell.compiler.core.Constructor;
import fr.lifl.jaskell.compiler.core.ConstructorPattern;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.Primitives;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.Variable;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * 
 * @author bailly
 * @version $Id: SubstitutionTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class SubstitutionTest extends TestCase {

	/**
	 * Constructor for SubstitutionTest.
	 * @param arg0
	 */
	public SubstitutionTest(String arg0) {
		super(arg0);
	}

	public void testSimple() {
		Application ap1 = new Application();
		Variable v = new Variable("fac");
		ap1.setFunction(v);
		Application ap2 = new Application();
		QualifiedVariable qv = new QualifiedVariable("(-)");
		qv.addPathElement("Prelude");
		ap2.setFunction(qv);
		ap2.addArgument(new Variable("x"));
		ap2.addArgument(new IntegerLiteral(1));
		ap1.addArgument(ap2);
		/* apply substitution */
		HashMap s = new HashMap();
		Object o2 = new IntegerLiteral(2);
		s.put("x", o2);
		Substitution subst = new Substitution(s);
		Application ap3 = (Application) ap1.visit(subst);
		Object o = ((Application) ap3.getArgs().get(0)).getArgs().get(0);
		assertEquals(o, o2);
	}

	/** 
	 *  a test for bound variables 
	 */
	public void testAbstractionBound() {
		Abstraction a1 = new Abstraction();
		a1.bind(new LocalBinding("x", Primitives.INT));

		Application ap0 = new Application();
		QualifiedVariable qv = new QualifiedVariable("(*)");
		qv.addPathElement("Prelude");
		ap0.setFunction(qv);
		Variable vx = new Variable("x");
		ap0.addArgument(vx);

		Application ap1 = new Application();
		Variable v = new Variable("fac");
		ap1.setFunction(v);
		Application ap2 = new Application();
		qv = new QualifiedVariable("(-)");
		qv.addPathElement("Prelude");
		ap2.setFunction(qv);
		ap2.addArgument(new Variable("x"));
		ap2.addArgument(new IntegerLiteral(1));
		ap1.addArgument(ap2);
		ap0.addArgument(ap1);

		a1.setBody(ap0);

		/* apply substitution */
		HashMap s = new HashMap();
		Object o2 = new IntegerLiteral(2);
		s.put("x", o2);
		Substitution subst = new Substitution(s);
		Abstraction abs = (Abstraction) a1.visit(subst);

		/* check x was not replaced */
		assertEquals(vx, ap0.getArgument(0));
	}

	/** 
	 *  a test for bound variables 
	 */
	public void testAbstractionFree() {
		Abstraction a1 = new Abstraction();
		a1.bind(new LocalBinding("x"));

		Application ap0 = new Application();
		QualifiedVariable qv = new QualifiedVariable("(*)");
		qv.addPathElement("Prelude");
		ap0.setFunction(qv);
		Variable vx = new Variable("x");
		ap0.addArgument(vx);

		Application ap1 = new Application();
		Variable v = new Variable("fac");
		ap1.setFunction(v);
		Application ap2 = new Application();
		qv = new QualifiedVariable("(-)");
		qv.addPathElement("Prelude");
		ap2.setFunction(qv);
		ap2.addArgument(new Variable("x"));
		ap2.addArgument(new IntegerLiteral(1));
		ap1.addArgument(ap2);
		ap0.addArgument(ap1);

		a1.setBody(ap0);

		/* apply substitution */
		HashMap s = new HashMap();
		Object o2 = new IntegerLiteral(2);
		s.put("fac", o2);
		Substitution subst = new Substitution(s);
		Abstraction abs = (Abstraction) a1.visit(subst);

		/* check x was not replaced */
		assertEquals(o2, ap1.getFunction());
	}

	public void testConditional() {
		Abstraction a1 = new Abstraction();
		a1.bind(new LocalBinding("x", Primitives.INT));

		Application ap0 = new Application();
		QualifiedVariable qv =
			new QualifiedVariable("(*)", Primitives.INT_INT_INT);
		qv.addPathElement("Prelude");
		ap0.setFunction(qv);
		ap0.addArgument(new Variable("x", Primitives.INT));

		Application ap1 = new Application();
		Variable v = new Variable("fac");
		ap1.setFunction(v);
		Application ap2 = new Application();
		qv = new QualifiedVariable("(-)", Primitives.INT_INT_INT);
		qv.addPathElement("Prelude");
		ap2.setFunction(qv);
		ap2.addArgument(new Variable("x", Primitives.INT));
		ap2.addArgument(new IntegerLiteral(1));
		ap1.addArgument(ap2);
		ap0.addArgument(ap1);

		Application ap3 = new Application();
		qv = new QualifiedVariable("(==)", Primitives.INT_INT_BOOL);
		qv.addPathElement("Prelude");
		ap3.setFunction(qv);
		ap3.addArgument(new Variable("x", Primitives.INT));
		ap3.addArgument(new IntegerLiteral(0));

		Conditional if1 = new Conditional();
		if1.setCondition(ap3);
		if1.setIfTrue(new IntegerLiteral(1));
		if1.setIfFalse(ap0);
		a1.setBody(if1);

		/* apply substitution */
		HashMap s = new HashMap();
		Object o2 = new IntegerLiteral(2);
		s.put("fac", o2);
		Substitution subst = new Substitution(s);
		Abstraction abs = (Abstraction) a1.visit(subst);

		/* check x was not replaced */
		assertEquals(o2, ap1.getFunction());
	}
	
	/**
	 * Tests substitution under case construct. Sub patterns within constructor patterns
	 * may bind variables which should restrict substitution
	 */
	public void testCase() {
		Alternative alt = new Alternative();
		alt.setBinding(new LocalBinding("x"));
		alt.setExpression(new Variable("y"));
		/* first pattern */
		ConstructorPattern cp = new ConstructorPattern();
		cp.setConstructor(new Constructor("([])"));
		Application ap0 = new Application();
		QualifiedVariable qv =
			new QualifiedVariable("(*)", Primitives.INT_INT_INT);
		qv.addPathElement("Prelude");
		ap0.setFunction(qv);
		ap0.addArgument(new Variable("y"));
		ap0.addArgument(new IntegerLiteral(1));
		alt.addPattern(cp, ap0);
		/* second pattern */
		cp = new ConstructorPattern();
		cp.setConstructor(new Constructor("(:)"));
		cp.addPattern(new LocalBinding("y"));
		cp.addPattern(new LocalBinding("ys"));
		Application ap1 = new Application();
		qv = new QualifiedVariable("(+)", Primitives.INT_INT_INT);
		qv.addPathElement("Prelude");
		ap1.setFunction(qv);
		Variable v = new Variable("y");
		ap1.addArgument(v);
		Application ap2 = new Application();
		ap2.setFunction(new Variable("length"));
		ap2.addArgument(new Variable("ys"));
		ap1.addArgument(ap2);
		alt.addPattern(cp, ap1);
		/* default*/
		System.out.println(alt);
		/* apply substitution on y */
		HashMap s = new HashMap();
		Object o2 = new IntegerLiteral(2);
		s.put("y", o2);
		Substitution subst = new Substitution(s);
		alt.visit(subst);
		assertEquals(o2, ap0.getArgument(0));
		assertEquals(v, ap1.getArgument(0));
	}
	
	/**
	 * Tests that substitution under a definition does not replace defined symbol
	 */
	public void testDefinition() {
		Module m = new Module("Main", null);
		// definition de g	
		Abstraction a1 = new Abstraction();
		a1.bind(new LocalBinding("x", Primitives.INT));

		Application ap0 = new Application();
		QualifiedVariable qv =
			new QualifiedVariable("(*)", Primitives.INT_INT_INT);
		qv.addPathElement("Prelude");
		ap0.setFunction(qv);
		ap0.addArgument(new Variable("x", Primitives.INT));

		Application ap1 = new Application();
		Variable v = new Variable("fac");
		ap1.setFunction(v);
		Application ap2 = new Application();
		qv = new QualifiedVariable("(-)", Primitives.INT_INT_INT);
		qv.addPathElement("Prelude");
		ap2.setFunction(qv);
		ap2.addArgument(new Variable("x", Primitives.INT));
		ap2.addArgument(new IntegerLiteral(1));
		ap1.addArgument(ap2);
		ap0.addArgument(ap1);

		Application ap3 = new Application();
		qv = new QualifiedVariable("(==)", Primitives.INT_INT_BOOL);
		qv.addPathElement("Prelude");
		ap3.setFunction(qv);
		ap3.addArgument(new Variable("x", Primitives.INT));
		ap3.addArgument(new IntegerLiteral(0));

		Conditional if1 = new Conditional();
		if1.setCondition(ap3);
		if1.setIfTrue(new IntegerLiteral(1));
		if1.setIfFalse(ap0);
		a1.setBody(if1);
		// definition de f
		m.bind("fac", a1);
		Definition def = new Definition();
		def.setName("fac");
		def.setDefinition(a1);

		/* apply substitution */
		HashMap s = new HashMap();
		Object o2 = new IntegerLiteral(2);
		s.put("fac", o2);
		Substitution subst = new Substitution(s);
		def.visit(subst);

		/* check fac was not replaced */
		assertEquals(v, ap1.getFunction());
	}

}
