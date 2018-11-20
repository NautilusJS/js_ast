package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;

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
		//TODO check superclass
	}
	
	@Test
	public void testClassImplements() {
		ClassDeclarationTree clazz = parseStatement("class Foo implements Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassImplementsMultiple() {
		ClassDeclarationTree clazz = parseStatement("class Foo implements Bar, Baz {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassGenerics() {
		ClassDeclarationTree clazz = parseStatement("class Foo<T> {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
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
