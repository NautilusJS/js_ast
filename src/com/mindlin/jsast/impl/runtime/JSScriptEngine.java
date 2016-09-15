package com.mindlin.jsast.impl.runtime;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class JSScriptEngine implements ScriptEngine, Compilable {
	Bindings globalBindings;

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(Reader reader) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, Bindings n) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(Reader reader, Bindings n) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bindings getBindings(int scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBindings(Bindings bindings, int scope) {
		// TODO Auto-generated method stub

	}

	@Override
	public Bindings createBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScriptContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(ScriptContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSScriptEngineFactory getFactory() {
		return JSScriptEngineFactory.getInstance();
	}

	@Override
	public CompiledScript compile(String script) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompiledScript compile(Reader script) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}
}
