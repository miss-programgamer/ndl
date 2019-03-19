package interpreter;

public class Variable {
	private String type;
	private Object value;
	
	public Variable(String type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}
