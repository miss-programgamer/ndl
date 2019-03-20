package interpreter;

import java.util.HashSet;
import java.util.Stack;

import parser.expressions.CallExpression;
import parser.expressions.Expression;
import parser.expressions.LiteralExpression;
import parser.expressions.VariableExpression;
import parser.statements.CallStatement;
import parser.statements.IfStatement;
import parser.statements.ScopeStatement;
import parser.statements.Statement;
import parser.statements.VariableStatement;
import parser.types.BuiltinType;

public class Interpreter {
	private ScopeStatement root;
	private Stack<Scope> scopes;
	private HashSet<String> builtinFunctions;
	
	public Interpreter(ScopeStatement root) {
		this.root = root;
		
		scopes = new Stack<Scope>();
		builtinFunctions = new HashSet<String>();
		
		// -= init set of builtin functions =-
		builtinFunctions.add("equals");
		builtinFunctions.add("printLine");
	}
	
	public void interpret() {
		executeStatement(root);
	}
	
	private Value computeValue(Expression expression) {
		Value value = null;
		
		if (expression instanceof VariableExpression) {
			value = computeVariable((VariableExpression)expression);
		} else if (expression instanceof CallExpression) {
			value = computeCall((CallExpression)expression);
		} else if (expression instanceof LiteralExpression) {
			value = computeLiteral((LiteralExpression)expression);
		}
		
		return value;
	}
	
	private Value computeVariable(VariableExpression variableExpression) {
		Value value = null;
		
		for (int index = scopes.size() - 1; index >= 0; --index) {
			Scope scope = scopes.get(index);
			Variable variable = scope.getVariable((variableExpression).getVarName());
			if (variable != null) {
				value = variable.getValue();
			}
		}
		
		return value;
	}
	
	private Value computeCall(CallExpression callExpression) {
		Value value = null;
		
		if (builtinFunctions.contains(callExpression.getOperator())) {
			switch (callExpression.getOperator()) {
			case "equals":
				Value arg1 = computeValue(callExpression.getArgument(0));
				Value arg2 = computeValue(callExpression.getArgument(1));
				value = builtinEquals(arg1, arg2);
				break;
			case "printLine":
				if (callExpression.getArgumentCount() == 1) {
					value = builtinPrintLine(new Value[] {computeValue(callExpression.getArgument(0))});
				} else if (callExpression.getArgumentCount() == 2) {
					value = builtinPrintLine(new Value[] {computeValue(callExpression.getArgument(0)), computeValue(callExpression.getArgument(1))});
				}
				break;
			}
		} else {
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
						value = scopes.peek().getVariables().get(function.getStatement().getResults().get(0).getName()).getValue();
					}
					
					// -= remove arg scope and break loop =-
					scopes.pop();
					break;
				} else {
					System.out.println("Unable to call function "+callExpression.getOperator());
				}
			}
		}
		
		return value;
	}
	
	private Value builtinEquals(Value lhs, Value rhs) {
		boolean result = false;
		
		if (lhs.getType().equals(rhs.getType()) && lhs.getValue().equals(rhs.getValue())) {
			result = true;
		}
		
		return new Value(BuiltinType.BooleanType, result);
	}
	
	private Value builtinPrintLine(Value[] arguments) {
		for (Value argument : arguments) {
			if (argument.getType() == BuiltinType.StringType) {
				System.out.println((String)argument.getValue());
			} else {
				System.out.println("undefined");
			}
		}
		
		return null;
	}
	
	private Value computeLiteral(LiteralExpression literalExpression) {
		return new Value(BuiltinType.StringType, literalExpression.getValue().getValue());
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
		scopes.peek().addVariable(variableStatement.getName(), computeValue(variableStatement.getValue()));
	}
	
	private void executeIf(IfStatement ifStatement) {
		Value value = computeValue(ifStatement.getCondition());
		if (value.getType() == BuiltinType.BooleanType && value.getValue().equals(true)) {
			executeStatement(ifStatement.getBody());
		}
	}
}
