package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.TypeTree;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.InterfaceTypeTree;
import com.mindlin.jsast.tree.type.IntersectionTypeTree;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.UnionTypeTree;
import com.mindlin.jsast.tree.InterfacePropertyTree;
import com.mindlin.jsast.tree.ObjectPropertyKeyTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class TypeTest {
	
	@SuppressWarnings("unchecked")
	static <T extends TypeTree> T parseType(String expr, Kind expectedKind) {
		JSLexer lexer = new JSLexer(expr);
		T result = (T) new JSParser().parseType(lexer, new Context());
		assertTrue("Did not read whole statement", lexer.isEOF());
		assertEquals(expectedKind, result.getKind());
		return result;
	}
	
	static void assertIdentifierType(String name, int numGenerics, TypeTree type) {
		assertEquals(Kind.IDENTIFIER_TYPE, type.getKind());
		assertIdentifier(name, ((IdentifierTypeTree)type).getIdentifier());
		assertEquals(numGenerics, ((IdentifierTypeTree)type).getGenerics().size());
	}
	
	@Test
	public void testIdentifierType() {
		IdentifierTypeTree type = parseType("string", Kind.IDENTIFIER_TYPE);
		assertIdentifierType("string", 0, type);
	}
	
	@Test
	public void testIdentifierTypeWithGeneric() {
		IdentifierTypeTree type = parseType("Foo<T>", Kind.IDENTIFIER_TYPE);
		assertIdentifierType("Foo", 1, type);
		assertIdentifierType("T", 0, type.getGenerics().get(0));
	}
	
	@Test
	public void testArrayType() {
		ArrayTypeTree type = parseType("T[]", Kind.ARRAY_TYPE);
		assertIdentifierType("T", 0, type.getBaseType());
	}
	
	@Test
	public void testNestedArrayType() {
		ArrayTypeTree type = parseType("T[][]", Kind.ARRAY_TYPE);
		
		assertEquals(Kind.ARRAY_TYPE, type.getBaseType().getKind());
		
		ArrayTypeTree base1 = (ArrayTypeTree) type.getBaseType();
		assertIdentifierType("T", 0, base1.getBaseType());
	}
	
	@Test
	public void testGenericArrayType() {
		ArrayTypeTree type = parseType("Array<T>", Kind.ARRAY_TYPE);
		assertIdentifierType("T", 0, type.getBaseType());
	}
	
	@Test
	public void testIdentifierTypeWithGenerics() {
		IdentifierTypeTree type = parseType("Map<K,V>", Kind.IDENTIFIER_TYPE);
		assertIdentifierType("Map", 2, type);
		assertIdentifierType("K", 0, type.getGenerics().get(0));
		assertIdentifierType("V", 0, type.getGenerics().get(1));
	}
	
	@Test
	public void testUnionType() {
		UnionTypeTree type = parseType("string|number", Kind.TYPE_UNION);
		assertIdentifierType("string", 0, type.getLeftType());
		assertIdentifierType("number", 0, type.getRightType());
	}
	
	@Test
	public void testIntersectionType() {
		IntersectionTypeTree type = parseType("A & B", Kind.TYPE_INTERSECTION);
		assertIdentifierType("A", 0, type.getLeftType());
		assertIdentifierType("B", 0, type.getRightType());
	}
	
	@Test
	public void testIntersectionTypeWithGenerics() {
		IntersectionTypeTree type = parseType("A<T> & B<R>", Kind.TYPE_INTERSECTION);
		assertIdentifierType("A", 1, type.getLeftType());
		assertIdentifierType("T", 0, ((IdentifierTypeTree)type.getLeftType()).getGenerics().get(0));
		assertIdentifierType("B", 1, type.getRightType());
		assertIdentifierType("R", 0, ((IdentifierTypeTree)type.getRightType()).getGenerics().get(0));
	}
	
	@Test
	public void testSimpleInlineInterfaceType() {
		InterfaceTypeTree type = parseType("{a:string}", Kind.INTERFACE_TYPE);
		assertEquals(1, type.getProperties().size());
		
		InterfacePropertyTree prop0 = type.getProperties().get(0);
		ObjectPropertyKeyTree key0 = prop0.getKey();
		assertFalse(key0.isComputed());
		assertIdentifier("a", key0);
		assertIdentifierType("string", 0, prop0.getType());
	}
	
	@Test
	public void testTupleType() {
		TupleTypeTree type = parseType("[string, number]", Kind.TUPLE_TYPE);
		assertEquals(2, type.getSlotTypes().size());
		assertIdentifierType("string", 0, type.getSlotTypes().get(0));
		assertIdentifierType("number", 0, type.getSlotTypes().get(1));
	}
	
	@Test
	public void testParentheticalType() {
		IntersectionTypeTree intersection = parseType("A&(B|C)", Kind.TYPE_INTERSECTION);
		assertIdentifierType("A", 0, intersection.getLeftType());
		
		assertEquals(Kind.TYPE_UNION, intersection.getRightType().getKind());
		UnionTypeTree union = (UnionTypeTree) intersection.getRightType();
		assertIdentifierType("B", 0, union.getLeftType());
		assertIdentifierType("C", 0, union.getRightType());
	}
}
