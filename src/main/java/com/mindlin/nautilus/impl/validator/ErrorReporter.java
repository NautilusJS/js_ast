package com.mindlin.nautilus.impl.validator;

import com.mindlin.nautilus.fs.SourceRange;
import com.mindlin.nautilus.tree.Tree;

public interface ErrorReporter {
	void report(ErrorLevel level, Tree target, String format, Object... args);
	void report(ErrorLevel level, SourceRange location, String format, Object... args);
	
	public static enum ErrorLevel {
		ERROR,
		WARNING,
		HINT,
	}
}
