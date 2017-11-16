package com.mindlin.jsast.impl.runtime.objects;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface PropertyDescriptor {
	/**
	 * If this property is writable
	 * @return if writable
	 */
	boolean isWritable();
	/**
	 * If this property is enumerable
	 * @return if enumerable
	 */
	boolean isEnumarable();
	/**
	 * If this property is configurable
	 * @return if configurable
	 */
	boolean isConfigurable();
	/**
	 * Setter method for property
	 * @return setter
	 */
	Consumer<Object> setter();
	/**
	 * Getter method for property
	 * @return getter
	 */
	default Supplier<Object> getter() {
		return ()->value().get();
	}
	/**
	 * 
	 * @return
	 */
	Optional<Object> value();
}
