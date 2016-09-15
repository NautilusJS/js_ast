package com.mindlin.jsast.json.api;

import java.io.InvalidClassException;
import java.util.Iterator;

import com.mindlin.jsast.json.JSONObject;

public interface JSONArrayInput extends SafelyCloseable, Iterator<Object> {
	<T> T read(Class<? extends T> clazz) throws InvalidClassException, JSONParseException;

	boolean readBoolean() throws JSONParseException;

	default byte readByte() throws JSONParseException {
		return (byte) readInt();
	}

	default char readChar() throws JSONParseException {
		String s = readString();
		if (s.length() != 1)
			throw new JSONParseException("Cannot read \"" + s + "\" as char");
		return s.charAt(0);
	}

	default short readShort() throws JSONParseException {
		int v = readInt();
		short s = (short) v;
		if (s != v)
			throw new JSONParseException("Cannot read " + v + " as short");
		return s;
	}

	default int readInt() throws JSONParseException {
		long v = readLong();
		int i = (int) v;
		if (i != v)
			throw new JSONParseException("Cannot read " + v + " as int");
		return i;
	}

	default float readFloat() throws JSONParseException {
		double v = readDouble();
		float f = (float) v;
		if (f != v)
			throw new JSONParseException("Cannot read " + v + " as float");
		return f;
	}

	long readLong() throws JSONParseException;

	double readDouble() throws JSONParseException;

	Number readNumber() throws JSONParseException;

	String readString() throws JSONParseException;

	Void readNull() throws JSONParseException;
	
	JSONObject readObject() throws JSONParseException;

	JSONArrayInput readArray() throws JSONParseException;

	boolean isNextBoolean();

	boolean isNextByte();

	boolean isNextChar();

	boolean isNextShort();

	boolean isNextInt();

	boolean isNextFloat();

	boolean isNextDouble();

	boolean isNextNumber();

	boolean isNextString();

	boolean isNextNull();

	boolean isNextObject();

	boolean isNextArray();

	void skip();
	
	@Override
	Object next() throws JSONParseException;

	@Override
	boolean hasNext();
}
