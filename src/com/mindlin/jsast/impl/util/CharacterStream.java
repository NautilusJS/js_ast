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
}