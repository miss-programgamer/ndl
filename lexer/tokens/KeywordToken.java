package lexer.tokens;

public enum KeywordToken implements Token {
	IfToken("if"),
	ElifToken("elif"),
	ElseToken("else"),
	WhileToken("while"),
	ThenToken("then"),
	VarToken("var"),
	FuncToken("func");
	
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
