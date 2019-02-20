package com.mindlin.jsast.impl.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindlin.jsast.impl.tree.BinaryTreeImpl;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.CastExpressionTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressionTreeVisitor;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.NullLiteralTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.SpreadElementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.TaggedTemplateLiteralTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.type.IntrinsicType;
import com.mindlin.jsast.type.LiteralType;
import com.mindlin.jsast.type.Signature;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.TypeMember;
import com.mindlin.jsast.type.TypeParameter;
import com.mindlin.jsast.type.TypeParameter.RebindableTypeParameter;

/**
 * ExpressionTree -> Type
 * @author mailmindlin
 *
 */
public class ExpressionTypeCalculator implements ExpressionTreeVisitor<Type, ReadonlyContext> {
	
	protected List<Type> resolveGenericsList(List<TypeParameterDeclarationTree> genericDecls, ReadonlyContext ctx) {
		Map<String, TypeParameter> generics = new HashMap<>();
		for (TypeParameterDeclarationTree genericDecl : genericDecls) {
			String name = genericDecl.getName().getName();
			TypeParameter param;
			if (genericDecl.getDefault() == null && genericDecl.getSupertype() == null)
				param = new TypeParameter(null, null);
			//TODO: finish
		}
		
		List<Type> result = new ArrayList<>(genericDecls.size());
		
		//TODO: finish
		return result;
	}
	
	@Override
	public Type visitArrayLiteral(ArrayLiteralTree node, ReadonlyContext d) {
		List<Type> types = new ArrayList<>(node.getElements().size());
		node.getElements().stream()
			//TODO: flatmap spreads here
			.map(expression -> expression.accept(this, d))
			.reduce(new ArrayList<TypeTree>(), (tuple, type) -> {
				for (TypeTree t : tuple) {
					//TODO reduce
					tuple.add(t);
				}
				return tuple;
			}, (a, b) -> {a.addAll(b); return a;});
		for (ExpressionTree expression : node.getElements())
			types.add(expression.accept(this, d));
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Type visitAssignment(AssignmentTree node, ReadonlyContext d) {
		if (node.getKind() == Kind.ASSIGNMENT)
			return node.getValue().accept(this, d);
		//(a += b) is equivalent to (a = a + b), so defer to visitBinary().
		//visitBinary() is written to map the [OPNAME]_ASSIGNMENT kinds to OPNAME internally.
		//For update assignments, the variable can't be a PatternTree that isn't an ExpressionTree.
		return this.visitBinary(new BinaryTreeImpl(node.getStart(), node.getEnd(), node.getKind(), (ExpressionTree) node.getVariable(), node.getValue()), d);//TODO: finish
	}

	@Override
	public Type visitAwait(AwaitTree node, ReadonlyContext d) {
		Type exprType = node.getExpression().accept(this, d);
		//TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitBinary(BinaryExpressionTree node, ReadonlyContext d) {
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
				return IntrinsicType.NUMBER;//TODO implement int32/int64 restrictions
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
				return IntrinsicType.BOOLEAN;
			case LOGICAL_AND:
			case LOGICAL_OR: {
				Type leftType = lhs.accept(this, d);
				Type rightType = rhs.accept(this, d);
				//Can be used as selectors, so union of both sides
				return TypeCalculator.union(d, true, leftType, rightType);
			}
			default:
				return null;
		}
		return null;
	}

	@Override
	public Type visitBooleanLiteral(BooleanLiteralTree node, ReadonlyContext d) {
		return IntrinsicType.BOOLEAN;//TODO: boolean literal?
	}

	@Override
	public Type visitCast(CastExpressionTree node, ReadonlyContext d) {
		return node.getType().accept(new TypeExpressionResolver(), d);
	}

	@Override
	public Type visitConditionalExpression(ConditionalExpressionTree node, ReadonlyContext d) {
		return TypeCalculator.union(d, true, node.getTrueExpression().accept(this, d), node.getFalseExpression().accept(this, d));
	}

	@Override
	public Type visitFunctionCall(FunctionCallTree node, ReadonlyContext d) {
		Type fnType = node.getCallee().accept(this, d);
		//return fnType.getReturnType();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitFunctionExpression(FunctionExpressionTree node, ReadonlyContext d) {
		RebindableTypeParameter thisParam = TypeParameter.unbound();//Parameter to use for 'this' types
		
		Map<String, TypeParameter> generics = new HashMap<>();
		
		//TODO: finish
		throw new UnsupportedOperationException();
	}

	@Override
	public Type visitIdentifier(IdentifierTree node, ReadonlyContext d) {
		return d.getVar(node.getName()).getCurrentType();
	}

	@Override
	public Type visitNew(NewTree node, ReadonlyContext d) {
		Type calleeType = node.getCallee().accept(this, d);
		
		List<Type> arguments = new ArrayList<>(node.getArguments().size());
		for (ExpressionTree argument : node.getArguments())
			arguments.add(argument.accept(this, d));
		
		//TODO: discover constructors on calleeType
		Collection<Signature> constructors = TypeCalculator.restrictSignatures(d, null, Collections.emptyList(), arguments);
		
		return constructors.iterator().next().getReturnType();
	}

	@Override
	public Type visitNull(NullLiteralTree node, ReadonlyContext d) {
		return IntrinsicType.NULL;
	}

	@Override
	public Type visitNumericLiteral(NumericLiteralTree node, ReadonlyContext d) {
		return LiteralType.of(node.getValue());
	}

	@Override
	public Type visitObjectLiteral(ObjectLiteralTree node, ReadonlyContext d) {
		node.getProperties();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitSequence(SequenceExpressionTree node, ReadonlyContext d) {
		if (node.getElements().isEmpty())
			return null;
		return node.getElements().get(node.getElements().size() - 1).accept(this, d);
	}

	@Override
	public Type visitStringLiteral(StringLiteralTree node, ReadonlyContext d) {
		return new LiteralType<String>(Type.Kind.STRING_LITERAL, node.getValue());
	}

	@Override
	public Type visitSuper(SuperExpressionTree node, ReadonlyContext d) {
		return d.superType();
	}
	
	@Override
	public Type visitTemplateLiteral(TemplateLiteralTree node, ReadonlyContext d) {
		return IntrinsicType.STRING;
	}

	@Override
	public Type visitThis(ThisExpressionTree node, ReadonlyContext d) {
		return d.thisType();
	}

	@Override
	public Type visitUnary(UnaryTree node, ReadonlyContext d) {
		switch (node.getKind()) {
			case BITWISE_NOT:
			case PREFIX_INCREMENT:
			case POSTFIX_INCREMENT:
			case PREFIX_DECREMENT:
			case POSTFIX_DECREMENT:
				return IntrinsicType.NUMBER;//TODO restriction
			case LOGICAL_NOT:
			case DELETE:
			case UNARY_PLUS:
			case UNARY_MINUS:
				return IntrinsicType.BOOLEAN;
			case TYPEOF:
				return IntrinsicType.STRING;//TODO: union of "string" | "number" | ...
			case VOID:
				return IntrinsicType.VOID;
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
	public Type visitParentheses(ParenthesizedTree node, ReadonlyContext d) {
		return node.getExpression().accept(this, d);
	}

	@Override
	public Type visitRegExpLiteral(RegExpLiteralTree node, ReadonlyContext d) {
		return d.getType("RegExp");
	}

	@Override
	public Type visitClassExpression(ClassExpressionTree node, ReadonlyContext d) {
		RebindableTypeParameter thisTP = TypeParameter.unbound();
		//TODO: resolve super type(s)
		List<TypeMember> staticProps = new ArrayList<>();
		List<Signature> constructSignatures = new ArrayList<>();
		List<TypeMember> props = new ArrayList<>();
		List<Signature> callSignatures = new ArrayList<>();
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitSpread(SpreadElementTree node, ReadonlyContext d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitTaggedTemplate(TaggedTemplateLiteralTree node, ReadonlyContext d) {
		// TODO Auto-generated method stub
		return null;
	}
}
