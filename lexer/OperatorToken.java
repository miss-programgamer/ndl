package lexer;

public enum OperatorToken implements Token {
	PlusToken("+"),
	MinusToken("-"),
	TimesToken("*"),
	DivideToken("/"),
	ModulusToken("%"),
	PowerToken("^"),
	
	IsToken("is", true),
	AndToken("and", true),
	OrToken("or", true),
	NotToken("not", true);
	
	private final String operator;
	private final boolean isWord;
	
	private OperatorToken(String operator, boolean isWord) {
		this.operator = operator;
		this.isWord = isWord;
	}
	
	private OperatorToken(String operator) {
		this.operator = operator;
		isWord = false;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public boolean isWord() {
		return isWord;
	}
	
	@Override
	public String toString() {
		return "["+operator+"]";
	}
}
