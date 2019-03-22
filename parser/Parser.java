package parser;

import java.util.ArrayList;
import java.util.List;

import interpreter.BuiltinFunction;
import interpreter.Value;
import lexer.tokens.*;
import parser.expressions.*;
import parser.statements.*;
import parser.types.*;

public class Parser {
	private ArrayList<Token> tokens;
	private boolean verbose;
	
	private ScopeStatement rootScope;
	private ScopeStatement currentScope;
	private int cursor;
	private int currentIndent;
	
	public Parser(ArrayList<Token> tokens, boolean verbose) {
		this.tokens = tokens;
		this.verbose = verbose;
	}
	
	public ScopeStatement parse() {
		cursor = 0;
		currentIndent = 0;
		
		rootScope = new ScopeStatement(null);
		currentScope = rootScope;
		matchStatements();
		
		return rootScope;
	}
	
	public void visit() {
		rootScope.visit();
	}
	
	private Token getCursorToken(int offset) {
		int index = cursor + offset;
		if (index < tokens.size()) {
			return tokens.get(index);
		} else  {
			return null;
		}
	}
	
	private Token getCursorToken() {
		return getCursorToken(0);
	}
	
	private void log(String out) {
		if (verbose) {
			System.out.println("Cursor: "+cursor+"\t- "+out);
		}
	}
	
	private void matchStatements() {
		Statement statement;
		while (cursor < tokens.size()) {
			if (matchIndentDown() || matchLast()) {
				break;
			}
			else if (matchSeparator());
			else if ((statement = matchVariable()) != null) {
				currentScope.addStatement(statement);
				currentScope.addVariable((VariableStatement)statement);
			}
			else if ((statement = matchAssignment()) != null) {
				currentScope.addStatement(statement);
			}
			else if ((statement = matchFunction()) != null) {
				currentScope.addFunction((FunctionStatement)statement);
			}
			else if ((statement = matchIf()) != null) {
				currentScope.addStatement(statement);
			}
			else if ((statement = matchWhile()) != null) {
				currentScope.addStatement(statement);
			}
			else if ((statement = matchCall()) != null) {
				currentScope.addStatement(statement);
			}
			else {
				// Invalid token sequence
				log("Invalid token sequence encountered "+getCursorToken().toString());
				break;
			}
		}
	}
	
	private ScopeStatement matchScope() {
		ScopeStatement match = null;
		
		if (matchIndentUp()) {
			currentScope = new ScopeStatement(currentScope);
			match = currentScope;
			
			matchStatements();
			
			currentScope = currentScope.getOuterScope();
		}
		
		return match;
	}
	
	private boolean matchIndentUp() {
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getIndent() > currentIndent) {
			match = true;
			++currentIndent;
			log("Matched indent up! (indent now "+currentIndent+")");
		}
		
		return match;
	}
	
	private boolean matchIndentDown() {
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getIndent() < currentIndent) {
			match = true;
			--currentIndent;
			log("Matched indent down! (indent now "+currentIndent+")");
		}
		
		return match;
	}
	
	private boolean matchSeparator() {
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken) {
			if (getCursorToken(1) instanceof IndentToken) {
				++cursor;
				match = true;
				log("Matched separator!");
			} else if (((IndentToken)getCursorToken()).getIndent() == currentIndent) {
				++cursor;
				match = true;
				log("Matched separator!");
			} else if (((IndentToken)getCursorToken()).getIndent() != currentIndent) {
				match = true;
				log("Matched separator!");
			}
		} else if (matchLast()) {
			match = true;
			log("Matched separator!");
		}
		
		return match;
	}
	
	private String matchName() {
		String match = null;
		
		if (getCursorToken() instanceof NameToken) {
			match = ((NameToken)getCursorToken()).getName();
			++cursor;
		}
		
		return match;
	}
	
	private String matchVariableName() {
		String match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && currentScope.getVariable(name) != null) {
			match = name;
			log("Matched variable name ["+name+"]!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private String matchFunctionName() {
		String match = null;
		int start = cursor;
		
		String name = matchName();
		OperatorToken operatorToken;
		if (name == null && (operatorToken = matchOperator()) != null) {
			name = operatorToken.getOperator();
		}
		
		if (currentScope.getFunction(name) != null || BuiltinFunction.contains(name) || OperatorToken.contains(name)) {
			match = name;
			log("Matched function name ["+match+"]!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private String matchTypeName() {
		String match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && BuiltinType.contains(name)) { // TODO Check that the name is a type name
			match = name;
			log("Matched type name "+name+"!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private String matchUndefinedName() {
		String match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && nameUndefined(name)) {
			match = name;
			log("Matched undefined name "+name+"!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private boolean nameUndefined(String name) {
		return currentScope.getVariable(name) == null && !BuiltinType.contains(name);
	}
	
	private boolean matchKeyword(KeywordToken token) {
		boolean match = false;
		
		if (getCursorToken() instanceof KeywordToken && getCursorToken() == token) {
			match = true;
			++cursor;
			log("Matched keyword "+token.getKeyword()+"!");
		}
		
		return match;
	}
	
	private boolean matchSymbol(SymbolToken token) {
		boolean match = false;
		
		if (getCursorToken() instanceof SymbolToken && getCursorToken() == token) {
			match = true;
			++cursor;
			log("Matched symbol "+token.getSymbol()+"!");
		}
		
		return match;
	}
	
	private OperatorToken matchOperator() {
		OperatorToken match = null;
		
		if (getCursorToken() instanceof OperatorToken) {
			match = (OperatorToken)getCursorToken();
			++cursor;
			log("Matched operator "+match.getOperator()+"!");
		}
		
		return match;
	}
	
	private VariableStatement matchVariable() {
		VariableStatement match = null;
		int start = cursor;
		
		String name = matchUndefinedName();
		if (name != null) {
			String type = matchTypeName();
			if (type != null) {
				Expression value = null;
				if (matchSymbol(SymbolToken.EqualsToken)) {
					value = matchExpression();
				}
				if (matchSeparator()) {
					match = new VariableStatement(name, BuiltinType.get(type), value);
					log("Matched variable "+name+" "+type+"!");
				}
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private AssignmentStatement matchAssignment() {
		AssignmentStatement match = null;
		
		String name = matchVariableName();
		if (name != null && matchSymbol(SymbolToken.EqualsToken)) {
			Expression value = matchExpression();
			if (value != null && matchSeparator()) {
				match = new AssignmentStatement(name, value);
			}
		}
		
		return match;
	}
	
	private FunctionStatement matchFunction() {
		// TODO This isn't functional yet, don't try to make functions
		
		FunctionStatement match = null;
		int start = cursor;
		
		String name = matchUndefinedName();
		if (name != null && matchKeyword(KeywordToken.FuncToken)) {
			ArrayList<VariableStatement> argumentList = new ArrayList<VariableStatement>();
			ArrayList<VariableStatement> resultList = new ArrayList<VariableStatement>();
			VariableStatement arg1 = matchVariable();
			if (arg1 != null) {
				if (matchSymbol(SymbolToken.CommaToken)) {
					VariableStatement arg2 = matchVariable();
					if (arg2 != null) {
						ScopeStatement body = matchScope();
						argumentList.add(arg1);
						argumentList.add(arg2);
						if (matchSeparator()) {
							match = new FunctionStatement(name, argumentList, resultList, body);
						}
					}
				} else {
					ScopeStatement body = matchScope();
					argumentList.add(arg1);
					if (matchSeparator()) {
						match = new FunctionStatement(name, argumentList, resultList, body);
					}
				}
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private IfStatement matchIf() {
		IfStatement match = null;
		int start = cursor;
		
		if (matchKeyword(KeywordToken.IfToken)) {
			Expression condition = matchExpression();
			if (condition != null) {
				ScopeStatement body = matchScope();
				if (body != null && matchSeparator()) {
					match = new IfStatement(condition, body);
					log("Matched if!");
				}
			}
		}

		if (match == null) cursor = start;
		return match;
	}
	
	private WhileStatement matchWhile() {
		WhileStatement match = null;
		int start = cursor;
		
		if (matchKeyword(KeywordToken.WhileToken)) {
			Expression condition = matchExpression();
			if (condition != null) {
				ScopeStatement body = matchScope();
				if (body != null && matchSeparator()) {
					match = new WhileStatement(condition, body);
					log("Matched while!");
				}
			}
		}

		if (match == null) cursor = start;
		return match;
	}
	
	private Expression matchExpression() {
		Expression match = null;
		int start = cursor;
		
		Expression expression;
		if ((expression = matchUnaryCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchBinaryCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchFunctionCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchParenExpression()) != null) {
			match = expression;
		} else if ((expression = matchVariableExpression()) != null) {
			match = expression;
		} else if ((expression = matchLiteralExpression()) != null) {
			match = expression;
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private Expression matchNonBinaryExpression() {
		Expression match = null;
		int start = cursor;
		
		Expression expression;
		if ((expression = matchUnaryCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchFunctionCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchParenExpression()) != null) {
			match = expression;
		} else if ((expression = matchVariableExpression()) != null) {
			match = expression;
		} else if ((expression = matchLiteralExpression()) != null) {
			match = expression;
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private CallExpression matchCallExpression() {
		CallExpression match = null;
		int start = cursor;
		
		CallExpression expression;
		if ((expression = matchUnaryCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchBinaryCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchFunctionCallExpression()) != null) {
			match = expression;
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private CallExpression matchUnaryCallExpression() {
		CallExpression match = null;
		int start = cursor;
		
		String operator;
		if ((operator = matchFunctionName()) != null) {
			Expression argument = matchExpression();
			if (argument != null) {
				List<Expression> arguments = new ArrayList<Expression>();
				arguments.add(argument);
				match = new CallExpression(operator, arguments);
				log("Matched unary operator call!");
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private CallExpression matchBinaryCallExpression() {
		CallExpression match = null;
		int start = cursor;
		
		Expression lhs = matchNonBinaryExpression();
		if (lhs != null) {
			String operator = matchFunctionName();
			if (operator != null) {
				Expression rhs = matchExpression();
				if (rhs != null) {
					List<Expression> arguments = new ArrayList<Expression>();
					arguments.add(lhs);
					arguments.add(rhs);
					match = new CallExpression(operator, arguments);
					log("Matched binary operator call!");
				}
			}
		}

		if (match == null) cursor = start;
		return match;
	}
	
	private CallExpression matchFunctionCallExpression() {
		CallExpression match = null;
		int start = cursor;
		
		String operator = matchFunctionName();
		if (operator != null) {
			if (matchSymbol(SymbolToken.OpenParToken)) {
				ArrayList<Expression> arguments = new ArrayList<Expression>();
				Expression argument = matchExpression();
				if (argument != null) {
					arguments.add(argument);
					while (matchSymbol(SymbolToken.CommaToken)) {
						if ((argument = matchExpression()) != null) {
							arguments.add(argument);
						}
					}
				}
				
				if (matchSymbol(SymbolToken.CloseParToken)) {
					match = new CallExpression(operator, arguments);
					log("Matched function call!");
				}
			}
		}

		if (match == null) cursor = start;
		return match;
	}
	
	private Expression matchParenExpression() {
		Expression match = null;
		int start = cursor;
		
		if (matchSymbol(SymbolToken.OpenParToken)) {
			Expression expression = matchExpression();
			if (expression != null && matchSymbol(SymbolToken.CloseParToken)) {
				match = expression;
				log("Matched expression with parentheses!");
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private VariableExpression matchVariableExpression() {
		VariableExpression match = null;
		int start = cursor;
		
		String varName = matchVariableName();
		if (varName != null) {
			match = new VariableExpression(varName);
			log("Matched variable expression!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private LiteralExpression matchLiteralExpression() {
		LiteralExpression match = null;
		int start = cursor;
		
		Value value;
		if ((value = matchLiteralString()) != null) {
			match = new LiteralExpression(value);
		} else if ((value = matchLiteralInteger()) != null) {
			match = new LiteralExpression(value);
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private Value matchLiteralString() {
		Value match = null;
		
		if (getCursorToken() instanceof StringToken) {
			match = new Value(BuiltinType.StringType, ((StringToken)getCursorToken()).getValue());
			++cursor;
			log("Matched literal string \""+match.getValue()+"\"!");
		}
		
		return match;
	}
	
	private Value matchLiteralInteger() {
		Value match = null;
		
		if (getCursorToken() instanceof IntegerToken) {
			match = new Value(BuiltinType.IntegerType, ((IntegerToken)getCursorToken()).getValue());
			++cursor;
			log("Matched literal integer "+match.getValue()+"!");
		}
		
		return match;
	}
	
	private CallStatement matchCall() {
		CallStatement match = null;
		int start = cursor;
		
		CallExpression call = matchCallExpression();
		if (call != null && matchSeparator()) {
			match = new CallStatement(call);
			log("Matched call statement!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private boolean matchLast() {
		return cursor >= tokens.size();
	}
}
