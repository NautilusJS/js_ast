package com.mindlin.jsast.json.api;

import java.util.Collection;

public interface JSONArrayOutput extends SafelyCloseable {
	void writeBoolean(boolean value);

	void writeByte(byte value);

	void writeChar(char value);

	void writeShort(short value);

	void writeInt(int value);

	void writeFloat(float value);

	void writeLong(long value);

	void writeDouble(double value);

	void writeString(String value);

	void writeObject(Object value);
	
	void write(Collection<?> values);

	void writeUnescaped(String data);

	void flush();
}
