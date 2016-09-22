import "types.ts";

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

	boolean isTrusted : boolean;
	timeStamp : TimeStamp;
}