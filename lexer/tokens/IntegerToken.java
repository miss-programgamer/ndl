package lexer.tokens;

public class IntegerToken implements Token {
	private int value;
	
	public IntegerToken(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "int "+value;
	}
}
