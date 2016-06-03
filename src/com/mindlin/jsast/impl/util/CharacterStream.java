package com.mindlin.jsast.impl.util;

public interface CharacterStream {
	char first();
	char current();
	char next();
	char next(long offset);
	char prev();
	char prev(long offset);
	char peekNext();
	char peek(long offset);
	CharacterStream skip(long offset);
	CharacterStream seek(long position);
	long position();
	CharacterStream position(long pos);
	boolean hasNext();
	boolean isEOF();
	boolean isEOL();
	boolean isWhitespace();
	CharacterStream skipNewline();
	CharacterStream skipWhitespace();
	CharacterStream mark();
	CharacterStream resetToMark();
	CharacterStream skipTo(final char c);
	String copyNext(long len);
	String copy(long start, long len);
	public static abstract class AbstractCharacterStream implements CharacterStream {
		protected final LongStack marks = new LongStack();
		@Override
		public String copyNext(long len) {
			StringBuilder sb = new StringBuilder((int) len);
			
			return sb.toString();
		}
		@Override
		public String copy(long start, long len) {
			String result = this.mark().position(start).copyNext(len);
			this.resetToMark();
			return result;
		}
		@Override
		public AbstractCharacterStream mark() {
			marks.push(position());
			return this;
		}
		@Override
		public AbstractCharacterStream resetToMark() {
			position(marks.pop());
			return this;
		}
	}
	public static class CharacterArrayStream extends AbstractCharacterStream {
		protected int position = 0;
		protected final char[] data;
		public CharacterArrayStream(char[] data) {
			this.data = data;
		}
		@Override
		public char next() {
			return data[position++];
		}
		@Override
		public char current() {
			return data[position - 1];
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
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public CharacterStream seek(long position) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long position() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public CharacterStream position(long pos) {
			// TODO Auto-generated method stub
			return null;
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
}