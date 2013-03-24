/*
 * Created on Jun 5, 2003 by Arnaud Bailly - bailly@lifl.fr
 * Copyright 2003 - Arnaud Bailly 
 * $Log: Main.java,v $
 * Revision 1.6  2004/02/09 20:40:02  nono
 * mise a jour repository central
 *
 * Revision 1.5  2003/06/23 06:33:31  bailly
 * Debugging
 * Added JError primitive type to raise runtime exceptions
 * when no match is found
 *
 */
package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.bytecode.ClassFileWriter;
import fr.lifl.jaskell.compiler.bytecode.CodeGenerator;
import fr.lifl.jaskell.compiler.bytecode.Type2Class;
import fr.lifl.jaskell.compiler.core.Module;
import fr.lifl.jaskell.parser.ParseException;
import fr.lifl.jaskell.parser.Yyparser;

import java.util.Iterator;

/**
 * The main program for compiling Jaskell sources
 * 
 * This is the class holding the main program for compiling jaskell
 * sources into java bytecode
 * 
 * @author bailly
 * @version $Id: Main.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Main {

	public static void usage() {
		System.err.println(
			"java -jar jaskell.jar  (-I <include-dir>)* [-v] [-d <dirname>] <file1> <file2> .. <filen>");
		System.exit(1);
	}

	public static void main(String argv[]) throws ClassNotFoundException {
		/* list of files to process */
		java.util.List files = new java.util.ArrayList();
		/* list of directories to look for import directives */
		java.util.List includes = new java.util.ArrayList();
		/* output directory */
		String outdir = ".";
		/* verbos output flag */
		boolean verbose = false;

		if (argv.length < 1)
			usage();
		/* parse command line */
		for (int i = 0; i < argv.length; i++)
			if (argv[i].startsWith("-")) // command-line option
				switch (argv[i].charAt(1)) {
					case 'I' : // include directory
						includes.add(argv[++i]);
						break;
					case 'd' : // output stream
						outdir = argv[++i];
						break;
					case 'v' : // verbose processing
						verbose = true;
						break;
					default :
						System.err.println("Unknown option :" + argv[i]);
						usage();
				} else
				files.add(argv[i]);

		/* process include directives */
		Iterator it = includes.iterator();
		StringBuffer incprop = new StringBuffer("");
		while (it.hasNext()) {
			incprop.append((String) it.next());
			if (it.hasNext())
				incprop.append(':');
		}
		String incstr = null;
		if (!incprop.toString().equals(""))
			incstr = incprop.toString();
		/* process files */
		Yyparser parser = new Yyparser(verbose);
		it = files.iterator();
		while (it.hasNext()) {
			java.io.FileInputStream instr = null;
			String fname = (String) it.next();
			if (verbose)
				System.err.println("Processing file " + fname);
			/* open file */
			try {
				parser.parse(fname);
			} catch (ParseException pex) {
				System.err.println(
					"Error while parsing " + fname + " : " + pex.getMessage());
				continue;
			}
		}

		/* generate code */
		/* load prelude */
		Class prim = Class.forName("jaskell.compiler.core.Primitives");
		
		Iterator modit = parser.getModules().iterator();
		while (modit.hasNext()) {
			Module m = (Module) modit.next();
			if (verbose) {
				System.err.println("Generating code for module :");
				System.err.println(m.toString());

			}
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
			ClassFileWriter writer = new ClassFileWriter(outdir);
			CodeGenerator.cleanupClassFiles(writer);
			Type2Class.cleanupClassFiles(writer);
			if (verbose)
				System.err.println(m);
		}
		/* done */
	}

}
