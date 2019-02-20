package com.mindlin.jsast.impl.util;

public class CharacterArrayStream extends AbstractCharacterStream {
	protected int position = -1;
	protected final char[] data;
	
	public CharacterArrayStream(char[] data) {
		this.data = data;
	}
	
	public CharacterArrayStream(String data) {
		this(data.toCharArray());
	}
	
	@Override
	public char current() {
		return data[position];
	}
	
	@Override
	public char next() {
		return data[++position];
	}
	
	@Override
	public char next(long offset) {
		return data[position += offset];
	}
	
	@Override
	public CharacterStream skip(long offset) {
		position += offset;
		return this;
	}
	
	@Override
	public long position() {
		return this.position;
	}
	
	@Override
	public CharacterStream position(long pos) {
		this.position = (int) pos;
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return position < data.length - 1;
	}
	
	@Override
	public boolean hasNext(long num) {
		if (num > Integer.MAX_VALUE)
			return false;
		
		return position < data.length - num;
	}
	
	@Override
	public CharacterStream skipWhitespace() {
		while (hasNext() && Characters.isJsWhitespace(data[position + 1]))
			position++;
		return this;
	}
	
	@Override
	public CharacterStream skipWhitespace(boolean passNewlines) {
		if (passNewlines)
			return this.skipWhitespace();
		
		while (position + 1 < data.length && Characters.isJsWhitespace(data[position + 1]))
			if (data[++position] == '\n')
				break;
		
		return this;
	}
	
	@Override
	public char peek(long offset) {
		long index = position + offset;
		
		if (index > data.length)//Also checks if > Integer.MAX_INT
			throw new IndexOutOfBoundsException("Cannot look ahead by " + offset);
		
		return data[(int) index];
	}

	@Override
	public String copyFromMark() {
		final long mark = this.marks.pop();
		final int len = (int) (position() - mark);
		char[] buf = new char[len];
		System.arraycopy(this.data, (int)mark + 1, buf, 0, len);
		return new String(buf, 0, len);
	}
	
}