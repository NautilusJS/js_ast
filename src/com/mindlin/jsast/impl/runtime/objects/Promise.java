package com.mindlin.jsast.impl.runtime.objects;

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
		throw new UnsupportedOperationException();
	}
	@JSExtern
	public static Promise resolve(Object reason) {
		throw new UnsupportedOperationException();
	}
	protected enum PromiseState {
		PENDING,
		FUFILLED,
		REJECTED;
	}
	protected JSFunction executor;
	@JSExtern
	public Promise(JSFunction executor) {
		this.executor = executor;
		executor.invoke((JSFunction)this::doResolve, (JSFunction)this::doReject);
	}
	
	@JSExtern(name="catch")
	public Promise _catch(JSFunction onRejected) {
		
	}
	
	public void doResolve(Object...value) {
		
	}
	
	public void doReject(Object...value) {
		
	}
	
	@JSExtern
	public Promise then(JSFunction onFufilled, @JSParam(optional=true) JSFunction onRejected) {
		
	}
}