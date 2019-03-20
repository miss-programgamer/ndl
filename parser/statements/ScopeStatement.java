package parser.statements;

import java.util.ArrayList;
import java.util.HashMap;

public class ScopeStatement implements Statement {
	private ScopeStatement outer;
	
	private ArrayList<Statement> statements;
	private HashMap<String, VariableStatement> variables;
	private HashMap<String, FunctionStatement> functions;
	
	public ScopeStatement(ScopeStatement outer) {
		this.outer = outer;
		
		statements = new ArrayList<Statement>();
		variables = new HashMap<String, VariableStatement>();
		functions = new HashMap<String, FunctionStatement>();
	}
	
	public ScopeStatement getOuterScope() {
		return outer;
	}
	
	public boolean addStatement(Statement statement) {
		return statements.add(statement);
	}
	
	public ArrayList<Statement> getStatements() {
		return statements;
	}
	
	public boolean addVariable(VariableStatement variable) {
		return variables.put(variable.getName(), variable) != null;
	}
	
	public VariableStatement getVariable(String name) {
		VariableStatement variable = variables.get(name);
		if (variable != null) {
			return variable;
		} else if (outer != null) {
			return outer.getVariable(name);
		} else {
			return null;
		}
	}
	
	public boolean addFunction(FunctionStatement function) {
		return functions.put(function.getName(), function) != null;
	}
	
	public FunctionStatement getFunction(String name) {
		FunctionStatement function = functions.get(name);
		if (function != null) {
			return function;
		} else if (outer != null) {
			return outer.getFunction(name);
		} else {
			return null;
		}
	}
	
	@Override
	public void visit() {
		for (Statement statement : statements) {
			statement.visit();
		}
	}
}
