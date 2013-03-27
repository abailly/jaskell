/*
 * Created on Mar 9, 2004
 * 
 * $Log: TypeComparatorTest.java,v $
 * Revision 1.2  2004/10/17 15:06:16  bailly
 * corrected calss to BET API
 * removed OverloadDefinition
 *
 * Revision 1.1  2004/06/24 12:57:44  bailly
 * mavenisation + creation arborescence de test
 *
 * Revision 1.1  2004/03/10 20:31:56  bailly
 * added type comparison classes
 *
 */
package fr.lifl.jaskell.compiler.types;
import fr.lifl.jaskell.compiler.core.Primitives;
import junit.framework.TestCase;

/**
 * @author bailly
 * @version $Id: TypeComparatorTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class TypeComparatorTest extends TestCase {

	/**
	 * Constructor for TypeComparatorTest.
	 * @param arg0
	 */
	public TypeComparatorTest(String arg0) {
		super(arg0);
	}

	/*
	 * test basic types
	 */
	public void testVar() throws Exception {
		Type t1 = Primitives.INT;
		Type t2 = TypeFactory.freshBinding();
		assertEquals(0,t1.compare(t2));
		assertEquals(0,t2.compare(t1));
	}
	
	public void testVar2() throws Exception {
		assertEquals(0,TypeFactory.freshBinding().compare(TypeFactory.freshBinding()));
	}
	
	public void testBase() throws Exception {
		try {
			Primitives.BOOL.compare(Primitives.INT);
			fail();
		}catch(UncomparableException uex) {
			
		}
	}
	
	public void testList() throws Exception {
		Type t1 = Types.apply(Primitives.LIST, Primitives.INT);
		Type t2 = Types.apply(Primitives.LIST, TypeFactory.freshBinding());
		assertEquals(0,t1.compare(t2));
		assertEquals(0,t2.compare(t1));
	}
	
	/*
	 * (Int -> a) <  (a -> b)
	 */
	public void testFunction() throws Exception {
		Type t1 = PrimitiveType.makeFunction(Primitives.INT,TypeFactory.freshBinding());
		Type t2 = PrimitiveType.makeFunction(TypeFactory.freshBinding(),TypeFactory.freshBinding());
		assertEquals(0,t1.compare(t2));
		assertEquals(0,t2.compare(t1));
	}
	

	
}
