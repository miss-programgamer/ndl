package parser.expressions;

import java.util.List;

public class CallExpression implements Expression {
	private String operator;
	private List<Expression> arguments;
	
	public CallExpression(String operator, List<Expression> arguments) {
		this.operator = operator;
		this.arguments = arguments;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public int getArgumentCount() {
		return arguments.size();
	}
	
	public Expression getArgument(int index) {
		return arguments.get(index);
	}
	
	@Override
	public String toString() {
		String repr = operator+" ( ";
		
		for (int index = 0; index < arguments.size(); ++index) {
			if (index > 0) repr += ", ";
			repr += arguments.get(index).toString();
		}
		
		repr += " )";
		
		return repr;
	}
}
