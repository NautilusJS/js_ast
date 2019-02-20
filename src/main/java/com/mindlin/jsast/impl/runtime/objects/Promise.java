package com.mindlin.jsast.impl.runtime.objects;

import java.util.List;

import com.mindlin.jsast.impl.runtime.annotations.JSProperty;
import com.mindlin.jsast.impl.runtime.annotations.JSParam;

public class Promise {
	@JSProperty
	public static Promise all(Promise...promises) {
		Promise result = new Promise();
		for (Promise promise : promises) {
			promise._catch(result::doReject);
		}
		throw new UnsupportedOperationException();
	}
	@JSProperty
	public static Promise race(Object...iterable) {
		throw new UnsupportedOperationException();
	}
	@JSProperty
	public static Promise reject(Object reason) {
		Promise result = new Promise();
		result.doReject(reason);
		return result;
	}
	@JSProperty
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
	protected Object[] result;
	protected PromiseState state = PromiseState.PENDING;
	protected List<JSFunction> resolutionHandlers = null;
	protected List<JSFunction> errorHandlers = null;
	protected Promise() {
		
	}
	@JSProperty
	public Promise(JSFunction executor) {
		this.executor = executor;
		executor.invoke((JSFunction)this::doResolve, (JSFunction)this::doReject);
	}
	
	@JSProperty(name="catch")
	public Promise _catch(JSFunction onRejected) {
		throw new UnsupportedOperationException();
	}
	
	public Object doResolve(Object...value) {
		if (this.state != PromiseState.PENDING)
			return null;
		this.state = PromiseState.FUFILLED;
		this.result = value;
		this.errorHandlers = null;
		for (JSFunction handler : this.resolutionHandlers)
			handler.invoke(this.result);
		this.resolutionHandlers = null;
		return null;
	}
	
	public Object doReject(Object...value) {
		if (this.state != PromiseState.PENDING)
			return null;
		this.state = PromiseState.REJECTED;
		this.result = value;
		this.resolutionHandlers = null;
		for (JSFunction handler : this.errorHandlers)
			handler.invoke(this.result);
		this.errorHandlers = null;
		return null;
	}
	
	@JSProperty
	public Promise then(JSFunction onFufilled, @JSParam(optional=true) JSFunction onRejected) {
		if (this.state == PromiseState.FUFILLED) {
			onFufilled.invoke(this.result);
		} else if (this.state == PromiseState.REJECTED) {
			onRejected.invoke(this.result);
		} else {
			//TODO finish
		}
		throw new UnsupportedOperationException();
	}
}