package com.mindlin.jsast.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
			StringBuilder sb = new StringBuilder();
			while (br.ready()) {
				sb.append(br.readLine());
				sb.append("\n");
			}
			text = sb.toString();
		}
		JSParser parser = new JSParser();
		System.out.println("Parsing...");
		CompilationUnitTree ast = parser.apply("everything.js", text);
		System.out.println("Done");
		
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("everything.js.json"))) {
			bw.write(ast.toString());
		}
		
		JSWriterOptions options = new JSWriterOptions();
		options.indentStyle = "\t";
		JSWriter writer = new JSWriterImpl(options);
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("everything.js"))) {
			writer.write(ast, bw);
		}
		
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
