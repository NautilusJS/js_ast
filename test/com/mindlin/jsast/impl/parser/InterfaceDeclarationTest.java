package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.PropertyDeclarationTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.type.InterfaceDeclarationTree;

public class InterfaceDeclarationTest {
	
	@Test
	public void testEmptyInterfaceDeclaration() {
		InterfaceDeclarationTree iface = parseStatement("interface Foo{}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("Foo", iface.getName());
	}
	
	@Test
	public void testSingleMember() {
		InterfaceDeclarationTree iface = parseStatement("interface Foo {name: string;}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("Foo", iface.getName());
		
		PropertyDeclarationTree prop0 = assertSingleElementKind(Kind.PROPERTY_SIGNATURE, iface.getDeclaredMembers());
		
	}
	
	@Test
	public void testMultipleMembers() {
		InterfaceDeclarationTree iface = parseStatement("interface Foo {fName: string; lName: string;}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("Foo", iface.getName());
	}
	
	@Test
	public void testSimpleMethodSignature() {
		
	}
	
	@Test
	public void testConstructorSignature() {
		
	}
	
	@Test
	public void testReadonly() {
		
	}
	
	@Test
	public void testAccessorDeclaration() {
		
	}
	
	// Some extra punctuation checks
	@Test
	public void testCommaSeparated() {
		
	}
	
	@Test
	public void testNoEndPunctuation() {
		
	}
}
