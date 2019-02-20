package com.mindlin.nautilus.example;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;

import com.mindlin.nautilus.impl.parser.JSParser;
import com.mindlin.nautilus.impl.runtime.JSScriptEngine;
import com.mindlin.nautilus.impl.writer.JSWriterImpl;
import com.mindlin.nautilus.transform.DeadCodeRemovalTransformation;
import com.mindlin.nautilus.transform.ES6Transpiler;
import com.mindlin.nautilus.transform.ExpressionFixerTf;
import com.mindlin.nautilus.transform.ExpressionFlattenerTransformation;
import com.mindlin.nautilus.transform.TransformerSeries;
import com.mindlin.nautilus.tree.CompilationUnitTree;
import com.mindlin.nautilus.writer.JSWriterOptions;

public class REPL {
	public static void main(String[] args) throws IOException {
		JSParser parser = new JSParser();
		
		JSWriterOptions options = new JSWriterOptions();
		options.indentStyle = "\t";
		JSWriterImpl writer = new JSWriterImpl(options);
		
		TransformerSeries preTransformer = new TransformerSeries();
		TransformerSeries transformer = new TransformerSeries(new ExpressionFlattenerTransformation(), new DeadCodeRemovalTransformation());
		TransformerSeries postTransformer = new TransformerSeries(new ExpressionFixerTf(), new ES6Transpiler());
		
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		
		JSScriptEngine engine = new JSScriptEngine();
		
		
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
			
			ast = preTransformer.apply(ast);
			ast = transformer.apply(ast);
			ast = postTransformer.apply(ast);
			out = new StringWriter();
			writer.write(ast, out);
			System.out.println(out.toString());
			System.out.println(ast);
			
			try {
				System.out.print("Evaluating...");
				System.out.println(engine.eval(sb.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
