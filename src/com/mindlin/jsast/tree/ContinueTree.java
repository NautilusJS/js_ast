package com.mindlin.jsast.tree;

/**
 * A tree for the {@code continue} statement. Example:
 * <code>
 * outer:
 * for (var i = 2; i < 100; i++)
 *   for (var j = 2; j < Math.sqrt(i); j++)
 *     if (i % j == 0)
 *       continue outer;
 * </code> 
 * @author mailmindlin
 */
public interface ContinueTree extends GotoTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.CONTINUE;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitContinue(this, data);
	}
}
