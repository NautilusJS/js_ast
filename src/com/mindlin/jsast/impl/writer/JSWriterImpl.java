package com.mindlin.jsast.impl.writer;

import java.io.IOException;
import java.io.Writer;

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
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.ThrowTree;
import com.mindlin.jsast.tree.Tree;
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
import com.mindlin.jsast.writer.JSWriter;
import com.mindlin.jsast.writer.JSWriterOptions;

public class JSWriterImpl implements JSWriter, TreeVisitor<Void, JSWriterImpl.WriterHelper> {
	protected final JSWriterOptions options;
	public JSWriterImpl(JSWriterOptions options) {
		//TODO clone
		this.options = options;
	}
	
	@Override
	public void write(CompilationUnitTree tree, Writer output) throws IOException {
		try {
			tree.accept(this, new WriterHelper(output));
		} catch (RuntimeException e) {
			throw (IOException) e.getCause();
		}
	}
	
	protected static class WriterHelper extends Writer {
		protected final Writer parent;
		public WriterHelper(Writer parent) {
			this.parent = parent;
		}
		@Override
		public void close() {
			try {
				parent.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void flush() {
			try {
				parent.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		@Deprecated
		public void write(char[] cbuf, int off, int len) {
			try {
				parent.write(cbuf, off, len);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		public Writer append(char c) {
			try {
				parent.append(c);
				return this;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		public Writer append(CharSequence csq, int start, int end) {
			try {
				parent.append(csq, start, end);
				return this;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		public WriterHelper append(CharSequence csq) {
			try {
				parent.append(csq);
				return this;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		@Deprecated
		public void write(char[] cbuf) throws IOException {
			parent.write(cbuf);
		}
		@Override
		@Deprecated
		public void write(int c) throws IOException {
			parent.write(c);
		}
		@Override
		@Deprecated
		public void write(String str, int off, int len) throws IOException {
			parent.write(str, off, len);
		}
		@Override
		@Deprecated
		public void write(String str) {
			try {
				parent.write(str);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}

	protected String stringify(Number value) {
		double dValue = value.doubleValue();
		if (!Double.isFinite(dValue)) {
			if (Double.isNaN(dValue))
				return "NaN";
			if (Double.isInfinite(dValue))
				return (dValue == Double.POSITIVE_INFINITY) ? "Infinity" : "-Infinity";
			throw new IllegalArgumentException("Unknown non-finite value " + value);
		}
		
		String result = value.toString();
		if (result.length() < 3)
			return result;
		
		//TODO finish
		return result;
	}
	
	@Override
	public Void visitAnyType(AnyTypeTree node, WriterHelper d) {
		d.append("any");
		return null;
	}

	@Override
	public Void visitArrayLiteral(ArrayLiteralTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitArrayPattern(ArrayPatternTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitArrayType(ArrayTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitAssignment(AssignmentTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitAssignmentPattern(AssignmentPatternTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitBinary(BinaryTree node, WriterHelper d) {
		node.getLeftOperand().accept(this, d);
		String operator;
		switch (node.getKind()) {
			case ADDITION:
				d.append('+');
				break;
			case SUBTRACTION:
				d.append('-');
			case MULTIPLICATION:
				d.append('*');
				break;
			case DIVISION:
				d.append('/');
				break;
			case REMAINDER:
				d.append('%');
				break;
			case EXPONENTIATION:
				d.append("**");
				break;
			case LEFT_SHIFT:
				d.append("<<");
				break;
			case RIGHT_SHIFT:
				d.append(">>");
				break;
			case UNSIGNED_RIGHT_SHIFT:
				d.append(">>>");
				break;
			case BITWISE_AND:
				d.append('&');
				break;
			case BITWISE_OR:
				d.append('|');
				break;
			case BITWISE_XOR:
				d.append('^');
				break;
			case EQUAL:
				d.append("==");
				break;
			case NOT_EQUAL:
				d.append("!=");
				break;
			case STRICT_EQUAL:
				d.append("===");
				break;
			case STRICT_NOT_EQUAL:
				d.append("!==");
				break;
			case GREATER_THAN:
				d.append('>');
				break;
			case LESS_THAN:
				d.append('<');
				break;
			case GREATER_THAN_EQUAL:
				d.append(">=");
				break;
			case LESS_THAN_EQUAL:
				d.append("<=");
				break;
			case LOGICAL_AND:
				d.append("&&");
				break;
			case LOGICAL_OR:
				d.append("||");
				break;
		}
		node.getRightOperand().accept(this, d);
		return null;
	}

	@Override
	public Void visitBlock(BlockTree node, WriterHelper d) {
		d.append('{');
		for (StatementTree statement : node.getStatements())
			statement.accept(this, d);
		d.append('}');
		return null;
	}

	@Override
	public Void visitBooleanLiteral(BooleanLiteralTree node, WriterHelper d) {
		d.append(node.getValue().toString());
		return null;
	}

	@Override
	public Void visitBreak(BreakTree node, WriterHelper d) {
		if (node.getLabel() == null) {
			d.append("break;");
		}
		return null;
	}

	@Override
	public Void visitClassDeclaration(ClassDeclarationTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitComment(CommentNode node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitCompilationUnit(CompilationUnitTree node, WriterHelper d) {
		for (StatementTree statement : node.getSourceElements())
			statement.accept(this, d);
		return null;
	}

	@Override
	public Void visitComputedPropertyKey(ComputedPropertyKeyTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitConditionalExpression(ConditionalExpressionTree node, WriterHelper d) {
		node.getCondition().accept(this, d);
		d.append('?');
		node.getTrueExpression().accept(this, d);
		d.append(':');
		node.getFalseExpression().accept(this, d);
		return null;
	}

	@Override
	public Void visitContinue(ContinueTree node, WriterHelper d) {
		if (node.getLabel() == null) {
			d.append("continue;");
			return null;
		}
		d.append("continue ").append(node.getLabel()).append(';');
		return null;
	}

	@Override
	public Void visitDebugger(DebuggerTree node, WriterHelper d) {
		d.append("debugger;");
		return null;
	}

	@Override
	public Void visitDoWhileLoop(DoWhileLoopTree node, WriterHelper d) {
		d.append("do{");
		node.getStatement().accept(this, d);
		d.append("}while(");
		node.getCondition().accept(this, d);
		d.append(");");
		return null;
	}

	@Override
	public Void visitEmptyStatement(EmptyStatementTree node, WriterHelper d) {
		return null;
	}

	@Override
	public Void visitEnumDeclaration(EnumDeclarationTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitExport(ExportTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitExpressionStatement(ExpressionStatementTree node, WriterHelper d) {
		node.getExpression().accept(this, d);
		d.append(';');
		return null;
	}

	@Override
	public Void visitForEachLoop(ForEachLoopTree node, WriterHelper d) {
		d.append("for(");
		node.getVariable().accept(this, d);
		if (node.getKind() == Tree.Kind.FOR_IN_LOOP) {
			d.append(" in ");
		} else if (node.getKind() == Tree.Kind.FOR_OF_LOOP) {
			d.append(" of ");
		} else {
			throw new IllegalArgumentException("Can only process for/in and for/of loops");
		}
		node.getExpression().accept(this, d);
		d.append(')');
		node.getStatement().accept(this, d);
		return null;
	}

	@Override
	public Void visitForLoop(ForLoopTree node, WriterHelper d) {
		d.append("for(");
		node.getInitializer().accept(this, d);
		node.getCondition().accept(this, d);
		d.append(';');
		node.getUpdate().accept(this, d);
		d.append(')');
		node.getStatement().accept(this, d);
		return null;
	}

	@Override
	public Void visitFunctionCall(FunctionCallTree node, WriterHelper d) {
		node.getCallee().accept(this, d);
		d.append('(');
		boolean isFirst = true;
		for (ExpressionTree arg : node.getArguments()) {
			if (!isFirst)
				d.append(',');
			isFirst = false;
			arg.accept(this, d);
		}
		d.append(')');
		return null;
	}

	@Override
	public Void visitFunctionExpression(FunctionExpressionTree node, WriterHelper d) {
		if (!node.isArrow()) {
			d.append("function");
			if (node.getName() != null) {
				d.append(' ');
				node.getName().accept(this, d);
			}
		}
		d.append('(');
		boolean isFirst = true;
		for (ParameterTree arg : node.getParameters()) {
			if (!isFirst)
				d.append(',');
			isFirst = false;
			arg.accept(this, d);
		}
		d.append(')');
		if (node.isArrow()) {
			d.append("=>");
			//TODO finish
		}
		node.getBody().accept(this, d);
		return null;
	}

	@Override
	public Void visitFunctionType(FunctionTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitGenericRefType(GenericRefTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitGenericType(GenericTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIdentifier(IdentifierTree node, WriterHelper d) {
		//TODO check if correct
		d.append(node.getName());
		return null;
	}

	@Override
	public Void visitIdentifierType(IdentifierTypeTree node, WriterHelper d) {
		if (node.getGenerics().isEmpty())
			return node.getIdentifier().accept(this, d);
		node.getIdentifier().accept(this, d);
		d.append('<');
		boolean isFirst = true;
		for (TypeTree type : node.getGenerics()) {
			if (!isFirst)
				d.append(',');
			isFirst = false;
			type.accept(this, d);
		}
		d.append('>');
		return null;
	}

	@Override
	public Void visitIf(IfTree node, WriterHelper d) {
		//TODO can probably shorten with JS && and || operators
		d.append("if(");
		node.getExpression().accept(this, d);
		d.append(')');
		node.getThenStatement().accept(this, d);
		if (node.getElseStatement() != null)
			node.getElseStatement().accept(this, d);
		return null;
	}

	@Override
	public Void visitImport(ImportTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIndexType(IndexTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitInterfaceDeclaration(InterfaceDeclarationTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitInterfaceType(InterfaceTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIntersectionType(IntersectionTypeTree node, WriterHelper d) {
		node.getLeftType().accept(this, d);
		d.append('&');
		node.getRightType().accept(this, d);
		return null;
	}

	@Override
	public Void visitLabeledStatement(LabeledStatementTree node, WriterHelper d) {
		node.getName().accept(this, d);
		return null;
	}

	@Override
	public Void visitLiteral(LiteralTree<?> node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitMemberType(MemberTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitMethodDefinition(MethodDefinitionTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitNew(NewTree node, WriterHelper d) {
		d.append("new ");
		node.getCallee().accept(this, d);
		d.append('(');
		boolean isFirst = true;
		for (ExpressionTree expr : node.getArguments()) {
			if (!isFirst)
				d.append(',');
			isFirst = false;
			expr.accept(this, d);
		}
		d.append(')');
		return null;
	}

	@Override
	public Void visitNull(NullLiteralTree node, WriterHelper d) {
		d.append("null");
		return null;
	}

	@Override
	public Void visitNumericLiteral(NumericLiteralTree node, WriterHelper d) {
		Number value = node.getValue();
		//TODO compress hex numbers, if possible
		d.append(value.toString());
		return null;
	}

	@Override
	public Void visitObjectLiteral(ObjectLiteralTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitObjectPattern(ObjectPatternTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitParameter(ParameterTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitParameterType(ParameterTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitParentheses(ParenthesizedTree node, WriterHelper d) {
		d.append('(');
		node.getExpression().accept(this, d);
		d.append(')');
		return null;
	}

	@Override
	public Void visitRegExpLiteral(RegExpLiteralTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitReturn(ReturnTree node, WriterHelper d) {
		if (node.getExpression() == null) {
			d.append("return;");
			return null;
		}
		d.append("return ");
		node.getExpression().accept(this, d);
		d.append(';');
		return null;
	}

	@Override
	public Void visitSequence(SequenceTree node, WriterHelper d) {
		d.append('(');
		boolean isFirst = true;
		for (ExpressionTree expr : node.getExpressions()) {
			if (!isFirst)
				d.append(',');
			isFirst = false;
			expr.accept(this, d);
		}
		d.append(')');
		return null;
	}

	@Override
	public Void visitStringLiteral(StringLiteralTree node, WriterHelper d) {
		String value = node.getValue();
		char quotes = '\'';
		if (value.indexOf('\'') >= 0 && value.indexOf('"') < 0)
			quotes = '"';
		d.append(quotes);
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			boolean escapeUnicode = false;
			if (c == quotes || c == '\\') {
				d.append('\\');
				d.append(c);
				continue;
			}
			switch (c) {
				case '\n':
					d.append("\\n");
					continue;
				case '\b':
					d.append("\\b");
					continue;
				//TODO finish
			}
			d.append(c);
		}
		return null;
	}

	@Override
	public Void visitSuper(SuperExpressionTree node, WriterHelper d) {
		d.append("super");
		return null;
	}

	@Override
	public Void visitSwitch(SwitchTree node, WriterHelper d) {
		d.append("switch(");
		node.getExpression().accept(this, d);
		d.append("){");
		for (CaseTree switchCase : node.getCases())
			switchCase.accept(this, d);
		return null;
	}

	@Override
	public Void visitThis(ThisExpressionTree node, WriterHelper d) {
		d.append("this");
		return null;
	}

	@Override
	public Void visitThrow(ThrowTree node, WriterHelper d) {
		d.append("throw ");
		node.getExpression().accept(this, d);
		return null;
	}

	@Override
	public Void visitTry(TryTree node, WriterHelper d) {
		d.append("try");
		node.getBlock().accept(this, d);
		if (!node.getCatches().isEmpty())
			for (CatchTree ct : node.getCatches())
				ct.accept(this, d);
		if (node.getFinallyBlock() != null) {
			d.append("finally");
			node.getFinallyBlock().accept(this, d);
		}
		return null;
	}

	@Override
	public Void visitTupleType(TupleTypeTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitUnary(UnaryTree node, WriterHelper d) {
		switch (node.getKind()) {
			case UNARY_PLUS:
				d.append('+');
				break;
			case UNARY_MINUS:
				d.append('-');
				break;
			case BITWISE_NOT:
				d.append('~');
				break;
			case LOGICAL_NOT:
				d.append('!');
				break;
			case PREFIX_INCREMENT:
				d.append("++");
				break;
			case PREFIX_DECREMENT:
				d.append("--");
				break;
			//Postfix operator
			case POSTFIX_INCREMENT:
				node.accept(this, d);
				d.append("++");
				return null;
			case POSTFIX_DECREMENT:
				node.accept(this, d);
				d.append("--");
				return null;
		}
		node.accept(this, d);
		return null;
	}

	@Override
	public Void visitUnionType(UnionTypeTree node, WriterHelper d) {
		node.getLeftType().accept(this, d);
		d.append('|');
		node.getRightType().accept(this, d);
		return null;
	}

	@Override
	public Void visitVariableDeclaration(VariableDeclarationTree node, WriterHelper d) {
		if (node.isConst()) {
			d.append("const ");
		} else if (node.isScoped()) {
			d.append("let ");
		} else {
			d.append("var ");
		}
		boolean isFirstDeclaration = true;
		for (VariableDeclaratorTree declarator : node.getDeclarations()) {
			if (!isFirstDeclaration) {
				d.append(',');
			}
			isFirstDeclaration = false;
			declarator.accept(this, d);
		}
		d.append(';');
		return null;
	}

	@Override
	public Void visitVoidType(VoidTypeTree node, WriterHelper d) {
		if (node.isImplicit())
			return null;
		d.append("void");
		return null;
	}

	@Override
	public Void visitWhileLoop(WhileLoopTree node, WriterHelper d) {
		d.append("while(");
		node.getCondition().accept(this, d);
		d.append(")");
		node.getStatement().accept(this, d);
		return null;
	}

	@Override
	public Void visitWith(WithTree node, WriterHelper d) {
		d.append("with(");
		node.getScope().accept(this, d);
		d.append(")");
		node.getStatement().accept(this, d);
		return null;
	}
}
