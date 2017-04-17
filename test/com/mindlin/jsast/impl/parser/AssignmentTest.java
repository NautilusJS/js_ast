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
		assertIdentifier("x", assignment.getLeftOperand());
		assertIdentifier("y", assignment.getRightOperand());
	}
	
	@Test
	public void testChainedAssignment() {
		AssignmentTree assignment = parseExpression("x=y=z", Kind.ASSIGNMENT);
		System.out.println(assignment);
		assertIdentifier("x", assignment.getLeftOperand());
		assertEquals(Tree.Kind.ASSIGNMENT, assignment.getRightOperand().getKind());
		
		AssignmentTree yzAssignment = (AssignmentTree) assignment.getRightOperand();
		assertIdentifier("y", yzAssignment.getLeftOperand());
		assertIdentifier("z", yzAssignment.getRightOperand());
	}
	
	@Test
	public void testAssignmentToLiteral() {
		String errorMessagePrefix = "Assignment may not be made to ";
//		assertExceptionalExpression("y++=x", errorMessagePrefix + "numeric literals");
		assertExceptionalExpression("'hello'=x", errorMessagePrefix + "string literals");
		assertExceptionalExpression("true=x", errorMessagePrefix + "boolean literals");
	}
}
