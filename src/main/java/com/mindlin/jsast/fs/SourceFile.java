package com.mindlin.jsast.fs;

import java.nio.file.Path;
import java.util.Arrays;

import com.mindlin.jsast.impl.tree.LineMap;
import com.mindlin.jsast.impl.util.CharacterArrayStream;
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
	 * @return New stream. Should be unique across calls.
	 */
	CharacterStream getSourceStream();
	
	/**
	 * Get unique source file name
	 * @return source file name
	 */
	String getName();
	
	/**
	 * Get LineMap for file
	 * @return line map (or null, if not present)
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
	
	public static class NominalSourceFile implements SourceFile {
		protected final String name;
		protected final String text;
		
		public NominalSourceFile(String name, String text) {
			this.name = name;
			this.text = text;
		}

		@Override
		public Path getPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public CharacterStream getSourceStream() {
			return new CharacterArrayStream(this.text);
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public long[] lineOffsets() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Path getOriginalPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isExtern() {
			return false;
		}
	}
}
