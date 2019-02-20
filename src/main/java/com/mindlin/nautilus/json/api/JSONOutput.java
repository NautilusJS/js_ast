package com.mindlin.nautilus.json.api;

public interface JSONOutput extends AutoCloseable {
	JSONObjectOutput makeObject();

	JSONArrayOutput makeArray();
	
	SafelyCloseable mark();

	void writeUnescaped(String text);

	void flush();
}
