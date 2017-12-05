package com.mindlin.jsast.impl.runtime;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.mindlin.jsast.impl.parser.JSDialect;
import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.tree.CompilationUnitTree;

public class JSScriptEngine implements ScriptEngine, Compilable {
	NestedBindings engineBindings = new NestedBindings(JSScriptEngineFactory.getInstance().globalBindings, ScriptContext.ENGINE_SCOPE);
	protected final JSParser parser;
	
	public JSScriptEngine() {
		this(JSDialect.JSStandardDialect.EVERYTHING);
	}
	
	public JSScriptEngine(JSDialect dialect) {
		this.parser = new JSParser(dialect);
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		CompilationUnitTree ast = parser.apply("asf", script);
		RuntimeScope scope = new RuntimeScope();
		scope.bindings = this.engineBindings;
		scope.context = context;
		
		
		Object result = ast.accept(new JSScriptInvoker(), scope);
		
		return JSRuntimeUtils.dereference(result);
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script) throws ScriptException {
		return this.eval(script, (ScriptContext) null);
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
		this.engineBindings.put(key, value);
	}

	@Override
	public Object get(String key) {
		return this.engineBindings.get(key);
	}

	@Override
	public Bindings getBindings(int scope) {
		if (scope == ScriptContext.GLOBAL_SCOPE)
			return JSScriptEngineFactory.getInstance().globalBindings;
		if (scope == ScriptContext.ENGINE_SCOPE)
			return this.engineBindings;
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
