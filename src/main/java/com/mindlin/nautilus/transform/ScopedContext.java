package com.mindlin.nautilus.transform;

import java.util.List;

import com.mindlin.nautilus.tree.ParameterTree;

/**
 * A context which is aware of the scope
 * @author mailmindlin
 */
public interface ScopedContext {
	void enterBlock();
	void exitBlock();
	
	void enterFunction(List<ParameterTree> params);
	void exitFunction();
	
	void enterClass();
	void exitClass();
	
	void enterCatch();
	void exitCatch();
}
