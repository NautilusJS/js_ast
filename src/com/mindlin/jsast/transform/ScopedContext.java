package com.mindlin.jsast.transform;

import java.util.List;

import com.mindlin.jsast.tree.ParameterTree;

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
