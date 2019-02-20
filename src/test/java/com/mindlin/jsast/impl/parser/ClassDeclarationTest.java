package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.HeritageClauseTree;
import com.mindlin.jsast.tree.HeritageExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;

public class ClassDeclarationTest {
	
	@Test
	public void testSimpleClassDeclaration() {
		ClassDeclarationTree clazz = parseStatement("class Foo{}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		assertEquals(clazz.getModifiers(), Modifiers.NONE);
	}
	
	@Test
	public void testEmptyAbstractClass() {
		ClassDeclarationTree clazz = parseStatement("abstract class Foo {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		assertEquals(clazz.getModifiers(), Modifiers.ABSTRACT);
	}
	
	@Test
	public void testClassExtends() {
		ClassDeclarationTree clazz = parseStatement("class Foo extends Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		
		HeritageClauseTree parent = assertSingleElementKind(Kind.EXTENDS_CLAUSE, clazz.getHeritage());
		
		HeritageExpressionTree superTarget = assertSingleElementKind(Kind.HERITAGE_EXPRESSION, parent.getTypes());
		assertIdentifier("Bar", superTarget.getExpression());
	}
	
	@Test
	public void testClassImplements() {
		ClassDeclarationTree clazz = parseStatement("class Foo implements Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		
		HeritageClauseTree impl = assertSingleElementKind(Kind.IMPLEMENTS_CLAUSE, clazz.getHeritage());
		
		HeritageExpressionTree implTarget = assertSingleElementKind(Kind.HERITAGE_EXPRESSION, impl.getTypes());
		assertIdentifier("Bar", implTarget.getExpression());
	}
	
	@Test
	public void testClassImplementsMultiple() {
		ClassDeclarationTree clazz = parseStatement("class Foo implements Bar, Baz {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		
		HeritageClauseTree impl = assertSingleElementKind(Kind.IMPLEMENTS_CLAUSE, clazz.getHeritage());
		assertEquals(2, impl.getTypes().size());
		
		HeritageExpressionTree implTarget0 = impl.getTypes().get(0);
		assertIdentifier("Bar", implTarget0.getExpression());
		
		HeritageExpressionTree implTarget1 = impl.getTypes().get(1);
		assertIdentifier("Baz", implTarget1.getExpression());
	}
	
	@Test
	public void testClassWithTypeParameters() {
		ClassDeclarationTree clazz = parseStatement("class Foo<T> {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		
		TypeParameterDeclarationTree tParam0 = assertSingleElementKind(Kind.TYPE_PARAMETER_DECLARATION, clazz.getTypeParameters());
		assertIdentifier("T", tParam0.getName());
	}
	
	@Test
	public void testClassMembers() {
		ClassDeclarationTree clazz = parseStatement("class Foo {bar;}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassAbstractProperties() {
		ClassDeclarationTree clazz = parseStatement("abstract class Foo {abstract bar();}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
}
