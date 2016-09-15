package com.mindlin.jsast.impl.runtime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class JSScriptEngineFactory implements ScriptEngineFactory {
	public static final String version = "0.0.1-alpha";
	protected static final List<String> extensions = Collections.unmodifiableList(Arrays.asList("js", "ts"));
	//@formatter:off
	protected static final List<String> mimeTypes = Collections.unmodifiableList(Arrays.asList(
			"application/javascript",
			"application/ecmascript",
			"application/typescript",
			"text/javascript",
			"text/ecmascript"));
	//@formatter:on
	//@formatter:off
	protected static final List<String> names = Collections.unmodifiableList(Arrays.asList(
			"nautilus",
			"Nautilus",
			"com.mindlin.nautilus",
			"js",
			"JS",
			"JavaScript",
			"javascript",
			"ECMAScript",
			"ecmascript",
			"TypeScript",
			"typescript"));
	//@formatter:on
	protected static final JSScriptEngineFactory INSTANCE = new JSScriptEngineFactory();

	public static JSScriptEngineFactory getInstance() {
		return INSTANCE;
	}
	
	protected final NestedBindings globalBindings = new NestedBindings(ScriptContext.GLOBAL_SCOPE);

	private JSScriptEngineFactory() {

	}

	@Override
	public String getEngineName() {
		return "mindlin.nautilus";
	}

	@Override
	public String getEngineVersion() {
		return version;
	}

	@Override
	public List<String> getExtensions() {
		return extensions;
	}

	@Override
	public List<String> getMimeTypes() {
		return mimeTypes;
	}

	@Override
	public List<String> getNames() {
		return names;
	}

	@Override
	public String getLanguageName() {
		return "TypeScript";
	}

	@Override
	public String getLanguageVersion() {
		return "TypeScript 1.8";
	}

	@Override
	public Object getParameter(String key) {
		switch (key) {
			case ScriptEngine.ENGINE:
				return getEngineName();
			case ScriptEngine.ENGINE_VERSION:
				return getEngineVersion();
			case ScriptEngine.LANGUAGE:
				return getLanguageName();
			case ScriptEngine.LANGUAGE_VERSION:
				return getLanguageVersion();
			case ScriptEngine.NAME:
				return "typescript";
		}
		return null;
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		StringBuilder sb = new StringBuilder();
		sb.append(obj).append('.').append(m).append('(');
		for (String arg : args)
			sb.append(arg).append(',');
		sb.setLength(sb.length() - 1);
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return "console.log(" + toDisplay + ")";
	}

	@Override
	public String getProgram(String... statements) {
		StringBuilder sb = new StringBuilder();
		for (String statement : statements)
			sb.append(statement).append(";\n");
		return sb.toString();
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return new JSScriptEngine();
	}

}
