package parser.statements;

import java.io.Serializable;

import parser.expressions.Expression;
import parser.types.Type;

/** @author Miguel Arseneault */
public class VariableStatement implements Statement, Serializable {
	private static final long serialVersionUID = 6017361715953706630L;
	
	private final String name;
	private final Type type;
	private final Expression value;
	
	public VariableStatement (String name, Type type, Expression value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public Expression getValue() {
		return value;
	}
	
	@Override
	public void visit() {
		System.out.println("Variable \""+name+"\" of type \""+type.getName()+"\" with value \""+value.toString()+"\"");
	}
}
