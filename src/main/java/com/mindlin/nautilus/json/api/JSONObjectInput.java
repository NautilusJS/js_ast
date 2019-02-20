package com.mindlin.jsast.json.api;

import java.util.Iterator;
import java.util.Map;

public interface JSONObjectInput extends SafelyCloseable, Iterator<Map.Entry<String, ? extends Object>> {
	boolean readAll();
}
