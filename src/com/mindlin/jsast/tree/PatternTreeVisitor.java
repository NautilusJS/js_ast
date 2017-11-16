package com.mindlin.jsast.tree;

public interface PatternTreeVisitor<R, D> {
	R visitArrayPattern(ArrayPatternTree node, D d);
	R visitAssignmentPattern(AssignmentPatternTree node, D d);
	R visitIdentifier(IdentifierTree node, D d);
	R visitMemberExpression(MemberExpressionTree node, D d);
	R visitObjectPattern(ObjectPatternTree node, D d);
	R visitVariableDeclaration(VariableDeclarationTree node, D d);
}
