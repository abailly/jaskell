package fr.lifl.jaskell.compiler.types;
import junit.framework.TestCase;

/**
 * @author bailly
 * @version $Id: TypeInstantiatorTest.java 1183 2005-12-07 22:45:19Z nono $
 */
public class TypeInstantiatorTest extends TestCase {
	public static final PrimitiveType FUNCTION =
		new PrimitiveType(
			"(->)",
			fr.lifl.jaskell.runtime.types.Closure.class,
			new FunctionKind(SimpleKind.K, FunctionKind.K_K),
			new TypeApplicationFormat() {
		public String formatApply(Type d, Type r) {
			return r + " ->";
		}
	}, new FunctionComparator());

	/**
	 * Constructor for TypeInstantiatorTest.
	 * @param arg0
	 */
	public TypeInstantiatorTest(String arg0) {
		super(arg0);
	}

	public void test01() {
		Type v = TypeFactory.freshBinding();
		Type v2 = TypeFactory.freshBinding();
		Type t1 =
			Types.makeApplication(
                    Types.makeApplication(FUNCTION, v),
                    v2);
		/* apply instance */
		Type t = new TypeInstantiator(t1).instance();
		System.err.println(
			"susbt " + t1.makeString() + " to " + t.makeString());
		assertTrue(!t.equals(t1));
	}

}
