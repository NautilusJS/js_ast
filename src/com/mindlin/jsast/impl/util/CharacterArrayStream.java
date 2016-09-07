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
		return Characters.isJsWhitespace(current());
	}
	@Override
	public CharacterStream skipNewline() {
		char c;
		while ((c = current()) == '\r' || c == '\n')
			skip(1);
		return this;
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
	@Override
	public CharacterStream skipComments() {
		while (true) {
			skipWhitespace();
			if (peek(1) != '/')
				break;
			if (peek(2) == '/') {
				skip(2);
				while (hasNext() && peek(1) != '\r' && peek(1) != '\n')
					skip(1);
				if (hasNext())
					skipNewline();
			} else
				break;
		}
		return this;
	}
}