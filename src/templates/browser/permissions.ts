import "types.ts";
import {EventTarget} from "event.ts";

export enum PermissionState {
	"granted",
	"denied",
	"prompt"
}

export enum PermissionName {
	"geolocation",
	"notifications",
	"push",
	"midi",
	"camera",
	"microphone",
	"speaker",
	"device-info",
	"background-sync",
	"bluetooth",
	"persistent-storage"
}

export interface PermissionStatus extends EventTarget {
	state: PermissionState;
	onchange: EventHandler;
}

export interface PermissionDescriptor {
	name: PermissionName;
}

export interface Permissions {
	query(permissionDesc : PermissionDescriptor) : Prommise<PermissionStatus>;
	request(permissionDesc : PermissionDescriptor) : Promise<PermissionStatus>;
	revoke(permissionDesc : PermissionDescriptor) : Promise<PermissionStatus>;
}