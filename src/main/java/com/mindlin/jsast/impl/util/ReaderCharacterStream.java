package com.mindlin.jsast.impl.util;

import java.io.Reader;
import java.nio.CharBuffer;

public class ReaderCharacterStream extends AbstractCharacterStream {
	protected final Reader in;
	protected long readerNextPosition = -1;
	protected long readerMarkPosition = -1;
	protected long readerMarkLength = -1;
	
	//Position that is used for this stream
	protected long position = -1;
	
	//In case our reader can't support buffers
	protected long bufferStart = -1;
	protected CharBuffer buffer;
	
	protected char current;
	
	public ReaderCharacterStream(Reader in) {
		if (in == null)
			throw new NullPointerException("Source reader must not be null");
		this.in = in;
	}
	
	@Override
	public char current() {
		return current;
	}
	
	@Override
	public char next() {
		//In buffer
		if (bufferStart >= 0) {
			
		}
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public char peek(long offset) {
		if (offset == 0)
			return current();
		
		if (in.markSupported()) {
			
		} else if (bufferStart > -1) {
			
		} else {
		}
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public CharacterStream skip(long offset) {
		long endPosition = this.position + offset;
		//We have a mark on our reader
		if (this.readerMarkPosition > -1) {
			
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long position() {
		return this.position;
	}
	
	@Override
	public CharacterStream position(long pos) {
		return skip(pos - this.position);
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean hasNext(long num) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isEOL() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public CharacterStream skipWhitespace() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CharacterStream skipWhitespace(boolean passNewlines) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String copyFromMark() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
