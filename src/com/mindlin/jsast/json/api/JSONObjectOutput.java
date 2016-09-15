package com.mindlin.jsast.json.api;

import java.util.Map;

public interface JSONObjectOutput extends SafelyCloseable {
	void writeBoolean(String key, boolean value);

	void writeByte(String key, byte value);

	void writeChar(String key, char value);

	void writeShort(String key, short value);

	void writeInt(String key, int value);

	void writeFloat(String key, float value);

	void writeLong(String key, long value);

	void writeDouble(String key, double value);

	void writeString(String key, String value);

	void writeObject(String key, Object value);

	void write(Map<String, ? extends Object> entries);

	void writeUnescaped(String data);

	void flush();
}
