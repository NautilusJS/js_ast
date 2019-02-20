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
import com.mindlin.jsast.impl.runtime.objects.JSConstants;
import com.mindlin.jsast.impl.runtime.objects.JSObject;
import com.mindlin.jsast.impl.runtime.objects.Symbol;

public class JSRuntimeUtils {
	public static final Object UNDEFINED = JSConstants.UNDEFINED;
	
	protected static Map<Class<?>, Method> ifaceMethods = new ConcurrentHashMap<>();
	
	public static Object dereference(Object value) {
		if (value != null && value instanceof Reference)
			return ((Reference)value).get();
		return value;
	}
	
	public static String typeof(Object value) {
		value = dereference(value);
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
	
	public static Object add(Object a, Object b) {
		a = dereference(a);
		b = dereference(b);
		String typeA = typeof(a), typeB = typeof(b);
		if ("string".equals(typeA) || "string".equals(typeB) || "object".equals(typeB))
			return toString(a) + toString(b);
		return toNumber(a).doubleValue() + toNumber(b).doubleValue();
	}

	public static boolean toBoolean(Object value) {
		value = dereference(value);
		
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
		value = dereference(value);
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
		value = dereference(value);
		if (value != null && value instanceof Symbol)
			throw new IllegalArgumentException("Can't convert symbol into string");
		return "" + value;
	}
	/**
	 * Attempt to invoke {@code target}.
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
		target = dereference(target);
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
		target = dereference(target);
		if (target instanceof JSObject)
			return ((JSObject) target).getSlot(slot);
		if (target.getClass().isArray())
			return ((Object[]) target)[slot];
		if (target instanceof List)
			return ((List<?>) target).get(slot);
		throw new JSTypeError("Cannot access slot " + slot + " on " + target);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getMember(Object target, String member) {
		target = dereference(target);
		if (target instanceof JSObject)
			return ((JSObject) target).getMember(member);
		if (target instanceof Map)
			return ((Map<String, Object>) target).getOrDefault(member, UNDEFINED);
		//TODO finish
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Object getMember(Object target, Symbol member) {
		target = dereference(target);
		if (target instanceof JSObject)
			return ((JSObject) target).getMember(member);
		if (target instanceof Map)
			return ((Map<Symbol, Object>) target).getOrDefault(member, UNDEFINED);
		//TODO finish
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static void setSlot(Object target, int slot, Object value) {
		target = dereference(target);
		if (target instanceof List) {
			if (slot >= ((List<?>)target).size())
				((List<Object>) target).add(slot, value);
			else
				((List<Object>) target).set(slot, value);
			return;
		} else {
			//Probably bad
			((Object[]) target)[slot] = value;
		}
		throw new JSTypeError("Cannot set slot " + slot + " on " + target);
	}
	
	@SuppressWarnings("unchecked")
	public static void setMember(Object target, String member, Object value) {
		target = dereference(target);
		value = dereference(value);
		if (target == null)
			throw new NullPointerException();
		if (target instanceof JSObject) {
			((JSObject) target).setMember(member, value);
			return;
		}
		if (target instanceof Map) {
			((Map<String, Object>) target).put(member, value);
			return;
		}
		if (target instanceof List || target.getClass().isArray()) {
			try {
				int slot = Integer.parseInt(member);
				System.out.println(slot);
				setSlot(target, slot, value);
				return;
			} catch (NumberFormatException e) {
				//We couldn't convert it to an int
			}
		}
		throw new JSTypeError("Cannot set member " + member + " on " + target);
	}

	@SuppressWarnings("unchecked")
	public static void setMember(Object target, Symbol member, Object value) {
		target = dereference(target);
		value = dereference(value);
		if (target instanceof JSObject)
			((JSObject) target).setMember(member, value);
		else if (target instanceof Map)
			((Map<Symbol, Object>) target).put(member, value);
		else
			throw new JSTypeError("Cannot set member " + member + " on " + target);
	}
}
