package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.fs.SourceFile.NominalSourceFile;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.CastExpressionTree;
import com.mindlin.jsast.tree.MemberExpressionTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.PropertyDeclarationTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.type.ArrayTypeTree;
import com.mindlin.jsast.tree.type.CompositeTypeTree;
import com.mindlin.jsast.tree.type.ConditionalTypeTree;
import com.mindlin.jsast.tree.type.ConstructorTypeTree;
import com.mindlin.jsast.tree.type.FunctionTypeTree;
import com.mindlin.jsast.tree.type.IdentifierTypeTree;
import com.mindlin.jsast.tree.type.IndexSignatureTree;
import com.mindlin.jsast.tree.type.UnaryTypeTree;
import com.mindlin.jsast.tree.type.MappedTypeTree;
import com.mindlin.jsast.tree.type.MemberTypeTree;
import com.mindlin.jsast.tree.type.ObjectTypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;
import com.mindlin.jsast.tree.type.TupleTypeTree;
import com.mindlin.jsast.tree.type.TypeParameterDeclarationTree;
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
		assertIdentifier(name, ((IdentifierTypeTree)type).getName());
		if (numGenerics == 0)
			assertNull(((IdentifierTypeTree)type).getGenerics());
		else
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
	public void testIdentifierTypeWithGenerics() {
		IdentifierTypeTree type = parseType("Map<K,V>", Kind.IDENTIFIER_TYPE);
		assertIdentifierType("Map", 2, type);
		assertIdentifierType("K", 0, type.getGenerics().get(0));
		assertIdentifierType("V", 0, type.getGenerics().get(1));
	}
	
	@Test
	public void testQualifiedReference() {
		IdentifierTypeTree type = parseType("A.B.C<T>", Kind.IDENTIFIER_TYPE);
		
		fail("Check not implemented");
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
	public void testParentheticalType() {
		CompositeTypeTree intersection = parseType("A&(B|C)", Kind.TYPE_INTERSECTION);
		assertIdentifierType("A", 0, intersection.getConstituents().get(0));
		
		assertEquals(Kind.TYPE_UNION, intersection.getConstituents().get(1).getKind());
		CompositeTypeTree union = (CompositeTypeTree) intersection.getConstituents().get(1);
		assertIdentifierType("B", 0, union.getConstituents().get(0));
		assertIdentifierType("C", 0, union.getConstituents().get(1));
	}
	
	@Test
	public void testMemberType() {
		MemberTypeTree type = parseType("T[k]", Kind.MEMBER_TYPE);
		assertIdentifierType("T", 0, type.getBaseType());
		assertIdentifierType("k", 0, type.getName());
	}
	
	@Test
	public void testNestedMemberType() {
		MemberTypeTree type = parseType("T[k0][k1]", Kind.MEMBER_TYPE);
		MemberTypeTree base = assertKind(Kind.MEMBER_TYPE, type.getBaseType());
		assertIdentifierType("T", 0, base.getBaseType());
		assertIdentifierType("k0", 0, base.getName());
		assertIdentifierType("k1", 0, type.getName());
	}
	
	@Test
	public void testConditionalType() {
		ConditionalTypeTree type = parseType("T extends any[] ? T[number] : any", Kind.CONDITIONAL_TYPE);
		
		assertIdentifierType("T", 0, type.getCheckType());
		
		ArrayTypeTree limitBase = assertKind(Kind.ARRAY_TYPE, type.getLimitType());
		assertSpecialType(SpecialType.ANY, limitBase.getBaseType());
		
		MemberTypeTree concequent = assertKind(Kind.MEMBER_TYPE, type.getConecquent());
		assertIdentifierType("T", 0, concequent.getBaseType());
		assertSpecialType(SpecialType.NUMBER, concequent.getName());
		
		assertSpecialType(SpecialType.ANY, type.getAlternate());
	}
	
	@Test
	public void testNestedConditionalType() {
		ConditionalTypeTree type = parseType("T extends any[] ? T[number] extends any[] ? T[number][number] : T[number] : any", Kind.CONDITIONAL_TYPE);
		
		assertIdentifierType("T", 0, type.getCheckType());
		
		ArrayTypeTree limit0Base = assertKind(Kind.ARRAY_TYPE, type.getLimitType());
		assertSpecialType(SpecialType.ANY, limit0Base.getBaseType());
		
		assertSpecialType(SpecialType.ANY, type.getAlternate());
		
		
		ConditionalTypeTree inner = assertKind(Kind.CONDITIONAL_TYPE, type.getConecquent());
		
		MemberTypeTree innerCheck = assertKind(Kind.MEMBER_TYPE, inner.getCheckType());
		assertIdentifierType("T", 0, innerCheck.getBaseType());
		assertSpecialType(SpecialType.NUMBER, innerCheck.getName());
		
		ArrayTypeTree limit1Base = assertKind(Kind.ARRAY_TYPE, type.getLimitType());
		assertSpecialType(SpecialType.ANY, limit1Base.getBaseType());
		
		MemberTypeTree innerCon = assertKind(Kind.MEMBER_TYPE, inner.getConecquent());
		assertSpecialType(SpecialType.NUMBER, innerCon.getName());
		MemberTypeTree innerCon1 = assertKind(Kind.MEMBER_TYPE, innerCon.getBaseType());
		assertIdentifierType("T", 0, innerCon1.getBaseType());
		assertSpecialType(SpecialType.NUMBER, innerCon1.getName());
		
		MemberTypeTree innerAlt = assertKind(Kind.MEMBER_TYPE, inner.getAlternate());
		assertIdentifierType("T", 0, innerAlt.getBaseType());
		assertSpecialType(SpecialType.NUMBER, innerAlt.getName());
	}
	
	@Test
	public void testInvalidNestedConditionalType() {
		assertExceptionalType("T extends R extends S ? A : B ? C : D", "Conditional type not allowed as check type in conditional type");
	}
	
	@Test
	public void testInferType() {
		ConditionalTypeTree type = parseType("T extends (infer E)[] ? E : any", Kind.CONDITIONAL_TYPE);
		
		assertIdentifierType("T", 0, type.getCheckType());
		
		ArrayTypeTree limit = assertKind(Kind.ARRAY_TYPE, type.getLimitType());
		TypeTree limit1 = assertKind(Kind.INFER_TYPE, limit.getBaseType());
		fail("Not implemented");
		
		assertIdentifierType("E", 0, type.getConecquent());
		
		assertSpecialType(SpecialType.ANY, type.getAlternate());
	}
	
	@Test
	public void testKeyofType() {
		UnaryTypeTree type = parseType("keyof T", Kind.KEYOF_TYPE);
		
		assertIdentifierType("T", 0, type.getBaseType());
	}
	
	@Test
	public void testTypeQuery() {
		TypeTree type = parseType("typeof X", Kind.TYPE_QUERY);
		fail("Not implemented");
	}
	
	@Test
	public void testUnique() {
		UnaryTypeTree type = parseType("unique symbol", Kind.UNIQUE_TYPE);
		
		assertSpecialType(SpecialType.SYMBOL, type.getBaseType());
	}
	
	@Test
	public void testEmptyTuple() {
		TupleTypeTree type = parseType("[]", Kind.TUPLE_TYPE);
		assertEquals(0, type.getSlotTypes().size());
	}
	
	@Test
	public void test1Tuple() {
		TupleTypeTree type = parseType("[Foo]", Kind.TUPLE_TYPE);
		assertEquals(1, type.getSlotTypes().size());
		assertIdentifierType("Foo", 0, type.getSlotTypes().get(0));
	}
	
	@Test
	//TODO: redundant?
	public void testPrimitiveTupleType() {
		TupleTypeTree type = parseType("[string, number]", Kind.TUPLE_TYPE);
		assertEquals(2, type.getSlotTypes().size());
		
		assertSpecialType(SpecialType.STRING, type.getSlotTypes().get(0));
		assertSpecialType(SpecialType.NUMBER, type.getSlotTypes().get(1));
	}
	
	@Test
	public void testTuple() {
		TupleTypeTree type = parseType("[Foo, Bar]", Kind.TUPLE_TYPE);
		assertEquals(2, type.getSlotTypes().size());
		assertIdentifierType("Foo", 0, type.getSlotTypes().get(0));
		assertIdentifierType("Bar", 0, type.getSlotTypes().get(1));
	}
	
	@Test
	public void testTupleOptionalElement() {
		TupleTypeTree type = parseType("[Foo, Bar?]", Kind.TUPLE_TYPE);
		assertEquals(2, type.getSlotTypes().size());
		
		assertIdentifierType("Foo", 0, type.getSlotTypes().get(0));
		
		UnaryTypeTree slot1 = assertKind(Kind.OPTIONAL_TYPE, type.getSlotTypes().get(1));
		assertIdentifierType("Bar", 0, slot1.getBaseType());
	}
	
	@Test
	public void testTupleRestElement() {
		TupleTypeTree type = parseType("[Foo, ...Bar]", Kind.TUPLE_TYPE);
		assertEquals(2, type.getSlotTypes().size());
		
		assertIdentifierType("Foo", 0, type.getSlotTypes().get(0));
		fail("Not implemented");
	}
	
	@Test
	public void testSimpleObjectType() {
		ObjectTypeTree type = parseType("{ a: Foo }", Kind.OBJECT_TYPE);
		assertEquals(1, type.getDeclaredMembers().size());
		
		PropertyDeclarationTree prop0 = assertKind(Kind.PROPERTY_DECLARATION, type.getDeclaredMembers().get(0));
		assertIdentifier("a", prop0.getName());
		assertIdentifierType("Foo", 0, prop0.getType());
	}
	
	@Test
	public void testIndexType() {
		ObjectTypeTree type = parseType("{[K: keyof T]: T[K]!}", Kind.OBJECT_TYPE);
		IndexSignatureTree idx = assertSingleElementKind(Kind.INDEX_SIGNATURE, type.getDeclaredMembers());
		
		TypeParameterDeclarationTree idxTp = idx.getIndexType();
		assertIdentifier("K", idxTp.getName());
		UnaryTypeTree idxSuper = assertKind(Kind.KEYOF_TYPE, idxTp.getSupertype());
		assertIdentifierType("T", 0, idxSuper.getBaseType());
		
		UnaryTypeTree res = assertKind(Kind.DEFINITE_TYPE, idx.getReturnType());
		MemberTypeTree res1 = assertKind(Kind.MEMBER_TYPE, res.getBaseType());
		assertIdentifierType("T", 0, res1.getBaseType());
		assertIdentifierType("K", 0, res1.getName());
		fail("Not implemented");
	}
	
	@Test
	public void testMappedType() {
		MappedTypeTree type = parseType("{[K in keyof T]: T[K]}", Kind.MAPPED_TYPE);
		assertEquals(Modifiers.NONE, type.getModifiers());
		
		TypeParameterDeclarationTree param = type.getParameter();
		assertIdentifier("K", param.getName());
		UnaryTypeTree query = assertKind(Kind.KEYOF_TYPE, param.getSupertype());
		assertIdentifierType("T", 0, query.getBaseType());
		
		MemberTypeTree rhs = assertKind(Kind.MEMBER_TYPE, type.getType());
		assertIdentifierType("T", 0, rhs.getBaseType());
		assertIdentifierType("K", 0, rhs.getName());
	}
	
	@Test
	public void testEmptyCallSignatureType() {
		FunctionTypeTree type = parseType("() => R", Kind.FUNCTION_TYPE);
		assertEquals(0, type.getTypeParameters().size());
		assertEquals(0, type.getParameters().size());
		
		assertIdentifierType("R", 0, type.getReturnType());
	}
	
	@Test
	public void testCallSignatureType() {
		FunctionTypeTree type = parseType("(a: number, b: number) => number", Kind.FUNCTION_TYPE);
		assertEquals(0, type.getTypeParameters().size());
		assertEquals(2, type.getParameters().size());
		
		ParameterTree param0 = type.getParameters().get(0);
		assertIdentifier("a", param0.getName());
		assertSpecialType(SpecialType.NUMBER, param0.getType());
		
		ParameterTree param1 = type.getParameters().get(1);
		assertIdentifier("b", param1.getName());
		assertSpecialType(SpecialType.NUMBER, param1.getType());
		
		assertSpecialType(SpecialType.NUMBER, type.getReturnType());
	}
	
	@Test
	public void testTypeParamCallSignatureType() {
		FunctionTypeTree type = parseType("<T>(a: T, b: T) => T", Kind.FUNCTION_TYPE);
		assertEquals(1, type.getTypeParameters().size());
		assertEquals(2, type.getParameters().size());
		
		TypeParameterDeclarationTree tParam0 = type.getTypeParameters().get(0);
		assertIdentifier("T", tParam0.getName());
		
		ParameterTree param0 = type.getParameters().get(0);
		assertIdentifier("a", param0.getName());
		assertIdentifierType("T", 0, param0.getType());
		
		ParameterTree param1 = type.getParameters().get(1);
		assertIdentifier("b", param1.getName());
		assertIdentifierType("T", 0, param1.getType());
		
		assertIdentifierType("T", 0, type.getReturnType());
	}
	
	@Test
	public void testEvilTypeParamCallSignatureType() {
		FunctionTypeTree type = parseType("<B extends object = {}, K extends keyof B = keyof B>(b: B, keys: K[]) => {[key: K]: B[key]}", Kind.FUNCTION_TYPE);
		assertEquals(2, type.getTypeParameters().size());
		assertEquals(2, type.getParameters().size());
		
		TypeParameterDeclarationTree tParam0 = type.getTypeParameters().get(0);
		assertIdentifier("B", tParam0.getName());
		assertIdentifierType("object", 0, tParam0.getSupertype());//TODO: is object special?
		ObjectTypeTree tp0Default = assertKind(Kind.OBJECT_TYPE, tParam0.getDefault());
		assertEquals(0, tp0Default.getDeclaredMembers().size());
		
		TypeParameterDeclarationTree tParam1 = type.getTypeParameters().get(1);
		assertIdentifier("K", tParam1.getName());
		UnaryTypeTree k0b1 = assertKind(Kind.KEYOF_TYPE, tParam1.getSupertype());
		assertIdentifierType("B", 0, k0b1.getBaseType());
		UnaryTypeTree k0b2 = assertKind(Kind.KEYOF_TYPE, tParam1.getDefault());
		assertIdentifierType("B", 0, k0b2.getBaseType());
		
		ParameterTree param0 = type.getParameters().get(0);
		assertIdentifier("b", param0.getName());
		assertIdentifierType("B", 0, param0.getType());
		
		ParameterTree param1 = type.getParameters().get(1);
		assertIdentifier("keys", param1.getName());
		ArrayTypeTree param1Base = assertKind(Kind.ARRAY_TYPE, param1.getType());
		assertIdentifierType("K", 0, param1Base.getBaseType());
		
		
		ObjectTypeTree result = assertKind(Kind.OBJECT_TYPE, type.getReturnType());
		IndexSignatureTree idx = assertSingleElementKind(Kind.INDEX_SIGNATURE, result.getDeclaredMembers());
		
		TypeParameterDeclarationTree idxTp = idx.getIndexType();
		System.out.println(idxTp);
		assertIdentifier("key", idxTp.getName());
		assertIdentifierType("K", 0, idxTp.getSupertype());
		
		MemberTypeTree idxR = assertKind(Kind.MEMBER_TYPE, idx.getReturnType());
		assertIdentifierType("B", 0, idxR.getBaseType());
		assertIdentifierType("key", 0, idxR.getName());
	}
	
	@Test
	public void testNullConstructSignatureType() {
		ConstructorTypeTree type = parseType("new () => R", Kind.CONSTRUCTOR_TYPE);
		assertEquals(0, type.getTypeParameters().size());
		assertEquals(0, type.getParameters().size());
		
		assertIdentifierType("R", 0, type.getReturnType());
	}
	
	@Test
	public void testConstructSignatureType() {
		ConstructorTypeTree type = parseType("new (p0: string, p1?: number, ...args: any[]) => R", Kind.CONSTRUCTOR_TYPE);
		assertEquals(0, type.getTypeParameters().size());
		assertEquals(3, type.getParameters().size());
		
		ParameterTree param0 = type.getParameters().get(0);
		assertIdentifier("p0", param0.getName());
		assertEquals(Modifiers.NONE, param0.getModifiers());
		assertSpecialType(SpecialType.STRING, param0.getType());
		
		ParameterTree param1 = type.getParameters().get(1);
		assertIdentifier("p1", param1.getName());
		assertEquals(Modifiers.OPTIONAL, param1.getModifiers());
		assertSpecialType(SpecialType.NUMBER, param1.getType());
		
		ParameterTree param2 = type.getParameters().get(2);
		assertIdentifier("args", param2.getName());
		assertEquals(Modifiers.NONE, param2.getModifiers());
		assertTrue(param2.isRest());
		ArrayTypeTree p2Base = assertKind(Kind.ARRAY_TYPE, param2.getType());
		assertSpecialType(SpecialType.ANY, p2Base.getBaseType());
		
		assertIdentifierType("R", 0, type.getReturnType());
	}
}
