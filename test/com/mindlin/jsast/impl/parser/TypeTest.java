package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.CastExpressionTree;
import com.mindlin.jsast.tree.MemberExpressionTree;
import com.mindlin.jsast.tree.PropertyDeclarationTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class TypeTest {
	
	@SuppressWarnings("unchecked")
	static <T extends TypeTree> T parseType(String expr, Kind expectedKind) {
		JSLexer lexer = new JSLexer(new NominalSourceFile(getTestName(), expr));
		T result = (T) new JSParser().parseType(lexer, new Context());
		assertTrue("Did not read whole statement", lexer.isEOF());
		assertEquals(expectedKind, result.getKind());
		return result;
	}
	
	static void assertExceptionalType(String expr, String errMsg) {
		try {
			JSLexer lexer = new JSLexer(expr);
			new JSParser().parseType(lexer, new Context());
			fail(errMsg);
		} catch (JSSyntaxException e) {
		}
	}
	
	static void assertIdentifierType(String name, int numGenerics, TypeTree type) {
		assertEquals(Kind.IDENTIFIER_TYPE, type.getKind());
		assertIdentifier(name, ((IdentifierTypeTree)type).getIdentifier());
		assertEquals(numGenerics, ((IdentifierTypeTree)type).getGenerics().size());
	}
	
	@Test
	public void testBracketCast() {
		CastExpressionTree expr = parseExpression("<Foo> x", Kind.CAST);
		assertIdentifierType("Foo", 0, expr.getType());
		assertIdentifier("x", expr.getExpression());
	}
	
	@Test
	public void testAsCast() {
		CastExpressionTree expr = parseExpression("x as Foo", Kind.CAST);
		assertIdentifierType("Foo", 0, expr.getType());
		assertIdentifier("x", expr.getExpression());
	}
	
	@Test
	public void testAsPrecedence() {
		//See stackoverflow.com/a/28316948/2759984
		{
			//`<SomeType> x.id` should parse as `<SomeType> (x.id)`
			CastExpressionTree cast = parseExpression("<SomeType> x.id", Kind.CAST);
			assertIdentifierType("SomeType", 0, cast.getType());
			
			MemberExpressionTree expr = assertKind(Kind.MEMBER_SELECT, cast.getExpression());
			assertIdentifier("x", expr.getLeftOperand());
			assertIdentifier("id", expr.getRightOperand());
		}
		//TODO: more precedence tests?
	}
	
	@Test
	public void testIdentifierType() {
		IdentifierTypeTree type = parseType("Foo", Kind.IDENTIFIER_TYPE);
		assertIdentifierType("Foo", 0, type);
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
		ArrayTypeTree base1 = assertKind(Kind.ARRAY_TYPE, type.getBaseType());
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
		CompositeTypeTree type = parseType("A | B", Kind.TYPE_UNION);
		assertIdentifierType("A", 0, type.getConstituents().get(0));
		assertIdentifierType("B", 0, type.getConstituents().get(1));
	}
	
	@Test
	public void testIntersectionType() {
		CompositeTypeTree type = parseType("A & B", Kind.TYPE_INTERSECTION);
		assertIdentifierType("A", 0, type.getConstituents().get(0));
		assertIdentifierType("B", 0, type.getConstituents().get(1));
	}
	
	@Test
	public void testIntersectionTypeWithGenerics() {
		CompositeTypeTree type = parseType("A<T> & B<R>", Kind.TYPE_INTERSECTION);
		assertEquals(2, type.getConstituents().size());
		
		TypeTree leftType = type.getConstituents().get(0);
		assertIdentifierType("A", 1, leftType);
		assertIdentifierType("T", 0, ((IdentifierTypeTree) leftType).getGenerics().get(0));
		
		TypeTree rightType = type.getConstituents().get(1);
		assertIdentifierType("B", 1, rightType);
		assertIdentifierType("R", 0, ((IdentifierTypeTree)rightType).getGenerics().get(0));
	}
	
	@Test
	public void testSimpleObjectType() {
		ObjectTypeTree type = parseType("{a:Foo}", Kind.OBJECT_TYPE);
		assertEquals(1, type.getDeclaredMembers().size());
		
		PropertyDeclarationTree prop0 = assertKind(Kind.PROPERTY_DECLARATION, type.getDeclaredMembers().get(0));
		assertIdentifier("a", prop0.getName());
		assertIdentifierType("Foo", 0, prop0.getType());
	}
	
	@Test
	public void testTupleType() {
		TupleTypeTree type = parseType("[string, number]", Kind.TUPLE_TYPE);
		assertEquals(2, type.getSlotTypes().size());
		assertSpecialType(SpecialType.STRING, type.getSlotTypes().get(0));
		assertSpecialType(SpecialType.NUMBER, type.getSlotTypes().get(1));
	}
	
	@Test
	public void testParentheticalType() {
		CompositeTypeTree intersection = parseType("A&(B|C)", Kind.TYPE_INTERSECTION);
		assertIdentifierType("A", 0, intersection.getConstituents().get(0));
		
		assertEquals(Kind.TYPE_UNION, intersection.getConstituents().get(1).getKind());
		CompositeTypeTree union = (CompositeTypeTree) intersection.getConstituents().get(1);
		assertIdentifierType("B", 0, union.getConstituents().get(0));
		assertIdentifierType("C", 0, union.getConstituents().get(1));
	}
}
