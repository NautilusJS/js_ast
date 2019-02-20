package com.mindlin.jsast.fs;

import java.util.List;

public interface SourceBundle {
	List<SourceFile> getSources();
	SourceFile getSource(String name);
}
