package com.mindlin.jsast.json;

import java.io.Reader;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;

import com.mindlin.jsast.json.api.JSONArrayInput;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.jsast.json.api.JSONObjectInput;
import com.mindlin.jsast.json.api.SafelyCloseable;

public class JSONInputStream implements JSONInput {
	protected final Reader in;
	protected final Stack<SafelyCloseable> children = new Stack<>();
	
	public JSONInputStream(Reader in) {
		this.in = in;
	}

	@Override
	public JSONObjectInput readObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T readObject(Class<? extends T> clazz) {
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
		String currentKey;
		
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

		@Override
		public <T> T mapTo(Class<? extends T> clazz) {
			// TODO Auto-generated method stub
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
		
	}
}
