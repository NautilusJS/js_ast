package com.mindlin.jsast.impl;

public class TestUtils {
	public static final void assertNumberEquals(Number expected, Number actual, String message) {
		if (expected.longValue() != actual.longValue() && Math.abs(expected.doubleValue() - actual.doubleValue()) > .0001)
			throw new AssertionError(message);
	}
	public static final void assertNumberEquals(Number expected, Number actual) {

		if (expected.longValue() != actual.longValue() && Math.abs(expected.doubleValue() - actual.doubleValue()) > .0001) {
			AssertionError e = new AssertionError("expected:<"+expected+"> but was:<"+actual+">");
			StackTraceElement[] trace = getStackTraceRelative(1);
			e.setStackTrace(trace);
			throw e;
		}
	}
	static final StackTraceElement[] getStackTraceRelative(int rel) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		final int offset = rel + 2;
		StackTraceElement[] result = new StackTraceElement[trace.length - offset];
		for (int i = 0, l = result.length; i < l; i++)
			result[i] = trace[i + offset];
		return result;
	}
}
