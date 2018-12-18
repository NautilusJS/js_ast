package com.mindlin.jsast.harness;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mindlin.jsast.impl.parser.JSDialect;
import com.mindlin.jsast.impl.parser.JSDialect.JSStandardDialect;

public class CompilerOptions {
	public static final CompilerOption<String> PRINT_HELP = new CompilerOption<>("print_help");
	public static final CompilerOption<JSDialect> SOURCE_LANGUAGE = new CompilerOption<>("source_language", JSStandardDialect.EVERYTHING);
	public static final CompilerOption<String> TARGET_LANGUAGE = new CompilerOption<>("target_language");
	public static final CompilerOption<Charset> ENCODING = new CompilerOption<>("source_encoding", Charset.defaultCharset());
	
	@SuppressWarnings("rawtypes")
	protected Map<CompilerOption, Object> options = new HashMap<>();
	
	public <T> void set(CompilerOption<T> option, T value) {
		options.put(option, value);
	}
	
	public <T> Optional<T> get(CompilerOption<? extends T> option) {
		@SuppressWarnings("unchecked")
		T result = (T) options.get(option);
		if (result == null && !options.containsKey(option))
			return Optional.ofNullable(option.defaultValue());
		return Optional.of(result);
	}
	
	public <T> T get(CompilerOption<? extends T> option, T defaultValue) {
		@SuppressWarnings("unchecked")
		T result = (T) options.get(option);
		if (result == null && !options.containsKey(option))
			result = defaultValue;
		return result;
	}
	
	public <T> void clear(CompilerOption<T> option) {
		options.remove(option);
	}
	
	public JSDialect parseDialect(String name) {
		switch (name.toUpperCase()) {
			case "ES6":
				return JSStandardDialect.ES6;
			case "ES7":
			case "ES2017":
				return JSStandardDialect.ES2017;
			default:
				return JSStandardDialect.EVERYTHING;
		}
	}
	
	public void parseCLIArguments(String...args) {
		//TODO: finish
	}
}
