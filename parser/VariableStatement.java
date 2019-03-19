package parser;

/** @author Miguel Arseneault */
public class VariableStatement implements Statement {
	private final String name;
	private final String type;
	private final String value;
	
	public VariableStatement (String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public void visit() {
		System.out.println("Variable \""+name+"\" of type \""+type+"\" with value \""+value+"\"");
	}
}
