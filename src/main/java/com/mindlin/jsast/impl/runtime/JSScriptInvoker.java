package com.mindlin.jsast.impl.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mindlin.jsast.exception.JSException;
import com.mindlin.jsast.impl.runtime.objects.Symbol;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.ArrayPatternTree;
import com.mindlin.jsast.tree.AssignmentPatternTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.CastTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.CommentNode;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ContinueTree;
import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.EmptyStatementTree;
import com.mindlin.jsast.tree.ExportTree;
import com.mindlin.jsast.tree.ExpressionStatementTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionDeclarationTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.MemberExpressionTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.NullLiteralTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.ReturnTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.ThrowTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeAliasTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.AnyTypeTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.EnumDeclarationTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.LiteralTypeTree;
import com.mindlin.jsast.tree.type.MemberTypeTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.TupleTypeTree;

public class JSScriptInvoker implements TreeVisitor<Object, RuntimeScope>{
	
	protected static class CfException extends RuntimeException {
		private static final long serialVersionUID = 3401507539292593908L;
		
	}
	
	protected static class ReturnException extends CfException {
		private static final long serialVersionUID = 9079412200499822479L;
		public final Object value;
		public ReturnException(Object value) {
			this.value = value;
		}
	}
	
	protected static class ThrowException extends CfException {
		private static final long serialVersionUID = 607082517845240249L;
		public final Object value;
		public ThrowException(Object value) {
			this.value = value;
		}
	}
	
	protected static class BreakException extends CfException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1557599322915151218L;
		public final String label;
		public BreakException(String label) {
			this.label = label;
		}
	}
	
	@Override
	public Object visitAnyType(AnyTypeTree node, RuntimeScope d) {
		return null;
	}
	
	@Override
	public Object visitArrayLiteral(ArrayLiteralTree node, RuntimeScope d) {
		return node.getElements()
			.stream()
			.map(element -> element == null ? JSRuntimeUtils.UNDEFINED : element.accept(this, d))
			.collect(Collectors.toList());
	}

	@Override
	public Object visitArrayPattern(ArrayPatternTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitArrayType(ArrayTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitAssignment(AssignmentTree node, RuntimeScope d) {
		Object value = JSRuntimeUtils.dereference(node.getValue().accept(this, d));
		Object target = node.getVariable().accept(this, d);//TODO: use custom LHS target TreeVisitor thing
		
		if (target == null || !(target instanceof Reference))
			throw new JSException("Invalid left hand side of assignment");
		
		Object result = value;
		if (node.getKind() == Kind.ASSIGNMENT) {
			result = value;
		} else if (node.getKind() == Kind.ADDITION_ASSIGNMENT) {
			result = JSRuntimeUtils.add(target, value);
		} else {
			Number l = JSRuntimeUtils.toNumber(target), r = JSRuntimeUtils.toNumber(value);
			switch (node.getKind()) {
				case SUBTRACTION_ASSIGNMENT:
					result = l.doubleValue() - r.doubleValue();
					break;
				case MULTIPLICATION_ASSIGNMENT:
					result = l.doubleValue() * r.doubleValue();
					break;
				case DIVISION_ASSIGNMENT:
					result = l.doubleValue() / r.doubleValue();
					break;
				case REMAINDER_ASSIGNMENT:
					result = l.doubleValue() % r.doubleValue();
					break;
				case EXPONENTIATION_ASSIGNMENT:
					result = Math.pow(l.doubleValue(), r.doubleValue());
					break;
				case LEFT_SHIFT_ASSIGNMENT:
					result = l.intValue() << r.intValue();
					break;
				case RIGHT_SHIFT_ASSIGNMENT:
					result = l.intValue() >> r.intValue();
					break;
				case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
					result = l.intValue() >>> r.intValue();
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		
		((Reference) target).set(result);
		return result;
	}

	@Override
	public Object visitAssignmentPattern(AssignmentPatternTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBinary(BinaryExpressionTree node, RuntimeScope d) {
		Object lhs = node.getLeftOperand().accept(this, d);
		ExpressionTree rhs = node.getRightOperand();
		Kind kind = node.getKind();
		switch (kind) {
			case MEMBER_SELECT:
				return new Reference() {
					final String name = ((IdentifierTree)rhs).getName();
					@Override
					public void set(Object value) {
						JSRuntimeUtils.setMember(lhs, name, value);
					}
					@Override
					public Object get() {
						return JSRuntimeUtils.getMember(lhs, name);
					}
				};
			case ARRAY_ACCESS: {
				final Object key = JSRuntimeUtils.dereference(rhs.accept(this, d));
				switch(JSRuntimeUtils.typeof(key)) {
					case "symbol":
						return new Reference() {
							final Symbol k = (Symbol) key;
							@Override
							public void set(Object value) {
								JSRuntimeUtils.setMember(lhs, k, value);
							}
							@Override
							public Object get() {
								return JSRuntimeUtils.getMember(lhs, k);
							}
						};
					//TODO support number?
					default:
						return new Reference() {
							final String k = JSRuntimeUtils.toString(key);
							@Override
							public void set(Object value) {
								JSRuntimeUtils.setMember(lhs, k, value);
							}
							@Override
							public Object get() {
								return JSRuntimeUtils.getMember(lhs, k);
							}
						};
				}
			}
			case LOGICAL_AND:
			case LOGICAL_OR:
				if (kind == Kind.LOGICAL_AND ^ JSRuntimeUtils.toBoolean(lhs))
					return lhs;
				return rhs.accept(this, d);
			case ADDITION:
				return JSRuntimeUtils.add(lhs,  rhs.accept(this, d));
			default:
				break;
		}
		
		double l = JSRuntimeUtils.toNumber(lhs).doubleValue(), r = JSRuntimeUtils.toNumber(rhs.accept(this, d)).doubleValue();
		switch (node.getKind()) {
			case SUBTRACTION:
				return l - r;
			case MULTIPLICATION:
				return l * r;
			case DIVISION:
				return l / r;
			case REMAINDER:
				return l % r;
			case EXPONENTIATION:
				return Math.pow(l, r);
			default:
				break;
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object visitBlock(BlockTree node, RuntimeScope d) {
		for (StatementTree stmt : node.getStatements())
			stmt.accept(this, d);
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteralTree node, RuntimeScope d) {
		return node.getValue();
	}

	@Override
	public Object visitBreak(BreakTree node, RuntimeScope d) {
		throw new BreakException(node.getLabel().getName());
	}

	@Override
	public Object visitCast(CastTree node, RuntimeScope d) {
		return node.getExpression().accept(this, d);
	}

	@Override
	public Object visitClassDeclaration(ClassDeclarationTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitComment(CommentNode node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitCompilationUnit(CompilationUnitTree node, RuntimeScope d) {
		Object result = null;
		for (StatementTree stmt : node.getSourceElements())
			result = stmt.accept(this, d);
		return result;
	}

	@Override
	public Object visitConditionalExpression(ConditionalExpressionTree node, RuntimeScope d) {
		ExpressionTree branch = JSRuntimeUtils.toBoolean(node.getCondition().accept(this, d)) ? node.getTrueExpression() : node.getFalseExpression();
		return branch.accept(this, d);
	}

	@Override
	public Object visitContinue(ContinueTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDebugger(DebuggerTree node, RuntimeScope d) {
		try {
			this.wait();
		} catch (InterruptedException e) {
			throw new ThrowException(e);
		}
		return null;
	}

	@Override
	public Object visitDoWhileLoop(DoWhileLoopTree node, RuntimeScope d) {
		StatementTree stmt = node.getStatement();
		ExpressionTree expr = node.getCondition();
		do {
			stmt.accept(this, d.pushBlock());
		} while (JSRuntimeUtils.toBoolean(expr.accept(this, d)));
		
		return null;
	}

	@Override
	public Object visitEmptyStatement(EmptyStatementTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitEnumDeclaration(EnumDeclarationTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExport(ExportTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpressionStatement(ExpressionStatementTree node, RuntimeScope d) {
		return node.getExpression().accept(this, d);
	}

	@Override
	public Object visitForEachLoop(ForEachLoopTree node, RuntimeScope d) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitForLoop(ForLoopTree node, RuntimeScope d) {
		RuntimeScope scope = d.pushBlock();
		ExpressionTree condition = node.getCondition();
		ExpressionTree update = node.getUpdate();
		StatementTree stmt = node.getStatement();
		for (node.getInitializer().accept(this, scope); JSRuntimeUtils.toBoolean(condition.accept(this, scope)); update.accept(this, scope)) {
			try {
				stmt.accept(this, scope);
			} catch (BreakException e) {
				//TODO check labels
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitFunctionCall(FunctionCallTree node, RuntimeScope d) {
		Object target = node.getCallee().accept(this, d);
		List<Object> args = node.getArguments().stream()
			.map(argument->argument.accept(this, d))
			.collect(Collectors.toList());
		return JSRuntimeUtils.invoke(target, target, args.toArray());
	}

	@Override
	public Object visitFunctionExpression(FunctionExpressionTree node, RuntimeScope d) {
		Function<Object[], Object> f = (args) -> {
			RuntimeScope scope = d.pushFunction();
			int i = 0;
			for (ParameterTree param : node.getParameters()) {
				PatternTree identifier = param.getName();
				//TODO fix patterns
				String name = ((IdentifierTree) identifier).getName();
				if (i >= args.length) {
					if (param.getInitializer() != null)
						scope.bindings.put(name, param.getInitializer().accept(this, scope));
					else if (param.isOptional())
						scope.bindings.put(name, null);
					else if (param.isRest())
						scope.bindings.put(name, new Object[0]);
					else
						break;
					i++;
					continue;
				}
				if (param.isRest()) {
					Object[] value = new Object[args.length - i];
					System.arraycopy(args, i, value, 0, args.length - i);
					scope.bindings.put(name, value);
					break;
				} else {
					scope.bindings.put(name, args[i++]);
				}
			}
			try {
				node.getBody().accept(this, scope);
			} catch (ReturnException e) {
				return e.value;
			}
			return null;
		};
		
		//TODO: fix per splitting off FunctionDeclarationTree
		if (node.getKind() == Kind.FUNCTION_DECLARATION)
			d.bindings.put(node.getName().getName(), f);
		return f;
	}

	@Override
	public Object visitFunctionType(FunctionTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitIdentifier(IdentifierTree node, RuntimeScope d) {
		return new Reference() {
			@Override
			public void set(Object value) {
				d.bindings.put(node.getName(), value);
			}
			@Override
			public Object get() {
				return d.bindings.get(node.getName());
			}
		};
	}

	@Override
	public Object visitIdentifierType(IdentifierTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitIf(IfTree node, RuntimeScope d) {
		ExpressionTree condition = node.getExpression();
		if (JSRuntimeUtils.toBoolean(condition.accept(this, d))) {
			return node.getThenStatement().accept(this, d);
		} else {
			return node.getElseStatement().accept(this, d);
		}
	}

	@Override
	public Object visitImport(ImportTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIndexType(IndexSignatureTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitInterfaceDeclaration(InterfaceDeclarationTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitIntersectionType(CompositeTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitLabeledStatement(LabeledStatementTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMemberType(MemberTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitNew(NewTree node, RuntimeScope d) {
		Object target = node.getCallee().accept(this, d);
		List<Object> args = node.getArguments().stream()
				.map(arg -> arg.accept(this, d))
				.collect(Collectors.toList());
		return JSRuntimeUtils.invokeNew(target, args.toArray());
	}

	@Override
	public Object visitNull(NullLiteralTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitNumericLiteral(NumericLiteralTree node, RuntimeScope d) {
		return node.getValue();
	}

	@Override
	public Object visitObjectLiteral(ObjectLiteralTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}

	@Override
	public Object visitObjectPattern(ObjectPatternTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParentheses(ParenthesizedTree node, RuntimeScope d) {
		return node.getExpression().accept(this, d);
	}

	@Override
	public Object visitRegExpLiteral(RegExpLiteralTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitReturn(ReturnTree node, RuntimeScope d) {
		ExpressionTree expr = node.getExpression();
		Object value = null;
		if (expr != null)
			value = expr.accept(this, d);
		throw new ReturnException(value);
	}

	@Override
	public Object visitSequence(SequenceExpressionTree node, RuntimeScope d) {
		Object value = null;
		for (ExpressionTree expr : node.getExpressions())
			value = expr.accept(this, d);
		return value;
	}

	@Override
	public Object visitSpecialType(SpecialTypeTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStringLiteral(StringLiteralTree node, RuntimeScope d) {
		return node.getValue();
	}

	@Override
	public Object visitSuper(SuperExpressionTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSwitch(SwitchTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTemplateLiteral(TemplateLiteralTree node, RuntimeScope d) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(node.getQuasis().get(0).getCooked());
		for (int i = 1; i < node.getQuasis().size(); i++) {
			sb.append(JSRuntimeUtils.toString(node.getExpressions().get(i-1).accept(this, d)));
			sb.append(node.getQuasis().get(i).getCooked());
		}
		
		return sb.toString();
	}

	@Override
	public Object visitThis(ThisExpressionTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitThrow(ThrowTree node, RuntimeScope d) {
		Object value = node.getExpression().accept(this, d);
		throw new ThrowException(value);
	}

	@Override
	public Object visitTry(TryTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTupleType(TupleTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitTypeAlias(TypeAliasTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitUnary(UnaryTree node, RuntimeScope d) {
		ExpressionTree expr = node.getExpression();
		switch (node.getKind()) {
			case VOID:
				if (expr != null)
					expr.accept(this, d);
				return null;
			case UNARY_PLUS:
				return JSRuntimeUtils.toNumber(expr.accept(this, d));
			case UNARY_MINUS:
				return -JSRuntimeUtils.toNumber(expr.accept(this, d)).doubleValue();
			case LOGICAL_NOT:
				return !JSRuntimeUtils.toBoolean(expr.accept(this, d));
			case DELETE:
				throw new UnsupportedOperationException();
			default:
				break;
		}
		
		//TODO finish
		
		return null;
	}

	@Override
	public Object visitUnionType(CompositeTypeTree node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitVariableDeclaration(VariableDeclarationTree node, RuntimeScope d) {
		boolean local = node.isScoped();
		for (VariableDeclaratorTree declarator : node.getDeclarations()) {
			ExpressionTree initializer = declarator.getInitializer();
			Object value = null;
			if (initializer != null)
				value = initializer.accept(this, d);
			
			PatternTree identifier = declarator.getName();
			if (identifier.getKind() == Kind.IDENTIFIER) {
				String name = ((IdentifierTree)identifier).getName();
				if (local && d.bindings.containsKey(name))
					throw new IllegalArgumentException("Cannot redefine '" + name + "'");
				if (node.isConst())
					d.bindings.putConst(name, value);
				else if (local)
					d.bindings.putLocal(name, value);
				else
					d.bindings.put(name, value);
			}
		}
		return null;
	}

	@Override
	public Object visitWhileLoop(WhileLoopTree node, RuntimeScope d) {
		ExpressionTree condition = node.getCondition();
		StatementTree stmt = node.getStatement();
		while (JSRuntimeUtils.toBoolean(condition.accept(this, d)))
			stmt.accept(this, d.pushBlock());
		return null;
	}

	@Override
	public Object visitWith(WithTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAwait(AwaitTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitInterfaceType(ObjectTypeTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMemberExpression(MemberExpressionTree node, RuntimeScope d) {
		return this.visitBinary(node, d);
	}

	@Override
	public Object visitLiteralType(LiteralTypeTree<?> node, RuntimeScope d) {
		return null;
	}

	@Override
	public Object visitFunctionDeclaration(FunctionDeclarationTree node, RuntimeScope d) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
