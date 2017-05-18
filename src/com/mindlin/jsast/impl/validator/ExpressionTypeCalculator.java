package com.mindlin.jsast.impl.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mindlin.jsast.impl.tree.FunctionTypeTreeImpl;
import com.mindlin.jsast.impl.tree.LiteralTypeTreeImpl;
import com.mindlin.jsast.impl.tree.SpecialTypeTreeImpl;
import com.mindlin.jsast.impl.validator.ExpressionTypeCalculator.TypeContext;
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
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.NullLiteralTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree;
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
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeAliasTree;
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
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;

public class ExpressionTypeCalculator implements TreeVisitor<TypeTree, TypeContext> {
	
	protected static class TypeContext {
		public TypeTree lookupIdentifierType(String identifier) {
			return null;
		}
		
		public FunctionTypeTree lookupFunctionType(String identifier) {
			return null;
		}
	}

	@Override
	public TypeTree visitSpecialType(SpecialTypeTree node, TypeContext d) {
		return node;
	}

	@Override
	public TypeTree visitArrayLiteral(ArrayLiteralTree node, TypeContext d) {
		List<TypeTree> types = new ArrayList<>(node.getElements().size());
		node.getElements().stream()
			.map(expression -> expression.accept(this, d))
			.reduce(new ArrayList<TypeTree>(), (tuple, type) -> {
				for (TypeTree t : tuple) {
					
				}
				return tuple;
			}, (a, b) -> {a.addAll(b); return a;});
		for (ExpressionTree expression : node.getElements())
			types.add(expression.accept(this, d));
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitArrayPattern(ArrayPatternTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitArrayType(ArrayTypeTree node, TypeContext d) {
		return node;
	}

	@Override
	public TypeTree visitAssignment(AssignmentTree node, TypeContext d) {
		if (node.getKind() == Kind.ASSIGNMENT)
			return node.getRightOperand().accept(this, d);
		//(a += b) is equivalent to (a = a + b), so defer to visitBinary().
		//visitBinary() is written to map the OPNAME_ASSIGNMENT kinds to OPNAME internally.
		return this.visitBinary(node, d);
	}

	@Override
	public TypeTree visitAssignmentPattern(AssignmentPatternTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public TypeTree visitAwait(AwaitTree node, TypeContext d) {
		//TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitBinary(BinaryTree node, TypeContext d) {
		ExpressionTree lhs = node.getLeftOperand();
		ExpressionTree rhs = node.getRightOperand();
		
		//Support xxx_ASSIGNMENT kinds, because of redirects from visitAssignment().
		switch (node.getKind()) {
			case ADDITION:
			case ADDITION_ASSIGNMENT:
				//String if non-numeric type
			case ARRAY_ACCESS:
				//Carry LHS type
				return lhs.accept(this, d);
			case ASSIGNMENT:
				//Cary RHS type for assignment
				return rhs.accept(this, d);
			case MEMBER_SELECT:
			case IDENTIFIER:
				//Unknown
				break;
			//Bitwise operators always return a number (0 or 1)
			case BITWISE_AND:
			case BITWISE_AND_ASSIGNMENT:
			case BITWISE_OR:
			case BITWISE_OR_ASSIGNMENT:
			case BITWISE_XOR:
			case BITWISE_XOR_ASSIGNMENT:
			//Normal arithmatic operators
			case SUBTRACTION:
			case SUBTRACTION_ASSIGNMENT:
			case EXPONENTIATION:
			case EXPONENTIATION_ASSIGNMENT:
			case LEFT_SHIFT:
			case LEFT_SHIFT_ASSIGNMENT:
			case DIVISION:
			case DIVISION_ASSIGNMENT:
			case MULTIPLICATION:
			case MULTIPLICATION_ASSIGNMENT:
			case REMAINDER:
			case REMAINDER_ASSIGNMENT:
			case RIGHT_SHIFT:
			case RIGHT_SHIFT_ASSIGNMENT:
			case UNSIGNED_RIGHT_SHIFT:
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
				return new SpecialTypeTreeImpl(SpecialType.NUMBER);
			case INSTANCEOF:
			case IN:
			case LESS_THAN:
			case LESS_THAN_EQUAL:
			case EQUAL:
			case NOT_EQUAL:
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
			case STRICT_EQUAL:
			case STRICT_NOT_EQUAL:
				return new SpecialTypeTreeImpl(SpecialType.BOOLEAN);
			case LOGICAL_AND:
			case LOGICAL_OR: {
				TypeTree leftType = lhs.accept(this, d);
				TypeTree rightType = rhs.accept(this, d);
				//Can be used as selectors, so union of both sides
				return TypeInheritanceValidator.union(null, leftType, rightType);
			}
			default:
				return null;
		}
		return null;
	}

	@Override
	public TypeTree visitBlock(BlockTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitBooleanLiteral(BooleanLiteralTree node, TypeContext d) {
		return new SpecialTypeTreeImpl(SpecialType.BOOLEAN);
	}

	@Override
	public TypeTree visitBreak(BreakTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitCast(CastTree node, TypeContext d) {
		return node.getType();
	}

	@Override
	public TypeTree visitClassDeclaration(ClassDeclarationTree node, TypeContext d) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public TypeTree visitComment(CommentNode node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitCompilationUnit(CompilationUnitTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitComputedPropertyKey(ComputedPropertyKeyTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitConditionalExpression(ConditionalExpressionTree node, TypeContext d) {
		return TypeInheritanceValidator.union(null, node.getTrueExpression().accept(this, d), node.getFalseExpression().accept(this, d));
	}

	@Override
	public TypeTree visitContinue(ContinueTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitDebugger(DebuggerTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitDoWhileLoop(DoWhileLoopTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitEmptyStatement(EmptyStatementTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitEnumDeclaration(EnumDeclarationTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitExport(ExportTree node, TypeContext d) {
		//TODO not sure if export has a value
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public TypeTree visitExpressionStatement(ExpressionStatementTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitForEachLoop(ForEachLoopTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitForLoop(ForLoopTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitFunctionCall(FunctionCallTree node, TypeContext d) {
		FunctionTypeTree fnType = null;//TODO lookup function
		return fnType.getReturnType();
	}

	@Override
	public TypeTree visitFunctionExpression(FunctionExpressionTree node, TypeContext d) {
		return new FunctionTypeTreeImpl(node.getStart(), node.getEnd(), true, node.getParameters(), Collections.emptyList(), node.getReturnType());
	}

	@Override
	public TypeTree visitFunctionType(FunctionTypeTree node, TypeContext d) {
		return node;
	}

	@Override
	public TypeTree visitGenericRefType(GenericRefTypeTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitGenericType(GenericTypeTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitIdentifier(IdentifierTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitIdentifierType(IdentifierTypeTree node, TypeContext d) {
		return node;
	}

	@Override
	public TypeTree visitIf(IfTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitImport(ImportTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitIndexType(IndexTypeTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitInterfaceDeclaration(InterfaceDeclarationTree node, TypeContext d) {
		//return TypeInheritanceValidator.reduceInterface(d, node.getSupertypes(), node.getProperties());
		return null;
	}

	@Override
	public TypeTree visitInterfaceType(InterfaceTypeTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitIntersectionType(IntersectionTypeTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitLabeledStatement(LabeledStatementTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitMemberType(MemberTypeTree node, TypeContext d) {
		//We accept it to optionally reduce the given base type.
		TypeTree baseType = node.getBaseType().accept(this, d);
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitNew(NewTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitNull(NullLiteralTree node, TypeContext d) {
		return new SpecialTypeTreeImpl(SpecialType.NULL);
	}

	@Override
	public TypeTree visitNumericLiteral(NumericLiteralTree node, TypeContext d) {
		return new SpecialTypeTreeImpl(SpecialType.NUMBER);
	}

	@Override
	public TypeTree visitObjectLiteral(ObjectLiteralTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitObjectPattern(ObjectPatternTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitParameterType(ParameterTypeTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitParentheses(ParenthesizedTree node, TypeContext d) {
		return node.getExpression().accept(this, d);
	}

	@Override
	public TypeTree visitRegExpLiteral(RegExpLiteralTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitReturn(ReturnTree node, TypeContext d) {
		return node.getExpression().accept(this, d);
	}

	@Override
	public TypeTree visitSequence(SequenceTree node, TypeContext d) {
		if (node.getExpressions().isEmpty())
			return null;
		return node.getExpressions().get(node.getExpressions().size() - 1).accept(this, d);
	}

	@Override
	public TypeTree visitStringLiteral(StringLiteralTree node, TypeContext d) {
		return new LiteralTypeTreeImpl<String>(node, true);
	}

	@Override
	public TypeTree visitSuper(SuperExpressionTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitSwitch(SwitchTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitTemplateLiteral(TemplateLiteralTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitThis(ThisExpressionTree node, TypeContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree visitThrow(ThrowTree node, TypeContext d) {
		return new SpecialTypeTreeImpl(SpecialType.NEVER);
	}

	@Override
	public TypeTree visitTry(TryTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitTupleType(TupleTypeTree node, TypeContext d) {
		return node;
	}

	@Override
	public TypeTree visitUnary(UnaryTree node, TypeContext d) {
		switch (node.getKind()) {
			case BITWISE_NOT:
			case PREFIX_INCREMENT:
			case POSTFIX_INCREMENT:
			case PREFIX_DECREMENT:
			case POSTFIX_DECREMENT:
				return new SpecialTypeTreeImpl(SpecialType.NUMBER);
			case LOGICAL_NOT:
			case DELETE:
			case UNARY_PLUS:
			case UNARY_MINUS:
				return new SpecialTypeTreeImpl(SpecialType.BOOLEAN);
			case TYPEOF:
				return new SpecialTypeTreeImpl(SpecialType.STRING);
			case VOID:
				return new SpecialTypeTreeImpl(SpecialType.VOID);
			case YIELD:
			case YIELD_GENERATOR:
				//TODO this is wrong.
				return node.getExpression().accept(this, d);
			case SPREAD:
				//Type of expression array
				//TODO finish
				return null;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public TypeTree visitUnionType(UnionTypeTree node, TypeContext d) {
		return TypeInheritanceValidator.reduce(null, node);
	}

	@Override
	public TypeTree visitVariableDeclaration(VariableDeclarationTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitWhileLoop(WhileLoopTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitWith(WithTree node, TypeContext d) {
		return null;
	}

	@Override
	public TypeTree visitTypeAlias(TypeAliasTree node, TypeContext d) {
		return null;
	}
}
