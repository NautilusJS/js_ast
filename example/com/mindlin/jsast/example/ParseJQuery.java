package com.mindlin.jsast.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.tree.CompilationUnitTree;

public class ParseJQuery {
	protected static final String JQUERY_URL = "https://code.jquery.com/jquery-3.2.1.js";
	public static void main(String...fred) throws IOException {
		URL url = new URL(JQUERY_URL);
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
		CompilationUnitTree ast = parser.apply("jquery-3.2.1.js", text);
		System.out.println("Done");
		
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("jquery-3.2.1.js.json"))) {
			bw.write(ast.toString());
		}
	}
}
