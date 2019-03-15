package compiler.lexer;

public enum SymbolToken implements Token {
	PeriodToken("."),
	ColonToken(":"),
	
	CommaToken(","),
	SemicolonToken(";"),
	
	OpenParToken("("),
	CloseParToken(")"),
	OpenBracketToken("["),
	CloseBracketToken("]"),
	OpenBraceToken("{"),
	CloseBraceToken("}");
	
	final String symbol;
	
	SymbolToken(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return "["+symbol+"]";
	}
}
