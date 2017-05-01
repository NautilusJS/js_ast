package com.mindlin.jsast.transform;

public class ASTTransformerContext {
	ContextData data = new ContextData();
	
	protected static class ContextData {
		final ContextData parent;

		ContextData() {
			this.parent = null;
		}
		
		ContextData(ContextData parent) {
			this.parent = parent;
		}
	}
}
