package com.mindlin.jsast.transform;

import java.util.List;

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

public class ASTTransformer implements ASTTransformation<Tree, ASTTransformerContext> {
	List<ASTTransformation> transformations;
	public CompilationUnitTree apply(CompilationUnitTree tree) {
		
		return null;
	}
	/*
	protected Tree applySinglePass(Tree node, ASTTransformerContext context) {
		Tree result = node;
		for (ASTTransformation<?> transformation : transformations)
			result = result.accept(transformation, context);
		return result;
	}*/
	
	@Override
	public Tree visitSpecialType(SpecialTypeTree node, ASTTransformerContext d) {
		Tree result = node;
		boolean modified = false;
		do {
			modified = false;
			
		} while (modified);
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitArrayLiteral(ArrayLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitArrayPattern(ArrayPatternTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitArrayType(ArrayTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitAssignment(AssignmentTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitAssignmentPattern(AssignmentPatternTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitBinary(BinaryTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitBlock(BlockTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitBooleanLiteral(BooleanLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitBreak(BreakTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitCast(CastTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitClassDeclaration(ClassDeclarationTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitComment(CommentNode node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitCompilationUnit(CompilationUnitTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitComputedPropertyKey(ComputedPropertyKeyTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitConditionalExpression(ConditionalExpressionTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitContinue(ContinueTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitDebugger(DebuggerTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitDoWhileLoop(DoWhileLoopTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitEmptyStatement(EmptyStatementTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitEnumDeclaration(EnumDeclarationTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitExport(ExportTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitExpressionStatement(ExpressionStatementTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitForEachLoop(ForEachLoopTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitForLoop(ForLoopTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitFunctionCall(FunctionCallTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitFunctionExpression(FunctionExpressionTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitFunctionType(FunctionTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitGenericRefType(GenericRefTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitGenericType(GenericTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitIdentifier(IdentifierTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitIdentifierType(IdentifierTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitIf(IfTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitImport(ImportTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitIndexType(IndexTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitInterfaceDeclaration(InterfaceDeclarationTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitInterfaceType(InterfaceTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitIntersectionType(IntersectionTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitLabeledStatement(LabeledStatementTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitMemberType(MemberTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitMethodDefinition(MethodDefinitionTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitNew(NewTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitNull(NullLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitNumericLiteral(NumericLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitObjectLiteral(ObjectLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitObjectPattern(ObjectPatternTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitParameter(ParameterTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitParameterType(ParameterTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitParentheses(ParenthesizedTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitRegExpLiteral(RegExpLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitReturn(ReturnTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitSequence(SequenceTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitStringLiteral(StringLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitSuper(SuperExpressionTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitSwitch(SwitchTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitTemplateLiteral(TemplateLiteralTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitThis(ThisExpressionTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitThrow(ThrowTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitTry(TryTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitTupleType(TupleTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitUnary(UnaryTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitUnionType(UnionTypeTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Tree visitVariableDeclaration(VariableDeclarationTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitWhileLoop(WhileLoopTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Tree visitWith(WithTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return null;
	}
}
