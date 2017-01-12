export interface EventHandler {
	(event:Event): boolean;
}
/**
 * Implementation of EventTarget
 */
export class Emitter implements EventTarget {
	handlers: {[index:string]:EventHandler[]};
	constructor() {
		this.handlers = {};
	}
	addEventListener(event:string, callback:EventHandler) : void {
		if (!(event in this.handlers))
			this.handlers[event] = [];
		this.handlers[event].push(callback);
	}
	removeEventListener(event:string, callback:EventHandler) : boolean {
		if (!(event in this.handlers))
			return false;
		var toRemove : string[];
		let handlers = this.handlers[event];
		for (var i in handlers)
			if (handlers[i] == callback)
				toRemove.push(i);
		for (var i of toRemove)
			delete handlers[i];
		return toRemove.length > 0;
	}
	dispatchEvent(event:Event) : boolean {
		Object.defineProperty(event, 'target', {value: this});
		var handlers : EventHandler[] = this.handlers[event.type];
		if (!handlers)
			return event.defaultPrevented;
		for (var handler of handlers) {
			handler.apply(event);
			if (event.defaultPrevented)
				return false;
		}
		return true;
	}
}