package com.mindlin.jsast.impl.tree;

import java.util.ArrayList;

public class LineMap {
	long[] newlinePositions;
	public static LineMap compile(String s) {
		ArrayList<Integer> offsets = new ArrayList<>();
		
		//TODO finish
		return new LineMap(offsets.stream().mapToLong(Number::longValue).toArray());
	}
	public LineMap(long[] positions) {
		this.newlinePositions = positions;
	}
	public int getLineNumber(final long position) {
		return 0;//TODO finish
	}
	public long getColumnNumber(final long position) {
		return position - newlinePositions[getLineNumber(position)];
	}
}
