package com.mindlin.jsast.impl.runtime.objects;

public interface JSObject {
	Object call(Object thiz, Object... args);

	Object callNew(Object... args);

	Object getMember(String key);

	Object getMember(Symbol key);

	default Object getSlot(int index) {
		return getMember(Integer.toString(index));
	}

	void setMember(String key, Object value);

	void setMember(Symbol key, Object value);

	default void setSlot(int index, Object value) {
		setMember(Integer.toString(index), value);
	}

	void removeMember(String key);

	void removeMember(Symbol key);

	default void removeSlot(int index) {
		removeMember(Integer.toString(index));
	}

	boolean hasMember(String key);

	boolean hasMember(Symbol key);

	default boolean hasSlot(int index) {
		return hasMember(Integer.toString(index));
	}

	boolean isInstanceOf(Object other);

	default String getType() {
		return "object";
	}

	void freeze(boolean frozen);

	void seal(boolean sealed);

	boolean isFrozen();

	boolean isSealed();
}
