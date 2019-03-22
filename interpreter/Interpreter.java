package interpreter;

import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import lexer.tokens.OperatorToken;
import parser.expressions.CallExpression;
import parser.expressions.Expression;
import parser.expressions.LiteralExpression;
import parser.expressions.VariableExpression;
import parser.statements.AssignmentStatement;
import parser.statements.CallStatement;
import parser.statements.FunctionStatement;
import parser.statements.IfStatement;
import parser.statements.ScopeStatement;
import parser.statements.Statement;
import parser.statements.VariableStatement;
import parser.statements.WhileStatement;
import parser.types.BuiltinType;

public class Interpreter {
	public class Scope {
		private ScopeStatement statement;
		private HashMap<String, Variable> variables;
		
		public Scope(ScopeStatement statement) {
			this.statement = statement;
			
			variables = new HashMap<String, Variable>();
		}
		
		public Variable addVariable(String variableName, Value value) {
			return variables.put(variableName, new Variable(variableName, value));
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
		
		public Interpreter.CustomFunction getFunction(String functionName) {
			FunctionStatement functionStatement = statement.getFunction(functionName);
			if (functionStatement != null) {	
				return new CustomFunction(functionStatement);
			} else {
				return null;
			}
		}
	}
	
	public class CustomFunction {
		private FunctionStatement statement;
		
		public CustomFunction (FunctionStatement statement) {
			this.statement = statement;
		}
		
		public FunctionStatement getStatement() {
			return statement;
		}

		public Value call(Value[] arguments) {
			Value value = null;
			
			// -= feed argument and result variables to the function =-
			scopes.add(new Scope(scopes.peek().getStatement()));
			for (VariableStatement variableStatement : statement.getArguments()) {
				executeVariable(variableStatement);
			}
			for (VariableStatement variableStatement : statement.getResults()) {
				executeVariable(variableStatement);
			}
			
			// -= call function =-
			executeStatement(statement);
			
			// -= set return value =-
			if (scopes.peek().getVariables().size() > 0) {
				value = scopes.peek().getVariables().get(statement.getResults().get(0).getName()).getValue();
			}
			
			// -= remove arg scope and break loop =-
			scopes.pop();
			
			return value;
		}
	}
	
	private ScopeStatement root;
	private Stack<Scope> scopes;
	
	public Interpreter(ScopeStatement root) {
		this.root = root;
		
		scopes = new Stack<Scope>();
	}
	
	public void interpret() {
		executeStatement(root);
	}
	
	private Variable findVariable(String name) {
		Variable variable = null;
		
		for (int index = scopes.size() - 1; index >= 0; --index) {
			Scope scope = scopes.get(index);
			variable = scope.getVariable(name);
			if (variable != null) {
				break;
			}
		}
		
		return variable;
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
		
		Variable variable = findVariable(variableExpression.getVarName());
		if (variable != null) {
			value = variable.getValue();
		}
		
		return value;
	}
	
	private Value computeCall(CallExpression callExpression) {
		Value value = null;
		
		String operator = callExpression.getOperator();
		int argsLength = callExpression.getArgumentCount();
		
		Value[] args;
		if (BuiltinFunction.contains(operator) || OperatorToken.contains(operator)) {
			if (operator.equals("equals") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinEquals(args[0], args[1]);
			} else if (operator.equals("print_line")) {
				args = computeArguments(callExpression);
				value = builtinPrintLine(args);
			} else if (operator.equals("+") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinPlus(args[0], args[1]);
			} else if (operator.equals("-") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinMinus(args[0], args[1]);
			} else if (operator.equals("*") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinTimes(args[0], args[1]);
			} else if (operator.equals("/") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinDivided(args[0], args[1]);
			} else if (operator.equals("%") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinModulus(args[0], args[1]);
			} else if (operator.equals("^") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinPower(args[0], args[1]);
			} else if (operator.equals("wait") && argsLength == 1) {
				args = computeArguments(callExpression);
				value = builtinWait(args[0]);
			} else if (operator.equals("and") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinAnd(args[0], args[1]);
			} else if (operator.equals("or") && argsLength == 2) {
				args = computeArguments(callExpression);
				value = builtinOr(args[0], args[1]);
			} else if (operator.equals("not") && argsLength == 1) {
				args = computeArguments(callExpression);
				value = builtinNot(args[0]);
			} 
		} else {
			args = computeArguments(callExpression);
			value = scopes.peek().getFunction(operator).call(args);
		}
		
		return value;
	}
	
	private Value[] computeArguments(CallExpression callExpression) {
		Value[] args = new Value[callExpression.getArgumentCount()];
		
		for (int index = 0; index < args.length; ++index) {
			args[index] = computeValue(callExpression.getArgument(index));
		}
		
		return args;
	}
	
	private Value builtinAnd(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.BooleanType && rhs.getType() == BuiltinType.BooleanType) {
			return new Value(BuiltinType.BooleanType, (Boolean)lhs.getValue() && (Boolean)rhs.getValue());
		} else {
			return null;
		}
	}
	
	private Value builtinOr(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.BooleanType && rhs.getType() == BuiltinType.BooleanType) {
			return new Value(BuiltinType.BooleanType, (Boolean)lhs.getValue() || (Boolean)rhs.getValue());
		} else {
			return null;
		}
	}
	
	private Value builtinNot(Value value) {
		if (value.getType() == BuiltinType.BooleanType) {
			return new Value(BuiltinType.BooleanType, !(Boolean)value.getValue());
		} else {
			return null;
		}
	}
	
	private Value builtinPlus(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.IntegerType && rhs.getType() == BuiltinType.IntegerType) {
			return new Value(BuiltinType.IntegerType, ((Integer)lhs.getValue()) + ((Integer)rhs.getValue()));
		} else {
			return null;
		}
	}
	
	private Value builtinMinus(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.IntegerType && rhs.getType() == BuiltinType.IntegerType) {
			return new Value(BuiltinType.IntegerType, ((Integer)lhs.getValue()) - ((Integer)rhs.getValue()));
		} else {
			return null;
		}
	}
	
	private Value builtinTimes(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.IntegerType && rhs.getType() == BuiltinType.IntegerType) {
			return new Value(BuiltinType.IntegerType, ((Integer)lhs.getValue()) * ((Integer)rhs.getValue()));
		} else {
			return null;
		}
	}
	
	private Value builtinDivided(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.IntegerType && rhs.getType() == BuiltinType.IntegerType) {
			return new Value(BuiltinType.IntegerType, ((Integer)lhs.getValue()) / ((Integer)rhs.getValue()));
		} else {
			return null;
		}
	}
	
	private Value builtinModulus(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.IntegerType && rhs.getType() == BuiltinType.IntegerType) {
			return new Value(BuiltinType.IntegerType, ((Integer)lhs.getValue()) % ((Integer)rhs.getValue()));
		} else {
			return null;
		}
	}
	
	private Value builtinPower(Value lhs, Value rhs) {
		if (lhs.getType() == BuiltinType.IntegerType && rhs.getType() == BuiltinType.IntegerType) {
			return new Value(BuiltinType.IntegerType, (int)Math.pow((Double)lhs.getValue(), (Double)rhs.getValue()));
		} else {
			return null;
		}
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
			if (argument.getType().equals(BuiltinType.StringType)) {
				System.out.print((String)argument.getValue());
			} else if (argument.getType().equals(BuiltinType.IntegerType)) {
				System.out.print((Integer)argument.getValue());
			} else {
				System.out.print("undefined");
			}
			System.out.print(" ");
		}
		System.out.println();
		
		return null;
	}
	
	private Value builtinWait(Value value) {
		if (value.getType() == BuiltinType.IntegerType) {
			int time = (Integer)value.getValue();
			try {
				TimeUnit.SECONDS.sleep(time);
			} catch (InterruptedException e) {
				// if this fails then whatever
			}
		}
		
		return null;
	}
	
	private Value computeLiteral(LiteralExpression literalExpression) {
		return new Value(literalExpression.getValue().getType(), literalExpression.getValue().getValue());
	}
	
	private void executeStatement(Statement statement) {
		if (statement instanceof ScopeStatement) {
			executeScope((ScopeStatement) statement);
		} else if (statement instanceof CallStatement) {
			executeCall((CallStatement) statement);
		} else if (statement instanceof VariableStatement) {
			executeVariable((VariableStatement)statement);
		} else if (statement instanceof AssignmentStatement) {
			executeAssignment((AssignmentStatement)statement);
		} else if (statement instanceof IfStatement) {
			executeIf((IfStatement)statement);
		} else if (statement instanceof WhileStatement) {
			executeWhile((WhileStatement)statement);
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
	
	private void executeAssignment(AssignmentStatement assignmentStatement) {
		String name = assignmentStatement.getName();
		Expression expression = assignmentStatement.getValue();
		Variable variable = findVariable(name);
		if (variable != null) {
			Value value = computeValue(expression);
			if (value.getType() == variable.getValue().getType()) {
				variable.getValue().setValue(value.getValue());
			}
		}
	}
	
	private void executeIf(IfStatement ifStatement) {
		Value value = computeValue(ifStatement.getCondition());
		if (value.getType() == BuiltinType.BooleanType && value.getValue().equals(true)) {
			executeStatement(ifStatement.getBody());
		}
	}
	
	private void executeWhile(WhileStatement whileStatement) {
		while (true) {
			Value value = computeValue(whileStatement.getCondition());
			if (value.getType() == BuiltinType.BooleanType && value.getValue().equals(true)) {
				executeStatement(whileStatement.getBody());
			} else {
				break;
			}
		}
	}
}
