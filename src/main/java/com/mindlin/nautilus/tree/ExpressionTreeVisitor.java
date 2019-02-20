package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;

public interface ExpressionTreeVisitor<R, D> {
	R visitArrayLiteral(ArrayLiteralTree node, D d);
	R visitAssignment(AssignmentTree node, D d);
	R visitAwait(AwaitTree node, D d);
	R visitBinary(BinaryExpressionTree node, D d);
	R visitBooleanLiteral(BooleanLiteralTree node, D d);
	R visitCast(CastExpressionTree node, D d);
	R visitClassExpression(ClassExpressionTree node, D d);
	R visitConditionalExpression(ConditionalExpressionTree node, D d);
	R visitFunctionCall(FunctionCallTree node, D d);
	R visitFunctionExpression(FunctionExpressionTree node, D d);
	R visitIdentifier(IdentifierTree node, D d);
	R visitNew(NewTree node, D d);
	R visitNull(NullLiteralTree node, D d);
	R visitNumericLiteral(NumericLiteralTree node, D d);
	R visitObjectLiteral(ObjectLiteralTree node, D d);
	R visitParentheses(ParenthesizedTree node, D d);
	R visitRegExpLiteral(RegExpLiteralTree node, D d);
	R visitSequence(SequenceExpressionTree node, D d);
	R visitSpread(SpreadElementTree node, D d);
	R visitStringLiteral(StringLiteralTree node, D d);
	R visitSuper(SuperExpressionTree node, D d);
	R visitTemplateLiteral(TemplateLiteralTree node, D d);
	R visitTaggedTemplate(TaggedTemplateLiteralTree node, D d);
	R visitThis(ThisExpressionTree node, D d);
	R visitUnary(UnaryTree node, D d);
}
