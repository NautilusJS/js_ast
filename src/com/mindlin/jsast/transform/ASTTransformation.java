package com.mindlin.jsast.transform;

import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.ArrayPatternTree;
import com.mindlin.jsast.tree.AssignmentPatternTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.CastTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.CommentNode;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ContinueTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.EmptyStatementTree;
import com.mindlin.jsast.tree.EnumDeclarationTree;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionStatementTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.NullLiteralTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.ReturnTree;
import com.mindlin.jsast.tree.SequenceTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.ThrowTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.GenericRefTypeTree;
import com.mindlin.jsast.tree.type.GenericTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IndexTypeTree;
import com.mindlin.jsast.tree.type.InterfaceTypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.MemberTypeTree;
import com.mindlin.jsast.tree.type.ParameterTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;

public interface ASTTransformation extends TreeVisitor<Tree, ASTTransformerContext> {
	@Override
	default Tree visitSpecialType(SpecialTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitArrayLiteral(ArrayLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitArrayPattern(ArrayPatternTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitArrayType(ArrayTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitAssignment(AssignmentTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitAssignmentPattern(AssignmentPatternTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitBinary(BinaryTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitBlock(BlockTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitBooleanLiteral(BooleanLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitBreak(BreakTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitCast(CastTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitClassDeclaration(ClassDeclarationTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitComment(CommentNode node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitCompilationUnit(CompilationUnitTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitComputedPropertyKey(ComputedPropertyKeyTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitConditionalExpression(ConditionalExpressionTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitContinue(ContinueTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitDebugger(DebuggerTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitDoWhileLoop(DoWhileLoopTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitEmptyStatement(EmptyStatementTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitEnumDeclaration(EnumDeclarationTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitExport(ExportTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitExpressionStatement(ExpressionStatementTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitForEachLoop(ForEachLoopTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitForLoop(ForLoopTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitFunctionCall(FunctionCallTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitFunctionExpression(FunctionExpressionTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitFunctionType(FunctionTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitGenericRefType(GenericRefTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitGenericType(GenericTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitIdentifier(IdentifierTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitIdentifierType(IdentifierTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitIf(IfTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitImport(ImportTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitIndexType(IndexTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitInterfaceDeclaration(InterfaceDeclarationTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitInterfaceType(InterfaceTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitIntersectionType(IntersectionTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitLabeledStatement(LabeledStatementTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitLiteral(LiteralTree<?> node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitMemberType(MemberTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitMethodDefinition(MethodDefinitionTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitNew(NewTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitNull(NullLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitNumericLiteral(NumericLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitObjectLiteral(ObjectLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitObjectPattern(ObjectPatternTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitParameter(ParameterTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitParameterType(ParameterTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitParentheses(ParenthesizedTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitRegExpLiteral(RegExpLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitReturn(ReturnTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitSequence(SequenceTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitStringLiteral(StringLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitSuper(SuperExpressionTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitSwitch(SwitchTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitTemplateLiteral(TemplateLiteralTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitThis(ThisExpressionTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitThrow(ThrowTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitTry(TryTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitTupleType(TupleTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitUnary(UnaryTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitUnionType(UnionTypeTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitVariableDeclaration(VariableDeclarationTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitWhileLoop(WhileLoopTree node, ASTTransformerContext d) {
		return node;
	}
	
	@Override
	default Tree visitWith(WithTree node, ASTTransformerContext d) {
		return node;
	}
}
