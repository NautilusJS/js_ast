package com.mindlin.jsast.impl.analysis;

/**
 * Just generates a ID.
 * 
 * @author mailmindlin
 *
 */
public class UIDGenerator {
	private long current = 0;
	
	public long next() {
		return ++this.current;
	}
}
