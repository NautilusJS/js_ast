package com.mindlin.jsast.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;

import com.mindlin.jsast.json.api.JSONArrayInput;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.jsast.json.api.JSONObjectInput;
import com.mindlin.jsast.json.api.SafelyCloseable;

import sun.reflect.ReflectionFactory;

public class JSONInputStream implements JSONInput {
	protected final Reader in;
	protected final Stack<SafelyCloseable> children = new Stack<>();
	
	public JSONInputStream(Reader in) {
		this.in = in;
	}
	
	private String nextToken;
	
	private String nextToken() throws IOException {
		if (nextToken == null)
			nextToken = readToken();
		return nextToken;
	}
	
	private String readStringLiteral(char openQuote) throws IOException {
		boolean escaped;
		StringBuffer sb = new StringBuffer();
		//TODO read in batches (ex. buffer of size 16)
		//TODO refactor with JSTokenizer
		boolean escaped = false;
		while (in.ready()) {
			int v = in.read();
			if (v < 0)//EOS
				throw new EOFException("Stream closed while reading string literal");
			if (escaped) {
				char escapedVal;
				switch (v) {
					case '\\':
					case '"':
					case '\'':
					case '/':
						escapedVal = (char) v;
						break;
					case 'b':
						escapedVal = '\b';
						break;
					case 'f':
						escapedVal = '\f';
						break;
					case 'n':
						escapedVal = '\n';
						break;
					case 'r':
						escapedVal = '\r';
						break;
					case 't':
						escapedVal = '\t';
						break;
					case 'u':
						//Unicode escape
						char[] buf = new char[4];
						if (in.read(buf) < 4)
							throw new EOFException("Unexpected end of input in unicode escape sequence");
						escapedVal = (char) Integer.valueOf(new String(buf), 16);
						break;
					default:
						throw new IOException("Unknown escape sequence in JSON: \\" + ((char) v));
				}
				escaped = false;
				sb.append(escapedVal);
			} else if (v == openQuote)
				break;
			else if (v == '\\')
				escaped = true;
			else
				sb.append((char) v);
		}
		return sb.toString();
	}
	
	private String readToken() throws IOException {
		int v = in.read();
		if (v < 0)
			return null;//EOS
		if (Character.isWhitespace(v)) {
			//Skip whitespace
			if (in.markSupported()) {
				//Skip whitespace, in batches
				while (true) {
					char[] buf = new char[16];
					in.mark(16);
					int len = in.read(buf);
					if (len < 0)
						return null;//EOS
					int offset;
					for (offset = 0; offset < len && Character.isWhitespace(buf[offset]); offset++);
					if (offset < len) {
						in.reset();
						in.skip(offset);//TODO check for off-by-one problems
						break;
					}
				}
			} else {
				//Skip whitespace, character-by-character
				while ((v = in.read()) >= 0 && Character.isWhitespace(v));
			}
		}
		switch (v) {
			case '{':
			case '}':
			case '[':
			case ']':
			case ',':
			case ':':
				return String.valueOf((char) v);
			case '"':
			case '\'':
				return readStringLiteral((char) v);
			//TODO support numbers
			//TODO support booleans
		}
		throw new UnsupportedOperationException("Not finished");
	}

	@Override
	public JSONObjectInput readObject() throws IOException {
		return new JSONObjectInputStream();
	}
	
	@Override
	public void close() {
		this.nextToken = null;
		try {
			this.in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T> T readObject(Class<? extends T> clazz) throws IOException {
		return readObject().mapTo(clazz);
	}

	@Override
	public JSONArrayInput readArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SafelyCloseable mark() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected class JSONObjectInputStream implements JSONObjectInput {
		protected String currentKey;
		protected Object currentValue;
		protected boolean open = true;
		
		protected JSONObjectInputStream() {
			if (!"{".equals(nextToken()))
				throw new IllegalStateException("Cannot read object");
			children.push(this);
		}
		
		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Entry<String, ? extends Object> next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean readAll() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String nextKey() {
			// TODO Auto-generated method stub
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T mapTo(Class<? extends T> clazz) {
			if (clazz == null)
				throw new NullPointerException("Class may not be null");
			if (clazz.isAssignableFrom(Serializable.class)) {
				Constructor<? extends T> ctor;
				try {
					//Get constructor via fun reflection
					ctor = (Constructor<? extends T>) ReflectionFactory.getReflectionFactory()
							.newConstructorForSerialization(clazz);
				} catch (SecurityException e) {
					//Get constructor via boring reflection
					try {
						ctor = clazz.getConstructor(new Class[0]);
					} catch (NoSuchMethodException | SecurityException e1) {
						e1.printStackTrace();
						e1.addSuppressed(e1);
						throw new RuntimeException(e1);
					}
				}
				T result;
				try {
					result = ctor.newInstance(new Object[0]);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
					throw new RuntimeException("Unable to instantiate object", e);
				}
				while (this.hasNext()) {
					String key = this.nextKey();
					Field field;
					try {
						field = clazz.getField(key);
					} catch (NoSuchFieldException | SecurityException e) {
						try {
							field = clazz.getDeclaredField(key);
						} catch (NoSuchFieldException | SecurityException e1) {
							e1.addSuppressed(e);
							throw new RuntimeException("Cannot map field name: " + key, e1);
						}
					}
					
				}
			}
			return null;
		}

		@Override
		public boolean canReadBoolean() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean getBoolean() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean optBoolean() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean optBoolean(boolean defaultValue) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean canReadNull() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Void readNull() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Void> optNull() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean canReadNumber() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Number getNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Number> optNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean canReadString() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<String> optString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean canReadObject() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public JSONObjectInput getObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<JSONObjectInput> optObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <C> boolean canReadObject(Class<? extends C> clazz) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public <C> C readObject(Class<? extends C> clazz) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <C> Optional<C> optObject(Class<? extends C> clazz) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean canReadArray() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public JSONArrayInput getArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<JSONArrayInput> optArray() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
