package com.mindlin.jsast.transform;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class VariableNameTransformer implements Function<String, String>, Supplier<String> {
	static final char[] identifierStartChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$_".toCharArray();
	static final char[] identifierMiddleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$_0123456789".toCharArray();
	protected byte[] last = new byte[1];
	protected final HashMap<String, String> map = new HashMap<>();
	
	/**
	 * Register an unmapable variable (rather, register it as an identity mapping).
	 */
	public void putSpecial(String s) {
		map.put(s, s);
	}
	
	/**
	 * Map a variable to a (usually) shorter name, generating one if not already done
	 */
	@Override
	public String apply(String var) {
		return map.computeIfAbsent(var, x->get());
	}
	
	/**
	 * Generate the next value in this series
	 */
	@Override
	public String get() {
		String result;
		synchronized (last) {
			do {
				//Increment last character
				last[last.length - 1]++;
				//Cary overflows
				for (int i = last.length - 1; i > 0; i--)
					if (last[i] > identifierMiddleChars.length) {
						last[i] = 0;
						last[i - 1]++;
					}
				//Overflow, so make the identifier one longer
				//Arrays are initialized as 0, so this is easy
				if (last[0] > identifierStartChars.length)
					last = new byte[last.length + 1];

				//Build result
				char[] resultChars = new char[last.length];
				resultChars[0] = identifierStartChars[last[0]];
				for (int i = 1; i < resultChars.length; i++)
					resultChars[i] = identifierMiddleChars[last[i]];
				result = new String(resultChars);
			} while (map.values().contains(result));//Check that this isn't mapped to anything already
		}
		return result;
	}
}
