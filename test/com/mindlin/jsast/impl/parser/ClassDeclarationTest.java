package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ClassDeclarationTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class ClassDeclarationTest {
	
	@Test
	public void testSimpleClassDeclaration() {
		ClassDeclarationTree clazz = parseExpression("class Foo{}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
		assertFalse(clazz.isAbstract());
	}
	
	@Test
	public void testEmptyAbstractClass() {
		ClassDeclarationTree clazz = parseExpression("abstract class Foo {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
		assertTrue(clazz.isAbstract());
	}
	
	@Test
	public void testClassExtends() {
		ClassDeclarationTree clazz = parseExpression("class Foo extends Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
		//TODO check superclass
	}
	
	@Test
	public void testClassImplements() {
		ClassDeclarationTree clazz = parseExpression("class Foo implements Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
	}
	
	@Test
	public void testClassImplementsMultiple() {
		ClassDeclarationTree clazz = parseExpression("class Foo implements Bar, Baz {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
	}
	
	@Test
	public void testClassGenerics() {
		ClassDeclarationTree clazz = parseExpression("class Foo<T> {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
	}
	
	@Test
	public void testClassMembers() {
		ClassDeclarationTree clazz = parseExpression("class Foo {bar;}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
	}
	
	@Test
	public void testClassAbstractProperties() {
		ClassDeclarationTree clazz = parseExpression("abstract class Foo {abstract bar();}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getIdentifier());
	}
}
