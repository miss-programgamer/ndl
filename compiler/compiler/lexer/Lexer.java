package compiler.lexer;

import java.util.ArrayList;

/** @author Miguel Arseneault */
public class Lexer {
	private String source;
	private ArrayList<Token> tokens;
	private int cursor;
	
	public Lexer(String source) {
		this.source = source;
		
		tokens = new ArrayList<Token>();
		cursor = 0;
	}
	
	public ArrayList<Token> lex() {
		if (getCursorCodePoint() == ' ') {
			System.out.println("Indenting the first line is forbidden");
		} else {
			while (cursor < source.length()) {
				if (matchSpaces()) {}
				else if (matchIndent()) {}
				else if (matchWord()) {}
				else if (matchSymbol()) {}
				else {
					// Welp, encountered an invalid character, pack it up boys and girls and others
					System.out.println("Invalid character encountered");
					break;
				}
			}
		}
		
		return tokens;
	}
	
	private int getCursorCodePoint() {
		if (cursor >= 0 && cursor < source.length()) {
			return source.codePointAt(cursor);
		} else {
			return 0;
		}
	}
	
	private boolean matchSpaces() {
		boolean result = false;
		
		while (getCursorCodePoint() == ' ' || getCursorCodePoint() == '\t') {
			result = true;
			++cursor;
		}
		
		return result;
	}
	
	private boolean matchIndent() {
		boolean result = false;
		
		if (getCursorCodePoint() == '\r') {
			++cursor;
		}
		
		if (getCursorCodePoint() == '\n') {
			result = true;
			
			++cursor;
			int indent = 0;
			while (getCursorCodePoint() == ' ' || getCursorCodePoint() == '\t') {
				switch (getCursorCodePoint()) {
				case ' ':
					++indent; break;
				case '\t':
					indent += 4; break;
				}
				
				++cursor;
			}
			tokens.add(new IndentToken(indent / 4));
		}
		
		return result;
	}
	
	private boolean matchWord() {
		boolean result = false;
		
		if (Character.isAlphabetic(getCursorCodePoint())) {
			result = true;
			
			int start = cursor;
			do {
				++cursor;
			} while (
				Character.isAlphabetic(getCursorCodePoint()) ||
				Character.isDigit(getCursorCodePoint())
			);
			tokens.add(new NameToken(source.substring(start, cursor)));
		}
		
		return result;
	}
	
	private boolean matchSymbol() {
		boolean result = false;
		
		tokenLoop:
		for (SymbolToken token : SymbolToken.values()) {
			int start = cursor;
			String symbol = token.getSymbol();
			for (int index = 0; index < symbol.length(); ++index) {
				if (symbol.codePointAt(index) != source.codePointAt(start + index)) {
					continue tokenLoop;
				}
			}
			result = true;
			cursor += symbol.length();
			tokens.add(token);
			break tokenLoop;
		}
		
		return result;
	}
}
