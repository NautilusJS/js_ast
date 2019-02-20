package com.mindlin.nautilus.writer;

import java.io.IOException;
import java.io.Writer;

import com.mindlin.nautilus.tree.CompilationUnitTree;

public interface JSWriter {
	void write(CompilationUnitTree src, Writer out) throws IOException;
}
