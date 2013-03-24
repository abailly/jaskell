package fr.lifl.jaskell.parser;
import junit.framework.TestCase;

/**
 * @author bailly
 * @version $Id: YylexTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class YylexTest extends TestCase {

	/**
	 * Constructor for YylexTest.
	 * @param arg0
	 */
	public YylexTest(String arg0) {
		super(arg0);
	}

	public void testYylex1() throws Exception {
		String data = "module where Huiui.h  = 0737 => 0x34 \"hkjhk \" 'k' ;";
		java.io.Reader rd = new java.io.StringReader(data);
		Yylex lex = new Yylex(rd);
		lex.parser = new Yyparser();
		int tok;
		while ((tok = lex.yylex()) > 0)
			System.err.println(Yyparser.yyname[tok]);
	}
}
