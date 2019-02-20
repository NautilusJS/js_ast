package com.mindlin.jsast.json.api;

public interface SafelyCloseable extends AutoCloseable {
	@Override
	void close();
}
