package com.mindlin.jsast.impl.runtime.objects;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface PropertyDescriptor {
	boolean isWritable();
	boolean isEnumarable();
	boolean isConfigurable();
	Consumer<Object> setter();
	default Supplier<Object> getter() {
		return ()->value().get();
	}
	Optional<Object> value();
}
