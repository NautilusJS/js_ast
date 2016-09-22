import "types.ts";
import {EventHandler} from "event.ts";

export interface BatteryManager {
	charging : boolean;
	chargingTime : double;
	dischargingTime : double;
	level : double;
	onchargingchange : EventHandler;
	onchargingtimechange : EventHandler;
	ondischargingtimechange : EventHandler;
	onlevelchange : EventHandler;
}