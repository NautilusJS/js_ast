package com.mindlin.jsast.impl.util;

public interface CharacterStream {
	char current();

	char next();

	default char next(long offset) {
		skip(offset);
		return current();
	}

	default char peek() {
		return peek(1);
	}

	char peek(long offset);

	CharacterStream skip(long offset);

	long position();

	CharacterStream position(long pos);

	/**
	 * Whether a call to {@link #next()} is valid (i.e., this CharacterStream
	 * has at least 1 more character).
	 * 
	 * @return Whether there is a next character
	 */
	boolean hasNext();

	/**
	 * Whether this CharacterStream has at least {@code num} more
	 * characters.
	 * 
	 * @param num
	 *            Number of characters to test for
	 * @return Whether there are that many characters remaining
	 */
	boolean hasNext(long num);
	
	boolean isEOL();

	default boolean isWhitespace() {
		return position() >= 0 && hasNext() && Characters.isJsWhitespace(current());
	}

	default CharacterStream skipWhitespace() {
		return skipWhitespace(true);
	}
	
	CharacterStream skipWhitespace(boolean passNewlines);
	
	CharacterStream mark();

	CharacterStream resetToMark();
	
	CharacterStream unmark();

	default CharacterStream skipTo(final char c) {
		while (next() != c)
			;
		return this;
	}

	String copyNext(long len);

	String copy(long start, long len);
	
	String copyFromMark();
}