package com.mindlin.jsast.transform;

import java.util.ArrayList;
import java.util.List;

import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.impl.tree.BinaryTypeTree;
import com.mindlin.jsast.impl.tree.CastTreeImpl;
import com.mindlin.jsast.impl.tree.CompilationUnitTreeImpl;
import com.mindlin.jsast.impl.tree.ConditionalExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.DoWhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.LabeledStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ParenthesizedTreeImpl;
import com.mindlin.jsast.impl.tree.ReturnTreeImpl;
import com.mindlin.jsast.impl.tree.SequenceTreeImpl;
import com.mindlin.jsast.impl.tree.TupleTypeTreeImpl;
import com.mindlin.jsast.impl.tree.WhileLoopTreeImpl;
import com.mindlin.jsast.impl.tree.WithTreeImpl;
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
import com.mindlin.jsast.tree.ExpressionTree;
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
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.ThrowTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeAliasTree;
import com.mindlin.jsast.tree.TypeTree;
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

public interface ASTTransformation<R extends Tree, D> extends TreeVisitor<R, D> {
	@Override
	@SuppressWarnings("unchecked")
	default R visitSpecialType(SpecialTypeTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitArrayLiteral(ArrayLiteralTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitArrayPattern(ArrayPatternTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitArrayType(ArrayTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitAssignment(AssignmentTree node, D ctx) {
		ExpressionTree oldLHS = node.getLeftOperand();
		ExpressionTree oldRHS = node.getRightOperand();
		
		ExpressionTree newLHS = (ExpressionTree) oldLHS.accept(this, ctx);
		ExpressionTree newRHS = (ExpressionTree) oldRHS.accept(this, ctx);
		
		if (oldLHS != newLHS || oldRHS != newRHS)
			node = new AssignmentTreeImpl(node.getStart(), node.getEnd(), node.getKind(), newLHS, newRHS);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitAssignmentPattern(AssignmentPatternTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitBinary(BinaryTree node, D ctx) {
		ExpressionTree oldLHS = node.getLeftOperand();
		ExpressionTree oldRHS = node.getRightOperand();
		
		ExpressionTree newLHS = (ExpressionTree) oldLHS.accept(this, ctx);
		ExpressionTree newRHS = (ExpressionTree) oldRHS.accept(this, ctx);
		
		//TODO fix for subtypes of BinaryTree
		if (oldLHS != newLHS || oldRHS != newRHS)
			node = new BinaryTreeImpl(node.getStart(), node.getEnd(), node.getKind(), newLHS, newRHS);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitBlock(BlockTree node, D ctx) {
		ArrayList<StatementTree> statements = new ArrayList<>();
		for (StatementTree statement : node.getStatements()) {
			
		}
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitBooleanLiteral(BooleanLiteralTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitBreak(BreakTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitCast(CastTree node, D ctx) {
		ExpressionTree oldExpr = node.getExpression();
		TypeTree oldType = node.getType();
		
		ExpressionTree newExpr = (ExpressionTree) oldExpr.accept(this, ctx);
		TypeTree newType = (TypeTree) oldType.accept(this, ctx);
		
		if (oldExpr != newExpr || oldType != newType)
			node = new CastTreeImpl(node.getStart(), node.getEnd(), newExpr, newType);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitClassDeclaration(ClassDeclarationTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitComment(CommentNode node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitCompilationUnit(CompilationUnitTree node, D ctx) {
		boolean modified = false;
		ArrayList<StatementTree> statements = new ArrayList<>();
		for (StatementTree statement : node.getSourceElements()) {
			StatementTree stmt = (StatementTree) statement.accept(this, ctx);
			statements.add(stmt);
			modified |= stmt != statement;
		}
		
		if (modified)
			node = new CompilationUnitTreeImpl(node.getStart(), node.getEnd(), node.getSourceFile(), node.getLineMap(), statements, node.isStrict());
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitComputedPropertyKey(ComputedPropertyKeyTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitConditionalExpression(ConditionalExpressionTree node, D ctx) {
		ExpressionTree oldCondition = node.getCondition();
		ExpressionTree oldTrueExprn = node.getTrueExpression();
		ExpressionTree oldFalseExpr = node.getFalseExpression();
		
		ExpressionTree newCondition = (ExpressionTree) oldCondition.accept(this, ctx);
		ExpressionTree newTrueExprn = (ExpressionTree) oldTrueExprn.accept(this, ctx);
		ExpressionTree newFalseExpr = (ExpressionTree) oldFalseExpr.accept(this, ctx);
		
		if (newCondition != oldCondition || newTrueExprn != oldTrueExprn || newFalseExpr != oldFalseExpr)
			node = new ConditionalExpressionTreeImpl(node.getStart(), node.getEnd(), newCondition, newTrueExprn,
					newFalseExpr);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitContinue(ContinueTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitDebugger(DebuggerTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitDoWhileLoop(DoWhileLoopTree node, D ctx) {
		ExpressionTree oldCondition = node.getCondition();
		StatementTree oldStatement = node.getStatement();
		
		ExpressionTree newCondition = (ExpressionTree) oldCondition.accept(this, ctx);
		StatementTree newStatement = (StatementTree) oldStatement.accept(this, ctx);
		
		if (oldCondition != newCondition || oldStatement != newStatement)
			node = new DoWhileLoopTreeImpl(node.getStart(), node.getEnd(), newStatement, newCondition);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitEmptyStatement(EmptyStatementTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitEnumDeclaration(EnumDeclarationTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitExport(ExportTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitExpressionStatement(ExpressionStatementTree node, D ctx) {
		ExpressionTree expr0 = node.getExpression();
		ExpressionTree expr1 = (ExpressionTree) expr0.accept(this, ctx);
		if (expr0 != expr1)
			node = new ExpressionStatementTreeImpl(node.getStart(), node.getEnd(), expr1);
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitForEachLoop(ForEachLoopTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitForLoop(ForLoopTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitFunctionCall(FunctionCallTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitFunctionExpression(FunctionExpressionTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitFunctionType(FunctionTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitGenericRefType(GenericRefTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitGenericType(GenericTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitIdentifier(IdentifierTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitIdentifierType(IdentifierTypeTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitIf(IfTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitImport(ImportTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitIndexType(IndexTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitInterfaceDeclaration(InterfaceDeclarationTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitInterfaceType(InterfaceTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitIntersectionType(IntersectionTypeTree node, D ctx) {
		TypeTree oldLeftType = node.getLeftType();
		TypeTree oldRightType= node.getRightType();
		
		TypeTree newLeftType = (TypeTree) oldLeftType.accept(this, ctx);
		TypeTree newRightType= (TypeTree) oldRightType.accept(this, ctx);
		
		if (newLeftType != oldLeftType || newRightType != oldRightType)
			node = new BinaryTypeTree(node.getStart(), node.getEnd(), node.isImplicit(), newLeftType, node.getKind(), newRightType);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitLabeledStatement(LabeledStatementTree node, D ctx) {
		StatementTree oldStmt = node.getStatement();
		StatementTree newStmt = (StatementTree) oldStmt.accept(this, ctx);
		
		if (oldStmt != newStmt)
			node = new LabeledStatementTreeImpl(node.getStart(), node.getEnd(), node.getName(), newStmt);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitLiteral(LiteralTree<?> node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitMemberType(MemberTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitMethodDefinition(MethodDefinitionTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitNew(NewTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitNull(NullLiteralTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitNumericLiteral(NumericLiteralTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitObjectLiteral(ObjectLiteralTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitObjectPattern(ObjectPatternTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitParameter(ParameterTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitParameterType(ParameterTypeTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitParentheses(ParenthesizedTree node, D ctx) {
		ExpressionTree oldExpr = node.getExpression();
		ExpressionTree newExpr = (ExpressionTree) oldExpr.accept(this, ctx);
		
		if (oldExpr != newExpr)
			node = new ParenthesizedTreeImpl(node.getStart(), node.getEnd(), newExpr);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitRegExpLiteral(RegExpLiteralTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitReturn(ReturnTree node, D ctx) {
		ExpressionTree oldExpr = node.getExpression();
		ExpressionTree newExpr = oldExpr == null ? null : (ExpressionTree) oldExpr.accept(this, ctx);
		
		if (oldExpr != newExpr)
			node = new ReturnTreeImpl(node.getStart(), node.getEnd(), newExpr);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitSequence(SequenceTree node, D ctx) {
		boolean modified = false;
		ArrayList<ExpressionTree> expressions = new ArrayList<>(node.getExpressions().size());
		for (ExpressionTree expression : node.getExpressions()) {
			ExpressionTree newExpression = (ExpressionTree) expression.accept(this, ctx);
			
			modified |= newExpression != expression;
			
			expressions.add(newExpression);
		}
		
		if (modified) {
			expressions.trimToSize();
			node = new SequenceTreeImpl(node.getStart(), node.getEnd(), expressions);
		}
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitStringLiteral(StringLiteralTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitSuper(SuperExpressionTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitSwitch(SwitchTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitTemplateLiteral(TemplateLiteralTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitThis(ThisExpressionTree node, D ctx) {
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitThrow(ThrowTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitTry(TryTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitTypeAlias(TypeAliasTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitTupleType(TupleTypeTree node, D ctx) {
		boolean modified = false;
		ArrayList<TypeTree> slots = new ArrayList<>();
		for (TypeTree slot : node.getSlotTypes()) {
			TypeTree newSlot = (TypeTree) slot.accept(this, ctx);
			if (newSlot != slot)
				modified = true;
			slots.add(newSlot);
		}
		
		if (modified)
			node = new TupleTypeTreeImpl(node.getStart(), node.getEnd(), node.isImplicit(), slots);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitUnary(UnaryTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitUnionType(UnionTypeTree node, D ctx) {
		TypeTree oldLeftType = node.getLeftType();
		TypeTree oldRightType= node.getRightType();
		
		TypeTree newLeftType = (TypeTree) oldLeftType.accept(this, ctx);
		TypeTree newRightType= (TypeTree) oldRightType.accept(this, ctx);
		
		if (newLeftType != oldLeftType || newRightType != oldRightType)
			node = new BinaryTypeTree(node.getStart(), node.getEnd(), node.isImplicit(), newLeftType, node.getKind(), newRightType);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitVariableDeclaration(VariableDeclarationTree node, D ctx) {
		//TODO finish
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitWhileLoop(WhileLoopTree node, D ctx) {
		ExpressionTree oldCondition = node.getCondition();
		StatementTree oldStatement = node.getStatement();
		
		ExpressionTree newCondition = (ExpressionTree) oldCondition.accept(this, ctx);
		StatementTree newStatement = (StatementTree) oldStatement.accept(this, ctx);
		
		if (oldCondition != newCondition || oldStatement != newStatement)
			node = new WhileLoopTreeImpl(node.getStart(), node.getEnd(), newCondition, newStatement);
		
		return (R) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	default R visitWith(WithTree node, D ctx) {
		ExpressionTree e1 = node.getScope();
		StatementTree s1 = node.getStatement();
		
		ExpressionTree e2 = (ExpressionTree) e1.accept(this, ctx);
		StatementTree s2 = (StatementTree) s1.accept(this, ctx);
		
		if (e1 != e2 || s1 != s2)
			node = new WithTreeImpl(node.getStart(), node.getEnd(), e2, s2);
		
		return (R) node;
	}
}
