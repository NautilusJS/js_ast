package com.mindlin.jsast.transform;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class VariableNameTransformer implements Function<String, String>, Supplier<String> {
	char[] identifierStartChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$_".toCharArray();
	char[] identifierMiddleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$_0123456789".toCharArray();
	byte[] last = new byte[1];
	HashMap<String, String> map = new HashMap<>();
	public void putSpecial(String s) {
		map.put(s, s);
	}
	@Override
	public String apply(String var) {
		return map.computeIfAbsent(var, x->get());
	}
	@Override
	public String get() {
		last[last.length - 1]++;
		for (int i = last.length - 1; i > 0; i--)
			;
		if (last[0] > identifierStartChars.length)
			last = new byte[last.length + 1];
		
		//Build result
		char[] resultChars = new char[last.length];
		resultChars[0] = identifierStartChars[last[0]];
		for (int i = 1; i < resultChars.length; i++)
			resultChars[i] = identifierMiddleChars[last[i]];
		String result = new String(resultChars);
		if (map.values().contains(result))
			//return the next in the series
			return get();
		return result;
	}
	
	
}
