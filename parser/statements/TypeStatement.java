package parser.statements;

import parser.types.Type;

public class TypeStatement implements Statement, Type {
	private String name;
	
	public TypeStatement(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void visit() {
		System.out.println(name);
	}
}
