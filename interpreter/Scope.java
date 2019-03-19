package interpreter;

import java.util.HashMap;

import parser.FunctionStatement;
import parser.ScopeStatement;

public class Scope {
	private ScopeStatement statement;
	private HashMap<String, Variable> variables;
	
	public Scope(ScopeStatement statement) {
		this.statement = statement;
		
		variables = new HashMap<String, Variable>();
	}
	
	public boolean addVariable(String variableName) {
		return variables.put(variableName, new Variable(variableName, null)) != null;
	}
	
	public ScopeStatement getStatement() {
		return statement;
	}
	
	public HashMap<String, Variable> getVariables() {
		return variables;
	}
	
	public Variable getVariable(String variableName) {
		return variables.get(variableName);
	}
	
	public Function getFunction(String functionName) {
		FunctionStatement functionStatement = statement.getFunction(functionName);
		if (functionStatement != null) {	
			return new Function(functionStatement);
		} else {
			return null;
		}
	}
}
