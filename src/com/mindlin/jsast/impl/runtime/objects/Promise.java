package com.mindlin.jsast.impl.runtime.objects;

import com.mindlin.jsast.impl.runtime.annotations.JSExtern;
import com.mindlin.jsast.impl.runtime.annotations.JSParam;

public class Promise {
	@JSExtern
	public static Promise all(Object...iterable) {
		for (Object o : iterable) {
			
		}
		throw new UnsupportedOperationException();
	}
	@JSExtern
	public static Promise race(Object...iterable) {
		throw new UnsupportedOperationException();
	}
	@JSExtern
	public static Promise reject(Object reason) {
		Promise result = new Promise();
		result.doReject(reason);
		return result;
	}
	@JSExtern
	public static Promise resolve(Object reason) {
		Promise result = new Promise();
		result.doResolve(reason);
		return result;
	}
	protected enum PromiseState {
		PENDING,
		FUFILLED,
		REJECTED;
	}
	protected JSFunction executor;
	protected Promise() {
		
	}
	@JSExtern
	public Promise(JSFunction executor) {
		this.executor = executor;
		executor.invoke((JSFunction)this::doResolve, (JSFunction)this::doReject);
	}
	
	@JSExtern(name="catch")
	public Promise _catch(JSFunction onRejected) {
		throw new UnsupportedOperationException();
	}
	
	public Object doResolve(Object...value) {
		throw new UnsupportedOperationException();
	}
	
	public Object doReject(Object...value) {
		throw new UnsupportedOperationException();
	}
	
	@JSExtern
	public Promise then(JSFunction onFufilled, @JSParam(optional=true) JSFunction onRejected) {
		throw new UnsupportedOperationException();
	}
}