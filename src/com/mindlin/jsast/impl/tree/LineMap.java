package com.mindlin.jsast.impl.tree;

import java.util.Arrays;

import com.mindlin.jsast.fs.FilePosition;
import com.mindlin.jsast.impl.util.LongStack;

public interface LineMap {
	
	/**
	 * Get line position
	 * @param position
	 * @return
	 */
	long getLineNumber(long position);
	
	/**
	 * Lookup position
	 * @param position
	 * @return
	 */
	FilePosition lookup(long position);
	
	public static LineMap compile(String s) {
		LongStack offsets = new LongStack(64, false);
		int lastOffset = -1;
		// Simple & happy. I love when programming languages
		// let you do tasks like this so easily
		while ((lastOffset = s.indexOf('\n', lastOffset)) > -1)
			offsets.push(lastOffset);
		return new CompiledLineMap(offsets.toArray());
	}
	
	public static class CompiledLineMap implements LineMap {
		final long[] newlinePositions;
		
		public CompiledLineMap(long[] positions) {
			this.newlinePositions = positions;
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
		public FilePosition lookup(final long position) {
			final long row = getLineNumber(position);
			return new FilePosition(row, position - newlinePositions[(int) row]);
		}
	}
	
	public static class LineMapBuilder implements LineMap {
		long[] newlinePositions = new long[64];
		int length = 0;
		
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
			int lo = 0;
			int hi = length;
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
		public FilePosition lookup(long position) {
			final long row = getLineNumber(position);
			return new FilePosition(row, position - newlinePositions[(int) row]);
		}
		
		public void shrink() {
			this.newlinePositions = Arrays.copyOf(this.newlinePositions, this.length);
		}
	}
}
