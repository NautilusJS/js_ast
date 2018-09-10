package com.mindlin.jsast.fs;

import java.nio.file.Path;
import java.util.Arrays;

import com.mindlin.jsast.impl.tree.LineMap;
import com.mindlin.jsast.impl.util.CharacterStream;

/**
 * Abstract away a JS source
 */
public interface SourceFile {
	/**
	 * Get path to file location. May return null
	 */
	Path getPath();
	
	/**
	 * Get stream of source
	 * @return
	 */
	CharacterStream getSourceStream();
	
	/**
	 * Get unique source file name
	 * @return source file name
	 */
	String getName();
	
	/**
	 * Get LineMap for file
	 * @return
	 */
	default LineMap getLineMap() {return null;}
	
	long[] lineOffsets();
	
	Path getOriginalPath();
	
	boolean isExtern();
	
	default int getLineOffset(long offset) {
		long[] offsets = lineOffsets();
		int idx = Arrays.binarySearch(offsets, offset);
		if (idx < 0)
			idx = 1 - idx;
		return idx;
	}
	
	default SourcePosition getOffsetPosotion(long offset) {
		long[] offsets = lineOffsets();
		int line = Arrays.binarySearch(offsets, offset);
		if (line < 0)
			line = 1 - line;
		int col = (int) (offset - offsets[line]);
		return new SourcePosition(this, offset, line, col);
	}
}
