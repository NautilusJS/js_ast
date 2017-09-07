package com.mindlin.jsast.impl.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.mindlin.jsast.exception.JSTypeError;
import com.mindlin.jsast.impl.runtime.objects.JSObject;
import com.mindlin.jsast.impl.runtime.objects.Symbol;

public class JSRuntimeUtils {
	public static final Object UNDEFINED = new Object() {
		@Override
		public String toString() {
			return "undefined";
		}
	};
	
	protected static Map<Class<?>, Method> ifaceMethods = new ConcurrentHashMap<>();
	
	public static String typeof(Object value) {
		if (value == null)
			return "object";//Yes, this is correct
		else if (value == UNDEFINED)
			return "undefined";
		else if (value instanceof Boolean)
			return "boolean";
		else if (value instanceof Number)
			return "number";
		else if (value instanceof Symbol)
			return "symbol";
		else if (value instanceof String)
			return "string";
		//TODO support functions
		return "object";
	}

	public static boolean toBoolean(Object value) {
		if (value == null || value == UNDEFINED)
			return false;
		if (value instanceof Boolean)
			return (Boolean) value;
		
		if (value instanceof Number) {
			double dValue = ((Number)value).doubleValue();
			return !Double.isNaN(dValue) && (dValue != 0.0);
		}
		
		if (value instanceof String)
			return !((String)value).isEmpty();
		return true;
	}
	
	public static Number toNumber(Object value) {
		if (value == null)
			return 0;
		else if (value == UNDEFINED)
			return Double.NaN;
		else if (value instanceof Boolean)
			return ((Boolean) value) ? 1 : 0;
		else if (value instanceof Number)
			return (Number) value;
		//TODO handle a few more cases
		return Double.NaN;
	}
	
	public static String toString(Object value) {
		return "" + value;
	}
	/**
	 * Attempt to invoke <code>target</code>.
	 * 
	 * @param target
	 * @param thiz
	 * @param args
	 * @return
	 * @throws JSTypeError
	 *             if there's a problem calling the function (e.g., the function
	 *             cannot be called).
	 */
	@SuppressWarnings("unchecked")
	public static Object invoke(Object target, Object thiz, Object... args) throws JSTypeError {
		if (target instanceof JSObject)
			return ((JSObject) target).call(thiz, args);
		else if (target instanceof Function)
			return ((Function<Object[], Object>)target).apply(args);
		Method m;
		if (target instanceof Method) {
			m = (Method) target;
		} else {
			m = ifaceMethods.computeIfAbsent(target.getClass(), clazz -> {
				for (Class<?> iface : clazz.getInterfaces()) {
					if (!iface.isAnnotationPresent(FunctionalInterface.class))
						continue;
					for (Method method : iface.getDeclaredMethods())
						if (!method.isDefault())
							return method;
				}
				return null;
			});
		}
		if (m != null) {
			final int l = m.getParameterCount();
			// Resize parameter array, padding with undefined's and stuff
			Object[] arguments;
			Parameter[] p = m.getParameters();
			if (l > 0 && p[p.length - 1].isVarArgs()) {
				if (args.length == l) {
					arguments = args;
					arguments[l - 1] = new Object[] { arguments[l - 1] };
				} else if (args.length < l) {
					arguments = Arrays.copyOf(args, l);
					arguments[l - 1] = new Object[0];
				} else if (args.length > l) {
					arguments = Arrays.copyOf(args, l);
					arguments[l - 1] = Arrays.copyOfRange(args, l - 1, args.length);
				}
			} else if (args.length != l) {
				arguments = Arrays.copyOf(args, l);
			}
			// Invoke method with argument array
			try {
				return m.invoke(thiz, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new JSTypeError(e);
			}
		}
		// No idea how to invoke target.
		throw new JSTypeError(target + " is not a function");
	}

	public static Object invokeNew(Object constructor, Object...args) {
		throw new UnsupportedOperationException();
	}

	public static Object getSlot(Object target, int slot) {
		if (target instanceof JSObject)
			return ((JSObject) target).getSlot(slot);
		if (target.getClass().isArray())
			return ((Object[]) target)[slot];
		if (target instanceof List)
			return ((List<?>) target).get(slot);
		throw new JSTypeError("Cannot access slot " + slot + " on " + target);
	}
	
	public static Object getMember(Object target, String member) {
		if (target instanceof JSObject)
			return ((JSObject) target).getMember(member);
		if (target instanceof Map)
			return ((Map<?, ?>) target).get(member);
		//TODO finish
		return null;
	}
	
	public static Object getMember(Object target, Symbol member) {
		if (target instanceof JSObject)
			return ((JSObject) target).getMember(member);
		if (target instanceof Map)
			return ((Map<?, ?>) target).get(member);
		//TODO finish
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void setMember(Object target, String member, Object value) {
		if (target instanceof JSObject)
			((JSObject) target).setMember(member, value);
		else if (target instanceof Map)
			((Map<String, Object>) target).put(member, value);
		throw new JSTypeError("Cannot set member " + member + " on " + target);
	}

	@SuppressWarnings("unchecked")
	public static void setMember(Object target, Symbol member, Object value) {
		if (target instanceof JSObject)
			((JSObject) target).setMember(member, value);
		else if (target instanceof Map)
			((Map<Symbol, Object>) target).put(member, value);
		throw new JSTypeError("Cannot set member " + member + " on " + target);
	}
}
