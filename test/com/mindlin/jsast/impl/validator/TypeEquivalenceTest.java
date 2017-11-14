package com.mindlin.jsast.impl.validator;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.impl.analysis.TypeContext;
import com.mindlin.jsast.impl.parser.JSParserTest;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.type.Type;

public class TypeEquivalenceTest {
	TypeContext ctx = new TypeContext() {
		@Override
		public Type getType(String name, List<Type> generics) {
			return null;
		}
		@Override
		public Type resolveGeneric(String name) {
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
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Type argumentsType() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	@Test
	public void testPrimitivesEqual() {
		{
			TypeTree n1 = JSParserTest.parseType("number");
			TypeTree n2 = JSParserTest.parseType("number");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, n1, n2));
		}
		
		{
			TypeTree s1 = JSParserTest.parseType("string");
			TypeTree s2 = JSParserTest.parseType("string");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, s1, s2));
		}
		
	}
	
	@Test
	public void testUnionsEq() {
		{
			TypeTree u1 = JSParserTest.parseType("string | number");
			TypeTree u2 = JSParserTest.parseType("string | number");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, u1, u2));
		}
		
		{
			TypeTree u1 = JSParserTest.parseType("string | number");
			TypeTree u2 = JSParserTest.parseType("number | string");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, u1, u2));
		}
	}
	
	@Test
	public void testUnionsNeq() {
		{
			TypeTree u1 = JSParserTest.parseType("string | number");
			TypeTree u2 = JSParserTest.parseType("string | boolean");
			
			assertFalse(TypeInheritanceValidator.isEquivalent(ctx, u1, u2));
		}
	}
	
	@Test
	public void testIntersectionsEq() {
		{
			TypeTree u1 = JSParserTest.parseType("string & number");
			TypeTree u2 = JSParserTest.parseType("string & number");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, u1, u2));
		}
		
		{
			TypeTree u1 = JSParserTest.parseType("string & number");
			TypeTree u2 = JSParserTest.parseType("number & string");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, u1, u2));
		}
	}
	
	@Test
	public void testIfaceEq() {
		{
			TypeTree i1 = JSParserTest.parseType("{foo: string}");
			TypeTree i2 = JSParserTest.parseType("{foo: string}");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, i1, i2));
		}
		
		{
			TypeTree i1 = JSParserTest.parseType("{foo: string, bar: number}");
			TypeTree i2 = JSParserTest.parseType("{bar: number, foo: string}");
			
			assertTrue(TypeInheritanceValidator.isEquivalent(ctx, i1, i2));
		}
	}
}
