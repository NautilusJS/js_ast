package com.mindlin.jsast.harness;

import java.util.stream.Stream;

public interface CompilerPass<S, R> {
	String getName();
	
	public abstract Stream<R> process(Stream<S> sources) throws Exception;
}
