/*
 * Created on Jun 9, 2003
 * Copyright 2003 Arnaud Bailly
  */
package fr.lifl.jaskell.compiler.core;

/**
 * This tag implementation stores information about the source file
 * location of an expression
 * 
 * @author bailly
 * @version $Id: FileAndLineTag.java 1153 2005-11-24 20:47:55Z nono $
 */
public class FileAndLineTag implements Tag {

	private static final String tagname = "source";

	private String filename;

	private int line;

	private int column;

	/**
	 * Construct a tag identifying the file name, the line number
	 * and the column number where the associated expression is
	 * defined.
	 * 
	 * @param filename a filename
	 * @param line an integer denoting line number
	 * @param col an integer denoting column number 
	 */
	public FileAndLineTag(String filename, int line, int col) {
		this.filename = filename;
		this.line = line;
		this.column = col;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.core.Tag#getName()
	 */
	public String getName() {
		return tagname;
	}

	/**
	 * @return
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return
	 */
	public int getLine() {
		return line;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return filename + "("+line +","+column+")";
	}

}
