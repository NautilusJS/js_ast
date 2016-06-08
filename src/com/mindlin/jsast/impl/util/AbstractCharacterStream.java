package com.mindlin.jsast.impl.util;

public abstract class AbstractCharacterStream implements CharacterStream {
	protected final LongStack marks = new LongStack();
	@Override
	public String copyNext(long len) {
		StringBuilder sb = new StringBuilder((int) len);
		
		return sb.toString();
	}
	@Override
	public String copy(long start, long len) {
		String result = this.mark()
				.position(start)
				.copyNext(len);
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