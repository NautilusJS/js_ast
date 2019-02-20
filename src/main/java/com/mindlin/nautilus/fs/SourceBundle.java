package com.mindlin.nautilus.fs;

import java.util.List;

public interface SourceBundle {
	List<SourceFile> getSources();
	SourceFile getSource(String name);
}
