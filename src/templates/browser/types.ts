declare type int = number;
declare type long = number;
declare type unsignedLong = long;
declare type float = number;
declare type double = number;
export interface Plugin {
	description : string;
	filename: string;
	length: number;
	name: string;
	item?: MimeType;
	namedItem(name:string):MimeType|void;
}
export interface MimeType {
	description: string;
	enabledPlugin: Plugin;
	suffixes: string;
	type: string;
}

interface SpecialArray<T> {
	length: unsignedLong;
	item(index : unsignedLong) : T|void;
	namedItem(name: string) : T|void;
}

export interface PluginArray extends SpecialArray<Plugin> {
	refresh(reload?:boolean) : void;
}

export interface MimeTypeArray extends SpecialArray<MimeType> {
}