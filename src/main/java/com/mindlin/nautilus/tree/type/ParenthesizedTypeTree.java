package com.mindlin.jsast.tree.type;

/**
 * Grammar: {@code ( TYPE )}
 * 
 * @author mailmindlin
 */
public interface ParenthesizedTypeTree extends TypeTree {
	TypeTree getType();
}
