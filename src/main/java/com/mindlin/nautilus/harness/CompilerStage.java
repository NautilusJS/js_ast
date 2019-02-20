package com.mindlin.jsast.harness;

public abstract class CompilerStage<S, R> {
	public abstract String getName();
	
	public void setProgress(float progress) {
		
	}
	
	public abstract R process(S source) throws Exception;
}
