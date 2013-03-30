package fr.lifl.jaskell.parser;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * 
 * @author bailly
 * @version $Id: ParserTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class ParserTest extends TestCase {

	/**
	 * Constructor for ParserTest.
	 * @param arg0
	 */
	public ParserTest(String arg0) {
		super(arg0);
	}

	/**
	 * Test simple definition
	 *  length [] = 0
	 *	length x:xs = length xs + 1
	 */
	public void testLength() {
		String text = "module Toto where { length [] = 0 ;" + "length (x : xs) = length xs + 1}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		System.out.println(p.getEquations());
	}

	/** test mappairs definition 
	 * 
	 * 	mappairs f [] ys = []
	 *  mappairs f (x:xs) [] = []
	 *  mappairs f (x:xs) (y:ys) = f x y : mappairs f xs ys
	 */
	public void testMappairs() {
		String text =
			"module Main where { length [] = 0 ;"
				+ "length (x : xs) = length xs + 1;"
				+ "	mappairs f [] ys = [] ;"
				+ " mappairs f (x:xs) [] = [];"
				+ " mappairs f (x:xs) (y:ys) = f x y : mappairs f xs ys }";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser();
		p.parse(sr);
		System.out.println(p.getEquations());
	}

	public void testEmptyClass() {
		String text =
			"module Main where { class Functor f where { fmap :: (a-> b) -> f a -> f b }  }";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser(true);
		p.parse(sr);
		System.out.println(p.getEquations());
	}
	
	public void testMonadClass() {
		String text =
			"module Main where { class Monad m where { "+
			"  (>>=) :: m a -> (a-> m b) -> m b ;" +
			"  (>>) :: m a -> m b -> m b; " +   
			"  return :: m -> m a ;" +
//			"  fail :: String -> m a ;" +
			"  m >> k = m >>= \\ _ -> k }}";
		StringReader sr = new StringReader(text);
		Yyparser p = new Yyparser(true);
		p.parse(sr);
		System.out.println(p.getEquations());
	}
		
}
