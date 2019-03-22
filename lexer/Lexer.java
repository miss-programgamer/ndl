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
		while (cursor < source.length()) {
			if (matchSpaces());
			else if (matchComment());
			else if (matchIndentToken());
			else if (matchOperator());
			else if (matchSymbolToken());
			else if (matchWord());
			else if (matchString());
			else if (matchInteger());
			else {
				System.out.println("Invalid character encountered");
				break;
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
	
	private boolean matchCodePoint(int codePoint) {
		boolean match = false;
		
		if (getCursorCodePoint() == codePoint) {
			++cursor;
			match = true;
		}
		
		return match;
	}
	
	private boolean matchAlphabetic() {
		boolean match = false;
		
		if (Character.isAlphabetic(getCursorCodePoint())) {
			++cursor;
			match = true;
		}
		
		return match;
	}
	
	private boolean matchDigit() {
		boolean match = false;
		
		if (Character.isDigit(getCursorCodePoint())) {
			++cursor;
			match = true;
		}
		
		return match;
	}
	
	private boolean matchString(String str) {
		boolean match = false;
		
		if (source.regionMatches(cursor, str, 0, str.length())) {
			cursor += str.length();
			match = true;
		}
		
		return match;
	}
	
	private boolean matchSpaces() {
		boolean matched = false;
		
		while (matchCodePoint(' ') || matchCodePoint('\t')) {
			matched = true;
		}
		
		return matched;
	}
	
	private boolean matchComment() {
		boolean matched = false;
		
		if (matchString("//")) {
			while (getCursorCodePoint() != '\n') {
				++cursor;
			}
			matched = true;
		} else if (matchString("/*")) {
			int nestLevel = 0;
			while (true) {
				if (matchString("/*")) {
					++nestLevel;
				} else if (matchString("*/")) {
					if (nestLevel > 0) {
						--nestLevel;
					} else {
						break;
					}
				} else {
					++cursor;
				}
			}
			matched = true;
		}
		
		return matched;
	}
	
	private boolean matchIndentToken() {
		boolean matched = false;
		
		if (matchCodePoint('\n')) {
			int indent = 0;
			while (true) {
				if (matchCodePoint(' ')) {
					++indent;
				} else if (matchCodePoint('\t')) {
					indent += 4;
				} else {
					break;
				}
			}
			tokens.add(new IndentToken(indent / 4));
			matched = true;
		}
		
		return matched;
	}
	
	private boolean matchWord() {
		boolean matched = false;
		
		int start = cursor;
		if (matchAlphabetic()) {
			while (matchAlphabetic() || matchDigit() || matchCodePoint('_'));
			
			String word = source.substring(start, cursor);
			if (KeywordToken.contains(word)) {
				tokens.add(KeywordToken.get(word));
			} else {
				tokens.add(new NameToken(word));
			}
			
			matched = true;
		}
		
		return matched;
	}
	
	private boolean matchSymbolToken() {
		boolean matched = false;
		
		for (SymbolToken token : SymbolToken.values()) {
			String symbol = token.getSymbol();
			if (matchString(symbol)) {
				tokens.add(token);
				matched = true;
				break;
			}
		}
		
		return matched;
	}
	
	private boolean matchOperator() {
		boolean matched = false;
		
		for (OperatorToken token : OperatorToken.values()) {
			String operator = token.getOperator();
			if (matchString(operator)) {
				tokens.add(token);
				matched = true;
				break;
			}
		}
		
		return matched;
	}
	
	private boolean matchString() {
		boolean matched = false;
		int start = cursor;
		
		if (matchCodePoint('\"')) {
			while (!matchCodePoint('\"')) {
				++cursor;
			}
			tokens.add(new StringToken(source.substring(start + 1, cursor - 1)));
			matched = true;
		}
		
		if (!matched) cursor = start;
		return matched;
	}
	
	private boolean matchInteger() {
		boolean matched = false;
		int start = cursor;
		
		if (matchDigit()) {
			while (matchDigit());
			tokens.add(new IntegerToken(Integer.parseInt(source.substring(start, cursor))));
			matched = true;
		}
		
		return matched;
	}
}
