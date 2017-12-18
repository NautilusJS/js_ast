package com.mindlin.jsast.type;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.impl.analysis.TypeCalculator;
import com.mindlin.jsast.impl.analysis.TypeContext;
import com.mindlin.jsast.impl.analysis.TypeExpressionResolver;
import com.mindlin.jsast.impl.parser.JSParserTest;
import com.mindlin.jsast.tree.type.TypeTree;

public class TypeInheritanceTest {
	static Type parseType(String typeStr) {
		//Parse to AST
		TypeTree ast = JSParserTest.parseType(typeStr);
		//Resolve to type
		Type result = ast.accept(new TypeExpressionResolver(), DEFAULT_CTX);
		return result;
	}
	
	static Type OBJECT = parseType("{}");
	public static TypeContext DEFAULT_CTX = new TypeContext() {

		@Override
		public Type getType(String name, List<Type> generics) {
			switch (name) {
				case "Object":
					return OBJECT;
			}
			return null;
		}

		@Override
		public Type resolveAliases(Type type) {
			return type;
		}

		@Override
		public Type thisType() {
			return null;
		}

		@Override
		public Type superType() {
			return null;
		}

		@Override
		public Type argumentsType() {
			return null;
		}
	};
	
	static boolean subtype(Type parent, Type child) {
		return TypeCalculator.isSubtype(null, child, parent);
	}
	
	static void assertSubtype(Type parent, Type child) {
		assertTrue(TypeCalculator.isSubtype(null, child, parent));
	}
	
	static void assertIdentical(Type a, Type b) {
		assertTrue(TypeCalculator.areIdentical(null, a, b));
	}
	
	
	@Test
	public void testPrimitivesEqual() {
		{
			Type n1 = parseType("number");
			Type n2 = parseType("number");
			
			assertTrue(TypeCalculator.areIdentical(null, n1, n2));
		}
		
		{
			Type s1 = parseType("string");
			Type s2 = parseType("string");
			
			assertTrue(TypeCalculator.areIdentical(null, s1, s2));
		}
		
	}
	
	@Test
	public void testSubtypeOfAny() {
		assertSubtype(IntrinsicType.ANY, LiteralType.of(true));
		assertSubtype(IntrinsicType.ANY, LiteralType.of(false));
		assertSubtype(IntrinsicType.ANY, LiteralType.of(0));
		assertSubtype(IntrinsicType.ANY, LiteralType.of(100));
		assertSubtype(IntrinsicType.ANY, LiteralType.of("hello"));
		assertSubtype(IntrinsicType.ANY, IntrinsicType.BOOLEAN);
	}
	
	@Test
	public void testSubtypeLiteral() {
		assertSubtype(IntrinsicType.STRING, LiteralType.of("hello"));
		assertSubtype(IntrinsicType.NUMBER, LiteralType.of(1));
		assertSubtype(IntrinsicType.BOOLEAN, LiteralType.of(true));
	}
	
	@Test
	public void testIntersectionSubtype() {
		Type strings = parseType("'a' & 'b' & 1");
		assertSubtype(IntrinsicType.STRING, strings);
		assertSubtype(IntrinsicType.NUMBER, strings);
		assertSubtype(LiteralType.of("a"), strings);
		assertSubtype(parseType("'a' & 'b'"), strings);
	}
	
	@Test
	public void testSupertypeOfUndefined() {
		
	}
	
	@Test
	public void testTypeParameterConstraint() {
		Type param = new TypeParameter(IntrinsicType.STRING, null);
		assertSubtype(IntrinsicType.STRING, param);
	}
	
	@Test
	public void testNullUndefined() {
		assertSubtype(IntrinsicType.BOOLEAN, IntrinsicType.NULL);
	}
	
	@Test
	public void testEnumNumber() {
		Type t1 = new EnumType();
		assertSubtype(IntrinsicType.NUMBER, t1);
	}
	
	@Test
	public void testObjectSimpleSubtype() {
		Type t1 = parseType("{foo: string}");
		Type t2 = parseType("{foo: any}");
		Type t3 = parseType("any");
		Type t4 = parseType("{foo?: string}");
		
		assertSubtype(t2, t1);
		assertSubtype(t1, t3);
		assertSubtype(t4, t1);
	}
	
	@Test
	public void testObjectSimpleAssignable() {
		
	}
	
	@Test
	public void testUnionsIdentical() {
		{
			Type u1 = parseType("string | number");
			Type u2 = parseType("string | number");
			
			assertIdentical(u1, u2);
		}
		
		{
			Type u1 = parseType("string | number");
			Type u2 = parseType("number | string");
			
			assertIdentical(u1, u2);
		}
	}
	
	@Test
	public void testUnionsNeq() {
		{
			Type u1 = parseType("string | number");
			Type u2 = parseType("string | boolean");
			
			assertFalse(TypeCalculator.areIdentical(null, u1, u2));
		}
	}
	
	@Test
	public void testIntersectionsEq() {
		{
			Type u1 = parseType("string & number");
			Type u2 = parseType("string & number");
			
			assertTrue(TypeCalculator.areIdentical(null, u1, u2));
		}
		
		{
			Type u1 = parseType("string & number");
			Type u2 = parseType("number & string");
			
			assertTrue(TypeCalculator.areIdentical(null, u1, u2));
		}
	}
	
	@Test
	public void testIfaceEq() {
		{
			Type i1 = parseType("{foo: string}");
			Type i2 = parseType("{foo: string}");
			
			assertTrue(TypeCalculator.areIdentical(null, i1, i2));
		}
		
		{
			Type i1 = parseType("{foo: string, bar: number}");
			Type i2 = parseType("{bar: number, foo: string}");
			
			assertTrue(TypeCalculator.areIdentical(null, i1, i2));
		}
	}
	
	@Test
	public void testIfaceAssignment() {
		{
			Type i1 = parseType("{id: number; name?: string}");
			Type i2 = parseType("{id: 1234}");
			Type i3 = parseType("{id: 1234; name: 'hello'}");
			
			assertTrue(TypeCalculator.isAssignableTo(DEFAULT_CTX, i1, i2));
		}
	}
}
