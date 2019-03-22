package parser.statements;

import parser.expressions.Expression;

public class AssignmentStatement implements Statement {
	private String name;
	private Expression value;
	
	public AssignmentStatement(String name, Expression value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Expression getValue() {
		return value;
	}
	
	@Override
	public void visit() {
		System.out.println(name+" gets value "+value.toString());
	}
}
