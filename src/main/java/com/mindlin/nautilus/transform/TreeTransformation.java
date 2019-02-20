package com.mindlin.nautilus.transform;

import com.mindlin.nautilus.tree.ArrayLiteralTree;
import com.mindlin.nautilus.tree.ArrayPatternTree;
import com.mindlin.nautilus.tree.AssignmentTree;
import com.mindlin.nautilus.tree.BinaryExpressionTree;
import com.mindlin.nautilus.tree.BlockTree;
import com.mindlin.nautilus.tree.BooleanLiteralTree;
import com.mindlin.nautilus.tree.BreakTree;
import com.mindlin.nautilus.tree.CastExpressionTree;
import com.mindlin.nautilus.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.nautilus.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.nautilus.tree.CompilationUnitTree;
import com.mindlin.nautilus.tree.ConditionalExpressionTree;
import com.mindlin.nautilus.tree.ContinueTree;
import com.mindlin.nautilus.tree.DebuggerTree;
import com.mindlin.nautilus.tree.DirectiveTree;
import com.mindlin.nautilus.tree.DoWhileLoopTree;
import com.mindlin.nautilus.tree.EmptyStatementTree;
//import com.mindlin.nautilus.tree.EmptyStatementTree;
import com.mindlin.nautilus.tree.ExportTree;
import com.mindlin.nautilus.tree.ExpressionStatementTree;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.ForEachLoopTree;
import com.mindlin.nautilus.tree.ForLoopTree;
import com.mindlin.nautilus.tree.FunctionCallTree;
import com.mindlin.nautilus.tree.FunctionDeclarationTree;
import com.mindlin.nautilus.tree.FunctionExpressionTree;
import com.mindlin.nautilus.tree.IdentifierTree;
import com.mindlin.nautilus.tree.IfTree;
import com.mindlin.nautilus.tree.ImportDeclarationTree;
import com.mindlin.nautilus.tree.LabeledStatementTree;
import com.mindlin.nautilus.tree.MemberExpressionTree;
import com.mindlin.nautilus.tree.NewTree;
import com.mindlin.nautilus.tree.NullLiteralTree;
import com.mindlin.nautilus.tree.NumericLiteralTree;
import com.mindlin.nautilus.tree.ObjectLiteralTree;
import com.mindlin.nautilus.tree.ObjectPatternTree;
import com.mindlin.nautilus.tree.ParenthesizedTree;
import com.mindlin.nautilus.tree.PatternTree;
import com.mindlin.nautilus.tree.RegExpLiteralTree;
import com.mindlin.nautilus.tree.ReturnTree;
import com.mindlin.nautilus.tree.SequenceExpressionTree;
import com.mindlin.nautilus.tree.SpreadElementTree;
import com.mindlin.nautilus.tree.StatementTree;
import com.mindlin.nautilus.tree.StringLiteralTree;
import com.mindlin.nautilus.tree.SuperExpressionTree;
import com.mindlin.nautilus.tree.SwitchTree;
import com.mindlin.nautilus.tree.TaggedTemplateLiteralTree;
import com.mindlin.nautilus.tree.TemplateLiteralTree;
import com.mindlin.nautilus.tree.ThisExpressionTree;
import com.mindlin.nautilus.tree.ThrowTree;
import com.mindlin.nautilus.tree.Tree;
import com.mindlin.nautilus.tree.TreeVisitor;
import com.mindlin.nautilus.tree.TryTree;
import com.mindlin.nautilus.tree.UnaryTree;
import com.mindlin.nautilus.tree.UnaryTree.AwaitTree;
import com.mindlin.nautilus.tree.VariableDeclarationTree;
import com.mindlin.nautilus.tree.WhileLoopTree;
import com.mindlin.nautilus.tree.WithTree;
import com.mindlin.nautilus.tree.comment.CommentNode;
import com.mindlin.nautilus.tree.type.ArrayTypeTree;
import com.mindlin.nautilus.tree.type.CompositeTypeTree;
import com.mindlin.nautilus.tree.type.ConditionalTypeTree;
import com.mindlin.nautilus.tree.type.ConstructorTypeTree;
import com.mindlin.nautilus.tree.type.EnumDeclarationTree;
import com.mindlin.nautilus.tree.type.FunctionTypeTree;
import com.mindlin.nautilus.tree.type.IdentifierTypeTree;
import com.mindlin.nautilus.tree.type.InterfaceDeclarationTree;
import com.mindlin.nautilus.tree.type.LiteralTypeTree;
import com.mindlin.nautilus.tree.type.MappedTypeTree;
import com.mindlin.nautilus.tree.type.MemberTypeTree;
import com.mindlin.nautilus.tree.type.ObjectTypeTree;
import com.mindlin.nautilus.tree.type.SpecialTypeTree;
import com.mindlin.nautilus.tree.type.TupleTypeTree;
import com.mindlin.nautilus.tree.type.TypeAliasTree;
import com.mindlin.nautilus.tree.type.TypeTree;
import com.mindlin.nautilus.tree.type.UnaryTypeTree;

public interface TreeTransformation<D> extends TreeVisitor<Tree, D> {
	
	@Override
	default ExpressionTree visitArrayLiteral(ArrayLiteralTree node, D d) {
		return node;
	}

	@Override
	default PatternTree visitArrayPattern(ArrayPatternTree node, D d) {
		return node;
	}

	@Override
	default TypeTree visitArrayType(ArrayTypeTree node, D d) {
		return node;
	}

	@Override
	default ExpressionTree visitAssignment(AssignmentTree node, D d) {
		return node;
	}

	@Override
	default ExpressionTree visitAwait(AwaitTree node, D d) {
		return node;
	}

	@Override
	default ExpressionTree visitBinary(BinaryExpressionTree node, D d) {
		return node;
	}

	@Override
	default StatementTree visitBlock(BlockTree node, D d) {
		return node;
	}

	@Override
	default ExpressionTree visitBooleanLiteral(BooleanLiteralTree node, D d) {
		return node;
	}

	@Override
	default StatementTree visitBreak(BreakTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitCast(CastExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default Tree visitClassDeclaration(ClassDeclarationTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitClassExpression(ClassExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default CommentNode visitComment(CommentNode node, D d) {
		return node;
	}
	
	@Override
	default CompilationUnitTree visitCompilationUnit(CompilationUnitTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitConditionalExpression(ConditionalExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitConditionalType(ConditionalTypeTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitConstructorType(ConstructorTypeTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitContinue(ContinueTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitDebugger(DebuggerTree node, D d) {
		return node;
	}
	
	@Override
	default DirectiveTree visitDirective(DirectiveTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitDoWhileLoop(DoWhileLoopTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitEmptyStatement(EmptyStatementTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitEnumDeclaration(EnumDeclarationTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitExport(ExportTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitExpressionStatement(ExpressionStatementTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitForEachLoop(ForEachLoopTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitForLoop(ForLoopTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitFunctionCall(FunctionCallTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitFunctionDeclaration(FunctionDeclarationTree node, D d) {
		return node;
	}

	@Override
	default ExpressionTree visitFunctionExpression(FunctionExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitFunctionType(FunctionTypeTree node, D d) {
		return node;
	}
	
	@Override
	default Tree visitIdentifier(IdentifierTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitIdentifierType(IdentifierTypeTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitIf(IfTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitImport(ImportDeclarationTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitInterfaceDeclaration(InterfaceDeclarationTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitInterfaceType(ObjectTypeTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitIntersectionType(CompositeTypeTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitLabeledStatement(LabeledStatementTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitLiteralType(LiteralTypeTree<?> node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitMappedType(MappedTypeTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitMemberExpression(MemberExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitMemberType(MemberTypeTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitNew(NewTree node, D d) {
		return node;
	}

	@Override
	default ExpressionTree visitNull(NullLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitNumericLiteral(NumericLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitObjectLiteral(ObjectLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default PatternTree visitObjectPattern(ObjectPatternTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitParentheses(ParenthesizedTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitRegExpLiteral(RegExpLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitReturn(ReturnTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitSequence(SequenceExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitSpecialType(SpecialTypeTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitSpread(SpreadElementTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitStringLiteral(StringLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitSuper(SuperExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitSwitch(SwitchTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitTaggedTemplate(TaggedTemplateLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitTemplateLiteral(TemplateLiteralTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitThis(ThisExpressionTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitThrow(ThrowTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitTry(TryTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitTupleType(TupleTypeTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitTypeAlias(TypeAliasTree node, D d) {
		return node;
	}
	
	@Override
	default ExpressionTree visitUnary(UnaryTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitUnaryType(UnaryTypeTree node, D d) {
		return node;
	}
	
	@Override
	default TypeTree visitUnionType(CompositeTypeTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitVariableDeclaration(VariableDeclarationTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitWhileLoop(WhileLoopTree node, D d) {
		return node;
	}
	
	@Override
	default StatementTree visitWith(WithTree node, D d) {
		return node;
	}
}
