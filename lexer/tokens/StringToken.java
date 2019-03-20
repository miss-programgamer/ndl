package lexer.tokens;

public class StringToken implements Token {
	private String value;
	
	public StringToken(String content) {
		this.value = content;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "\""+value+"\"";
	}
}
