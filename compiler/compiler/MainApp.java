package compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;

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
		System.out.println("Lexing...");
		String source = slurpFile(args[0]);
		Lexer lexer = new Lexer(source);
		ArrayList<Token> tokens = lexer.lex();
		
		for (Token token : tokens) {
			System.out.print(token.toString() + " ");
		}
		
		System.out.println("Parsing...");
		Parser parser = new Parser(tokens);
		parser.parse();
		parser.visit();
	}
	
	private static String slurpFile(String filename) {
		try {
			return new String(Files.readAllBytes(Paths.get(filename)));
		} catch (IOException e) {
			return "";
		}
	}
}
