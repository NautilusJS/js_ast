package com.mindlin.jsast.tree;

import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.type.EnumDeclarationTree;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;
import com.mindlin.jsast.tree.type.TypeAliasTree;

public interface StatementTreeVisitor<R, D> {
	R visitBlock(BlockTree node, D d);
	R visitBreak(BreakTree node, D d);
	R visitClassDeclaration(ClassDeclarationTree node, D d);
	R visitContinue(ContinueTree node, D d);
	R visitDirective(DirectiveTree node, D d);
	R visitDebugger(DebuggerTree node, D d);
	R visitDoWhileLoop(DoWhileLoopTree node, D d);
	R visitEmptyStatement(EmptyStatementTree node, D d);
	R visitEnumDeclaration(EnumDeclarationTree node, D d);
	R visitExport(ExportTree node, D d);
	R visitExpressionStatement(ExpressionStatementTree node, D d);
	R visitForEachLoop(ForEachLoopTree node, D d);
	R visitForLoop(ForLoopTree node, D d);
	R visitFunctionDeclaration(FunctionDeclarationTree node, D d);
	R visitIf(IfTree node, D d);
	R visitImport(ImportDeclarationTree node, D d);
	R visitInterfaceDeclaration(InterfaceDeclarationTree node, D d);
	R visitLabeledStatement(LabeledStatementTree node, D d);
	R visitReturn(ReturnTree node, D d);
	R visitSwitch(SwitchTree node, D d);
	R visitThrow(ThrowTree node, D d);
	R visitTry(TryTree node, D d);
	R visitTypeAlias(TypeAliasTree node, D d);
	R visitVariableDeclaration(VariableDeclarationTree node, D d);
	R visitWhileLoop(WhileLoopTree node, D d);
	R visitWith(WithTree node, D d);
}
