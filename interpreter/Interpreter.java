package interpreter;

import java.util.Stack;

import parser.CallExpression;
import parser.CallStatement;
import parser.Expression;
import parser.IfStatement;
import parser.ScopeStatement;
import parser.Statement;
import parser.VariableExpression;
import parser.VariableStatement;

public class Interpreter {
	private ScopeStatement ast;
	private Stack<Scope> scopes;
	
	public Interpreter(ScopeStatement ast) {
		this.ast = ast;
		
		scopes = new Stack<Scope>();
	}
	
	public void interpret() {
		executeStatement(ast);
	}
	
	private Object computeValue(Expression expression) {
		Object value = null;
		
		if (expression instanceof VariableExpression) {
			value = computeVariable((VariableExpression)expression);
		} else if (expression instanceof CallExpression) {
			value = computeCall((CallExpression)expression);
		}
		
		return value;
	}
	
	private Object computeVariable(VariableExpression variableExpression) {
		Object value = null;
		
		for (int index = scopes.size() - 1; index >= 0; --index) {
			Scope scope = scopes.get(index);
			Variable variable = scope.getVariable((variableExpression).getVarName());
			if (variable != null) {
				value = variable.getValue();
			}
		}
		
		return value;
	}
	
	private Object computeCall(CallExpression callExpression) {
		Object value = null;
		
		for (int index = scopes.size() - 1; index >= 0; --index) { 
			Scope scope = scopes.get(index);
			Function function = scope.getFunction(callExpression.getOperator());
			if (function != null) {
				// -= feed argument and result variables to the function =-
				scopes.push(new Scope(new ScopeStatement(scopes.peek().getStatement())));
				for (VariableStatement variableStatement : function.getStatement().getArguments()) {
					executeVariable(variableStatement);
				}
				for (VariableStatement variableStatement : function.getStatement().getResults()) {
					executeVariable(variableStatement);
				}
				
				// -= call function =-
				executeStatement(scopes.peek().getStatement());
				
				// -= set return value =-
				if (scopes.peek().getVariables().size() > 0) {
					value = scopes.peek().getVariables().get(function.getStatement().getResults().get(0).getName());
				}
				
				// -= remove arg scope and break loop =-
				scopes.pop();
				break;
			} else {
				System.out.println("Unable to call function "+callExpression.getOperator());
			}
		}
		
		return value;
	}
	
	private void executeStatement(Statement statement) {
		if (statement instanceof ScopeStatement) {
			executeScope((ScopeStatement) statement);
		} else if (statement instanceof CallStatement) {
			executeCall((CallStatement) statement);
		} else if (statement instanceof VariableStatement) {
			executeVariable((VariableStatement)statement);
		} else if (statement instanceof IfStatement) {
			executeIf((IfStatement)statement);
		} else {
			System.out.println("Unsuported statement type "+statement.getClass().getCanonicalName());
		}
	}
	
	private void executeScope(ScopeStatement scopeStatement) {
		scopes.push(new Scope(scopeStatement));
		for (Statement statement : scopeStatement.getStatements()) {
			executeStatement(statement);
		}
		scopes.pop();
	}
	
	private void executeCall(CallStatement callStatement) {
		computeValue(callStatement.getCall());
	}
	
	private void executeVariable(VariableStatement variableStatement) {
		scopes.peek().addVariable(variableStatement.getName());
	}
	
	private void executeIf(IfStatement ifStatement) {
		Object value = computeValue(ifStatement.getCondition());
		if (value instanceof Boolean && value.equals(true)) {
			executeStatement(ifStatement.getBody());
		}
	}
}
