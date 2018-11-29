package com.mindlin.jsast.impl.tree;

import java.io.Serializable;
import java.util.Arrays;

import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.impl.util.LongStack;

/**
 * Provider for mapping raw offsets to {@link SourcePosition SourcePositions}. 
 * @author mailmindlin
 */
public interface LineMap {
	
	SourceFile getSource();
	
	/**
	 * Get line position
	 * @param position
	 * @return Line number that {@code position} would fall on (starting at 0).
	 */
	long getLineNumber(long position);
	
	/**
	 * Get offset of the start of a line.
	 * 
	 * @param line
	 * @return Character offset
	 * @throws IndexOutOfBoundsException If {@code line} is not mapped.
	 */
	long getLineOffset(long line);
	
	/**
	 * Lookup position
	 * @param position
	 * @return mapped position
	 */
	default SourcePosition lookup(long position) {
		// Definately non-optimal impl. Please override.
		long row = getLineNumber(position);
		long col = position - getLineOffset(row);
		return new SourcePosition(getSource(), position, row, col);
	}
	
	public static LineMap compile(String s) {
		LongStack offsets = new LongStack(64, false);
		
		// Simple & happy. I love when programming languages
		// let you do tasks like this so easily
		int lastOffset = -1;
		while ((lastOffset = s.indexOf('\n', lastOffset)) > -1)
			offsets.push(lastOffset);
		
		return new CompiledLineMap(null, offsets.toArray());
	}
	
	public static class CompiledLineMap implements LineMap, Serializable {
		private static final long serialVersionUID = -7936542102077343629L;
		protected final SourceFile source; 
		protected final long[] newlinePositions;
		
		// For serialization
		@SuppressWarnings("unused")
		private CompiledLineMap() {
			this(null, null);
		}
		
		public CompiledLineMap(SourceFile source, long[] positions) {
			this.source = source;
			this.newlinePositions = positions;
		}
		
		@Override
		public SourceFile getSource() {
			return this.source;
		}
		
		@Override
		public long getLineNumber(final long position) {
			// Binary search to get the line number
			// Find highest newline less than value
			int lo = 0;
			int hi = newlinePositions.length;
			while (lo < hi) {
				int idx = (hi - lo) / 2 + lo;
				long val = newlinePositions[idx];
				if (val > position)
					hi = idx;
				else if (val < position)
					lo = idx;
				else
					return idx;
			}
			return lo;// lo==hi
		}
		
		@Override
		public SourcePosition lookup(final long position) {
			long row = getLineNumber(position);
			long col = position - newlinePositions[(int) row];
			return new SourcePosition(source, position, row, col);
		}

		@Override
		public long getLineOffset(long line) {
			if (line < 0 || this.newlinePositions.length <= line)
				throw new IndexOutOfBoundsException(String.format("%d is out of range [0, %d]", line, this.newlinePositions.length));
			
			return this.newlinePositions[(int) line];
		}
	}
	
	public static class LineMapBuilder implements LineMap {
		protected SourceFile source;
		protected long[] newlinePositions = new long[64];
		protected int length = 0;
		
		public LineMapBuilder(SourceFile source) {
			this.source = source;
		}
		
		@Override
		public SourceFile getSource() {
			return this.source;
		}
		
		public void putNewline(long position) {
			if (newlinePositions.length == length) {
				int newCap = length * 3 / 2 + 1;
				
				//Overflow checking stuff
				if (newCap < length)
					newCap = Integer.MAX_VALUE - 8;//Something something array constant overhead
				if (newCap <= length)//Still can't please everyone
					throw new OutOfMemoryError("Somehow, you managed to overflow array capacity limits. Congrats.");
				
				newlinePositions = Arrays.copyOf(newlinePositions, newCap);
			}
			
			newlinePositions[length++] = position;
		}
		
		@Override
		public long getLineNumber(long position) {
			// Binary search to get the line number
			int idx = Arrays.binarySearch(newlinePositions, 0, length, position);
			return idx < 0 ? (-1 - idx) : idx;
		}
		
		@Override
		public long getLineOffset(long line) {
			if (line < 0 || this.length <= line)
				throw new ArrayIndexOutOfBoundsException(String.format("%d is out of range [0, %d]", line, this.length));
			
			return this.newlinePositions[(int) line];
		}
		
		@Override
		public SourcePosition lookup(long offset) {
			long row = this.getLineNumber(offset);
			long col = offset - newlinePositions[(int) row];
			
			return new SourcePosition(this.source, offset, row, col);
		}
		
		public void shrink() {
			this.newlinePositions = Arrays.copyOf(this.newlinePositions, this.length);
		}
		
		public LineMap compiled() {
			return new CompiledLineMap(this.getSource(), Arrays.copyOf(this.newlinePositions, this.length));
		}
	}
}
