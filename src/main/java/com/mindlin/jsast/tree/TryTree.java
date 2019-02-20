package com.mindlin.jsast.tree;

import java.util.Collections;
import java.util.List;

/**
 * Try tree.
 * 
 * @author mailmindlin
 */
public interface TryTree extends StatementTree {
	/**
	 * Get block of code executed under the exception catching.
	 * 
	 * @return try block
	 */
	BlockTree getBlock();
	
	/**
	 * Get any/all catch blocks.
	 * 
	 * @return list of catch trees; if none are present,
	 *         {@link Collections#emptyList()} should be returned in favor of
	 *         null.
	 */
	List<? extends CatchTree> getCatches();
	
	/**
	 * Get finally block, if present.
	 * @return finally block, else null if not present
	 */
	BlockTree getFinallyBlock();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TRY;
	}
	
	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitTry(this, data);
	}
}
