package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.tree.BinaryTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class BinaryExpressionTest {
	@Test
	public void testBinaryExpression() {
		ExpressionTree expr = parseExpression("a>b");
		assertEquals(Kind.GREATER_THAN, expr.getKind());
		BinaryTree binary = (BinaryTree) expr;
		assertIdentifier("a", binary.getLeftOperand());
		assertIdentifier("b", binary.getRightOperand());
	}

	@Test
	public void testNormalLTR() {
		// Non-commutative ltr associativity
		BinaryTree expr = parseExpression("a<<b<<c");
		System.out.println(expr);
		assertEquals(Kind.LEFT_SHIFT, expr.getKind());
		assertIdentifier("c", expr.getRightOperand());
		assertEquals(Kind.LEFT_SHIFT, expr.getLeftOperand().getKind());
		BinaryTree left = (BinaryTree) expr.getLeftOperand();
		assertIdentifier("a", left.getLeftOperand());
		assertIdentifier("b", left.getRightOperand());
	}

	@Test
	public void testExponentiationRTL() {
		BinaryTree expr = parseExpression("a**b**c");
		System.out.println(expr);
		assertEquals(Kind.EXPONENTIATION, expr.getKind());
		assertIdentifier("a", expr.getLeftOperand());
		assertEquals(Kind.EXPONENTIATION, expr.getRightOperand().getKind());
		BinaryTree right = (BinaryTree) expr.getRightOperand();
		assertIdentifier("b", right.getLeftOperand());
		assertIdentifier("c", right.getRightOperand());
	}
	
	@Test
	public void profile() {
		String[] exprs = new String[500];
		String[] ops = new String[]{"+","-","*","/","<<",">>",">>>","|","&","||","&&","^","==","!=","===","!==","<",">","<=",">="};
		String vars = "bcdefghijklmnopqrstuvwxyz";
		Random r = new Random();
		for (int i = 0; i < exprs.length; i++) {
			StringBuffer sb = new StringBuffer();
			int numOps = r.nextInt(10) + 1;
			sb.append("a");
			for (int j = 0; j < numOps; j++)
				sb.append(ops[r.nextInt(ops.length)]).append(vars.charAt(j));
			exprs[i] = sb.toString();
		}
		long[] times = new long[500];
		JSParser parser = new JSParser();
		for (int i = 0; i < 500; i++) {
			System.out.println(exprs[i]);
			JSLexer lexer = new JSLexer(exprs[i]);
			JSParser.Context context = new JSParser.Context();
			long start = System.nanoTime();
			parser.parseNextExpression(lexer, context);
			long end = System.nanoTime();
			long delta = end - start;
			times[i] = delta;
		}
		System.out.println(Arrays.toString(times));
		double avg = 0;
		for (int i = 0; i < 500; i++)
			avg += times[i];
		avg/=500.0;
		double stddev = 0;
		for (int i = 0; i < 500; i++) {
			double diff = times[i] - avg;
			stddev += diff * diff;
		}
		stddev = Math.sqrt(stddev);
		System.out.println("Avg: " + avg + "; stddev: " + stddev);
	}
}
