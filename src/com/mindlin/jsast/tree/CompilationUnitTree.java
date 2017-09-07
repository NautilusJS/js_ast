package com.mindlin.jsast.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.impl.tree.LineMap;

public interface CompilationUnitTree extends Tree {
	LineMap getLineMap();
	
	List<StatementTree> getSourceElements();
	
	/**
	 * Get the source file that this code came from
	 * @return source file
	 */
	SourceFile getSourceFile();
	
	/**
	 * Whether this compilation unit is strict
	 * @return if strict
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
		if (this == other)
			return true;
		
		if (other.getKind() != Tree.Kind.COMPILATION_UNIT)
			return false;
		
		CompilationUnitTree o = (CompilationUnitTree) other;
		
		if (!Objects.equals(this.getSourceFile(), o.getSourceFile()))
			return false;
		
		
		//Compare source elements
		List<StatementTree> elems0 = this.getSourceElements(),
				elems1 = o.getSourceElements();
		
		if (elems0 == elems1)
			return true;
		
		if (elems0.size() != elems1.size())
			return false;
		
		Iterator<StatementTree> li0 = elems0.iterator(), li1 = elems1.iterator();
		
		while (li0.hasNext()) {
			StatementTree a = li0.next(), b = li1.next();
			if (a != b && (a == null || !a.equivalentTo(b)))
				return false;
		}
		
		return true;
	}
}