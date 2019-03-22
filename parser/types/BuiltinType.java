package parser.types;

import java.util.HashMap;

public enum BuiltinType implements Type {
	BooleanType("Boolean"),
	IntegerType("Integer"),
	StringType("String");
	
	private static HashMap<String, BuiltinType> typeMap;
	
	static {
		typeMap = new HashMap<String, BuiltinType>();
		for (BuiltinType builtinType : BuiltinType.values()) {
			typeMap.put(builtinType.getName(), builtinType);
		}
	}
	
	public static BuiltinType get(String name) {
		return typeMap.get(name);
	}
	
	public static boolean contains(String name) {
		return typeMap.containsKey(name);
	}

	private final String name;
	
	private BuiltinType(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
