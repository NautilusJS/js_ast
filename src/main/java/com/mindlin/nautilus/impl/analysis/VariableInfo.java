package com.mindlin.jsast.impl.analysis;

import java.util.List;
import java.util.Set;

import com.mindlin.jsast.type.Type;

/**
 * VariableInfo for SSA stuff
 * @author mailmindlin
 *
 */
public interface VariableInfo {
	/**
	 * Get variable id. Unique.
	 * @return id
	 */
	long getId();
	
	/**
	 * Return if assignable (e.g., not const)
	 * @return assignable
	 */
	boolean isAssignable();
	
	/**
	 * Return if this variable is a meta var (e.g., {@code this}, {@code super}, {@code arguments}).
	 * @return if meta
	 */
	boolean isMetaVar();
	
	/**
	 * If variable is function
	 * @return
	 */
	default boolean isFunction() {
		return false;
	}
	
	Set<VariableInfo> getControlEdges();
	
	List<Object> getReads();
	List<Object> getWrites();
	
	VariableInfo getProperty(String name);
	
	/**
	 * Get type that the variable was declared as.
	 * @return
	 */
	Type getRestrictionType();
	
	/**
	 * Get the type that the value of the variable is currently
	 * @return
	 */
	Type getCurrentType();
}
