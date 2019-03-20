package parser.statements;

import java.util.ArrayList;

public class FunctionStatement implements Statement {
	private String name;
	private ArrayList<VariableStatement> arguments;
	private ArrayList<VariableStatement> results;
	private ScopeStatement body;
	
	public FunctionStatement(String name, ArrayList<VariableStatement> arguments, ArrayList<VariableStatement> results, ScopeStatement body) {
		this.name = name;
		this.arguments = arguments;
		this.results = results;
		this.body = body;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<VariableStatement> getArguments() {
		return arguments;
	}
	
	public ArrayList<VariableStatement> getResults() {
		return results;
	}
	
	public ScopeStatement getBody() {
		return body;
	}

	@Override
	public void visit() {
		System.out.print("Function Statement \""+name+"\" ( ");
		
		for (int index = 0; index < arguments.size(); ++index) {
			if (index > 0) System.out.print(", ");
			System.out.print(arguments.get(index).getName());
		}
		
		System.out.print(" returns ");
		
		for (int index = 0; index < arguments.size(); ++index) {
			if (index > 0) System.out.print(", ");
			System.out.print(arguments.get(index).getName());
		}
		
		System.out.println(" )");
		
		body.visit();
	}
}
