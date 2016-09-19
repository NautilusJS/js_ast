package com.mindlin.jsast.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import com.mindlin.jsast.json.api.JSONArrayOutput;
import com.mindlin.jsast.json.api.JSONObjectOutput;
import com.mindlin.jsast.json.api.JSONOutput;
import com.mindlin.jsast.json.api.JSONSerializationException;
import com.mindlin.jsast.json.api.SafelyCloseable;

public class JSONOutputStream implements JSONOutput {
	protected final JSONSerializationConfig config = new JSONSerializationConfig();
	protected final Writer out;
	protected Stack<SafelyCloseable> children = new Stack<>();

	public JSONOutputStream(Writer out) {
		this.out = out;
	}

	@Override
	public JSONObjectOutputStream makeObject() {
		writeSafe("{");
		JSONObjectOutputStream result = new JSONObjectOutputStream();
		children.push(result);
		return result;
	}

	@Override
	public JSONArrayOutputStream makeArray() {
		writeSafe("[");
		JSONArrayOutputStream result = new JSONArrayOutputStream();
		children.push(result);
		return result;
	}
	
	@Override
	public SafelyCloseable mark() {
		return children.push(new JSONOutputStreamMark());
	}

	@Override
	public void writeUnescaped(String text) {
		writeSafe(text);
	}

	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}

	public static class JSONSerializationConfig {
		boolean indent;
		boolean wrapObjectEntries;
		boolean wrapArrayEntries;
		boolean pretty = false;
		boolean forceQuoteKeys = true;
		char indentChar = '\t';
	}
	
	void indent() throws IOException {
		if (!config.indent)
			return;
		for (int i = 0, l = children.size(); i < l; i++)
			out.write(config.indentChar);
	}
	
	void writeStringEscaped(char[] chars) {
		try {
			out.write('"');
			out.write(JSONUtils.escape(chars));
			out.write('"');
		} catch (IOException e) {
			throw new JSONSerializationException(e);
		}
	}
	
	void writeSafe(String text) {
		try {
			out.write(text);
		} catch (IOException e) {
			throw new JSONSerializationException(e);
		}
	}
	
	void writeSafe(char[] chars) {
		writeSafe(chars, 0, chars.length);
	}
	
	void writeSafe(char[] chars, int off, int len) {
		try {
			out.write(chars, off, len);
		} catch (IOException e) {
			throw new JSONSerializationException(e);
		}
	}

	void doClose(AutoCloseable e) throws RuntimeException {
		if (children.isEmpty())
			throw new RuntimeException(e + " is not a child");
		SafelyCloseable child;
		while (!(children.isEmpty() || (child = children.peek()) == e))
			child.close();
		if (children.isEmpty())
			throw new RuntimeException("Stack is empty");
		children.pop();
	}
	
	protected class JSONOutputStreamMark implements SafelyCloseable {
		private boolean closed = false;
		@Override
		public void close() {
			if (closed)
				return;
			closed = true;
			doClose(this);
		}
	}
	
	protected class JSONObjectOutputStream implements JSONObjectOutput {
		private boolean closed = false;
		private boolean isStart = true;

		private void writeSeparator() {
			try {
				if (!isStart)
					out.write(',');
				if (config.wrapObjectEntries) {
					out.write('\n');
					indent();
				} else if (config.pretty && !isStart) {
					out.write(' ');
				}
				if (isStart)
					isStart = false;
			} catch (IOException e) {
				throw new JSONSerializationException(e);
			}
		}

		private void writeKey(String key) {
			writeSeparator();
			String escaped = JSONUtils.escapeKeyIfNeeded(key.toCharArray(), config.forceQuoteKeys);
			try {
				out.write(escaped);
				out.write(':');
				if (config.pretty)
					out.write(' ');
			} catch (IOException e) {
				throw new JSONSerializationException(e);
			}
		}

		@Override
		public void writeBoolean(String key, boolean value) {
			writeKey(key);
			writeSafe(Boolean.toString(value));
		}

		@Override
		public void writeByte(String key, byte value) {
			writeKey(key);
			writeSafe(Byte.toString(value));
		}

		@Override
		public void writeChar(String key, char value) {
			writeKey(key);
			writeStringEscaped(new char[]{value});
		}

		@Override
		public void writeShort(String key, short value) {
			writeKey(key);
			writeSafe(Short.toString(value));
		}

		@Override
		public void writeInt(String key, int value) {
			writeKey(key);
			writeSafe(Integer.toString(value));
		}

		@Override
		public void writeFloat(String key, float value) {
			writeKey(key);
			writeSafe(Float.toString(value));
		}

		@Override
		public void writeLong(String key, long value) {
			writeKey(key);
			writeSafe(Long.toString(value));
		}

		@Override
		public void writeDouble(String key, double value) {
			writeKey(key);
			writeSafe(Double.toString(value));
		}

		@Override
		public void writeString(String key, String value) {
			writeKey(key);
			writeStringEscaped(value.toCharArray());
		}

		@Override
		public void writeObject(String key, Object value) {
			writeKey(key);
			JSONUtils.serialize(value, JSONOutputStream.this);
		}

		@Override
		public void write(Map<String, ? extends Object> entries) {
			for (String key : entries.keySet())
				writeObject(key, entries.get(key));
		}

		@Override
		public void writeUnescaped(String data) {
			JSONOutputStream.this.writeUnescaped(data);
		}

		@Override
		public void flush() {
			JSONOutputStream.this.flush();
		}

		@Override
		public void close() {
			if (closed)
				return;
			closed = true;
			try {
				doClose(this);
				if (config.wrapObjectEntries) {
					out.write('\n');
					indent();
				}
				out.write('}');
			} catch (Exception e) {
				throw new JSONSerializationException(e);
			}
		}
	}
	
	protected class JSONArrayOutputStream implements JSONArrayOutput {
		private boolean isStart = true;
		private boolean closed = false;
		
		private void writeSeparator() {
			try {
				if (!isStart)
					out.write(',');
				if (config.wrapArrayEntries) {
					out.write('\n');
					indent();
				} else if (config.pretty && !isStart) {
					out.write(' ');
				}
				if (isStart)
					isStart = false;
			} catch (IOException e) {
				throw new JSONSerializationException(e);
			}
		}
		
		@Override
		public void writeBoolean(boolean value) {
			writeSeparator();
			writeSafe(Boolean.toString(value));
		}

		@Override
		public void writeByte(byte value) {
			writeSeparator();
			writeSafe(Byte.toString(value));
		}

		@Override
		public void writeChar(char value) {
			writeSeparator();
			writeStringEscaped(new char[]{value});
		}

		@Override
		public void writeShort(short value) {
			writeSeparator();
			writeSafe(Short.toString(value));
		}

		@Override
		public void writeInt(int value) {
			writeSeparator();
			writeSafe(Integer.toString(value));
		}

		@Override
		public void writeFloat(float value) {
			writeSeparator();
			writeSafe(Float.toString(value));
		}

		@Override
		public void writeLong(long value) {
			writeSeparator();
			writeSafe(Long.toString(value));
		}

		@Override
		public void writeDouble(double value) {
			writeSeparator();
			writeSafe(Double.toString(value));
		}

		@Override
		public void writeString(String value) {
			writeSeparator();
			writeStringEscaped(value.toCharArray());
		}

		@Override
		public void writeObject(Object value) {
			writeSeparator();
			JSONUtils.serialize(value, JSONOutputStream.this);
		}

		@Override
		public void write(Collection<?> values) {
			for (Object value : values)
				writeObject(value);
		}

		@Override
		public void writeUnescaped(String text) {
			JSONOutputStream.this.writeUnescaped(text);
		}

		@Override
		public void flush() {
			JSONOutputStream.this.flush();
		}

		@Override
		public void close() {
			if (closed)
				return;
			closed = true;
			try {
				doClose(this);
				if (config.wrapArrayEntries) {
					out.write('\n');
					indent();
				}
				out.write(']');
			} catch (Exception e) {
				throw new JSONSerializationException(e);
			}
		}
	}
}
