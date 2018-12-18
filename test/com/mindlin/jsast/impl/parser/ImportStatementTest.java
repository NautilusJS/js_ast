package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.ImportDeclarationTree;
import com.mindlin.jsast.tree.ImportSpecifierTree;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;
public class ImportStatementTest {

	/**
	 * Pretty exhaustive tests for {@code import} statement parsing.
	 * I honestly can't think of any (non-trivial) test cases that could be added.
	 */
	@Test
	public void testThingsThatShouldntWork() {
		final String msg = "Failed to throw error on illegal import statement";
		//Things that *shouldn't* work
		assertExceptionalStatement("import ;", msg);
		assertExceptionalStatement("import foo;", msg);
		assertExceptionalStatement("import from 'foo.js';", msg);
		assertExceptionalStatement("import def 'foo.js';", msg);
		assertExceptionalStatement("import * 'foo.js';", msg);
		assertExceptionalStatement("import * as def 'foo.js';", msg);
		assertExceptionalStatement("import * from 'foo.js';", msg);
		assertExceptionalStatement("import {} from 'foo.js';", msg);
		assertExceptionalStatement("import {*} from 'foo.js';", msg);
		assertExceptionalStatement("import {* as def} from 'foo.js';", msg);
		assertExceptionalStatement("import {def} 'foo.js';", msg);
		assertExceptionalStatement("import {def, *} from 'foo.js';", msg);
		assertExceptionalStatement("import {def ghi} from 'foo.js';", msg);
		assertExceptionalStatement("import * as bar;", msg);
		assertExceptionalStatement("import * as bar, def;", msg);
		assertExceptionalStatement("import * as a, {foo as bar} from 'foo.js';", msg);
		assertExceptionalStatement("import {foo as bar from 'foo.js';", msg);
		assertExceptionalStatement("import {'hello' as world} from 'foo.js';", msg);
		assertExceptionalStatement("import {hello as 'world'} from 'foo.js';", msg);
		assertExceptionalStatement("import default from 'foo.js';", msg);
	}
	
	//Things that *should* work
	//Examples taken from developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import
	@Test
	public void testImportDefaultMember() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import defaultMember from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(1, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier = impt.getSpecifiers().get(0);
		assertIdentifier("defaultMember", specifier.getImported());
		assertIdentifier("defaultMember", specifier.getAlias());
		assertEquals(specifier.getImported(), specifier.getAlias());
		assertTrue(specifier.isDefault());
	}
	
	@Test
	public void testImportWildcard() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import * as name from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(1, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier = impt.getSpecifiers().get(0);
		assertIdentifier("*", specifier.getImported());
		assertIdentifier("name", specifier.getAlias());
		assertFalse(specifier.isDefault());
	}
	
	@Test
	public void testImportSingleNamed() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import { member } from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(1, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier = impt.getSpecifiers().get(0);
		assertIdentifier("member", specifier.getImported());
		assertIdentifier("member", specifier.getAlias());
		assertEquals(specifier.getImported(), specifier.getAlias());
		assertFalse(specifier.isDefault());
	}
	@Test
	public void testImportSingleAliased() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import { member as alias } from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(1, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
		assertIdentifier("member", specifier0.getImported());
		assertIdentifier("alias", specifier0.getAlias());
		assertFalse(specifier0.isDefault());
	}
	@Test
	public void testImportMultipleNamed() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import { member1 , member2 } from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(2, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
		assertIdentifier("member1", specifier0.getImported());
		assertIdentifier("member1", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertFalse(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
		assertIdentifier("member2", specifier1.getImported());
		assertIdentifier("member2", specifier1.getAlias());
		assertEquals(specifier1.getImported(), specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	@Test
	public void testImportMultipleMixed() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import { member1 , member2 as alias2 } from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(2, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
		assertIdentifier("member1", specifier0.getImported());
		assertIdentifier("member1", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertFalse(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
		assertIdentifier("member2", specifier1.getImported());
		assertIdentifier("alias2", specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	@Test
	public void testImportDefaultAndSingleNamed() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import defaultMember, { member } from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(2, impt.getSpecifiers().size());
		
		ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
		assertIdentifier("defaultMember", specifier0.getImported());
		assertIdentifier("defaultMember", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertTrue(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
		assertIdentifier("member", specifier1.getImported());
		assertIdentifier("member", specifier1.getAlias());
		assertEquals(specifier1.getImported(), specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	@Test
	public void testImportDefaultAndWildcardAliased() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import defaultMember, * as name from 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(2, impt.getSpecifiers().size());
		ImportSpecifierTree specifier0 = impt.getSpecifiers().get(0);
		assertIdentifier("defaultMember", specifier0.getImported());
		assertIdentifier("defaultMember", specifier0.getAlias());
		assertEquals(specifier0.getImported(), specifier0.getAlias());
		assertTrue(specifier0.isDefault());
		
		ImportSpecifierTree specifier1 = impt.getSpecifiers().get(1);
		assertIdentifier("*", specifier1.getImported());
		assertIdentifier("name", specifier1.getAlias());
		assertFalse(specifier1.isDefault());
	}
	@Test
	public void testImportRaw() {
		ImportDeclarationTree impt = (ImportDeclarationTree)parseStatement("import 'module-name';");
		assertLiteral("module-name", impt.getSource());
		assertEquals(0, impt.getSpecifiers().size());
	}
}
