package com.mindlin.jsast.impl.runtime;

import javax.script.ScriptContext;

public class RuntimeScope {
	ScriptContext context;
	NestedBindings bindings;
	public RuntimeScope pushBlock() {
		return this;
	}
	public RuntimeScope pushFunction() {
		return this;
	}
}
