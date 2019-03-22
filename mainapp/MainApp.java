package mainapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import interpreter.Interpreter;
import lexer.Lexer;
import lexer.tokens.Token;
import parser.Parser;
import parser.statements.ScopeStatement;

/** @author Miguel Arseneault */
public class MainApp {
	private String[] args;
	
	public static void main(String[] args) {
		MainApp app = new MainApp(args);
		app.run();
	}
	
	public MainApp(String[] args) {
		this.args = args;
	}
	
	public void run() {
		// -= input =-
		String source = null;
		if (args.length >= 1) {
			source = slurpFile(args[0]);
		}
		
		boolean verbose = false;
		if (args.length >= 2 && args[1].equals("-v")) {
			verbose = true;
		}
		
		// -= lexing =-
		if (verbose) {
			System.out.println("Lexing...");
		}
		
		Lexer lexer = new Lexer(source);
		ArrayList<Token> tokens = lexer.lex();
		
		if (verbose) {
			for (Token token : tokens) {
				System.out.println(token.toString());
			}
			System.out.println();
		}
		
		// -= parsing =-
		if (verbose) {
			System.out.println("Parsing...");
		}
		
		Parser parser = new Parser(tokens, verbose);
		ScopeStatement root = parser.parse();
		
		if (verbose) {
			System.out.println();
		}
		
		// -= visiting =-
		if (verbose) {
			System.out.println("Visiting...");
			parser.visit();
			System.out.println();
		}
		
		// -= interpreting =-
		System.out.println("Interpreting...");
		Interpreter interpreter = new Interpreter(root);
		interpreter.interpret();
		System.out.println();
		
		// -= done =-
		System.out.println("Done!");
	}
	
	private static String slurpFile(String filename) {
		StringBuilder content = new StringBuilder();
		
		Scanner file = null;
		try {
			file = new Scanner(new File(filename));
			while (file.hasNextLine()) {
				content.append(file.nextLine());
				if (file.hasNextLine()) {
					content.append('\n');
				}
			}
		} catch (IOException ex) {
			content.delete(0, content.length());
		} finally {
			file.close();
		}
		
		return content.toString();
	}
}
