package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import org.junit.Test;

import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

public class AssignmentTest {
	@Test
	public void testAssignment() {
		AssignmentTree assignment = parseExpression("x=y", Kind.ASSIGNMENT);
		assertIdentifier("x", assignment.getVariable());
		assertIdentifier("y", assignment.getValue());
	}
	
	@Test
	public void testChainedAssignment() {
		AssignmentTree assignment = parseExpression("x=y=z", Kind.ASSIGNMENT);
		assertIdentifier("x", assignment.getVariable());
		assertEquals(Tree.Kind.ASSIGNMENT, assignment.getValue().getKind());
		
		AssignmentTree yzAssignment = (AssignmentTree) assignment.getValue();
		assertIdentifier("y", yzAssignment.getVariable());
		assertIdentifier("z", yzAssignment.getValue());
	}
	
	@Test
	public void testAssignmentToLiteral() {
		String errorMessagePrefix = "Assignment may not be made to ";
//		assertExceptionalExpression("y++=x", errorMessagePrefix + "numeric literals");
		assertExceptionalExpression("'hello'=x", errorMessagePrefix + "string literals");
		assertExceptionalExpression("true=x", errorMessagePrefix + "boolean literals");
		assertExceptionalExpression("~x=y", errorMessagePrefix + "opaque expressions");
	}
}
