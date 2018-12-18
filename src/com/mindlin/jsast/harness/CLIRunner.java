package com.mindlin.jsast.harness;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CLIRunner implements Callable<Integer> {
	public static int EXIT_SUCCESS = 0;
	public static int EXIT_FAILURE = 1;
	/**
	 * Return code for if something unexpected happened
	 */
	public static int EXIT_PRETTY_BAD = -1;
	
	protected PrintStream out = System.out;
	protected PrintStream err = System.err;
	protected CompilerOptions options = new CompilerOptions();
	
	public CLIRunner(String...args) {
		options.parseCLIArguments(args);
	}
	
	public int printHelp() {
		//TODO: finish
		out.println("<<help message>>");
		return EXIT_SUCCESS;
	}
	
	public int callInternal() throws RuntimeException {
		if (options.get(CompilerOptions.PRINT_HELP).isPresent())
			return this.printHelp();
		
		List<CompilerPass> passes = new ArrayList<>();
		passes.add(new ReadPass(options));
		passes.add(new BindPass(options));
		passes.add(new CheckPass(options));
		passes.add(new TransformPass(options));
		
		Compiler compiler = new Compiler(options, passes);
		try {
			compiler.compile();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return EXIT_SUCCESS;
	}

	@Override
	public Integer call() {
		try {
			return this.callInternal();
		} catch (Exception e) {
			e.printStackTrace(this.err);
			return EXIT_PRETTY_BAD;
		}
	}
	
	public static void main(String...args) {
		CLIRunner runner = new CLIRunner(args);
		
		int result;
		try {
			result = runner.call();
		} catch (Exception e) {
			result = -1;
		}
		System.exit(result);
	}
}
