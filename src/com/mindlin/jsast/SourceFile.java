package com.mindlin.jsast;

/**
 * Abstract away a JS source
 */
public interface SourceFile {
	/**
	 * Get path to file location. May return null
	 */
	Path getPath();
}
