package lexer;

public class NameToken implements Token {
	private String name;
	
	public NameToken(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "["+name+"]";
	}
}
