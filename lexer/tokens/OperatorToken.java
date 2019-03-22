package lexer.tokens;

import java.util.HashMap;

public enum OperatorToken implements Token {
	PlusToken("+"),
	MinusToken("-"),
	TimesToken("*"),
	DivideToken("/"),
	ModulusToken("%"),
	PowerToken("^");
	
	private static HashMap<String, OperatorToken> operatorMap;
	
	static {
		operatorMap = new HashMap<String, OperatorToken>();
		for (OperatorToken builtinType : OperatorToken.values()) {
			operatorMap.put(builtinType.getOperator(), builtinType);
		}
	}
	
	public static OperatorToken get(String name) {
		return operatorMap.get(name);
	}
	
	public static boolean contains(String name) {
		return operatorMap.containsKey(name);
	}
	
	private final String operator;
	
	private OperatorToken(String operator) {
		this.operator = operator;
	}
	
	public String getOperator() {
		return operator;
	}
	
	@Override
	public String toString() {
		return "["+operator+"]";
	}
}
