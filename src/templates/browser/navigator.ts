import "types.ts";
export interface NavigatorID {
	appCodeName: string;
	appName: string;
	appVersion: string;
	platform: string;
	product: string;
	userAgent: string;
}
export interface NavigatorLanguage {
	language?: string;
	languages: string[];
}
export interface NavigatorPlugins {
	javaEnabled: boolean;
	mimeTypes: MimeTypeArray;
	plugins: PluginArray;
}

export interface NavigatorStorageUtils {
	cookieEnabled: boolean;
	yieldForStorageUpdates() : void;
}

export interface NavigatorConcurrentHardware {
	hardwareConcurrency : number;
}

export interface NavigatorOnLine {
	onLine: boolean;
}

interface Navigator extends NavigatorConcurrentHardware,
			NavigatorID,
			NavigatorLanguage,
			NavigatorOnLine,
			NavigatorPlugins,
			NavigatorStorageUtils {
}

export var navigator : Navigator;