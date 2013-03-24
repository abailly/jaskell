package fr.lifl.jaskell.parser;
import fr.lifl.jaskell.compiler.core.Module;

import java.io.StringReader;

import junit.framework.TestCase;

/**
 * 
 * @author bailly
 * @version $Id: NormalizerTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class NormalizerTest extends TestCase {

	/**
	 * Constructor for NormalizerTest.
	 * @param arg0
	 */
	public NormalizerTest(String arg0) {
		super(arg0);
	}

	public void testSimple() {
		
		String text =
			"toto True = 0; toto False =1";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.err.println(m);
	}

	public void testLength() {
		String text = "length [] = 0 ;" + "length (x : xs) = length xs + 1";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.err.println(m);
	}

	public void testMappairs() {
		String text =
			"	mappairs f [] ys = [] ;"
				+ " mappairs f (x:xs) [] = [];"
				+ " mappairs f (x:xs) (y:ys) = f x y : mappairs f xs ys";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.err.println(m);
	}

	public void testMap() {
		String text =
			"	map f [] = [] ;" + " map f (x:xs) = f x : (map f xs)";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		Module m = (Module) Module.getToplevels().get("Main");
		System.err.println(m);
	}


}
