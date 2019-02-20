package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

public class BinaryExpressionTest {
	@Test
	public void testBinaryExpression() {
		BinaryExpressionTree expr = parseExpression("a>b", Kind.GREATER_THAN);
		assertIdentifier("a", expr.getLeftOperand());
		assertIdentifier("b", expr.getRightOperand());
	}

	@Test
	public void testNormalLTR() {
		// Non-commutative ltr associativity
		BinaryExpressionTree expr = parseExpression("a<<b<<c", Kind.LEFT_SHIFT);
		assertIdentifier("c", expr.getRightOperand());
		BinaryExpressionTree left = assertKind(Kind.LEFT_SHIFT, expr.getLeftOperand());
		assertIdentifier("a", left.getLeftOperand());
		assertIdentifier("b", left.getRightOperand());
	}

	@Test
	public void testExponentiationRTL() {
		BinaryExpressionTree expr = parseExpression("a**b**c", Kind.EXPONENTIATION);
		assertIdentifier("a", expr.getLeftOperand());
		
		BinaryExpressionTree right = assertKind(Kind.EXPONENTIATION, expr.getRightOperand());
		assertIdentifier("b", right.getLeftOperand());
		assertIdentifier("c", right.getRightOperand());
	}
	
	@Test
	public void testArrayAccess() {
		BinaryExpressionTree expr = parseExpression("a[b]", Kind.ARRAY_ACCESS);
		assertIdentifier("a", expr.getLeftOperand());
		assertIdentifier("b", expr.getRightOperand());
	}
	
	@Test
	public void testMemberSelect() {
		BinaryExpressionTree expr = parseExpression("a.b", Kind.MEMBER_SELECT);
		assertIdentifier("a", expr.getLeftOperand());
		assertIdentifier("b", expr.getRightOperand());
	}
	
	@Test
	public void testOrderOfOperationsSimple() {
		//(a+(b*c))
		BinaryExpressionTree expr = parseExpression("a+b*c", Kind.ADDITION);
		
		assertIdentifier("a", expr.getLeftOperand());
		
		BinaryExpressionTree right = assertKind(Kind.MULTIPLICATION, expr.getRightOperand());
		assertIdentifier("b", right.getLeftOperand());
		assertIdentifier("c", right.getRightOperand());
	}
	
	@Test
	public void testOrderOfOperationsParentheses() {
		BinaryExpressionTree expr = parseExpression("(a+b)*c", Tree.Kind.MULTIPLICATION);
		
		assertIdentifier("c", expr.getRightOperand());
		
		ParenthesizedTree _left = assertKind(Kind.PARENTHESIZED, expr.getLeftOperand());
		BinaryExpressionTree left = assertKind(Kind.ADDITION, _left.getExpression());
		assertEquals(Tree.Kind.ADDITION, left.getKind());
		assertIdentifier("a", left.getLeftOperand());
		assertIdentifier("b", left.getRightOperand());
	}
	
	/*@Ignore ("Profiling only")
	@Test
	public void profile() {
		String[] exprs = new String[100];
		String[] ops = new String[]{"+","-","*","/","<<",">>",">>>","|","&","||","&&","^","==","!=","===","!==","<",">","<=",">=",".","**"};
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
		long[] times = new long[exprs.length];
		JSParser parser = new JSParser();
		for (int i = 0, l = times.length; i < l; i++) {
			JSLexer lexer = new JSLexer(exprs[i]);
			JSParser.Context context = new JSParser.Context();
			long start = System.nanoTime();
			parser.parseNextExpression(lexer, context);
			long end = System.nanoTime();
			long delta = end - start;
			times[i] = delta;
		}
		System.out.println(Arrays.toString(times));
		long[] sorted = Arrays.copyOf(times, times.length);
		Arrays.sort(sorted);
		{
			long q1 = sorted[(int) (sorted.length * .25)];
			long q3 = sorted[(int) (sorted.length * .75)];
			System.out.println("Min: " + sorted[0]);
			System.out.println("Q1:  " + q1);
			System.out.println("Med: " + sorted[sorted.length / 2]);
			System.out.println("Q3:  " + q3);
			System.out.println("Max: " + sorted[sorted.length - 1]);
		}
		double avg = 0;
		for (int i = 0, l = sorted.length; i < l; i++)
			avg += times[i];
		avg/=sorted.length;
		System.out.println("Avg: " + avg);
		double stddev = 0;
		for (int i = 0, l = sorted.length; i < l; i++) {
			double diff = times[i] - avg;
			stddev += diff * diff;
		}
		stddev = Math.sqrt(stddev/sorted.length);
		System.out.println("Stddev: " + stddev);
	}*/
}
