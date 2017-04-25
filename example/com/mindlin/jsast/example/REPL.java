package com.mindlin.jsast.example;

import java.util.Scanner;

import com.mindlin.jsast.impl.parser.JSParser;
import com.mindlin.jsast.tree.CompilationUnitTree;

public class REPL {
	public static void main(String[] args) {
		JSParser parser = new JSParser();
		Scanner s = new Scanner(System.in);
		while (true) {
			String line = s.nextLine();
			CompilationUnitTree ast = parser.apply("tmp", line);
			System.out.println(ast);
		}
	}
}
