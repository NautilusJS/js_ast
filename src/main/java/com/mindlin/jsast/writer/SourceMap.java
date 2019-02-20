package com.mindlin.jsast.writer;

import java.util.Map;

import com.mindlin.jsast.fs.FilePosition;
import com.mindlin.jsast.fs.SourceFile;

public class SourceMap {
	Map<String, SourceFile> sources;
	
	public void addMapping(String sourceName, long srcPos, int dstLine, int dstCol, String name) {
		SourceFile source = sources.get(sourceName);
		FilePosition src = source.getOffsetPosotion(srcPos);
	}
	
}
