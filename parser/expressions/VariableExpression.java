package parser.expressions;

public class VariableExpression implements Expression {
	private String name;
	
	public VariableExpression(String name) {
		this.name = name;
	}
	
	public String getVarName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
