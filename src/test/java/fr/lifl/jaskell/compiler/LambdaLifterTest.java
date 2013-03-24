/*
 * Created on May 27, 2003 by Arnaud Bailly - bailly@lifl.fr
 * Copyright 2003 - Arnaud Bailly 
 */
package fr.lifl.jaskell.compiler;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.parser.Yyparser;

import java.io.StringReader;

import junit.framework.TestCase;

/**
 * @author bailly
 * @version $Id: LambdaLifterTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class LambdaLifterTest extends TestCase {

	/**
	 * Constructor for LambdaLifterTest.
	 * @param arg0
	 */
	public LambdaLifterTest(String arg0) {
		super(arg0);
	}

	public void testSimple() {
		String text = "{f x = (\\ y -> x + y) 4 + 2}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser(false);
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		/* typecheck module */
		LambdaLifter ll = new LambdaLifter(m);
		m.visit(ll);
		/* retrieve lambda0 */
		assertNotNull(m.lookup("lambda0"));	
		System.out.println(m);
	}
	
	public void testNested() {
		String text = "{f x = (\\ y -> x + (\\ z -> y * x + z)) 4 + 2}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser(false);
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		/* typecheck module */
		LambdaLifter ll = new LambdaLifter(m);
		m.visit(ll);
		/* retrieve lambda0 */
		assertNotNull(m.lookup("lambda0"));	
		assertNotNull(m.lookup("lambda1"));	
		System.out.println(m);
	}
		
	public void testLet() {
		String text = "{f x y = let a k = x + k in (a 1) * y}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.out.println(m);
		/* typecheck module */
		LambdaLifter ll = new LambdaLifter(m);
		m.visit(ll);
		/* retrieve lambda0 */
		assertNotNull(m.lookup("lambda0"));	
		System.out.println(m);
	}
	
	public void testRecursiveLet() {
		String text = "{f x  = let { even 0 = true; even x = odd (x + -1); odd 0  =false; odd x = even (x + -1) } in odd x; main = f 10;}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.out.println(m);
		/* typecheck module */
		LambdaLifter ll = new LambdaLifter(m);
		m.visit(ll);
		/* retrieve lambda0 */
		assertNotNull(m.lookup("lambda0"));	
		System.out.println(m);
	}
		
		
}
