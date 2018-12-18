package com.mindlin.jsast.harness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.impl.parser.JSDialect;
import com.mindlin.jsast.impl.validator.ErrorReporter;
import com.mindlin.jsast.impl.validator.ParameterDeclarationValidator;
import com.mindlin.jsast.impl.validator.StatelessValidator;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ForEachLoopTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionDeclarationTree;
import com.mindlin.jsast.tree.GotoTree;
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.HeritageExpressionTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.ReturnTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.ThrowTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.jsast.tree.WithTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.ConditionalTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.ParenthesizedTypeTree;
import com.mindlin.jsast.tree.type.UnaryTypeTree;
import com.mindlin.jsast.tree.util.TreePath;

public class CheckPass implements CompilerPass<CompilationUnitTree, CompilationUnitTree> {
	JSDialect dialect;
	List<StatelessValidator<?>> cfVals;
	
	public CheckPass(CompilerOptions options) {
		dialect = options.get(CompilerOptions.SOURCE_LANGUAGE).get();
		
		this.cfVals = new ArrayList<>();
		this.cfVals.add(new ParameterDeclarationValidator(dialect));
	}
	
	@Override
	public String getName() {
		return "check";
	}
	
	public CompilationUnitTree processOne(CompilationUnitTree source) {
		ErrorReporter reporter = new ErrorReporterImpl();
		for (StatelessValidator<?> val : this.cfVals) {
			ContextFreeChecker<?> checker = new ContextFreeChecker<>(val, reporter);
			checker.check(null, source);
		}
		
		return source;
	}

	@Override
	public Stream<CompilationUnitTree> process(Stream<CompilationUnitTree> sources) throws Exception {
		return sources.map(this::processOne);
	}
	
	public static class ErrorReporterImpl implements ErrorReporter {
		@Override
		public void report(ErrorLevel level, Tree target, String format, Object... args) {
			System.err.println("" + level + "/" + target + "/" + format);
			throw new UnsupportedOperationException();
		}

		@Override
		public void report(ErrorLevel level, SourceRange location, String format, Object... args) {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Helper for CF checkers
	 * @author mailmindlin
	 *
	 * @param <T>
	 */
	public static class ContextFreeChecker<T extends Tree> extends CompilerStage<CompilationUnitTree, CompilationUnitTree> {
		protected final StatelessValidator<T> checker;
		protected final ErrorReporter reporter;
		
		public ContextFreeChecker(StatelessValidator<T> checker, ErrorReporter reporter) {
			this.checker = checker;
			this.reporter = reporter;
		}
		
		@Override
		public String getName() {
			return null;
		}
		
		public void check(TreePath<? extends Tree> path, Collection<? extends Tree> nodes) {
			if (nodes == null)
				return;
			for (Tree node : nodes)
				check(path, node);
		}
		
		@SuppressWarnings("unchecked")
		public void check(TreePath<? extends Tree> path, Tree node) {
			if (node == null)
				return;
			
			TreePath<? extends Tree> current = new TreePath<>(path, node);
			if (checker.test(current))
				checker.check((T) node, reporter);
			
			if (!checker.follow(current))
				return;
			
			switch (node.getKind()) {
				case ADDITION:
				case BITWISE_AND:
				case BITWISE_OR:
				case BITWISE_XOR:
				case DIVISION:
				case EQUAL:
				case EXPONENTIATION:
				case NOT_EQUAL:
				case IN:
				case GREATER_THAN:
				case GREATER_THAN_EQUAL:
				case INSTANCEOF:
				case LEFT_SHIFT:
				case LESS_THAN:
				case LESS_THAN_EQUAL:
				case LOGICAL_AND:
				case LOGICAL_OR:
				case MULTIPLICATION:
				case REMAINDER:
				case RIGHT_SHIFT:
				case STRICT_EQUAL:
				case STRICT_NOT_EQUAL:
				case SUBTRACTION:
				case UNSIGNED_RIGHT_SHIFT:
				case MEMBER_SELECT:
				case ARRAY_ACCESS: {
					BinaryExpressionTree tree = (BinaryExpressionTree) node;
					check(current, tree.getLeftOperand());
					check(current, tree.getRightOperand());
					return;
				}
				case ADDITION_ASSIGNMENT:
				case BITWISE_AND_ASSIGNMENT:
				case BITWISE_OR_ASSIGNMENT:
				case BITWISE_XOR_ASSIGNMENT:
				case DIVISION_ASSIGNMENT:
				case EXPONENTIATION_ASSIGNMENT:
				case LEFT_SHIFT_ASSIGNMENT:
				case MULTIPLICATION_ASSIGNMENT:
				case REMAINDER_ASSIGNMENT:
				case RIGHT_SHIFT_ASSIGNMENT:
				case SUBTRACTION_ASSIGNMENT:
				case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
				case ASSIGNMENT: {
					AssignmentTree tree = (AssignmentTree) node;
					check(current, tree.getVariable());
					check(current, tree.getValue());
					return;
				}
				case BITWISE_NOT:
				case DELETE:
				case LOGICAL_NOT:
				case PARENTHESIZED:
				case POSTFIX_DECREMENT:
				case POSTFIX_INCREMENT:
				case PREFIX_DECREMENT:
				case PREFIX_INCREMENT:
				case SPREAD:
				case TYPEOF:
				case UNARY_MINUS:
				case UNARY_NONNULL:
				case UNARY_PLUS:
				case YIELD:
				case YIELD_GENERATOR:
				case VOID: {
					UnaryTree tree = (UnaryTree) node;
					check(current, tree.getExpression());
					return;
				}
				case SEQUENCE: {
					SequenceExpressionTree tree = (SequenceExpressionTree) node;
					check(current, tree.getElements());
					return;
				}
				case ARRAY_LITERAL:
					break;
				case ARRAY_PATTERN:
					break;
				case ASSIGNMENT_PATTERN:
					break;
				case ASSIGNMENT_PROPERTY:
					break;
				case AWAIT:
					break;
				case BLOCK: {
					BlockTree tree = (BlockTree) node;
					check(current, tree.getStatements());
					return;
				}
				case CALL_SIGNATURE:
					break;
				case CASE:
					break;
				case CAST:
					break;
				case CATCH:
					break;
				case CLASS_DECLARATION:
					break;
				case CLASS_EXPRESSION:
					break;
				case FUNCTION_EXPRESSION:
					break;
				case COMMENT:
					break;
				case COMPILATION_BUNDLE:
					break;
				case COMPILATION_UNIT: {
					CompilationUnitTree tree = (CompilationUnitTree) node;
					check(current, tree.getSourceElements());
					return;
				}
				case COMPUTED_PROPERTY_KEY: {
					ComputedPropertyKeyTree tree = (ComputedPropertyKeyTree) node;
					check(current, tree.getExpression());
					return;
				}
				case CONDITIONAL: {
					ConditionalExpressionTree tree = (ConditionalExpressionTree) node;
					check(current, tree.getCondition());
					check(current, tree.getTrueExpression());
					check(current, tree.getFalseExpression());
					return;
				}
				case NEW: {
					NewTree tree = (NewTree) node;
					check(current, tree.getCallee());
					check(current, tree.getTypeArguments());
					check(current, tree.getArguments());
					return;
				}
				case CONSTRUCTOR_DECLARATION:
					break;
				case CONSTRUCTOR_TYPE:
					break;
				case CONSTRUCT_SIGNATURE:
					break;
				case BREAK:
				case CONTINUE: {
					GotoTree tree = (GotoTree) node;
					check(current, tree);
					return;
				}
				case LABELED_STATEMENT: {
					LabeledStatementTree tree = (LabeledStatementTree) node;
					check(current, tree.getName());
					check(current, tree.getStatement());
					return;
				}
				case DECORATOR:
					break;
				case DIRECTIVE:
					break;
				case ENUM_DECLARATION:
					break;
				case EXPORT:
					break;
				case EXPRESSION_STATEMENT:
					break;
				case IMPLEMENTS_CLAUSE:
				case EXTENDS_CLAUSE: {
					HeritageClauseTree tree = (HeritageClauseTree) node;
					check(current, tree.getTypes());
					return;
				}
				case HERITAGE_EXPRESSION: {
					HeritageExpressionTree tree = (HeritageExpressionTree) node;
					check(current, tree.getExpression());
					check(current, tree.getTypeAguments());
					return;
				}
				case PARAMETER: {
					ParameterTree tree = (ParameterTree) node;
					//TODO: decorators
					//TODO: jsdoc
					check(current, tree.getIdentifier());
					check(current, tree.getType());
					check(current, tree.getInitializer());
					return;
				}
				case FOR_LOOP: {
					ForLoopTree tree = (ForLoopTree) node;
					check(current, tree.getInitializer());
					check(current, tree.getCondition());
					check(current, tree.getUpdate());
					check(current, tree.getStatement());
					return;
				}
				case FOR_IN_LOOP:
				case FOR_OF_LOOP: {
					ForEachLoopTree tree = (ForEachLoopTree) node;
					check(current, tree.getVariable());
					check(current, tree.getExpression());
					check(current, tree.getStatement());
					return;
				}
				case FUNCTION_INVOCATION:
					break;
				case IMPORT:
					break;
				case IMPORT_SPECIFIER:
					break;
				case IF: {
					IfTree tree = (IfTree) node;
					check(current, tree.getExpression());
					check(current, tree.getThenStatement());
					check(current, tree.getElseStatement());
					return;
				}
				case FUNCTION_DECLARATION: {
					FunctionDeclarationTree tree = (FunctionDeclarationTree) node;
					check(current, tree.getDecorators());
					check(current, tree.getName());
					check(current, tree.getTypeParameters());
					check(current, tree.getParameters());
					check(current, tree.getReturnType());
					check(current, tree.getBody());
					return;
				}
				case GET_ACCESSOR_DECLARATION:
				case INTERFACE_DECLARATION:
					break;
				case ARRAY_TYPE: {
					ArrayTypeTree tree = (ArrayTypeTree) node;
					check(current, tree.getBaseType());
					return;
				}
				case CONDITIONAL_TYPE: {
					ConditionalTypeTree tree = (ConditionalTypeTree) node;
					check(current, tree.getCheckType());
					check(current, tree.getLimitType());
					check(current, tree.getConecquent());
					check(current, tree.getAlternate());
					return;
				}
				case IDENTIFIER_TYPE: {
					IdentifierTypeTree tree = (IdentifierTypeTree) node;
					check(current, tree.getIdentifier());
					check(current, tree.getGenerics());
					return;
				}
				case FUNCTION_TYPE:
					break;
				case INDEX_SIGNATURE:
					break;
				case INFER_TYPE:
					break;
				case JSDOC_ANNOTATION:
					break;
				case JSDOC_DIRECTIVE:
					break;
				case OPTIONAL_TYPE:
				case DEFINITE_TYPE:
				case UNIQUE_TYPE:
				case KEYOF_TYPE: {
					UnaryTypeTree tree = (UnaryTypeTree) node;
					check(current, tree.getBaseType());
					return;
				}
				case PARENTHESIZED_TYPE: {
					ParenthesizedTypeTree tree = (ParenthesizedTypeTree) node;
					check(current, tree.getType());
					return;
				}
				case LITERAL_TYPE:
					break;
				case MAPPED_TYPE:
					break;
				case MEMBER_TYPE:
					break;
				case METHOD_DECLARATION:
					break;
				case METHOD_SIGNATURE:
					break;
				case MODULE_DECLARATION:
					break;
				case NAMESPACE_DECLARATION:
					break;
				case OBJECT_LITERAL:
					break;
				case OBJECT_LITERAL_PROPERTY:
					break;
				case OBJECT_PATTERN:
					break;
				case OBJECT_TYPE:
					break;
				case PROPERTY_DECLARATION:
					break;
				case PROPERTY_SIGNATURE:
					break;
				case REGEXP_LITERAL:
					break;
				case REST_PATTERN:
					break;
				case REST_TYPE:
					break;
				case SET_ACCESSOR_DECLARATION:
					break;
				case SHORTHAND_ASSIGNMENT_PATTERN:
					break;
				case SHORTHAND_ASSIGNMENT_PROPERTY:
					break;
				case SWITCH: {
					SwitchTree tree = (SwitchTree) node;
					check(current, tree.getExpression());
					check(current, tree.getCases());
					return;
				}
				case TAGGED_TEMPLATE:
					break;
				case TEMPLATE_ELEMENT:
					break;
				case TEMPLATE_LITERAL:
					break;
				case RETURN: {
					ReturnTree tree = (ReturnTree) node;
					check(current, tree.getExpression());
					return;
				}
				case THROW: {
					ThrowTree tree = (ThrowTree) node;
					check(current, tree.getExpression());
					return;
				}
				case TRY: {
					TryTree tree = (TryTree) node;
					check(current, tree.getBlock());
					check(current, tree.getCatches());
					check(current, tree.getFinallyBlock());
					return;
				}
				case TUPLE_TYPE:
					break;
				case TYPE_ALIAS:
					break;
				case TYPE_INTERSECTION:
					break;
				case TYPE_PARAMETER_DECLARATION:
					break;
				case TYPE_PREDICATE:
					break;
				case TYPE_QUERY:
					break;
				case TYPE_UNION:
					break;
				case VARIABLE_DECLARATION:
					break;
				case VARIABLE_DECLARATOR:
					break;
				case DO_WHILE_LOOP: {
					DoWhileLoopTree tree = (DoWhileLoopTree) node;
					check(current, tree.getStatement());
					check(current, tree.getCondition());
					return;
				}
				case WHILE_LOOP: {
					WhileLoopTree tree = (WhileLoopTree) node;
					check(current, tree.getCondition());
					check(current, tree.getStatement());
					return;
				}
				case WITH: {
					WithTree tree = (WithTree) node;
					check(current, tree.getScope());
					check(current, tree.getStatement());
					return;
				}
				case IDENTIFIER:
				case SPECIAL_TYPE:
				case BOOLEAN_LITERAL:
				case NULL_LITERAL:
				case NUMERIC_LITERAL:
				case STRING_LITERAL:
				case DEBUGGER:
				case THIS_EXPRESSION:
				case SUPER_EXPRESSION:
				case EMPTY_STATEMENT:
					// No children
					return;
				case OTHER:
				case ERROR:
					throw new UnsupportedOperationException("Unsupported kind: " + node.getKind());
				default:
					break;
			}
			//TODO: finish
			throw new UnsupportedOperationException("Not finished: " + node.getKind());
		}

		@Override
		public CompilationUnitTree process(CompilationUnitTree source) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
