package com.mindlin.jsast.impl.util;

public abstract class AbstractCharacterStream implements CharacterStream {
	protected final LongStack marks = new LongStack();
	
	@Override
	public String copyNext(long len) {
		StringBuilder sb = new StringBuilder((int) len);
		while (len-- > 0)
			sb.append(next());
		return sb.toString();
	}
	
	@Override
	public String copy(long start, long len) {
		String result = this.mark().position(start - 1).copyNext(len);
		this.resetToMark();
		return result;
	}
	
	@Override
	public AbstractCharacterStream mark() {
		long pos = position();
		marks.push(pos);
		return this;
	}
	
	@Override
	public AbstractCharacterStream resetToMark() {
		long pos = marks.pop();
		position(pos);
		return this;
	}
	
	@Override
	public AbstractCharacterStream unmark() {
		marks.pop();
		return this;
	}
}