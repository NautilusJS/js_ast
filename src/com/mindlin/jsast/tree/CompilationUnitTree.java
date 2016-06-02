package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.impl.parser.LineMap;

public interface CompilationUnitTree extends Tree {
        LineMap getLineMap();
        List<? extends Tree> getSourceElements();
        String getSourceName();
        boolean isStrict();
	default Tree.Kind getKind() {
	        return Tree.Kind.COMPILATION_UNIT;
	}
}