package com.mindlin.nautilus.tree.type;

/**
 * Grammar: {@code ( TYPE )}
 * 
 * @author mailmindlin
 */
public interface ParenthesizedTypeTree extends TypeTree {
	TypeTree getType();
}
