package fr.lifl.jaskell.parser;

import fr.lifl.jaskell.compiler.core.Expression;

import java.util.List;

/**
 * @author bailly
 * @version $Id: PatternMatch.java 1154 2005-11-24 21:43:37Z nono $
 */
 class PatternMatch {

	List patterns;

	Expression expr;
	
	public String toString() {
		return "(" + patterns.toString()+","+ expr.toString() +")";
	}

}
