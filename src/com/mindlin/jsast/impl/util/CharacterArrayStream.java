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
	public char next() {
		return data[++position];
	}
	@Override
	public char current() {
		return data[position];
	}
	@Override
	public char prev() {
		return data[--position];
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
		this.position = (int)pos;
		return this;
	}
	@Override
	public boolean hasNext() {
		return position < data.length - 1;
	}
	@Override
	public boolean isEOL() {
		final char c = current();
		return c == '\r' || c == '\n';
	}
	@Override
	public boolean isWhitespace() {
		throw new UnsupportedOperationException();
	}
	@Override
	public CharacterStream skipNewline() {
		throw new UnsupportedOperationException();
	}
	@Override
	public CharacterStream skipWhitespace() {
		while(hasNext() && Characters.isJsWhitespace(data[1 + position]))
			position++;
		return this;
	}
	@Override
	public char peek(long offset) {
		return data[(int) (position + offset)];
	}
}