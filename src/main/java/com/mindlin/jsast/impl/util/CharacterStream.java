package com.mindlin.jsast.impl.util;

public interface CharacterStream {
	/**
	 * Get current character (the character at the current {@link #position()}
	 * in the stream).
	 * 
	 * @return current character
	 */
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

	/**
	 * Get current position in the stream.
	 * @return Current position, or -1 if the stream has not yet been read from.
	 */
	long position();

	CharacterStream position(long pos);

	/**
	 * Whether a call to {@link #next()} is valid (i.e., this CharacterStream
	 * has at least 1 more character).
	 * <br/>
	 * This method should be equivalent to {@link #hasNext(long) hasNext(1)}.
	 * 
	 * @return Whether there is a next character
	 */
	default boolean hasNext() {
		return hasNext(1);
	}

	/**
	 * Whether this CharacterStream has at least {@code num} more
	 * characters.
	 * 
	 * @param num
	 *            Number of characters to test for
	 * @return Whether there are that many characters remaining
	 */
	boolean hasNext(long num);
	
	//TODO: remove?
	default boolean isEOL() {
		if (!hasNext() || position() < 0)
			return false;
		
		final char c = current();
		return c == '\r' || c == '\n';
	}

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