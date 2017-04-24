package com.mindlin.jsast.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.impl.writer.JSWriterImpl;
import com.mindlin.jsast.tree.CompilationUnitTree;
import com.mindlin.jsast.writer.JSWriter;
import com.mindlin.jsast.writer.JSWriterOptions;

public class ParseEverythingJS {
	protected static final String EVERYTHINGJS_URL = "https://rawgit.com/michaelficarra/everything.js/master/es2015-script.js";
	public static void main(String...fred) throws IOException {
		URL url = new URL(EVERYTHINGJS_URL);
		String text;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				BufferedWriter bw = Files.newBufferedWriter(Paths.get("everything-base1.js"))) {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[4096];
			while (br.ready()) {
				int len = br.read(buffer, 0, buffer.length);
				sb.append(buffer, 0, len);
			}
			text = sb.toString();
			bw.write(text);
		}
		
		System.out.println("Tokenizing...");
		JSLexer lexer = new JSLexer(text);
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("everything-tokens.js"))) {
			while (!lexer.isEOF()) {
				bw.write(lexer.nextToken().toString());
				bw.write('\n');
			}
		}
		
		JSParser parser = new JSParser();
		System.out.println("Parsing...");
		CompilationUnitTree ast = parser.apply("everything.js", text);
		
		System.out.println("Writing JSON...");
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("everything.js.json"))) {
			bw.write(ast.toString());
		}
		
		System.out.println("Writing JS...");
		JSWriterOptions options = new JSWriterOptions();
		options.indentStyle = "\t";
		JSWriter writer = new JSWriterImpl(options);
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("everything.js"))) {
			writer.write(ast, bw);
		}
		
		System.out.println("Re-parsing...");
		//Re-read it
		try (BufferedReader br = Files.newBufferedReader(Paths.get("everything.js"))) {
			StringBuilder sb = new StringBuilder();
			while (br.ready()) {
				sb.append(br.readLine());
				sb.append("\n");
			}
			text = sb.toString();
		}
		CompilationUnitTree ast2 = parser.apply("everything.1.js", text);
	}
}
