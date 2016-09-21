package com.mindlin.jsast.tree;

import java.util.List;

public interface ObjectPatternTree extends PatternTree {
	List<ObjectLiteralPropertyTree> getProperties();
}
