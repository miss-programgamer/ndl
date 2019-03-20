package parser;

import java.util.ArrayList;
import java.util.List;

import interpreter.Value;
import lexer.tokens.*;
import parser.expressions.*;
import parser.statements.*;
import parser.types.*;

public class Parser {
	private ArrayList<Token> tokens;
	
	private ScopeStatement rootScope;
	private ScopeStatement currentScope;
	private int cursor;
	private int currentIndent;
	
	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public ScopeStatement parse() {
		cursor = 0;
		currentIndent = -1;
		rootScope = matchScope(null);
		
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
		System.out.println("Cursor: "+cursor+"\t- "+out);
	}
	
	private ScopeStatement matchScope(ScopeStatement outer) {
		ScopeStatement match = new ScopeStatement(outer);
		currentScope = match;
		
		if (matchIndentUp()) {
			while (cursor < tokens.size()) {
				Statement statement;
				if (matchIndentSame()) {
					// Consume indent tokens that are of the same indent level
				} else if (matchIndentDown()) {
					break;
				} else if ((statement = matchVariable()) != null) {
					match.addStatement(statement);
					match.addVariable((VariableStatement)statement);
				} else if ((statement = matchFunction()) != null) {
					match.addFunction((FunctionStatement)statement);
				} else if ((statement = matchIf()) != null) {
					match.addStatement(statement);
				} else if ((statement = matchCall()) != null) {
					match.addStatement(statement);
				} else {
					// Invalid token sequence
					log("Invalid token sequence encountered "+getCursorToken().toString());
					break;
				}
			}
		}
		
		currentScope = match.getOuterScope();
		return match;
	}
	
	private boolean matchIndentUp() {
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() - currentIndent > 0) {
			match = true;
			++currentIndent;
			log("Matched indent up! (indent now "+currentIndent+")");
		}
		
		return match;
	}
	
	private boolean matchIndentDown() {
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() - currentIndent < 0) {
			match = true;
			--currentIndent;
			log("Matched indent down! (indent now "+currentIndent+")");
		}
		
		return match;
	}
	
	private boolean matchIndentSame() {
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() == currentIndent) {
			match = true;
			++cursor;
			log("Matched scope same!");
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
			log("Matched variable name "+name+"!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private String matchFunctionName() {
		String match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && currentScope.getVariable(name) == null) { // At this point, we determine that if it's not a variable, then it must be a function. This assumption is verified later in a second pass
			match = name;
			log("Matched function name "+name+"!");
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
			log("Matched symbol \""+token.getSymbol()+"\"!");
		}
		
		return match;
	}
	
	private boolean matchOperator(OperatorToken token) {
		boolean match = false;
		
		if (getCursorToken() instanceof OperatorToken && getCursorToken() == token) {
			match = true;
			++cursor;
			log("Matches operator ["+token.getOperator()+"]!");
		}
		
		return match;
	}
	
	private VariableStatement matchVariable() {
		VariableStatement match = null;
		int start = cursor;
		
		String name = matchUndefinedName();
		if (name != null && matchKeyword(KeywordToken.VarToken)) {
			String type = matchTypeName();
			if (type != null) {
				Expression value = null;
				if (matchSymbol(SymbolToken.EqualsToken)) {
					value = matchExpression();
				}
				match = new VariableStatement(name, BuiltinType.get(type), value);
				log("Matched variable "+name+" "+type+"!");
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private FunctionStatement matchFunction() {
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
						ScopeStatement body = matchScope(currentScope);
						argumentList.add(arg1);
						argumentList.add(arg2);
						match = new FunctionStatement(name, argumentList, resultList, body);
					}
				} else {
					ScopeStatement body = matchScope(currentScope);
					argumentList.add(arg1);
					match = new FunctionStatement(name, argumentList, resultList, body);
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
				ScopeStatement body = matchScope(currentScope);
				if (body != null) {
					match = new IfStatement(condition, body);
					log("Matched if!");
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
		
		String operator = matchFunctionName();
		if (operator != null) {
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
		if (call != null) {
			match = new CallStatement(call);
			log("Matched call statement!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
}
