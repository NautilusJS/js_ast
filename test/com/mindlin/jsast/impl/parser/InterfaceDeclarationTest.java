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
		InterfaceDeclarationTree iface = parseStatement("interface HasName {name: string;}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("HasName", iface.getName());
		
		PropertyDeclarationTree prop0 = assertSingleElementKind(Kind.PROPERTY_DECLARATION, iface.getDeclaredMembers());
		
		fail("Not finished");
	}
	
	@Test
	public void testMultipleMembers() {
		InterfaceDeclarationTree iface = parseStatement("interface TwoPartName {fName: string; lName: string;}", Kind.INTERFACE_DECLARATION);
		assertIdentifier("TwoPartName", iface.getName());
		
		fail("Not finished");
	}
	
	@Test
	public void testSimpleMethodSignature() {
		InterfaceDeclarationTree iface = parseStatement("interface HasFoo { foo(); }", Kind.INTERFACE_DECLARATION);
		
		fail("Not finished");
	}
	
	@Test
	public void testConstructorSignature() {
		InterfaceDeclarationTree iface = parseStatement("interface HasCtor { new (); }", Kind.INTERFACE_DECLARATION);
		
		fail("Not finished");
	}
	
	@Test
	public void testReadonlyProperty() {
		InterfaceDeclarationTree iface = parseStatement("interface Foo { readonly bar: string; }", Kind.INTERFACE_DECLARATION);
		
		fail("Not finished");
	}
	
	// Some extra punctuation checks
	@Test
	public void testCommaSeparated() {
		
	}
	
	@Test
	public void testNoEndPunctuation() {
		
	}
}
