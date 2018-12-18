package com.mindlin.jsast.fs;

import java.io.Serializable;
import java.util.Objects;

public class SourceRange implements Serializable {
	private static final long serialVersionUID = -1130923738278939904L;
	
	public static SourceRange startingFrom(SourcePosition start) {
		return new SourceRange(start, null);
	}
	
	public static SourceRange endingAt(SourcePosition end) {
		return new SourceRange(null, end);
	}
	
	protected SourcePosition start;
	protected SourcePosition end;
	
	/**
	 * Create range spanning from {@code start} to {@code end}.
	 * @param start Start position (inclusive)
	 * @param end End position (inclusive)
	 */
	public SourceRange(SourcePosition start, SourcePosition end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * @return Start position (inclusive). May be null.
	 */
	public SourcePosition getStart() {
		return this.start;
	}
	
	/**
	 * @return End position (inclusive). May be null.
	 */
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
		final SourcePosition start = this.getStart(), end = this.getEnd();
		
		if (start == null || end == null)
			return String.format("<%s - %s>", start == null ? "?" : start, end == null ? "?" : end);
		
		if (!Objects.equals(start.getSource(), end.getSource()))
			return String.format("<%s - %s>", this.getStart(), this.getEnd());
		
		String name = start.getSource() == null ? "unknown" : start.getSource().getName();
		
		//TODO: there are a few more edge cases 'round here that we can display better
		if (start.getLine() != end.getLine()) {
			if (start.getLine() == -1)
				return String.format("<%s(%d - %d)>", name, start.getCol(), end.getCol());
			return String.format("<%s(%d:%d - %d:%d)>", name, start.getLine(), start.getCol(), end.getLine(), end.getCol());
		}
		
		return String.format("<%s(%d:%d-%d)>", name, start.line, start.col, end.col);
	}
}
