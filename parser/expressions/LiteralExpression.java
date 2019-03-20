package parser.expressions;

import interpreter.Value;

public class LiteralExpression implements Expression {
	private Value value;
	
	public LiteralExpression(Value value) {
		this.value = value;
	}
	
	public Value getValue() {
		return value;
	}
}
