package com.mindlin.jsast.json.api;

public class JSONParseException extends RuntimeException {
	private static final long serialVersionUID = -3159024489087636084L;

	public JSONParseException(Exception e) {
		super(e);
	}

	public JSONParseException(String msg) {
		super(msg);
	}
}
