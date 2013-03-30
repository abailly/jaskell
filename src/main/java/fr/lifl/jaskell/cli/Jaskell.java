package fr.lifl.jaskell.cli;

import fr.lifl.jaskell.compiler.ConstantPropagator;
import fr.lifl.jaskell.compiler.LambdaLifter;
import fr.lifl.jaskell.compiler.StrictnessAnalyzer;
import fr.lifl.jaskell.compiler.TypeChecker;
import fr.lifl.jaskell.compiler.bytecode.BytecodeGenerator;
import fr.lifl.jaskell.compiler.bytecode.CodeGenerator;
import fr.lifl.jaskell.compiler.bytecode.Type2Class;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.parser.Yyparser;
import oqube.bytes.loading.ClassFileLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Jaskell implements Runnable {

    private final Yyparser parser = new Yyparser();
    private final StrictnessAnalyzer strictnessAnalyzer = new StrictnessAnalyzer();
    private final TypeChecker typeChecker = new TypeChecker();
    private final ConstantPropagator constantPropagator = new ConstantPropagator();
    private final CodeGenerator codeGenerator = new CodeGenerator();
    private final ClassFileLoader classFileLoader = new ClassFileLoader(ClassLoader.getSystemClassLoader());

    private final InputStream inputStream;
    private final OutputStream outputStream;


    private Exception exception;

    public Jaskell(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        BytecodeGenerator.setLoader(classFileLoader);
        parser.parse(inputStream);

        for (Module m : parser.getModules()) {
            /* generate types */
            Type2Class t2c = new Type2Class(m);
            //m.visit(t2c);
            /* generate code */
            LambdaLifter ll = new LambdaLifter(m);
            StrictnessAnalyzer sa = new StrictnessAnalyzer();
            TypeChecker tc = new TypeChecker();
            CodeGenerator cg = new CodeGenerator();
            /* work ! */
            m.visit(ll);
            m.visit(sa);
            m.visit(tc);
            /* generate types */
            m.visit(t2c);
			/* generate code */
            m.visit(cg);
			/* write */
            CodeGenerator.cleanupClassFiles(classFileLoader);
        }

        Object theMain = null;
        PrintStream oldOut = System.out;
        try {
            System.setOut(new PrintStream(outputStream));
            runMain();
        } catch (Exception e) {
            this.exception = e;
        } finally {
            System.setOut(oldOut);
        }
    }

    private Object runMain() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class cls = classFileLoader.loadClass("Main$Module");
        Method met = cls.getMethod("main", new Class[]{String[].class});
        return met.invoke(null, new Object[]{new String[0]});
    }

    public Exception getException() {
        return exception;
    }
}
