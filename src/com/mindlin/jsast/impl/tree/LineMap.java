package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.impl.util.LongStack;

public class LineMap {
	final long[] newlinePositions;

	public static LineMap compile(String s) {
		LongStack offsets = new LongStack(64, false);
		int lastOffset = -1;
		// Simple & happy. I love when programming languages
		// let you do tasks like this so easily
		while ((lastOffset = s.indexOf('\n', lastOffset)) > -1)
			offsets.push(lastOffset);
		return new LineMap(offsets.toArray());
	}

	public LineMap(long[] positions) {
		this.newlinePositions = positions;
	}

	public long getLineNumber(final long position) {
		// Binary search to get the line number
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

	public Position lookup(final long position) {
		final long row = getLineNumber(position);
		return new Position(row, position - newlinePositions[(int) row]);
	}

	public static final class Position {
		final long row, col;

		public Position(long row, long col) {
			this.row = row;
			this.col = col;
		}

		public long getRow() {
			return this.row;
		}

		public long getCol() {
			return this.col;
		}
	}
}
