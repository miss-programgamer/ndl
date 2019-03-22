package interpreter;

import java.util.HashMap;

public enum BuiltinFunction {
	AndFunction("and"),
	OrFunction("or"),
	NotFunction("not"),
	
	EqualsFunction("equals"),
	PrintLineFunction("print_line"),
	WaitFunction("wait");
	
	private static HashMap<String, BuiltinFunction> builtinFunctionMap;
	
	static {
		builtinFunctionMap = new HashMap<String, BuiltinFunction>();
		for (BuiltinFunction builtinType : BuiltinFunction.values()) {
			builtinFunctionMap.put(builtinType.getName(), builtinType);
		}
	}
	
	public static BuiltinFunction get(String name) {
		return builtinFunctionMap.get(name);
	}
	
	public static boolean contains(String name) {
		return builtinFunctionMap.containsKey(name);
	}
	
	private final String name;
	
	private BuiltinFunction(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
