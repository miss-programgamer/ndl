package compiler.parser;

public class CallExpression implements Expression {
	private String operator;
	private Expression[] arguments;
	
	public CallExpression(String operator, Expression[] arguments) {
		this.operator = operator;
		this.arguments = arguments;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public int getArgumentCount() {
		return arguments.length;
	}
	
	public Expression getArgument(int index) {
		return arguments[index];
	}
	
	@Override
	public String toString() {
		return "["+operator+" ("+arguments.length+" args)]";
	}
}
