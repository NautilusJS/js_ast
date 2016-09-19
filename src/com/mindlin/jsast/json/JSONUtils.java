package com.mindlin.jsast.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import com.mindlin.jsast.json.api.JSONArrayOutput;
import com.mindlin.jsast.json.api.JSONExternalizable;
import com.mindlin.jsast.json.api.JSONIfiable;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.jsast.json.api.JSONObjectOutput;
import com.mindlin.jsast.json.api.JSONOutput;
import com.mindlin.jsast.json.api.JSONParseException;
import com.mindlin.jsast.json.api.JSONSerializationException;
import com.mindlin.jsast.json.api.SafelyCloseable;

public class JSONUtils {
	static String escapeKeyIfNeeded(char[] chars, boolean force) {
		return '"' + escape(chars) + '"';
	}

	/**
	 * Escape string for JSON.
	 * @param chars
	 * @return
	 */
	static String escape(char[] chars) {
		final int l = chars.length;
		StringBuilder sb = new StringBuilder(l + (l >>> 3));
		for (int i = 0; i < l; i++) {
			char c = chars[i];
			switch (c) {
				case '\r':
					c = 'r';
					break;
				case '\n':
					c = 'n';
					break;
				case '\b':
					c = 'b';
					break;
				case '\f':
					c = 'f';
					break;
				case '\t':
					c = 't';
					break;
				case '\\':
				case '/':
				case '"':
					break;
				default:
					sb.append(c);
					continue;
			}
			sb.append(new char[] { '\\', c });
		}
		return sb.toString();
	}

	static String unescapedString(Reader in, char start) throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean escaped = false;
		while (true) {
			char c = (char) in.read();
			if (escaped) {
				escaped = false;
				switch (c) {
					case '\\':
					case '\'':
					case '/':
					case '"':
						break;
					case 'n':
						c = '\n';
						break;
					case 'b':
						c = '\b';
						break;
					case 'f':
						c = '\f';
						break;
					case 't':
						c = '\t';
						break;
					case 'u':
						throw new UnsupportedOperationException("Unicode escape sequences not yet supported");
					default:
						throw new JSONParseException("Unknown escape sequence: \\" + c);
				}
			} else if (c == '\\') {
				escaped = true;
				continue;
			} else if (c == start) {
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * Serialize an object to JSON.
	 * @param o Object to serialize
	 * @return JSON string
	 */
	public static String serialize(Object o) {
		StringWriter sw = new StringWriter();
		JSONOutput out = new JSONOutputStream(sw);
		serialize(o, out);
		return sw.toString();
	}

	public static <T> T deserialize(Class<? extends T> clazz, JSONInput in) throws ClassNotFoundException {
		if (JSONExternalizable.class.isAssignableFrom(clazz)) {
			try {
				T result = clazz.newInstance();
				try (SafelyCloseable mark = in.mark()) {
					((JSONExternalizable) result).readJSON(in);
				}
				return result;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new JSONParseException(e);
			}
		}
		return null;
	}

	/**
	 * Utility method to serialize an object, choosing the optimal strategy
	 * @param o Object to serialize
	 * @param out JSONOutput to write to
	 */
	public static void serialize(Object o, JSONOutput out) {
		if (o == null) {
			out.writeUnescaped("null");
			return;
		}
		Class<?> c = o.getClass();
		if (o instanceof JSONExternalizable) {
			try (SafelyCloseable mark = out.mark()) {
				((JSONExternalizable) o).writeJSON(out);
			}
		} else if (o instanceof JSONIfiable) {
			out.writeUnescaped(((JSONIfiable) o).toJSON());
		} else if (c.equals(String.class) || c.equals(Character.class)) {
			out.writeUnescaped('"' + escape(o.toString().toCharArray()) + '"');
		} else if (c.equals(Boolean.class) || c.equals(Short.class) || c.equals(Integer.class) || c.equals(Float.class)
				|| c.equals(Long.class) || c.equals(Double.class)) {
			out.writeUnescaped(o.toString());
		} else if (o instanceof Map) {
			try (JSONObjectOutput obj = out.makeObject()) {
				@SuppressWarnings("unchecked")
				Map<String, ? extends Object> m = (Map<String, ? extends Object>) o;
				obj.write(m);
			}
		} else if (o instanceof Collection) {
			try (JSONArrayOutput arr = out.makeArray()) {
				Collection<?> cl = (Collection<?>) o;
				arr.write(cl);
			}
		} else if (c.isArray()) {
			try (JSONArrayOutput arr = out.makeArray()) {
				Object[] objects = (Object[]) o;
				for (Object object : objects)
					arr.writeObject(object);
			}
		} else {
			try (JSONObjectOutput obj = out.makeObject()) {
				// TODO improve
				for (Field f : c.getDeclaredFields()) {
					if (!f.isAccessible())
						f.setAccessible(true);
					if (!f.isAccessible())
						continue;
					try {
						obj.writeObject(f.getName(), f.get(o));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new JSONSerializationException(e);
					}
				}
			}
		}
	}

	private JSONUtils() {

	}
}
