package com.mindlin.nautilus.tree;

import com.mindlin.nautilus.tree.comment.CommentNode;
import com.mindlin.nautilus.tree.type.TypeTreeVisitor;

public interface TreeVisitor<R, D> extends StatementTreeVisitor<R, D>, ExpressionTreeVisitor<R, D>, TypeTreeVisitor<R, D>, PatternTreeVisitor<R, D>, ClassElementVisitor<R, D>, TypeElementVisitor<R, D> {
	R visitComment(CommentNode node, D d);
	R visitCompilationUnit(CompilationUnitTree node, D d);
}
