package com.mindlin.jsast.fs;

public class FilePosition {
	protected final int line;
	protected final int col;
	
	public FilePosition(int line, int col) {
		this.line = line;
		this.col = col;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getCol() {
		return col;
	}
}
