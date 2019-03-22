package lexer.tokens;

import java.util.HashMap;

public enum KeywordToken implements Token {
	IfToken("if"),
	ElifToken("elif"),
	ElseToken("else"),
	WhileToken("while"),
	ThenToken("then"),
	FuncToken("func");
	
	private static HashMap<String, KeywordToken> keywordMap;
	
	static {
		keywordMap = new HashMap<String, KeywordToken>();
		for (KeywordToken builtinType : KeywordToken.values()) {
			keywordMap.put(builtinType.getKeyword(), builtinType);
		}
	}
	
	public static KeywordToken get(String name) {
		return keywordMap.get(name);
	}
	
	public static boolean contains(String name) {
		return keywordMap.containsKey(name);
	}
	
	final String keyword;
	
	KeywordToken(String keyword) {
		this.keyword = keyword;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	@Override
	public String toString() {
		return keyword;
	}
}
