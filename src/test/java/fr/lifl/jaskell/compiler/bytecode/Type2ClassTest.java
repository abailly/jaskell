package fr.lifl.jaskell.compiler.bytecode;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.compiler.core.Primitives;
import fr.lifl.jaskell.compiler.types.PrimitiveType;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.compiler.types.Types;
import junit.framework.TestCase;

public class Type2ClassTest extends TestCase {

	public Type2ClassTest(String arg) {
		super(arg);
	}

	public void testType() {
		Type t2 = PrimitiveType.makeFunction(Primitives.INT, Primitives.BOOL);
		Type2Class tv = new Type2Class(Module.PRELUDE);
		Class cls = (Class) t2.visit(tv);
		assertEquals(fr.lifl.jaskell.runtime.types.Closure.class, cls);
	}

	public void testTypeGen01() {
		Type t2 =
			PrimitiveType.makeFunction(
				Primitives.INT,
				PrimitiveType.makeFunction(Primitives.INT, Primitives.BOOL));
		Type2Class tv = new Type2Class(Module.PRELUDE);
		Class cls = (Class) t2.visit(tv);
		assertEquals(
			"fr.lifl.jaskell.runtime.types.Closure",
			cls.getName());
	}

	public void testListNameEncoding() {
		Type lt = Types.apply(Primitives.LIST, Primitives.INT);
		assertEquals("_5bInt_5d", BytecodeGenerator.encodeName2Java(lt.toString()));
	}

	public void testListType() {
		Type lt = Types.apply(Primitives.LIST, Primitives.INT);
		Type2Class tv = new Type2Class(Module.PRELUDE);
		Class cls = (Class) lt.visit(tv);
		ClassFileWriter writer = new ClassFileWriter("test");
		assertEquals("fr.lifl.jaskell.runtime.types.JList", cls.getName());
	}

	public void testListType2() {
		Type lt =
			Types.apply(
                    Primitives.LIST,
                    Types.apply(
                            Primitives.LIST,
                            PrimitiveType.makeFunction(
                                    Primitives.INT,
                                    Primitives.STRING)));
		Type2Class tv = new Type2Class(Module.PRELUDE);
		Class cls = (Class) lt.visit(tv);
		ClassFileWriter writer = new ClassFileWriter("test");
		BytecodeGenerator.cleanupClassFiles(writer);
	}


}
