import "types.ts";

interface EventListenerOptions {
	capture: boolean;
}

interface AddEventListenerOptions extends EventListenerOptions {
	passive: boolean;
	once: boolean;
}

interface EventListener {
	(event : Event) : boolean | void;
}

interface EventTarget {
	addEventListener(type : string, callback? : EventListener, options? : AddEventListenerOptions) : void;
	removeEventListener(type : string, callback? : EventListener, options? : EventListenerOptions) : void;
	dispatchEvent(event : Event) : void;
}

interface Event {
	type : string;
	target? : EventTarget;
	currentTarget? : EventTarget;
	composedPath() : EventTarget[];

	const NONE : 0;
	const CAPTURING_PHASE : 1;
	const AT_TARGET : 2;
	const BUBBLING_PHASE : 3;
	eventPhase : short;

	stopPropagation() : void;
	stopImmediatePropagation() : void;

	bubbles : boolean;
	cancelable : boolean;
	preventDefault() : void;
	defaultPrevented : boolean;
	composed : boolean;

	isTrusted : boolean;
	timeStamp : TimeStamp;
}


interface EventInit {
	bubbles? : boolean;
	cancelable? : boolean;
}

interface UIEventInit extends EventInit {
	view? : Window;
	detail? : number;
}

interface 