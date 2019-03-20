package lexer;

import java.util.ArrayList;

import lexer.tokens.IndentToken;
import lexer.tokens.IntegerToken;
import lexer.tokens.KeywordToken;
import lexer.tokens.NameToken;
import lexer.tokens.OperatorToken;
import lexer.tokens.StringToken;
import lexer.tokens.SymbolToken;
import lexer.tokens.Token;

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
		tokens.add(new IndentToken(0));
		if (getCursorCodePoint() == ' ') {
			System.out.println("Indenting the first line is forbidden");
		} else {
			while (cursor < source.length()) {
				if (matchSpaces()) {}
				else if (matchIndent()) {}
				else if (matchOperator()) {}
				else if (matchSymbol()) {}
				else if (matchWord()) {}
				else if (matchString()) {}
				else if (matchInteger()) {}
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
		boolean matched = false;
		
		while (getCursorCodePoint() == ' ' || getCursorCodePoint() == '\t') {
			matched = true;
			++cursor;
		}
		
		return matched;
	}
	
	private boolean matchIndent() {
		boolean matched = false;
		
		if (getCursorCodePoint() == '\r') {
			++cursor;
		}
		
		if (getCursorCodePoint() == '\n') {
			matched = true;
			
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
		
		return matched;
	}
	
	private boolean matchWord() {
		boolean matched = false;
		
		if (Character.isAlphabetic(getCursorCodePoint())) {
			int start = cursor;
			do {
				++cursor;
			} while (
				Character.isAlphabetic(getCursorCodePoint()) ||
				Character.isDigit(getCursorCodePoint())
			);
			
			String word = source.substring(start, cursor);
			for (KeywordToken token : KeywordToken.values()) {
				if (token.getKeyword().equals(word)) {
					matched = true;
					tokens.add(token);
					break;
				}
			}
			
			if (!matched) {
				matched = true;
				tokens.add(new NameToken(word));
			}
		}
		
		return matched;
	}
	
	private boolean matchSymbol() {
		boolean matched = false;
		
		tokenLoop:
		for (SymbolToken token : SymbolToken.values()) {
			int start = cursor;
			String symbol = token.getSymbol();
			for (int index = 0; index < symbol.length(); ++index) {
				if (symbol.codePointAt(index) != source.codePointAt(start + index)) {
					continue tokenLoop;
				}
			}
			matched = true;
			cursor += symbol.length();
			tokens.add(token);
			break tokenLoop;
		}
		
		return matched;
	}
	
	private boolean matchOperator() {
		boolean matched = false;
		
		tokenLoop:
		for (OperatorToken token : OperatorToken.values()) {
			// TODO Split inside of this loop depenting on value of isWord()
			int start = cursor;
			String operator = token.getOperator();
			for (int index = 0; index < operator.length(); ++index) {
				if (operator.codePointAt(index) != source.codePointAt(start + index)) {
					continue tokenLoop;
				}
			}
			if (token.isWord() && Character.isAlphabetic(source.codePointAt(cursor + 1))) {
				continue tokenLoop;
			}
			matched = true;
			cursor += operator.length();
			tokens.add(token);
			break tokenLoop;
		}
		
		return matched;
	}
	
	private boolean matchString() {
		boolean matched = false;
		int start = cursor;
		
		if (getCursorCodePoint() == '\"') {
			++cursor;
			while (getCursorCodePoint() != '\"') {
				++cursor;
			}
			++cursor;
			tokens.add(new StringToken(source.substring(start + 1, cursor - 1)));
			matched = true;
		}
		
		if (!matched) cursor = start;
		return matched;
	}
	
	private boolean matchInteger() {
		boolean matched = false;
		int start = cursor;
		
		if (Character.isDigit(getCursorCodePoint())) {
			++cursor;
			while (Character.isDigit(getCursorCodePoint())) {
				++cursor;
			}
			tokens.add(new IntegerToken(Integer.parseInt(source.substring(start, cursor))));
			matched = true;
		}
		
		if (!matched) cursor = start;
		return matched;
	}
}
