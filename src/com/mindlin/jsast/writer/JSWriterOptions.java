package com.mindlin.jsast.writer;

public class JSWriterOptions {
	private boolean minify = true;
	
	public static class Builder {
		public boolean minify = true;
		
		public JSWriterOptions build() {
			JSWriterOptions result = new JSWriterOptions();
			result.minify = this.minify;
			return result;
		}
	}
}
