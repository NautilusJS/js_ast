package com.mindlin.jsast.fs;

import java.io.Serializable;
import java.util.Objects;

public class SourceRange implements Serializable {
	private static final long serialVersionUID = -1130923738278939904L;
	
	protected SourcePosition start;
	protected SourcePosition end;
	
	public SourceRange(SourcePosition start, SourcePosition end) {
		this.start = start;
		this.end = end;
	}
	
	public SourcePosition getStart() {
		return this.start;
	}
	
	public SourcePosition getEnd() {
		return this.end;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getStart(), this.getEnd());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SourceRange))
			return false;
		
		SourceRange other = (SourceRange) obj;
		return Objects.equals(this.getStart(), other.getStart())
				&& Objects.equals(this.getEnd(), other.getEnd());
	}
	
	@Override
	public String toString() {
		return String.format("<%s - %s>", this.getStart(), this.getEnd());
	}
}
