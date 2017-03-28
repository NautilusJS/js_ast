package com.mindlin.jsast.writer;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import com.mindlin.jsast.impl.util.Characters;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.ArrayPatternTree;
import com.mindlin.jsast.tree.AssignmentPatternTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.CaseTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.ClassPropertyTree.AccessModifier;
import com.mindlin.jsast.tree.ClassPropertyTree.PropertyDeclarationType;
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
import com.mindlin.jsast.tree.ObjectLiteralPropertyTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.ReturnTree;
import com.mindlin.jsast.tree.SequenceTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.ThrowTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.AnyTypeTree;
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
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;
import com.mindlin.jsast.tree.type.VoidTypeTree;

public class JSConciseWriter implements JSWriter, TreeVisitor<Void, PrintWriter> {
	
	@Override
	public void write(CompilationUnitTree src, Writer out) {
		for (Tree tree : src.getSourceElements()) {
		}
	}
	
	void writeTypeMaybe(TypeTree type, PrintWriter out) {
		if (type == null || type.isImplicit())
			return;
		out.write(':');
		type.accept(this, out);
	}

	@Override
	public Void visitAnyType(AnyTypeTree node, PrintWriter out) {
		out.write("any");
		return null;
	}

	@Override
	public Void visitArrayLiteral(ArrayLiteralTree node, PrintWriter out) {
		out.write('[');
		boolean isFirst = true;
		for (ExpressionTree element : node.getElements()) {
			if (!isFirst)
				out.write(',');
			isFirst = false;
			if (element != null)
				element.accept(this, out);
		}
		out.write(']');
		return null;
	}

	@Override
	public Void visitArrayPattern(ArrayPatternTree node, PrintWriter out) {
		out.write('[');
		boolean isFirst = true;
		for (PatternTree element : node.getElements()) {
			if (!isFirst)
				out.write(',');
			isFirst = false;
			if (element != null)
				element.accept(this, out);
		}
		out.write(']');
		return null;
	}

	@Override
	public Void visitArrayType(ArrayTypeTree node, PrintWriter out) {
		node.getBaseType().accept(this, out);
		out.write("[]");
		return null;
	}

	@Override
	public Void visitAssignment(AssignmentTree node, PrintWriter out) {
		node.getLeftOperand().accept(this, out);
		out.write('=');
		node.getRightOperand().accept(this, out);
		return null;
	}

	@Override
	public Void visitAssignmentPattern(AssignmentPatternTree node, PrintWriter out) {
		node.getLeft().accept(this, out);
		out.write('=');
		node.getRight().accept(this, out);
		return null;
	}

	@Override
	public Void visitBinary(BinaryTree node, PrintWriter out) {
		node.getLeftOperand().accept(this, out);
		String operator;
		switch (node.getKind()) {
			case ADDITION:
				operator =  "+";
				break;
			case ADDITION_ASSIGNMENT:
				operator = "+=";
				break;
			case ARRAY_ACCESS:
				out.write('[');
				node.getRightOperand().accept(this, out);
				out.write(']');
				return null;
			case MEMBER_SELECT:
				operator = ".";
				break;
			case BITWISE_AND:
				operator = "&";
				break;
			case BITWISE_AND_ASSIGNMENT:
				operator = "&=";
				break;
			case BITWISE_OR:
				operator = "|";
				break;
			case BITWISE_OR_ASSIGNMENT:
				operator = "|=";
				break;
			case BITWISE_XOR:
				operator = "^";
				break;
			case BITWISE_XOR_ASSIGNMENT:
				operator = "^=";
				break;
			case DIVISION:
				operator = "/";
				break;
			case DIVISION_ASSIGNMENT:
				operator = "/=";
				break;
			case EQUAL:
				operator = "==";
				break;
			case EXPONENTIATION:
				operator = "**";
				break;
			case EXPONENTIATION_ASSIGNMENT:
				operator = "**=";
				break;
			case GREATER_THAN:
				operator = ">";
				break;
			case GREATER_THAN_EQUAL:
				operator = "<";
				break;
			case IN:
				operator = " in ";
				break;
			case INSTANCEOF:
				operator = " instanceof ";
				break;
			case LEFT_SHIFT:
				operator = "<<";
				break;
			case LEFT_SHIFT_ASSIGNMENT:
				operator = "<<=";
				break;
			case LESS_THAN:
				operator = "<";
				break;
			case LESS_THAN_EQUAL:
				operator = "<=";
				break;
			case LOGICAL_AND:
				operator = "&&";
				break;
			case LOGICAL_OR:
				operator = "||";
				break;
			case MULTIPLICATION:
				operator = "*";
				break;
			case MULTIPLICATION_ASSIGNMENT:
				operator = "*=";
				break;
			case NOT_EQUAL:
				operator = "!=";
				break;
			case REMAINDER:
				operator = "%";
				break;
			case REMAINDER_ASSIGNMENT:
				operator = "%=";
				break;
			case RIGHT_SHIFT:
				operator = ">>";
				break;
			case RIGHT_SHIFT_ASSIGNMENT:
				operator = ">>=";
				break;
			case STRICT_EQUAL:
				operator = "===";
				break;
			case STRICT_NOT_EQUAL:
				operator = "!==";
				break;
			case SUBTRACTION:
				operator = "-";
				break;
			case SUBTRACTION_ASSIGNMENT:
				operator = "-=";
				break;
			case UNSIGNED_RIGHT_SHIFT:
				operator = ">>>";
				break;
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
				operator = ">>>=";
				break;
			default:
				throw new IllegalArgumentException();
		}
		out.write(operator);
		node.getRightOperand().accept(this, out);
		return null;
	}

	@Override
	public Void visitBlock(BlockTree node, PrintWriter out) {
		out.write('{');
		for (StatementTree statement : node.getStatements())
			statement.accept(this, out);
		out.write('}');
		return null;
	}

	@Override
	public Void visitBooleanLiteral(BooleanLiteralTree node, PrintWriter out) {
		out.write(node.getValue().toString());
		return null;
	}

	@Override
	public Void visitBreak(BreakTree node, PrintWriter out) {
		out.write("break");
		if (node.getLabel() != null) {
			out.write(' ');
			out.write(node.getLabel());
		}
		out.write(';');
		return null;
	}

	@Override
	public Void visitClassDeclaration(ClassDeclarationTree node, PrintWriter out) {
		out.write("class");
		if (node.getIdentifier() != null) {
			out.write(' ');
			node.getIdentifier().accept(this, out);
		}
		if (node.getSuperType().isPresent()) {
			out.write(" extends ");
			node.getSuperType().get().accept(this, out);
		}
		if (!node.getImplementing().isEmpty()) {
			out.write(" implements ");
			boolean isFirst = true;
			for (TypeTree iface : node.getImplementing()) {
				if (!isFirst)
					out.write(',');
				isFirst = false;
				iface.accept(this, out);
			}
		}
		
		out.write('{');
		for (ClassPropertyTree<?> property : node.getProperties()) {
			//TODO finish
			throw new UnsupportedOperationException();
		}
		out.write('}');
		return null;
	}

	@Override
	public Void visitComment(CommentNode node, PrintWriter out) {
		return null;
	}

	@Override
	public Void visitCompilationUnit(CompilationUnitTree node, PrintWriter out) {
		for (StatementTree stmt : node.getSourceElements())
			stmt.accept(this, out);
		return null;
	}

	@Override
	public Void visitComputedPropertyKey(ComputedPropertyKeyTree node, PrintWriter out) {
		out.write('[');
		node.getExpression().accept(this, out);
		out.write(']');
		return null;
	}

	@Override
	public Void visitConditionalExpression(ConditionalExpressionTree node, PrintWriter out) {
		node.getCondition().accept(this, out);
		out.write('?');
		node.getTrueExpression().accept(this, out);
		out.write(':');
		node.getFalseExpression().accept(this, out);
		return null;
	}

	@Override
	public Void visitContinue(ContinueTree node, PrintWriter out) {
		out.write("continue");
		if (node.getLabel() != null) {
			out.write(' ');
			out.write(node.getLabel());
		}
		out.write(';');
		return null;
	}

	@Override
	public Void visitDebugger(DebuggerTree node, PrintWriter out) {
		out.write("debugger;");
		return null;
	}

	@Override
	public Void visitDoWhileLoop(DoWhileLoopTree node, PrintWriter out) {
		out.write("do ");
		node.getStatement().accept(this, out);
		out.write("while(");
		node.getCondition().accept(this, out);
		out.write(");");
		return null;
	}

	@Override
	public Void visitEmptyStatement(EmptyStatementTree node, PrintWriter out) {
		out.write(';');
		return null;
	}

	@Override
	public Void visitEnumDeclaration(EnumDeclarationTree node, PrintWriter out) {
		out.write("enum ");
		node.getIdentifier().accept(this, out);
		out.write('{');
		// TODO Auto-generated method stub
		out.write('}');
		return null;
	}

	@Override
	public Void visitExport(ExportTree node, PrintWriter out) {
		out.write("export ");
		node.getExpression().accept(this, out);
		out.write(';');
		return null;
	}

	@Override
	public Void visitExpressionStatement(ExpressionStatementTree node, PrintWriter out) {
		node.getExpression().accept(this, out);
		out.write(';');
		return null;
	}

	@Override
	public Void visitForEachLoop(ForEachLoopTree node, PrintWriter out) {
		out.write("for(");
		node.getVariable().accept(this, out);
		out.write(node.getKind() == Kind.FOR_IN_LOOP ? " in " : " of ");
		node.getExpression().accept(this, out);
		out.write(')');
		node.getStatement().accept(this, out);
		return null;
	}

	@Override
	public Void visitForLoop(ForLoopTree node, PrintWriter out) {
		out.write("for(");
		node.getInitializer().accept(this, out);
		node.getCondition().accept(this, out);
		out.write(';');
		node.getUpdate().accept(this, out);
		out.write(')');
		node.getStatement().accept(this, out);
		return null;
	}

	@Override
	public Void visitFunctionCall(FunctionCallTree node, PrintWriter out) {
		node.getCallee().accept(this, out);
		out.write('(');
		boolean isFirst = true;
		for (ExpressionTree argument : node.getArguments()) {
			if(!isFirst)
				out.write(',');
			isFirst = false;
			argument.accept(this, out);
		}
		out.write(')');
		return null;
	}

	@Override
	public Void visitFunctionExpression(FunctionExpressionTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitFunctionType(FunctionTypeTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitGenericRefType(GenericRefTypeTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitGenericType(GenericTypeTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIdentifier(IdentifierTree node, PrintWriter out) {
		out.write(node.getName());
		return null;
	}

	@Override
	public Void visitIdentifierType(IdentifierTypeTree node, PrintWriter out) {
		node.getIdentifier().accept(this, out);
		if (!node.getGenerics().isEmpty()) {
			out.write('<');
			boolean isFirst = true;
			for (TypeTree generic : node.getGenerics()) {
				if (!isFirst)
					out.write(',');
				isFirst = false;
				generic.accept(this, out);
			}
			out.write('>');
		}
		return null;
	}

	@Override
	public Void visitIf(IfTree node, PrintWriter out) {
		out.write("if(");
		node.getExpression().accept(this, out);
		out.write(')');
		StatementTree thenStmt = node.getThenStatement();
		StatementTree elseStmt = node.getElseStatement();
		if (elseStmt != null && thenStmt.getKind() == Kind.IF) {
			out.write('{');
			thenStmt.accept(this, out);
			out.write('}');
		} else {
			thenStmt.accept(this, out);
		}
		if (elseStmt == null || elseStmt.getKind() == Kind.EMPTY_STATEMENT)
			return null;
		out.write("else");
		if (elseStmt.getKind() == Kind.IF)
			out.write(' ');
		elseStmt.accept(this, out);
		return null;
	}

	@Override
	public Void visitImport(ImportTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIndexType(IndexTypeTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitInterfaceDeclaration(InterfaceDeclarationTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitInterfaceType(InterfaceTypeTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIntersectionType(IntersectionTypeTree node, PrintWriter out) {
		if (node.getLeftType().isImplicit())
			return node.getRightType().accept(this, out);
		node.getLeftType().accept(this, out);
		if (!node.getRightType().isImplicit()) {
			out.write('&');
			node.getRightType().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitLabeledStatement(LabeledStatementTree node, PrintWriter out) {
		node.getName().accept(this, out);
		out.write(':');
		StatementTree stmt = node.getStatement();
		node.getStatement().accept(this, out);
		out.write(';');
		return null;
	}

	@Override
	public Void visitLiteral(LiteralTree<?> node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitMemberType(MemberTypeTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitMethodDefinition(MethodDefinitionTree node, PrintWriter out) {
		//Write access modifier; public is implied
		if (node.getAccess() == AccessModifier.PROTECTED)
			out.write("protected ");
		else if (node.getAccess() == AccessModifier.PRIVATE)
			out.write("private ");
		
		if (node.isStatic())
			out.write("static ");

		//Pretty sure that 'readonly' isn't valid
		
		
		switch (node.getDeclarationType()) {
			case CONSTRUCTOR:
				out.write("constructor");
				break;
			case GETTER:
				out.write("get ");
				break;
			case SETTER:
				out.write("set ");
				break;
			case METHOD:
				break;
			default:
				throw new IllegalArgumentException();
		}
		
		if (node.getDeclarationType() != PropertyDeclarationType.CONSTRUCTOR)
			node.getKey().accept(this, out);
		
		//TODO check that this is right
		node.getValue().accept(this, out);
		return null;
	}

	@Override
	public Void visitNew(NewTree node, PrintWriter out) {
		out.write("new ");
		node.getCallee().accept(this, out);
		out.write('(');
		if (node.getArguments() != null && !node.getArguments().isEmpty()) {
			Iterator<ExpressionTree> arguments = node.getArguments().iterator();
			arguments.next().accept(this, out);
			do {
				out.write(',');
				arguments.next().accept(this, out);
			} while (arguments.hasNext());
		}
		out.write(')');
		return null;
	}

	@Override
	public Void visitNull(NullLiteralTree node, PrintWriter out) {
		out.write("null");
		return null;
	}

	@Override
	public Void visitNumericLiteral(NumericLiteralTree node, PrintWriter out) {
		out.write("" + node.getValue().doubleValue());
		return null;
	}

	@Override
	public Void visitObjectLiteral(ObjectLiteralTree node, PrintWriter out) {
		out.write('{');
		boolean isFirst = true;
		for (ObjectLiteralPropertyTree property : node.getProperties()) {
			if (!isFirst)
				out.write(',');
			isFirst = false;
			if (property.getKind() == Kind.METHOD_DEFINITION) {
				MethodDefinitionTree method = (MethodDefinitionTree) property;
				FunctionExpressionTree fn = method.getValue();
				if (fn.isGenerator())
					out.write("* ");
				else if (method.getDeclarationType() == PropertyDeclarationType.GETTER)
					out.write("get ");
				else if (method.getDeclarationType() == PropertyDeclarationType.SETTER)
					out.write("set ");
				method.getKey().accept(this, out);
				out.write('(');
				{
					boolean isFirstParam = true;
					for (ParameterTree param : fn.getParameters()) {
						if (!isFirstParam)
							out.write(',');
						isFirstParam = false;
						param.accept(this, out);
					}
				}
				out.write(')');
				this.writeTypeMaybe(fn.getReturnType(), out);
				fn.getBody().accept(this, out);
			} else {
				ObjectPropertyKeyTree key = property.getKey();
				key.accept(this, out);
				if (!key.equals(property.getValue())) {
					out.write(':');
					property.getValue().accept(this, out);
				}
			}
		}
		out.write('}');
		return null;
	}

	@Override
	public Void visitObjectPattern(ObjectPatternTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitParameter(ParameterTree node, PrintWriter out) {
		node.getIdentifier().accept(this, out);
		if (node.isOptional())
			out.write('?');
		this.writeTypeMaybe(node.getType(), out);
		if (node.getInitializer() != null) {
			out.write('=');
			node.getInitializer().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitParameterType(ParameterTypeTree node, PrintWriter out) {
		node.getName().accept(this, out);
		if (node.isOptional())
			out.append("?");
		if (node.getType() != null && !node.getType().isImplicit()) {
			out.append(':');
			node.getType().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitParentheses(ParenthesizedTree node, PrintWriter out) {
		out.write('(');
		node.getExpression().accept(this, out);
		out.write(')');
		return null;
	}

	@Override
	public Void visitRegExpLiteral(RegExpLiteralTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitReturn(ReturnTree node, PrintWriter out) {
		out.write("return");
		if (node.getExpression() != null) {
			out.write(' ');
			node.getExpression().accept(this, out);
		}
		out.write(';');
		return null;
	}

	@Override
	public Void visitSequence(SequenceTree node, PrintWriter out) {
		Iterator<ExpressionTree> iterator = node.getExpressions().iterator();
		iterator.next().accept(this, out);
		while (iterator.hasNext()) {
			out.write(',');
			iterator.next().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitStringLiteral(StringLiteralTree node, PrintWriter out) {
		String value = node.getValue();
		boolean singleQuotes = value.indexOf('"') >= 0;
		out.write(singleQuotes ? '\'' : '"');
		for (int i = 0, l = value.length(); i < l; i++) {
			char c = value.charAt(i);
			boolean escape = false;
			switch (c) {
				case '\t':
					c = 't';
					break;
				case '\b':
					c = 'b';
					break;
				case '\r':
					c = 'r';
					break;
				case '\n':
					c = 'n';
					break;
				case Characters.VT:
					c = 'v';
					break;
				case '\f':
					c = 'f';
					break;
				case '\0':
					c = '0';
					break;
				case '\\':
					break;
				case '\'':
					if (singleQuotes)
						break;
				default:
					out.write(c);
					continue;
			}
			out.write('\\');
			out.write(c);
		}
		out.write(singleQuotes ? '\'' : '"');
		return null;
	}

	@Override
	public Void visitSuper(SuperExpressionTree node, PrintWriter out) {
		out.write("super");
		return null;
	}

	@Override
	public Void visitSwitch(SwitchTree node, PrintWriter out) {
		out.write("switch(");
		node.getExpression().accept(this, out);
		out.write("){");
		boolean isFirst = true;
		for (CaseTree caseStmt : node.getCases()) {
			if (!isFirst)
				out.write(' ');
			isFirst = false;
			ExpressionTree expr = caseStmt.getExpression();
			if (expr == null) {
				out.write("default:");
			} else {
				out.write("case ");
				expr.accept(this, out);
				out.write(':');
			}
			for (StatementTree stmt : caseStmt.getStatements())
				stmt.accept(this, out);
		}
		out.write('}');
		return null;
	}

	@Override
	public Void visitThis(ThisExpressionTree node, PrintWriter out) {
		out.write("this");
		return null;
	}

	@Override
	public Void visitThrow(ThrowTree node, PrintWriter out) {
		out.write("throw ");
		node.getExpression().accept(this, out);
		out.write(';');
		return null;
	}

	@Override
	public Void visitTry(TryTree node, PrintWriter out) {
		out.write("try");
		node.getBlock().accept(this, out);
		for (CatchTree c : node.getCatches()) {
			out.write("catch(");
			c.getParameter().accept(this, out);
			if (c.getType() != null) {
				out.write(':');
				c.getType().accept(this, out);
			}
			c.getBlock().accept(this, out);
		}
		if (node.getFinallyBlock() != null) {
			out.write("finally");
			node.getFinallyBlock().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitTupleType(TupleTypeTree node, PrintWriter out) {
		out.write('[');
		boolean isFirst = true;
		for (TypeTree type : node.getSlotTypes()) {
			if (!isFirst)
				out.write(',');
			isFirst = false;
			type.accept(this, out);
		}
		out.write(']');
		return null;
	}

	@Override
	public Void visitUnary(UnaryTree node, PrintWriter out) {
		switch (node.getKind()) {
			case PREFIX_INCREMENT:
				out.write("++");
				break;
			case PREFIX_DECREMENT:
				out.write("--");
				break;
			case TYPEOF:
				out.write("typeof ");
				break;
			case VOID:
				out.write("void");
				 if (node.getExpression() == null) {
					 return null;
				 }
				 out.write(' ');
				 break;
			case DELETE:
				out.write("delete ");
				break;
			case UNARY_PLUS:
				out.write('+');
				break;
			case UNARY_MINUS:
				out.write('-');
				break;
			case POSTFIX_INCREMENT:
				node.getExpression().accept(this, out);
				out.write("++");
				return null;
			case POSTFIX_DECREMENT:
				node.getExpression().accept(this, out);
				out.write("--");
				return null;
			default:
				throw new UnsupportedOperationException();
		}
		node.getExpression().accept(this, out);
		return null;
	}

	@Override
	public Void visitUnionType(UnionTypeTree node, PrintWriter out) {
		if (node.getLeftType().isImplicit())
			return node.getRightType().accept(this, out);
		node.getRightType().accept(this, out);
		if (!node.getRightType().isImplicit()) {
			out.write('|');
			node.getRightType().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitVariableDeclaration(VariableDeclarationTree node, PrintWriter out) {
		if (node.isConst())
			out.write("const ");
		else if (node.isScoped())
			out.write("let ");
		else
			out.write("var ");
		
		boolean isFirst = true;
		for (VariableDeclaratorTree declaration : node.getDeclarations()) {
			if (!isFirst)
				out.write(',');
			isFirst = false;
			declaration.getIdentifier().accept(this, out);
			this.writeTypeMaybe(declaration.getType(), out);
			if (declaration.getIntitializer() != null) {
				out.write('=');
				declaration.getIntitializer().accept(this, out);
			}
		}
		out.write(';');
		return null;
	}

	@Override
	public Void visitVoidType(VoidTypeTree node, PrintWriter out) {
		out.write("void");
		return null;
	}

	@Override
	public Void visitWhileLoop(WhileLoopTree node, PrintWriter out) {
		out.write("while(");
		node.getCondition().accept(this, out);
		out.write(')');
		node.getStatement().accept(this, out);
		return null;
	}

	@Override
	public Void visitWith(WithTree node, PrintWriter out) {
		// TODO Auto-generated method stub
		return null;
	}
}
