package com.mindlin.jsast.impl.writer;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
import com.mindlin.jsast.tree.CastTree;
import com.mindlin.jsast.tree.CatchTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.ClassPropertyTree.AccessModifier;
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
import com.mindlin.jsast.tree.ImportSpecifierTree;
import com.mindlin.jsast.tree.ImportTree;
import com.mindlin.jsast.tree.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
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
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TreeVisitor;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeAliasTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
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
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;
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
			WriterHelper wh = new WriterHelper(output);
			tree.accept(this, wh);
		} catch (RuntimeException e) {
			if (e.getCause() != null)
				throw (IOException) e.getCause();
			throw e;
		}
	}
	
	void writeTypeMaybe(TypeTree type, WriterHelper out) {
		if (type == null || type.isImplicit())
			return;
		out.append(':').optionalSpace();
		type.accept(this, out);
	}
	
	void writeMethodDefinition(MethodDefinitionTree method, WriterHelper out) {
		//Write access modifier; public is implied
		if (method.getAccess() == AccessModifier.PROTECTED)
			out.append("protected").append(options.space);
		else if (method.getAccess() == AccessModifier.PRIVATE)
			out.append("private").append(options.space);
		
		if (method.isStatic())
			out.append("static").append(options.space);
		
		if (method.isAbstract())
			out.append("abstract").append(options.space);
		
		//Pretty sure that 'readonly' isn't a valid modifier here
		
		
		switch (method.getDeclarationType()) {
			case GETTER:
				out.append("get").append(options.space);
				break;
			case SETTER:
				out.append("set").append(options.space);
				break;
			case GENERATOR:
				out.append("*").append(options.space);
				break;
			case ASYNC_METHOD:
				out.append("async").append(options.space);
			case CONSTRUCTOR:
			case METHOD:
				break;
			default:
				throw new IllegalArgumentException("Unknown declaration type: " + method.getDeclarationType());
		}
		
		
		FunctionExpressionTree fn = method.getValue();
		
		method.getKey().accept(this, out);
		
		FunctionTypeTree type = (FunctionTypeTree) method.getType();
		
		out.append('(');
		writeList(type.getParameters(), out);
		out.append(')');
		
		this.writeTypeMaybe(type.getReturnType(), out);
		
		if (!method.isAbstract()) {
			fn.getBody().accept(this, out);
			out.finishStatement(false);
		} else {
			out.finishStatement(true);
		}
	}
	
	protected class WriterHelper implements Closeable {
		protected final Writer parent;
		protected int indentLevel = options.baseIndent;
		private String indent = "";
		private int newlineBacklog = 0;
		protected Stack<WriterHelperContext> context = new Stack<>();
		
		public WriterHelper(Writer parent) {
			this.parent = parent;
			context.push(new WriterHelperContext());
		}
		
		public void beginRegion(long srcStart) {
			//TODO impl
		}
		
		public void endRegion(long srcEnd) {
			
		}
		
		public void pushIndent() {
			indentLevel++;
			indent += options.indentStyle;
		}
		
		public void popIndent() {
			if (--indentLevel < options.baseIndent) {
				indentLevel++;
				return;
			}
			indent = indent.substring(0, indent.length() - options.indentStyle.length());
		}
		
		public void pushContext() {
			this.context.push(new WriterHelperContext(this.context.peek()));
		}
		
		public void popContext() {
			this.context.pop();
		}
		
		public void doFinishWithNewline(boolean enableNewline) {
			this.context.peek().noNewline = !enableNewline;
		}
		
		public void finishStatement(boolean semicolon) {
			if (this.context.peek().noNewline)
				return;
			if (semicolon)
				this.append(';');
			this.newline();
		}
		
		@Override
		public void close() {
			try {
				parent.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void flush() {
			flushNewlines();
			try {
				parent.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public WriterHelper append(long srcStart, long srcEnd, String s) {
			flushNewlines();
			try {
				parent.append(s);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public WriterHelper append(char c) {
			flushNewlines();
			try {
				parent.append(c);
				return this;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public WriterHelper append(CharSequence csq, int start, int end) {
			flushNewlines();
			return doAppend(csq, start, end);
		}
		
		public WriterHelper append(CharSequence csq) {
			flushNewlines();
			return doAppend(csq, 0, csq.length());
		}
		
		public WriterHelper appendIsolated(CharSequence csq) {
			append(options.space);
			append(csq);
			append(options.space);
			return this;
		}
		
		protected void flushNewlines() {
			if (this.newlineBacklog == 0)
				return;
			String newline = "\n" + indent;
			while (this.newlineBacklog > 0) {
				this.newlineBacklog--;
				doAppend(newline, 0, newline.length());
			}
		}
		
		protected WriterHelper doAppend(CharSequence csq, int start, int end) {
			try {
				parent.append(csq, start, end);
				return this;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public WriterHelper newline() {
			this.newlineBacklog++;
//			this.append("\n" + indent);
			return this;
		}
		
		public WriterHelper optionalSpace() {
			append(options.space);
			return this;
		}
		
		protected class WriterHelperContext {
			boolean noNewline = false;
			
			protected WriterHelperContext() {
				
			}
			
			protected WriterHelperContext(WriterHelperContext parent) {
				this.noNewline = parent.noNewline;
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
	
	void writeInterfaceProperty(InterfacePropertyTree property, WriterHelper out) {
		if (property.isReadonly())
			out.append("readonly").append(options.space);
		
		if (property.getKey() == null) {
			//Is function interface
			out.append('(');
			FunctionTypeTree type = (FunctionTypeTree) property.getType();
			writeList(type.getParameters(), out);
			out.append(')').append(':').optionalSpace();
			type.getReturnType().accept(this, out);
		} else if (property.getType().getKind() == Kind.INDEX_TYPE) {
			//Is index type (format: `[name: KeyType]: ValueType`)
			out.append('[');
			property.getKey().accept(this, out);

			IndexTypeTree type = (IndexTypeTree) property.getType();
			out.append(':').optionalSpace();
			type.getIndexType().accept(this, out);
			
			out.append("]:").optionalSpace();
			type.getReturnType().accept(this, out);
		} else {
			property.getKey().accept(this, out);
			if (property.isOptional())
				out.append('?');
			
			writeTypeMaybe(property.getType(), out);
		}
	}
	
	protected void writeInterfaceBody(List<? extends InterfacePropertyTree> properties, WriterHelper out, boolean isType) {
		if (isType && properties.size() == 1) {
			writeInterfaceProperty(properties.get(0), out);
			return;
		}
		
		for (InterfacePropertyTree property : properties) {
			writeInterfaceProperty(property, out);
			out.append(';');
			if (!isType)
				out.newline();
		}
	}
	
	/**
	 * Write a comma-separated list with the given values
	 * @param values
	 * @param out
	 */
	public void writeList(List<? extends Tree> values, WriterHelper out) {
		boolean isFirst = true;
		for (Tree value : values) {
			if (!isFirst)
				out.append(',').optionalSpace();
			else
				isFirst = false;
			
			if (value != null)
				value.accept(this, out);
		}
	}
	
	public <T> void writeList(List<T> values, WriterHelper out, BiConsumer<T, WriterHelper> serializer, Consumer<WriterHelper> delimiter) {
		boolean isFirst = true;
		for (T value : values) {
			if (!isFirst)
				delimiter.accept(out);
			else
				isFirst = false;
			
			serializer.accept(value, out);
		}
	}
	
	@Override
	public Void visitSpecialType(SpecialTypeTree node, WriterHelper out) {
		out.beginRegion(node.getStart());
		String name;
		switch (node.getType()) {
			case ANY:
				name = "any";
				break;
			case BOOLEAN:
				name = "boolean";
				break;
			case NEVER:
				name = "never";
				break;
			case NULL:
				name = "null";
				break;
			case NUMBER:
				name = "number";
				break;
			case STRING:
				name = "string";
				break;
			case UNDEFINED:
				name = "undefined";
				break;
			case VOID:
				name = "void";
				break;
			default:
				throw new IllegalArgumentException("Unknown type: " + node.getType());
		}
		out.append(name);
		out.endRegion(node.getEnd());
		return null;
	}

	@Override
	public Void visitArrayLiteral(ArrayLiteralTree node, WriterHelper out) {
		out.beginRegion(node.getStart());
		out.append('[');
		writeList(node.getElements(), out);
		out.append(']');
		out.endRegion(node.getEnd());
		return null;
	}

	@Override
	public Void visitArrayPattern(ArrayPatternTree node, WriterHelper out) {
		out.append('[');
		writeList(node.getElements(), out);
		out.append(']');
		return null;
	}

	@Override
	public Void visitArrayType(ArrayTypeTree node, WriterHelper out) {
		node.getBaseType().accept(this, out);
		out.append("[]");
		return null;
	}

	@Override
	public Void visitAssignment(AssignmentTree node, WriterHelper out) {
		node.getLeftOperand().accept(this, out);
		out.optionalSpace().append('=').optionalSpace();
		node.getRightOperand().accept(this, out);
		return null;
	}

	@Override
	public Void visitAssignmentPattern(AssignmentPatternTree node, WriterHelper out) {
		node.getLeft().accept(this, out);
		out.append('=');
		node.getRight().accept(this, out);
		return null;
	}

	@Override
	public Void visitBinary(BinaryTree node, WriterHelper out) {
		node.getLeftOperand().accept(this, out);
		String operator;
		boolean optionalSpace = true;
		switch (node.getKind()) {
			case ADDITION:
				operator =  "+";
				break;
			case ADDITION_ASSIGNMENT:
				operator = "+=";
				break;
			case ARRAY_ACCESS:
				out.append('[');
				node.getRightOperand().accept(this, out);
				out.append(']');
				return null;
			case MEMBER_SELECT:
				operator = ".";
				optionalSpace = false;
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
				optionalSpace = false;
				break;
			case INSTANCEOF:
				operator = " instanceof ";
				optionalSpace = false;
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
		if (optionalSpace)
			out.optionalSpace().append(operator).optionalSpace();
		else
			out.append(operator);
		node.getRightOperand().accept(this, out);
		return null;
	}

	@Override
	public Void visitBlock(BlockTree node, WriterHelper out) {
		out.append('{');
		out.pushIndent();
		out.newline();
		
		out.pushContext();
		out.doFinishWithNewline(true);
		for (StatementTree statement : node.getStatements())
			statement.accept(this, out);
		out.popContext();
		
		out.popIndent();
		out.append('}');
		out.finishStatement(false);
		
		return null;
	}

	@Override
	public Void visitBooleanLiteral(BooleanLiteralTree node, WriterHelper out) {
		out.append(node.getValue().toString());
		return null;
	}

	@Override
	public Void visitBreak(BreakTree node, WriterHelper out) {
		if (node.getLabel() == null)
			out.append("break");
		else
			out.append("break " + node.getLabel());
		out.finishStatement(true);
		return null;
	}
	
	@Override
	public Void visitCast(CastTree node, WriterHelper out) {
		ExpressionTree expression = node.getExpression();
		TypeTree type = node.getType();
		
		expression.accept(this, out);
		
		if (type.isImplicit())
			return null;
		
		out.appendIsolated("as");
		type.accept(this, out);
		return null;
	}

	@Override
	public Void visitClassDeclaration(ClassDeclarationTree node, WriterHelper out) {
		out.append("class");
		if (node.getIdentifier() != null) {
			out.append(options.space);
			node.getIdentifier().accept(this, out);
		}
		if (node.getSuperType().isPresent()) {
			out.appendIsolated("extends");
			node.getSuperType().get().accept(this, out);
		}
		if (!node.getImplementing().isEmpty()) {
			out.appendIsolated("implements");
			
			writeList(node.getImplementing(), out);
		}
		
		out.optionalSpace().append('{');
		out.newline().pushIndent();
		for (ClassPropertyTree<?> property : node.getProperties()) {
			if (property.getKind() == Tree.Kind.METHOD_DEFINITION)
				this.writeMethodDefinition((MethodDefinitionTree) property, out);
			else {
				//TODO finish
			}
		}
		out.popIndent();
		out.newline();
		out.append('}');
		return null;
	}

	@Override
	public Void visitComment(CommentNode node, WriterHelper out) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Void visitCompilationUnit(CompilationUnitTree node, WriterHelper out) {
		for (StatementTree statement : node.getSourceElements())
			statement.accept(this, out);
		return null;
	}

	@Override
	public Void visitComputedPropertyKey(ComputedPropertyKeyTree node, WriterHelper out) {
		out.append('[');
		node.getExpression().accept(this, out);
		out.append(']');
		return null;
	}

	@Override
	public Void visitConditionalExpression(ConditionalExpressionTree node, WriterHelper out) {
		node.getCondition().accept(this, out);
		out.optionalSpace().append('?').optionalSpace();
		node.getTrueExpression().accept(this, out);
		out.optionalSpace().append(':').optionalSpace();
		node.getFalseExpression().accept(this, out);
		return null;
	}

	@Override
	public Void visitContinue(ContinueTree node, WriterHelper out) {
		out.append("continue");
		if (node.getLabel() != null)
			out.append(options.space).append(node.getLabel());
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitDebugger(DebuggerTree node, WriterHelper out) {
		out.append("debugger").finishStatement(true);
		return null;
	}

	@Override
	public Void visitDoWhileLoop(DoWhileLoopTree node, WriterHelper out) {
		out.append("do");
		
		StatementTree statement = node.getStatement();
		
		out.pushContext();
		if (statement.getKind() == Kind.BLOCK) {
			out.optionalSpace();
			out.doFinishWithNewline(false);
		} else {
			out.newline();
			out.pushIndent();
		}
		
		statement.accept(this, out);
		
		if (statement.getKind() == Kind.BLOCK) {
			out.optionalSpace();
		} else {
			out.popIndent();
			out.newline();
		}
		out.popContext();
		
		out.append("while(");
		node.getCondition().accept(this, out);
		out.append(")").finishStatement(true);
		return null;
	}

	@Override
	public Void visitEmptyStatement(EmptyStatementTree node, WriterHelper out) {
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitEnumDeclaration(EnumDeclarationTree node, WriterHelper out) {
		out.append("enum").append(options.space);
		node.getIdentifier().accept(this, out);
		out.append('{');
		out.pushIndent();
		out.newline();
		
		writeList(node.getValues(), out);
		
		// TODO Auto-generated method stub
		out.popIndent();
		out.newline();
		out.append('}');
		return null;
	}

	@Override
	public Void visitExport(ExportTree node, WriterHelper out) {
		out.append("export").append(options.space);
		
		out.pushContext();
		out.doFinishWithNewline(false);
		node.getExpression().accept(this, out);
		out.popContext();
		
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitExpressionStatement(ExpressionStatementTree node, WriterHelper out) {
		node.getExpression().accept(this, out);
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitForEachLoop(ForEachLoopTree node, WriterHelper out) {
		out.append("for(");
		
		out.pushContext();
		out.doFinishWithNewline(false);
		node.getVariable().accept(this, out);
		out.popContext();
		
		if (node.getKind() == Tree.Kind.FOR_IN_LOOP)
			out.appendIsolated("in");
		else if (node.getKind() == Tree.Kind.FOR_OF_LOOP)
			out.appendIsolated("of");
		else
			throw new IllegalArgumentException("Can only process for/in and for/of loops");
		
		node.getExpression().accept(this, out);
		out.append(')');
		
		StatementTree statement = node.getStatement();
		if (statement.getKind() == Kind.BLOCK)//Optional space between ')' in for header and '{' in block
			out.optionalSpace();
		statement.accept(this, out);
		return null;
	}

	@Override
	public Void visitForLoop(ForLoopTree node, WriterHelper out) {
		out.append("for(");
		
		StatementTree initializer = node.getInitializer();
		if (initializer != null) {
			out.pushContext();
			out.doFinishWithNewline(false);
			initializer.accept(this, out);
			out.popContext();
		}
		out.append(';').optionalSpace();
		
		ExpressionTree condition = node.getCondition();
		if (condition != null)
			condition.accept(this, out);
		out.append(';').optionalSpace();
		
		ExpressionTree update = node.getUpdate();
		if (update != null)
			update.accept(this, out);
		out.append(')');
		
		StatementTree statement = node.getStatement();
		if (statement.getKind() == Kind.BLOCK)//Optional space between ')' in for header and '{' in block
			out.optionalSpace();
		statement.accept(this, out);
		
		return null;
	}

	@Override
	public Void visitFunctionCall(FunctionCallTree node, WriterHelper out) {
		node.getCallee().accept(this, out);
		out.append('(');
		writeList(node.getArguments(), out);
		out.append(')');
		return null;
	}
	
	protected void writeFunctionParameter(ParameterTree param, WriterHelper out) {
		if (param.isRest())
			out.append("...");
		param.getIdentifier().accept(this, out);
		
		if (param.isOptional())
			out.append('?');
		this.writeTypeMaybe(param.getType(), out);
		if (param.getInitializer() != null) {
			out.optionalSpace().append('=').optionalSpace();
			param.getInitializer().accept(this, out);
		}
	}

	protected void writeFunctionParameters(FunctionExpressionTree node, WriterHelper out) {
		List<ParameterTree> params = node.getParameters();
		if (node.isArrow() && params.size() == 1) {
			ParameterTree param0 = params.get(0);
			if (!param0.isOptional() && param0.getInitializer() == null && !param0.isRest() && (param0.getType() == null || param0.getType().isImplicit())) {
				//We don't have to write parentheses
				this.writeFunctionParameter(param0, out);
				return;
			}
		}
		out.append('(');
		writeList(params, out, this::writeFunctionParameter, wh->wh.append(',').optionalSpace());
		out.append(')');
	}
	
	@Override
	public Void visitFunctionExpression(FunctionExpressionTree node, WriterHelper out) {
		if (!node.isArrow()) {
			out.append("function");
			if (node.getName() != null) {
				out.append(options.space);
				node.getName().accept(this, out);
			}
		}
		
		//Write parameters
		this.writeFunctionParameters(node, out);
		
		this.writeTypeMaybe(node.getReturnType(), out);
		
		out.optionalSpace();
		
		if (node.isArrow()) {
			out.append("=>");
			out.optionalSpace();
			if (node.getBody().getKind() == Kind.RETURN)
				((ReturnTree) node.getBody()).getExpression().accept(this, out);
			else
				node.getBody().accept(this, out);
		} else {
			out.pushContext();
			out.doFinishWithNewline(false);
			node.getBody().accept(this, out);
			out.popContext();
		}
		return null;
	}

	@Override
	public Void visitFunctionType(FunctionTypeTree node, WriterHelper out) {
		List<ParameterTree> parameters = node.getParameters();
		List<GenericTypeTree> generics = node.getGenerics();
		TypeTree returnType = node.getReturnType();
		
		if (generics == null || generics.isEmpty()) {
			if (parameters.size() == 1) {
				parameters.get(0).accept(this, out);
			} else {
				out.append('(');
				writeList(parameters, out);
				out.append(')');
			}
			
			out.optionalSpace();
			out.append("=>");
			out.optionalSpace();
			returnType.accept(this, out);
		} else {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}
		
		return null;
	}

	@Override
	public Void visitGenericRefType(GenericRefTypeTree node, WriterHelper out) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Void visitGenericType(GenericTypeTree node, WriterHelper out) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Void visitIdentifier(IdentifierTree node, WriterHelper out) {
		//TODO check if correct
		out.append(node.getName());
		return null;
	}

	@Override
	public Void visitIdentifierType(IdentifierTypeTree node, WriterHelper out) {
		node.getIdentifier().accept(this, out);
		if (node.getGenerics().isEmpty())
			return null;
		
		out.append('<');
		writeList(node.getGenerics(), out);
		out.append('>');
		return null;
	}

	@Override
	public Void visitIf(IfTree node, WriterHelper out) {
		out.append("if(");
		node.getExpression().accept(this, out);
		out.append(')');
		
		StatementTree thenStmt = node.getThenStatement();
		StatementTree elseStmt = node.getElseStatement();
		
		if (elseStmt != null && elseStmt.getKind() == Kind.EMPTY_STATEMENT)
			elseStmt = null;
		
		if (thenStmt.getKind() == Kind.BLOCK) {
			out.optionalSpace();

			out.pushContext();
			if (elseStmt != null)
				out.doFinishWithNewline(false);
			
			thenStmt.accept(this, out);
			
			out.popContext();
			
			if (elseStmt != null)
				out.optionalSpace();
		} else {
			thenStmt.accept(this, out);
		}
		
		
		if (elseStmt == null || elseStmt.getKind() == Kind.EMPTY_STATEMENT)
			return null;
		
		out.append("else");
		
		if (elseStmt.getKind() == Kind.IF)
			out.append(options.space);
		else if (elseStmt.getKind() == Kind.BLOCK)
			out.optionalSpace();
		
		elseStmt.accept(this, out);
		return null;
	}

	@Override
	public Void visitImport(ImportTree node, WriterHelper out) {
		List<ImportSpecifierTree> specifiers = node.getSpecifiers();
		StringLiteralTree source = node.getSource();
		
		out.append("import").append(options.space);
		
		if (!specifiers.isEmpty()) {
			Iterator<ImportSpecifierTree> si = specifiers.iterator();
			ImportSpecifierTree specifier = si.next();
			
			if (specifier.isDefault()) {
				specifier.getImported().accept(this, out);
				if (si.hasNext()) {
					out.append(',').optionalSpace();
					specifier = si.next();
				} else {
					specifier = null;
				}
			}
			
			if (specifier.getImported().getName().equals("*")) {
				out.append('*');
				out.appendIsolated("as");
				specifier.getAlias().accept(this, out);
				//There shouldn't be any more specifiers
				if (si.hasNext())
					throw new IllegalStateException();
			} else if (specifier != null) {
				out.append('{');
				do {
					IdentifierTree imported = specifier.getImported();
					IdentifierTree alias = specifier.getAlias();
					imported.accept(this, out);
					if (imported != alias && alias != null) {
						out.appendIsolated("as");
						alias.accept(this, out);
					}
					if (si.hasNext())
						out.append(',').optionalSpace();
				} while (si.hasNext());
				out.append('}');
			}
			
			out.appendIsolated("from");
		}
		
		source.accept(this, out);
		
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitIndexType(IndexTypeTree node, WriterHelper out) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Void visitInterfaceDeclaration(InterfaceDeclarationTree node, WriterHelper out) {
		
		out.append("interface");
		if (node.getIdentifier() != null) {
			out.append(options.space);
			node.getIdentifier().accept(this, out);
		}
		
		//TODO what if no identifier but yes supertypes
		if (node.getSupertypes() != null && !node.getSupertypes().isEmpty()) {
			out.appendIsolated("extends");
			writeList(node.getSupertypes(), out);
		}
		out.optionalSpace().append('{');
		out.pushIndent();
		out.newline();
		
		this.writeInterfaceBody(node.getProperties(), out, false);
		
		out.popIndent();
		out.append('}');
		out.finishStatement(false);
		return null;
	}

	@Override
	public Void visitInterfaceType(InterfaceTypeTree node, WriterHelper out) {
		out.append('{');
		writeInterfaceBody(node.getProperties(), out, true);
		out.append('}');
		return null;
	}

	@Override
	public Void visitIntersectionType(IntersectionTypeTree node, WriterHelper out) {
		node.getLeftType().accept(this, out);
		out.optionalSpace();
		out.append('&');
		out.optionalSpace();
		node.getRightType().accept(this, out);
		return null;
	}

	@Override
	public Void visitLabeledStatement(LabeledStatementTree node, WriterHelper out) {
		node.getName().accept(this, out);
		out.append(':');
		StatementTree stmt = node.getStatement();
		stmt.accept(this, out);
		return null;
	}

	@Override
	public Void visitMemberType(MemberTypeTree node, WriterHelper out) {
		node.getBaseType().accept(this, out);
		out.append('.');
		node.getName().accept(this, out);
		return null;
	}

	@Override
	public Void visitNew(NewTree node, WriterHelper out) {
		out.append("new").append(options.space);
		node.getCallee().accept(this, out);
		out.append('(');
		writeList(node.getArguments(), out);
		out.append(')');
		return null;
	}

	@Override
	public Void visitNull(NullLiteralTree node, WriterHelper out) {
		out.append("null");
		return null;
	}

	@Override
	public Void visitNumericLiteral(NumericLiteralTree node, WriterHelper out) {
		Number value = node.getValue();
		//TODO compress hex numbers, if possible
		out.append(value.toString());
		return null;
	}

	@Override
	public Void visitObjectLiteral(ObjectLiteralTree node, WriterHelper out) {
		out.append('{');
		boolean isFirst = true;
		for (ObjectLiteralPropertyTree property : node.getProperties()) {
			if (!isFirst)
				out.append(',').optionalSpace();
			isFirst = false;
			if (property.getKind() == Kind.METHOD_DEFINITION) {
				this.writeMethodDefinition((MethodDefinitionTree) property, out);
			} else {
				ObjectPropertyKeyTree key = property.getKey();
				key.accept(this, out);
				if (!key.equals(property.getValue())) {
					out.append(':').optionalSpace();
					property.getValue().accept(this, out);
				}
			}
		}
		out.append('}');
		return null;
	}

	@Override
	public Void visitObjectPattern(ObjectPatternTree node, WriterHelper out) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Void visitParameterType(ParameterTypeTree node, WriterHelper out) {
		node.getName().accept(this, out);
		if (node.isOptional())
			out.append('?');
		writeTypeMaybe(node.getType(), out);
		return null;
	}

	@Override
	public Void visitParentheses(ParenthesizedTree node, WriterHelper out) {
		out.append('(');
		node.getExpression().accept(this, out);
		out.append(')');
		return null;
	}

	@Override
	public Void visitRegExpLiteral(RegExpLiteralTree node, WriterHelper out) {
		System.out.println(Arrays.toString(node.getValue()));
		//TODO escape correctly
		out.append('/');
		out.append(node.getValue()[0]);
		out.append('/');
		out.append(node.getValue()[1]);
		return null;
	}

	@Override
	public Void visitReturn(ReturnTree node, WriterHelper out) {
		out.append("return");
		if (node.getExpression() != null) {
			out.append(options.space);
			node.getExpression().accept(this, out);
		}
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitSequence(SequenceTree node, WriterHelper out) {
		writeList(node.getExpressions(), out);
		return null;
	}

	@Override
	public Void visitStringLiteral(StringLiteralTree node, WriterHelper out) {
		String value = node.getValue();
		boolean singleQuotes = value.indexOf('"') >= 0;
		out.append(singleQuotes ? '\'' : '"');
		for (int i = 0, l = value.length(); i < l; i++) {
			char c = value.charAt(i);
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
					out.append(c);
					continue;
			}
			out.append('\\');
			out.append(c);
		}
		out.append(singleQuotes ? '\'' : '"');
		return null;
	}

	@Override
	public Void visitSuper(SuperExpressionTree node, WriterHelper out) {
		out.append("super");
		return null;
	}

	@Override
	public Void visitSwitch(SwitchTree node, WriterHelper out) {
		out.append("switch(");
		node.getExpression().accept(this, out);
		out.append(')').optionalSpace().append('{');
		
		boolean isFirst = true;
		for (CaseTree caseStmt : node.getCases()) {
			if (!isFirst)
				out.append(options.space);
			isFirst = false;
			ExpressionTree expr = caseStmt.getExpression();
			if (expr == null) {
				out.append("default:");
			} else {
				out.append("case").append(options.space);
				expr.accept(this, out);
				out.append(':');
			}
			out.newline();
			for (StatementTree stmt : caseStmt.getStatements())
				stmt.accept(this, out);
		}
		out.append('}');
		out.finishStatement(false);
		return null;
	}

	@Override
	public Void visitThis(ThisExpressionTree node, WriterHelper out) {
		out.append("this");
		return null;
	}

	@Override
	public Void visitThrow(ThrowTree node, WriterHelper out) {
		out.append("throw");
		out.append(options.space);
		node.getExpression().accept(this, out);
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitTry(TryTree node, WriterHelper out) {
		out.append("try").optionalSpace();
		node.getBlock().accept(this, out);
		if (!node.getCatches().isEmpty())
			for (CatchTree ct : node.getCatches()) {
				out.append("catch(");
				ct.getParameter().accept(this, out);
				writeTypeMaybe(ct.getType(), out);
				out.append(')');
				
				ct.getBlock().accept(this, out);
			}
		if (node.getFinallyBlock() != null) {
			out.append("finally");
			node.getFinallyBlock().accept(this, out);
		}
		return null;
	}

	@Override
	public Void visitTupleType(TupleTypeTree node, WriterHelper out) {
		out.append('[');
		writeList(node.getSlotTypes(), out);
		out.append(']');
		return null;
	}

	@Override
	public Void visitUnary(UnaryTree node, WriterHelper out) {
		String operator;
		boolean spaceAfter = false;
		switch (node.getKind()) {
			case PREFIX_INCREMENT:
				operator = "++";
				break;
			case PREFIX_DECREMENT:
				operator = "--";
				break;
			case TYPEOF:
				operator = "typeof";
				spaceAfter = true;
				break;
			case VOID:
				 if (node.getExpression() == null) {
					 out.append("void");
					 return null;
				 }
				 operator = "void";
				 spaceAfter = true;
				 break;
			case DELETE:
				operator = "delete";
				spaceAfter = true;
				break;
			case UNARY_PLUS:
				operator = "+";
				break;
			case UNARY_MINUS:
				operator = "-";
				break;
			case LOGICAL_NOT:
				operator = "!";
				break;
			case BITWISE_NOT:
				operator = "~";
				break;
			case POSTFIX_INCREMENT:
				node.getExpression().accept(this, out);
				out.append("++");
				return null;
			case POSTFIX_DECREMENT:
				node.getExpression().accept(this, out);
				out.append("--");
				return null;
			case YIELD:
				operator = "yield";
				spaceAfter = true;
				break;
			case YIELD_GENERATOR:
				operator = "yield*";
				spaceAfter = true;
				break;
			default:
				throw new IllegalArgumentException("Unknown operator type: " + node.getKind());
		}
		out.append(operator);
		if (spaceAfter)
			out.append(options.space);
		node.getExpression().accept(this, out);
		return null;
	}

	@Override
	public Void visitUnionType(UnionTypeTree node, WriterHelper out) {
		node.getLeftType().accept(this, out);
		out.optionalSpace();
		out.append('|');
		out.optionalSpace();
		node.getRightType().accept(this, out);
		return null;
	}

	@Override
	public Void visitVariableDeclaration(VariableDeclarationTree node, WriterHelper out) {
		if (node.isConst())
			out.append("const");
		else if (node.isScoped())
			out.append("let");
		else
			out.append("var");
		
		out.append(options.space);
		
		boolean isFirstDeclaration = true;
		for (VariableDeclaratorTree declarator : node.getDeclarations()) {
			if (!isFirstDeclaration)
				out.append(',').optionalSpace();
			isFirstDeclaration = false;
			
			declarator.getIdentifier().accept(this, out);
			
			writeTypeMaybe(declarator.getType(), out);
			
			ExpressionTree initializer = declarator.getIntitializer();
			if (initializer != null) {
				out.optionalSpace().append('=').optionalSpace();
				initializer.accept(this, out);
			}
		}
		out.finishStatement(true);
		return null;
	}

	@Override
	public Void visitWhileLoop(WhileLoopTree node, WriterHelper out) {
		out.append("while(");
		node.getCondition().accept(this, out);
		out.append(")");
		StatementTree statement = node.getStatement();
		if (statement.getKind() == Kind.BLOCK) {
			out.optionalSpace();
			statement.accept(this, out);
		} else {
			out.pushIndent();
			statement.accept(this, out);
			out.popIndent();
		}
		return null;
	}

	@Override
	public Void visitWith(WithTree node, WriterHelper out) {
		out.append("with(");
		node.getScope().accept(this, out);
		out.append(")");
		node.getStatement().accept(this, out);
		return null;
	}

	@Override
	public Void visitTemplateLiteral(TemplateLiteralTree node, WriterHelper d) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Void visitTypeAlias(TypeAliasTree node, WriterHelper out) {
		out.append("type").append(options.space);
		node.getAlias().accept(this, out);
		if (!node.getGenericParameters().isEmpty()) {
			out.append('<');
			writeList(node.getGenericParameters(), out);
			out.append('>');
		}
		out.optionalSpace();
		out.append('=');
		out.optionalSpace();
		node.getValue().accept(this, out);
		out.finishStatement(true);
		return null;
	}
}
