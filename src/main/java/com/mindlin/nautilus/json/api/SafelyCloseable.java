package com.mindlin.nautilus.json.api;

public interface SafelyCloseable extends AutoCloseable {
	@Override
	void close();
}
