package mainapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
		// -= lexing =-
		System.out.println("Lexing...");
		String source = slurpFile(args[0]);
		Lexer lexer = new Lexer(source);
		ArrayList<Token> tokens = lexer.lex();
		
		for (Token token : tokens) {
			System.out.println(token.toString());
		}
		
		// -= parsing =-
		System.out.println("\nParsing...");
		Parser parser = new Parser(tokens);
		ScopeStatement root = parser.parse();
		
		// -= visiting =-
		System.out.println("\nVisiting...");
		parser.visit();
		
		// -= interpreting =-
		System.out.println("\nInterpreting...");
		Interpreter interpreter = new Interpreter(root);
		interpreter.interpret();
		
		// -= done =-
		System.out.println("\nDone!");
	}
	
	private static String slurpFile(String filename) {
		try {
			return new String(Files.readAllBytes(Paths.get(filename)));
		} catch (IOException e) {
			return "";
		}
	}
}
