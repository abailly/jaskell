/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
/*
 * Created on Mar 10, 2004
 *
 * $Log: Generator.java,v $
 * Revision 1.7  2004/09/07 10:04:04  bailly
 * cleared imports
 *
 * Revision 1.6  2004/09/06 08:35:17  bailly
 * corrected error in primitives code generation
 * moved class loading to BET
 * added internal loading of generated classes instead of file writing
 *
 * Revision 1.5  2004/08/30 21:00:35  bailly
 * cosmetic change in debug output
 *
 * Revision 1.4  2004/07/23 06:59:48  bailly
 * cosmetic
 *
 * Revision 1.3  2004/06/29 15:25:26  bailly
 * modification des types pour les messages
 *
 * Revision 1.2  2004/03/11 22:16:24  bailly
 * Modif Type2Class to generate CDef directly
 * Modif CodeGenerator and all visitors to handle properly
 * recursive namespaces
 *
 * Revision 1.1  2004/03/10 20:36:00  bailly
 * added GEnerator class for starightforward compilation
 *
 */
package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.bytecode.ClassFileWriter;
import fr.lifl.jaskell.compiler.bytecode.CodeGenerator;
import fr.lifl.jaskell.compiler.bytecode.Type2Class;
import fr.lifl.jaskell.compiler.core.Module;

import oqube.bytes.loading.ClassFileLoader;


/**
 * This class encapsulates the compilation process
 *
 * @author  bailly
 * @version $Id: Generator.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Generator {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private boolean verbose;

    private String outdir;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Compiles given module m.
     *
     * @param m          Module to compile
     * @param withSource output source information
     */
    public void generate(Module m, boolean withSource) {
        if (verbose) {
            System.err.println("[Generator] => Generating code for module " + m.getName());
        }
        //m.visit(t2c);
        Type2Class t2c = new Type2Class();
        /* generate code */
        LambdaLifter ll = new LambdaLifter();
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
        ClassFileWriter writer = new ClassFileWriter(outdir);
        writer.setLineInfo(withSource);
        CodeGenerator.cleanupClassFiles(writer);
        if (verbose)
            System.err.println("[Generator] => end " + m.getName());
    }

    /**
     * @return
     */
    public String getOutdir() {
        return outdir;
    }

    /**
     * @return
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @param string
     */
    public void setOutdir(String string) {
        outdir = string;
    }

    /**
     * @param b
     */
    public void setVerbose(boolean b) {
        verbose = b;
    }

    /**
     * Do all the various compilation phases on given module and load generated classes in given class loader. This
     * method does the following on the module <code>m</code> :
     *
     * <ol>
     * <li>lambda lifting,</li>
     * <li>strictness analysis,</li>
     * <li>type checking,</li>
     * <li>types generation,</li>
     * <li>code generation.</li>
     * </ol>
     *
     * @param m      the module to analyse - may not be null
     * @param loader the loader to use - may not be null
     */
    public void generateInternal(Module m, ClassFileLoader loader) {

        //m.visit(t2c);
        Type2Class t2c = new Type2Class();
        /* generate code */
        LambdaLifter ll = new LambdaLifter();
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
        CodeGenerator.cleanupClassFiles(loader);
    }

}
