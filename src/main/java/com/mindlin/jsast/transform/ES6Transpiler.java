package com.mindlin.jsast.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.mindlin.jsast.impl.tree.AbstractFunctionTree.FunctionExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.AssignmentTreeImpl;
import com.mindlin.jsast.impl.tree.BlockTreeImpl;
import com.mindlin.jsast.impl.tree.AbstractClassTree;
import com.mindlin.jsast.impl.tree.ExpressionStatementTreeImpl;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;
import com.mindlin.jsast.impl.tree.MemberExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.MethodDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.ParameterTreeImpl;
import com.mindlin.jsast.impl.tree.ThisExpressionTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclarationTreeImpl;
import com.mindlin.jsast.impl.tree.VariableDeclaratorTreeImpl;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.BlockTree;
import com.mindlin.jsast.tree.CastTree;
import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassPropertyTree;
import com.mindlin.jsast.tree.ClassPropertyTree.PropertyDeclarationType;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ForLoopTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.MethodDefinitionTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.TryTree;
import com.mindlin.jsast.tree.TypeAliasTree;
import com.mindlin.jsast.tree.VariableDeclarationTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;

/**
 * Compilation pass that transforms TS => ES6
 * @author mailmindlin
 */
public class ES6Transpiler implements TreeTransformation<ASTTransformerContext> {

	@Override
	public ExpressionTree visitCast(CastTree node, ASTTransformerContext d) {
		return node.getExpression();
	}

	@Override
	public StatementTree visitTypeAlias(TypeAliasTree node, ASTTransformerContext d) {
		return null;
	}
	
	@Override
	public Tree visitClassDeclaration(ClassDeclarationTree node, ASTTransformerContext d) {
		boolean modified = false;
		
		modified |= !node.getImplementing().isEmpty();
		
		List<ClassPropertyTree<?>> properties = new ArrayList<>(node.getProperties());
		//TODO optimize
		
		boolean ctorModified = false;
		MethodDefinitionTree oldCtor = (MethodDefinitionTree) properties.stream()
				.filter(prop -> prop.getDeclarationType() == PropertyDeclarationType.CONSTRUCTOR)
				.findFirst()
				.orElse(null);
		
		List<ParameterTree> ctorParams = new ArrayList<>();
		List<StatementTree> ctorBody = new ArrayList<>();
		if (oldCtor != null) {
			//Copy from old constructor
			FunctionExpressionTree oldCtorFn = oldCtor.getInitializer();
			ctorParams.addAll(oldCtorFn.getParameters());
			//TODO can we assume that the body is a block?
			ctorBody.addAll(((BlockTree)oldCtorFn.getBody()).getStatements());
		}
		
		int ctorBodyInjectOffset = 0;
		//Check if the first statement in the body is a super call
		if (!ctorBody.isEmpty()) {
			StatementTree first = ctorBody.get(0);
			if (first.getKind() == Tree.Kind.FUNCTION_INVOCATION && ((FunctionCallTree)first).getCallee().getKind() == Tree.Kind.SUPER_EXPRESSION)
				ctorBodyInjectOffset = 1;
		}
		
		for (ListIterator<ParameterTree> i = ctorParams.listIterator(); i.hasNext();) {
			final ParameterTree oldParam = i.next();
			ParameterTree param = oldParam;
			if (param.getModifiers() != null) {
				boolean wasOptional = param.isOptional();
				
				param = new ParameterTreeImpl(param.getStart(), param.getEnd(), param.getName(), param.isRest(), false, null, param.getInitializer());
				//Inject assignment into constructor
				//TODO: support destructuring in parameters here
				if (oldParam.getName().getKind() != Kind.IDENTIFIER)
					throw new UnsupportedOperationException("Cannot convert destructuring parameter to property (yet)");
				
				//this.[parameter name]
				IdentifierTree identifier = (IdentifierTree) param.getName();
				PatternTree lhs = new MemberExpressionTreeImpl(Kind.MEMBER_SELECT, new ThisExpressionTreeImpl(-1, -1), identifier);
				
				//TODO fix for optional (shouldn't overwrite default values)
				if (wasOptional)
					throw new UnsupportedOperationException();
				
				//this.[parameter name] = [parameter name] 
				StatementTree propSetStmt = new ExpressionStatementTreeImpl(new AssignmentTreeImpl(Tree.Kind.ASSIGNMENT, lhs, identifier));
				ctorBody.add(ctorBodyInjectOffset++, propSetStmt);
			}
			
			if (!Tree.equivalentTo(param, oldParam)) {
				ctorModified = true;
				i.set(param);
			}
		}
		
		for (Iterator<ClassPropertyTree<?>> i = properties.iterator(); i.hasNext();) {
			ClassPropertyTree<?> property = i.next();
			switch (property.getDeclarationType()) {
				case CONSTRUCTOR:
					continue;
				case FIELD: {
					i.remove();
					if (property.getInitializer() != null) {
						//Inject initializer into constructor
						PatternTree lhs = new MemberExpressionTreeImpl(property.getKey().getKind() == Kind.IDENTIFIER ? Kind.MEMBER_SELECT : Kind.ARRAY_ACCESS, new ThisExpressionTreeImpl(-1, -1), property.getKey());
						StatementTree initializerStmt = new ExpressionStatementTreeImpl(new AssignmentTreeImpl(Tree.Kind.ASSIGNMENT, lhs, property.getInitializer()));
						ctorBody.add(ctorBodyInjectOffset++, initializerStmt);
						ctorModified = true;
					}
					break;
				}
			}
		}
		
		//Rebuild constructor
		if (ctorModified) {
			//Copy positioning from old constructor, if possible
			long oldStart = -1, oldEnd = -1, oldFnStart = -1, oldFnEnd = -1, oldBodyStart = -1, oldBodyEnd = -1;
			Modifiers oldModifiers = Modifiers.NONE;
			IdentifierTree name;
			if (oldCtor != null) {
				oldStart = oldCtor.getStart();
				oldEnd = oldCtor.getEnd();
				
				FunctionExpressionTree oldCtorFn = oldCtor.getInitializer();
				oldFnStart = oldCtorFn.getStart();
				oldFnEnd = oldCtorFn.getEnd();
				
				oldBodyStart = oldCtorFn.getBody().getStart();
				oldBodyEnd = oldCtorFn.getBody().getEnd();
				
				oldModifiers = oldCtor.getModifiers();//TODO: check modifiers are good
				name = oldCtorFn.getName();
			} else {
				name = new IdentifierTreeImpl(-1, -1, "constructor");
			}
			
			
			BlockTree newCtorBody = new BlockTreeImpl(oldBodyStart, oldBodyEnd, ctorBody);
			//TODO fix isStrict
			FunctionExpressionTree ctorFn = new FunctionExpressionTreeImpl(oldFnStart, oldFnEnd, false, name, null, ctorParams, null, false, newCtorBody, false, false);
			MethodDefinitionTree ctor = new MethodDeclarationTreeImpl(oldStart, oldEnd, oldModifiers, PropertyDeclarationType.CONSTRUCTOR, name, null, ctorFn);
			if (oldCtor != null)
				properties.set(properties.indexOf(oldCtor), ctor);
			else
				properties.add(ctor);
			modified = true;
		}
		
		//TODO transform if abstract
		//TODO check supertype
		if (!modified)
			return node;
		return new AbstractClassTree(node.getStart(), node.getEnd(), node.isAbstract(), node.getName(),
				Collections.emptyList(), node.getSuperType(), Collections.emptyList(), properties);
	}

	@Override
	public StatementTree visitForLoop(ForLoopTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return TreeTransformation.super.visitForLoop(node, d);
	}

	@Override
	public StatementTree visitTry(TryTree node, ASTTransformerContext d) {
		// TODO Auto-generated method stub
		return TreeTransformation.super.visitTry(node, d);
	}

	@Override
	public StatementTree visitVariableDeclaration(VariableDeclarationTree node, ASTTransformerContext d) {
		boolean modified = false;
		List<VariableDeclaratorTree> declarations = new ArrayList<>(node.getDeclarations());
		for (ListIterator<VariableDeclaratorTree> i = declarations.listIterator(); i.hasNext();) {
			VariableDeclaratorTree declarator = i.next();
			if (declarator.getType() == null)
				continue;
			i.set(new VariableDeclaratorTreeImpl(declarator.getStart(), declarator.getEnd(), declarator.getName(), null, declarator.getInitializer()));
			modified = true;
		}
		
		if (modified)
			node = new VariableDeclarationTreeImpl(node.getStart(), node.getEnd(), node.isScoped(), node.isConst(), declarations);
		return node;
	}
}
