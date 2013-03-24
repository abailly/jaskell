package fr.lifl.jaskell.compiler.core;

import junit.framework.TestCase;

/**
 * @author bailly
 * @version $Id: ModuleTest.java 1183 2005-12-07 22:45:19Z nono $
 *  */
public class ModuleTest extends TestCase {

	private Module main;
	
	/**
	 * Constructor for ModuleTest.
	 * @param arg0
	 */
	public ModuleTest(String arg0) {
		super(arg0);
	}


	public void testAddTopModule() {
		Module mod = new Module("Test", null);
	}

	public void testAddTopMainModule() {
		Module mod = new Module("Main", null);
	}

	public void testAddSubModule() {
		Module mod = new Module("Sub", main);
	}

	public void testAddDuplicateSubModule() {
		try {
			Module mod = new Module("Sub", main);
			mod = new Module("Sub", main);
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
			return;
		}
		fail();
	}

	public void testBindOK() {
		Module mod = new Module("Sub", main);
		mod.bind("toto", new StringLiteral("toto"));
		assertEquals("toto",((StringLiteral)mod.lookup("toto")).getString());
	}


	public void testLookup() {
		ExpressionBase obj = new Abstraction();
		Module mod = main;
		mod.bind("toto",obj);
		Object o1 = mod.lookup("toto");
		assertSame(obj,o1);
	}

	public void testDeepLookup() {
    	 Expression obj = new Abstraction();
		Module mod = new Module("Sub",main);
		main.bind("toto",obj);
		Object o1 = mod.lookupDeep("toto");
		assertNotNull(o1);
		assertSame(obj,o1);
	}
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		// create new main module
		main = new Module("Main",null);
	}


}

