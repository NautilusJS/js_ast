package com.mindlin.jsast.impl.lexer;

public interface JSLiteral {
	JSLiteralKind getKind();
	
	public static class JSNullLiteral implements JSLiteral {
		@Override
		public JSLiteralKind getKind() {
			return JSLiteralKind.NULL;
		}
	}
	
	public static enum JSBooleanLiteral implements JSLiteral {
		TRUE, FALSE;
		protected final boolean value;
		
		JSBooleanLiteral() {
			value = this.name().equals("TRUE");
		}
		
		public boolean getValue() {
			return this.value;
		}
		
		@Override
		public JSLiteralKind getKind() {
			return JSLiteralKind.BOOLEAN;
		}
	}
	
	public static class JSNumberLiteral implements JSLiteral {
		protected final Number value;
		
		public JSNumberLiteral(Number value) {
			this.value = value;
		}
		
		public Number getValue() {
			return this.value;
		}
		
		@Override
		public JSLiteralKind getKind() {
			return JSLiteralKind.NUMBER;
		}
	}
	
	public static class JSStringLiteral implements JSLiteral {
		protected final String value;
		
		public JSStringLiteral(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public JSLiteralKind getKind() {
			return JSLiteralKind.STRING;
		}
	}
	
	public static enum JSLiteralKind {
		NULL, BOOLEAN, NUMBER, STRING
	}
}
