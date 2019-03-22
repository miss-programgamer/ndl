package lexer.tokens;

public class IndentToken implements Token {
	private int indent;
	
	public IndentToken(int length) {
		this.indent = length;
	}
	
	public int getIndent() {
		return indent;
	}
	
	@Override
	public String toString() {
		return "---indent: "+indent;
	}
}
