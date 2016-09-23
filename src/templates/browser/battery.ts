import "types.ts";
import {EventTarget, EventListener} from "event.ts";

export interface BatteryManager extends EventTarget {
	charging : boolean;
	chargingTime : double;
	dischargingTime : double;
	level : double;
	onchargingchange : EventListener;
	onchargingtimechange : EventListener;
	ondischargingtimechange : EventListener;
	onlevelchange : EventListener;
}