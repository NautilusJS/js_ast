package com.mindlin.jsast.impl.runtime;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CompilerOptions {
	
	public static class Builder {
		public boolean preserveStackTrace;
		public boolean rewriteExcpeptionTraces;
		public boolean freezeClasses;
		
		public Builder() {
			
		}
		
		public CompilerOptions build() {
			return new CompilerOptions(this.preserveStackTrace, this.rewriteExcpeptionTraces, this.freezeClasses);
		}
	}
	
	public final boolean preserveStackTrace;
	public final boolean rewriteExceptionTraces;
	public final boolean freezeClasses;
	
	public CompilerOptions(boolean preserveStackTrace, boolean rewriteExceptionTraces, boolean freezeClasses) {
		this.preserveStackTrace = preserveStackTrace;
		this.rewriteExceptionTraces = rewriteExceptionTraces;
		this.freezeClasses = freezeClasses;
	}
	
}