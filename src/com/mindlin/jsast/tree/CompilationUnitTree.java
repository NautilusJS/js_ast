package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.impl.tree.LineMap;

public interface CompilationUnitTree extends Tree {
	LineMap getLineMap();
	
	List<StatementTree> getSourceElements();
	
	/**
	 * Get the source file that this code came from
	 * @return
	 */
	SourceFile getSourceFile();
	
	/**
	 * Whether this compilation unit is strict
	 * @return
	 */
	boolean isStrict();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.COMPILATION_UNIT;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitCompilationUnit(this, data);
	}
	
	@Override
	default boolean equivalentTo(Tree other) {
		if (this == other || this.equals(other))
			return true;
		
		if (other.getKind() != Tree.Kind.COMPILATION_UNIT)
			return false;
		
		CompilationUnitTree o = (CompilationUnitTree) other;
		
		if (this.getSourceFile() != o.getSourceFile())
			return false;
		
		//TODO finish
		return false;
	}
}