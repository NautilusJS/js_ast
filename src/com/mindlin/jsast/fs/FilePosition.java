package com.mindlin.jsast.fs;

public class FilePosition {
	protected final long line;
	protected final long col;
	
	public FilePosition(long line, long col) {
		this.line = line;
		this.col = col;
	}
	
	public long getLine() {
		return line;
	}
	
	public long getCol() {
		return col;
	}
	
	@Override
	public String toString() {
		return String.format("%d:%d", line, col);
	}
}
