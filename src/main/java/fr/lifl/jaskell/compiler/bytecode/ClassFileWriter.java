/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler.bytecode;

import java.io.*;

import java.util.Iterator;

import oqube.bytes.*;
import oqube.bytes.ClassFile;
import oqube.bytes.attributes.CodeAttribute;
import oqube.bytes.attributes.LineNumberTableAttribute;
import oqube.bytes.attributes.SourceFileAttribute;
import oqube.bytes.instructions.Instruction;
import oqube.bytes.loading.ClassFileHandler;
import oqube.bytes.struct.ClassFileInfo;
import oqube.bytes.struct.MethodFileInfo;


/**
 * Writes class files to disk This class writes the ClassFile objects to .class files. The package fr.lifl.directories
 * are created if non existent, relative to a root directory.
 */
public class ClassFileWriter implements ClassFileHandler {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* base directory where we write class fiels */
    private String rootDir = ".";

    /* output line info ?*/
    private boolean lineInfo = false;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Parameter less constructor. Files are written relative to current directory
     */
    public ClassFileWriter() {
    }

    /**
     * Constructor which sets the root directory from where files are created
     *
     * @param rootDir the base directory for creating directories and files
     */
    public ClassFileWriter(String rootDir) {
        this.rootDir = rootDir;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Called by class generators for handling generated class files
     */
    public void handle(String name, ClassFile cf) {
        try {
            String fname = rootDir + "/" + name + ".class";
            // create directory structure
            File dir = new File(fname.substring(0, fname.lastIndexOf('/')));
            if (!dir.exists())
                dir.mkdirs();
            File gendir = new File(rootDir + "/gen/" + fname.substring(0, fname.lastIndexOf('/')));
            if (!gendir.exists())
                gendir.mkdirs();
            if (lineInfo)
                makeSourceFile(name + ".java", cf);
            // create file
            FileOutputStream fos = new FileOutputStream(fname);
            DataOutputStream dos = new DataOutputStream(fos);
            cf.write(dos);
            fos.close();
            dos.close();
        } catch (IOException ioex) {
            System.err.println("Error in writing class file " +
                name +
                " : " + ioex.getMessage());
        }
    }

    /**
     * @return
     */
    public boolean isLineInfo() {
        return lineInfo;
    }

    /**
     * @param b
     */
    public void setLineInfo(boolean b) {
        lineInfo = b;
    }

    /**
     * Generate a source for given classfile which represents code in assembly form. This method adds a SourceFile and
     * LineAttribute attributes to the given classfile.
     *
     * @param name source file name
     * @param cf   the classfile object to modify
     */
    private void makeSourceFile(String name, ClassFile cf) throws IOException {
        int ln = 1;
        File f = new File(rootDir + "/gen/" + name);
        PrintWriter w = new PrintWriter(new FileWriter(f));
        /* add source file attribute */
        SourceFileAttribute attr = new SourceFileAttribute(cf);
        attr.setSourceFile(name.substring(name.lastIndexOf('/') + 1));
        cf.add(attr);
        /* output class name */
        ClassFileInfo info = cf.getClassFileInfo();
        w.println("class " + info.getName());
        ln++;
        Iterator it = cf.getAllMethods().iterator();
        while (it.hasNext()) {
            MethodFileInfo mfi = (MethodFileInfo) it.next();
            w.println("  method " + mfi.getName() + mfi.getSignature());
            ln++;
            CodeAttribute code = mfi.getCodeAttribute();
            LineNumberTableAttribute lt = new LineNumberTableAttribute(cf);
            code.addAttribute(lt);
            Iterator it2 = code.getAllInstructions().iterator();
            while (it2.hasNext()) {
                Instruction ins = (Instruction) it2.next();
                w.println("    " + ins.toString());
                lt.addLineInfo(ins.getPc(), ln);
                ln++;
            }
        }
        w.flush();
        w.close();
    }

}
