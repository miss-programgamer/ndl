package compiler.parser;

public class VariableExpression implements Expression {
	private String name;
	
	public VariableExpression(String name) {
		this.name = name;
	}
	
	public String getVarName() {
		return name;
	}
}
