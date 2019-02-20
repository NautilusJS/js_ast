package com.mindlin.jsast.writer;

public class JSWriterOptions implements Cloneable {
	public boolean minify = true;
	/**
	 * String to use for newline
	 */
	public String newline = System.lineSeparator();
	/**
	 * String to use for whitespace
	 */
	public String space = " ";
	/**
	 * Initial indent level
	 */
	public int baseIndent = 0;
	/**
	 * String to be used for indenting.
	 */
	public String indentStyle = "\t";
	
	/**
	 * Whether to output hexadecimal numbers if shorter
	 */
	public boolean hexNumbers = true;
	
	/**
	 * Set for minimal whitespace possible
	 */
	public boolean compact = true;
	
	/**
	 * Whether to write comments that are attached to the AST
	 */
	public boolean writeComments = true;
}
