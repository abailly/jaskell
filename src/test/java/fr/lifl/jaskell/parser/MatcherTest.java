package fr.lifl.jaskell.parser;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.Application;
import fr.lifl.jaskell.compiler.core.Constructor;
import fr.lifl.jaskell.compiler.core.ConstructorPattern;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.QualifiedVariable;
import fr.lifl.jaskell.compiler.core.Variable;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * @author bailly
 * @version $Id: MatcherTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class MatcherTest extends TestCase {

	/**
	 * Constructor for MatcherTest.
	 * @param arg0
	 */
	public MatcherTest(String arg0) {
		super(arg0);
	}

	/** test length definition
		length [] = 0
		length x:xs = length xs + 1
	*/
	public void test01() {
		List vlist = new LinkedList();
		List plist = new LinkedList();
		vlist.add(LocalBinding.freshBinding());
		/* match empty list */
		PatternMatch pm = new PatternMatch();
		pm.patterns = new ArrayList();
		ConstructorPattern cp = new ConstructorPattern();
		cp.setConstructor(new Constructor("([])"));
		pm.patterns.add(cp);	
		pm.expr = new IntegerLiteral(0);
		plist.add(pm);
		/* match non empty list */
		 pm = new PatternMatch();
		pm.patterns = new ArrayList();
		cp = new ConstructorPattern();
		cp.setConstructor(new Constructor("(:)"));
		cp.addPattern(new LocalBinding("x"));
		cp.addPattern(new LocalBinding("xs"));
		pm.patterns.add(cp);	
		Application ap1 = new Application();
		QualifiedVariable qv = new QualifiedVariable("(+)");
		qv.addPathElement("Prelude");
		ap1.setFunction(qv);
		Application ap2 = new Application();
		ap2.setFunction(new Variable("length"));
		ap2.addArgument(new Variable("xs"));
		ap1.addArgument(ap2);
		ap1.addArgument(new IntegerLiteral(1));
		pm.expr = ap1;
		plist.add(pm);
		/* try MAtcher */
		Matcher m = new Matcher();
		Expression e = m.match(vlist,plist,null);
		assertTrue(e instanceof Alternative);
		System.out.println(e);
	}
	
	/** test mappairs definition
	 * 	mappairs f [] ys = []
	 *  mappairs f (x:xs) [] = []
	 *  mappairs f (x:xs) (y:ys) = f x y : mappairs f xs ys
	 */
	public void testMappairs() {
		List vlist = new LinkedList();
		List plist = new LinkedList();
		/* add three variables */
		vlist.add(LocalBinding.freshBinding());
		vlist.add(LocalBinding.freshBinding());
		vlist.add(LocalBinding.freshBinding());
		/* first list of patterns */
		PatternMatch pm = new PatternMatch();
		pm.patterns = new ArrayList();
		pm.patterns.add(new LocalBinding("f"));
		ConstructorPattern cp  = new ConstructorPattern();
		cp.setConstructor(new Constructor("([])"));
		pm.patterns.add(cp);
		pm.patterns.add(new LocalBinding("ys"));
		pm.expr = new Constructor("([])");
		plist.add(pm);
		/* second list of patterns */
	pm = new PatternMatch();
		pm.patterns = new ArrayList();
		pm.patterns.add(new LocalBinding("f"));
		cp  = new ConstructorPattern();
		cp.setConstructor(new Constructor("(:)"));
		cp.addPattern(new LocalBinding("x"));
		cp.addPattern(new LocalBinding("xs"));
		pm.patterns.add(cp);
		cp  = new ConstructorPattern();
		cp.setConstructor(new Constructor("([])"));
		pm.patterns.add(cp);
		pm.expr = new Constructor("([])");
		plist.add(pm);
		/* thirs list of patterns */
		pm = new PatternMatch();
		pm.patterns = new ArrayList();
		pm.patterns.add(new LocalBinding("f"));
		cp  = new ConstructorPattern();
		cp.setConstructor(new Constructor("(:)"));
		cp.addPattern(new LocalBinding("x"));
		cp.addPattern(new LocalBinding("xs"));
		pm.patterns.add(cp);
		cp  = new ConstructorPattern();
		cp.setConstructor(new Constructor("(:)"));
		cp.addPattern(new LocalBinding("y"));
		cp.addPattern(new LocalBinding("ys"));
		pm.patterns.add(cp);
		Application ap = new Application();
		ap.setFunction(new Constructor("(:)"));
		Application ap1 = new Application();
		ap1.setFunction(new Variable("f"));
		ap1.addArgument(new Variable("x"));
		ap1.addArgument(new Variable("y"));
		ap.addArgument(ap1);
		ap1 = new Application();
		ap1.setFunction(new Variable("mappairs"));
		ap1.addArgument(new Variable("f"));
		ap1.addArgument(new Variable("xs"));
		ap1.addArgument(new Variable("ys"));
		ap.addArgument(ap1);
		pm.expr = ap;
		plist.add(pm);
		/* try MAtcher */
		Matcher m = new Matcher();
		Expression e = m.match(vlist,plist,null);
		assertTrue(e instanceof Alternative);
		System.out.println(e);
	}
		
	public void testHead() {
		String text =
			"module Main where {	head 0 y = [] ;" + " head n (x:xs) = x : (head (n-1) xs)}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.err.println(m);
	}
		
}
