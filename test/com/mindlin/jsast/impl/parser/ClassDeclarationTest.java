package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseExpression;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ClassTreeBase.ClassDeclarationTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class ClassDeclarationTest {
	
	@Test
	public void testSimpleClassDeclaration() {
		ClassDeclarationTree clazz = parseExpression("class Foo{}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		assertEquals(clazz.getModifiers(), Modifiers.NONE);
	}
	
	@Test
	public void testEmptyAbstractClass() {
		ClassDeclarationTree clazz = parseExpression("abstract class Foo {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		assertEquals(clazz.getModifiers(), Modifiers.ABSTRACT);
	}
	
	@Test
	public void testClassExtends() {
		ClassDeclarationTree clazz = parseExpression("class Foo extends Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
		//TODO check superclass
	}
	
	@Test
	public void testClassImplements() {
		ClassDeclarationTree clazz = parseExpression("class Foo implements Bar {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassImplementsMultiple() {
		ClassDeclarationTree clazz = parseExpression("class Foo implements Bar, Baz {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassGenerics() {
		ClassDeclarationTree clazz = parseExpression("class Foo<T> {}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassMembers() {
		ClassDeclarationTree clazz = parseExpression("class Foo {bar;}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
	
	@Test
	public void testClassAbstractProperties() {
		ClassDeclarationTree clazz = parseExpression("abstract class Foo {abstract bar();}", Kind.CLASS_DECLARATION);
		assertIdentifier("Foo", clazz.getName());
	}
}
