package com.mindlin.jsast.example;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;

import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.impl.writer.JSWriterImpl;
import com.mindlin.jsast.transform.ASTTransformer;
import com.mindlin.jsast.transform.ASTTransformerContext;
import com.mindlin.jsast.transform.ExpressionFlattenerTransformation;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.writer.JSWriterOptions;

public class REPL {
	public static void main(String[] args) throws IOException {
		JSParser parser = new JSParser();
		JSWriterOptions options = new JSWriterOptions();
		options.indentStyle = "\t";
		JSWriterImpl writer = new JSWriterImpl(options);
		ASTTransformer<ASTTransformerContext> transformer = new ASTTransformer<>(new ExpressionFlattenerTransformation());
		Scanner s = new Scanner(System.in);
		System.out.println("Nautilus JS transpiler");
		while (true) {
			StringBuffer sb = new StringBuffer();
			while (true) {
				System.out.print(">>> ");
				String line = s.nextLine().trim();
				if (line.endsWith("\\")) {
					sb.append(line, 0, line.length() - 1);
					sb.append('\n');
				} else {
					sb.append(line);
					break;
				}
			}
			CompilationUnitTree ast = parser.apply("tmp", sb.toString());
//			System.out.print(ast);
			StringWriter out = new StringWriter();
			writer.write(ast, out);
			System.out.println(out.toString());
			
			ast = (CompilationUnitTree) ast.accept(transformer, null);
			out = new StringWriter();
			writer.write(ast, out);
			System.out.println(out.toString());
//			System.err.println(ast);
		}
	}
}
