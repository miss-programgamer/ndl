package lexer.tokens;

public class IndentToken implements Token {
	private int size;
	
	public IndentToken(int length) {
		this.size = length;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return "---indent: "+size;
	}
}
