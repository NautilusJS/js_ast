package com.mindlin.jsast.fs;

import java.nio.file.Path;

import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;

public class MemorySourceFile implements SourceFile {
	protected char[] data;
	
	public MemorySourceFile(char[] data) {
		this.data = data;
	}
	
	@Override
	public Path getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharacterStream getSourceStream() {
		return new CharacterArrayStream(data);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] lineOffsets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getOriginalPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExtern() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
