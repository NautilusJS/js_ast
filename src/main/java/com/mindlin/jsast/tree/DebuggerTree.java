package com.mindlin.jsast.tree;

/**
 * A tree for the debugger statement.
 * 
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/debugger">MDN</a>
 * @author mailmindlin
 */
public interface DebuggerTree extends StatementTree {
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.DEBUGGER;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitDebugger(this, data);
	}
}
