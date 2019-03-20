package interpreter;

public class Variable {
	private String name;
	private Value value;
	
	public Variable(String name, Value value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Value getValue() {
		return value;
	}
}
