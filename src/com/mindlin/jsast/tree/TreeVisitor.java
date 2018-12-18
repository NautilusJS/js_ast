package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.comment.CommentNode;
import com.mindlin.jsast.tree.type.TypeTreeVisitor;

public interface TreeVisitor<R, D> extends StatementTreeVisitor<R, D>, ExpressionTreeVisitor<R, D>, TypeTreeVisitor<R, D>, PatternTreeVisitor<R, D> {
	R visitComment(CommentNode node, D d);
	R visitCompilationUnit(CompilationUnitTree node, D d);
}
