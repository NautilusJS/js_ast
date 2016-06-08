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
	public boolean isEOF() {
		return position >= data.length;
	}
	@Override
	public char first() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public char next(long offset) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public char prev(long offset) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isEOL() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isWhitespace() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public CharacterStream skipNewline() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CharacterStream skipWhitespace() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public char peekNext() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public char peek(long offset) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public CharacterStream skipTo(char c) {
		// TODO Auto-generated method stub
		return null;
	}
}