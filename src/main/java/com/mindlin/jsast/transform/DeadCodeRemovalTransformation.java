package com.mindlin.jsast.transform;

import java.util.ArrayList;
import java.util.Optional;

import com.mindlin.jsast.impl.analysis.SideEffectValidator;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.EmptyStatementTreeImpl;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.IfTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.VariableDeclarationTree;

public class DeadCodeRemovalTransformation implements TreeTransformation<ASTTransformerContext> {

	@Override
	public StatementTree visitBlock(BlockTree node, ASTTransformerContext ctx) {
		ArrayList<VariableDeclarationTree> declarations = new ArrayList<>();
		ArrayList<StatementTree> statements = new ArrayList<>();
		
		boolean isAtTop = true;
		boolean modified = false;
		for (StatementTree stmt : node.getStatements()) {
			if (isAtTop && stmt.getKind() != Kind.VARIABLE_DECLARATION)
				isAtTop = false;
			
			if (isAtTop) {
				declarations.add((VariableDeclarationTree) stmt);
				continue;
			}
			
			if (stmt.getKind() == Kind.EMPTY_STATEMENT) {
				modified = true;
				continue;
			} else if (stmt.getKind() == Kind.VARIABLE_DECLARATION) {
				//Shift declaration to top
				modified = true;
				declarations.add((VariableDeclarationTree) stmt);
				continue;
			} else if (stmt.getKind() == Kind.BLOCK) {
				//TODO See if we can merge these blocks
				if (Math.random() > 100) {
					//merge blocks
					continue;
				}
			}
			
			statements.add(stmt);
		}
		
		//TODO Reduce declarations
		{
			boolean modifiedDeclarations = false;
			VariableDeclarationTree varTree = null, letTree = null, constTree = null;
			for (VariableDeclarationTree declaration : declarations) {
				switch (declaration.getDeclarationStyle()) {
					case CONST:
						if (constTree == null) {
							constTree = declaration;
							continue;
						}
						break;
					case LET:
						if (letTree == null) {
							letTree = declaration;
							continue;
						}
						break;
					case VAR:
						if (varTree == null) {
							varTree = declaration;
							continue;
						}
						break;
					default:
						throw new IllegalArgumentException("Unknown style: " + declaration.getDeclarationStyle());
				}
			}
			if (modifiedDeclarations) {
				modified = true;
				declarations = new ArrayList<>();
				if (varTree != null)
					declarations.add(varTree);
				if (letTree != null)
					declarations.add(letTree);
				if (constTree != null)
					declarations.add(constTree);
			}
		}
		//Add declarations back on top
		statements.addAll(0, declarations);
		
		statements.trimToSize();
		
		
		//There are no statements in this block (it's an empty block), so let's get rid of it.
		if (statements.isEmpty())
			return new EmptyStatementTreeImpl(node.getStart(), node.getEnd());
		
		//If there's only one statement in this block, we can just unwrap it. (TODO check if this is correct)
		if (statements.size() == 1)
			return statements.get(0);//TODO fix bounds
		
		if (modified)
			node = new BlockTreeImpl(node.getStart(), node.getEnd(), statements);
		
		return node;
	}
	
	@Override
	public StatementTree visitIf(IfTree node, ASTTransformerContext ctx) {
		StatementTree concequent = node.getThenStatement();
		StatementTree alternative = node.getElseStatement();
		
		if (concequent.getKind() == Kind.EMPTY_STATEMENT && (alternative == null || alternative.getKind() == Kind.EMPTY_STATEMENT)) {
			if (SideEffectValidator.hasSideEffectsMaybe(ctx, node.getExpression()))
				return new ExpressionStatementTreeImpl(node.getStart(), node.getEnd(), node.getExpression());
			return new EmptyStatementTreeImpl(node.getStart(), node.getEnd());
		}
		
		Optional<Boolean> coerced = SideEffectValidator.coerceToBoolean(ctx, node.getExpression());
		//TODO support side-effect-y condition as StatementExpression
		if (coerced.isPresent() && !SideEffectValidator.hasSideEffectsMaybe(ctx, node.getExpression())) {
			if (coerced.get())
				return node.getThenStatement();
			else {
				if (alternative == null)
					return new EmptyStatementTreeImpl(node.getStart(), node.getEnd());
				else
					return alternative;
			}
		}
		
		return node;
	}
}
