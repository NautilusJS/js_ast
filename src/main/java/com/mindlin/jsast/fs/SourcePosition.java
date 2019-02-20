package com.mindlin.jsast.fs;

import java.io.Serializable;
import java.util.Objects;

public class SourcePosition implements Serializable {
	private static final long serialVersionUID = 4861187271740573475L;
	
	protected final SourceFile source;
	protected final long offset;
	protected final long line;
	protected final long col;
	
	public SourcePosition(SourceFile source, long offset, long line, long col) {
		this.source = source;
		this.offset = offset;
		this.line = line;
		this.col = col;
	}
	
	public long getOffset() {
		return this.offset;
	}
	
	public long getLine() {
		return line;
	}
	
	public long getCol() {
		return col;
	}
	
	public SourceFile getSource() {
		return this.source;
	}
	
	@Override
	public String toString() {
		String sourceName = this.getSource() == null ? "unknown" : this.getSource().getName();
		if (this.getLine() != -1) {
			return String.format("%s(%d:%d)", sourceName, this.getLine(), this.getCol());
		} else if (this.getOffset() != -1) {
			// Try fallback to offset
			return String.format("%s(%d)", sourceName, this.getOffset());
		} else {
			return "???";
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getSource(), this.getOffset(), this.getLine(), this.getCol());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SourcePosition))
			return false;
		
		SourcePosition otherPos = (SourcePosition) obj;
		return this.getOffset() == otherPos.getOffset()
				&& this.getLine() == otherPos.getLine()
				&& this.getCol() == otherPos.getCol()
				&& Objects.equals(this.getSource(), otherPos.getSource());
	}
}
