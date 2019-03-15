package parser;

import java.util.ArrayList;

import lexer.IndentToken;
import lexer.KeywordToken;
import lexer.NameToken;
import lexer.OperatorToken;
import lexer.SymbolToken;
import lexer.Token;

public class Parser {
	private ArrayList<Token> tokens;
	
	private ScopeStatement rootScope;
	private ScopeStatement currentScope;
	private int cursor;
	private int currentIndent;
	
	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public void parse() {
		cursor = 0;
		currentIndent = 0;
		rootScope = matchScope(null);
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
	
	private ScopeStatement matchScope(ScopeStatement outer) {
		System.out.println("Matching scope"+cursor);
		ScopeStatement match = new ScopeStatement(outer);
		currentScope = match;
		
		while (cursor < tokens.size()) {
			Statement statement;
			if (matchScopeSame()) {
				// Consume indent tokens that are of the same indent level
			} else if (matchScopeUp()) {
				match.addStatement(matchScope(match));
			} else if (matchScopeDown()) {
				currentScope = match.getOuterScope();
				break;
			} else if ((statement = matchVariable()) != null) {
				match.addVariable((VariableStatement)statement);
			} else if ((statement = matchFunction()) != null) {
				match.addFunction((FunctionStatement)statement);
			} else if ((statement = matchIf()) != null) {
				match.addStatement(statement);
			} else if ((statement = matchCall()) != null) {
				match.addStatement(statement);
			} else {
				// Invalid token sequence
				System.out.println("Invalid token sequence encountered "+getCursorToken().toString());
				break;
			}
		}
		
		return match;
	}
	
	private boolean matchScopeUp() {
		System.out.println("Matching scope up "+cursor);
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() - currentIndent > 0) {
			match = true;
			currentIndent += ((IndentToken)getCursorToken()).getSize() - currentIndent;
			++cursor;
			System.out.println("Matched scope up!");
		}
		
		return match;
	}
	
	private boolean matchScopeDown() {
		System.out.println("Matching scope down "+cursor);
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() - currentIndent < 0) {
			match = true;
			currentIndent += ((IndentToken)getCursorToken()).getSize() - currentIndent;
			++cursor;
			System.out.println("Matched scope down!");
		}
		
		return match;
	}
	
	private boolean matchScopeSame() {
		System.out.println("Matching scope same "+cursor);
		boolean match = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() == currentIndent) {
			match = true;
			++cursor;
			System.out.println("Matched scope same!");
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
		System.out.println("Matching variable name "+cursor);
		String match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && currentScope.getVariable(name) != null) {
			match = name;
			System.out.println("Matched variable name "+name);
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private String matchFunctionName() {
		System.out.println("Matching function name "+cursor);
		String match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && currentScope.getVariable(name) == null) { // At this point, we determine that if it's not a variable, then it must be a function. This assumption is verified later in a second pass
			match = name;
			System.out.println("Matched function name "+name);
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private boolean matchKeyword(KeywordToken token) {
		System.out.println("Matching keyword "+cursor);
		boolean match = false;
		
		if (getCursorToken() instanceof KeywordToken && getCursorToken() == token) {
			match = true;
			++cursor;
		}
		
		return match;
	}
	
	private boolean matchSymbol(SymbolToken token) {
		System.out.println("Matching symbol "+cursor);
		boolean match = false;
		
		if (getCursorToken() instanceof SymbolToken && getCursorToken() == token) {
			match = true;
			++cursor;
		}
		
		return match;
	}
	
	private boolean matchOperator(OperatorToken token) {
		boolean match = false;
		
		if (getCursorToken() instanceof OperatorToken && getCursorToken() == token) {
			match = true;
			++cursor;
		}
		
		return match;
	}
	
	private VariableStatement matchVariable() {
		System.out.println("Matching variable "+cursor);
		VariableStatement match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && matchKeyword(KeywordToken.VarToken)) {
			String type = matchName();
			if (type != null) {
				match = new VariableStatement(name, type, "");
				System.out.println("Matched variable!");
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private FunctionStatement matchFunction() {
		System.out.println("Matching function "+cursor);
		FunctionStatement match = null;
		int start = cursor;
		
		String name = matchName();
		if (name != null && matchKeyword(KeywordToken.FuncToken)) {
			VariableStatement arg1 = matchVariable();
			if (arg1 != null) {
				if (matchSymbol(SymbolToken.CommaToken)) {
					VariableStatement arg2 = matchVariable();
					if (arg2 != null) {
						ScopeStatement body = matchScope(currentScope);
						match = new FunctionStatement(name, new ArrayList<VariableStatement>() {{add(arg1); add(arg2);}}, new ArrayList<VariableStatement>(), body);
					}
				} else {
					ScopeStatement body = matchScope(currentScope);
					match = new FunctionStatement(name, new ArrayList<VariableStatement>() {{add(arg1);}}, new ArrayList<VariableStatement>(), body);
				}
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private IfStatement matchIf() {
		System.out.println("Matching if"+cursor);
		IfStatement match = null;
		int start = cursor;
		
		if (matchKeyword(KeywordToken.IfToken)) {
			Expression condition = matchExpression();
			if (condition != null && matchScopeUp()) {
				match = new IfStatement(condition, matchScope(currentScope));
				System.out.println("Matched if!");
			}
		}

		if (match == null) cursor = start;
		return match;
	}
	
	private Expression matchExpression() {
		// TODO literal
		
		Expression match = null;
		int start = cursor;
		
		Expression expression;
		if ((expression = matchParenExpression()) != null) {
			match = expression;
		} else if ((expression = matchCallExpression()) != null) {
			match = expression;
		} else if ((expression = matchVariableExpression()) != null) {
			match = expression;
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private Expression matchParenExpression() {
		System.out.println("Matching expression with parentheses "+cursor);
		Expression match = null;
		int start = cursor;
		
		if (matchSymbol(SymbolToken.OpenParToken)) {
			Expression expression = matchExpression();
			if (expression != null && matchSymbol(SymbolToken.CloseParToken)) {
				match = expression;
				System.out.println("Matched expression with parentheses");
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private CallExpression matchCallExpression() {
		System.out.println("Matching call "+cursor);
		CallExpression match = null;
		int start = cursor;
		
		String operator = matchFunctionName();
		if (operator != null) {
			System.out.println("operator not null");
			if (matchSymbol(SymbolToken.OpenParToken)) {
				System.out.println("openpar matched");
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
					match = new CallExpression(operator, (Expression[]) arguments.toArray());
					System.out.println("Matched function call!");
				}
			} else {
				Expression argument = matchExpression();
				if (argument != null) {
					match = new CallExpression(operator, new Expression[] {argument});
					System.out.println("Matched unary operator call!");
				}
			}
		} else {
			Expression lhs = matchVariableExpression();
			if (lhs != null) {
				operator = matchFunctionName();
				if (operator != null) {
					Expression rhs = matchVariableExpression();
					if (rhs != null) {
						match = new CallExpression(operator, new Expression[] {lhs, rhs});
						System.out.println("Matched binary operator call!");
					}
				}
			}
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private VariableExpression matchVariableExpression() {
		System.out.println("Matching variable expression "+cursor);
		VariableExpression match = null;
		int start = cursor;
		
		String varName = matchVariableName();
		if (varName != null) {
			match = new VariableExpression(varName);
			System.out.println("Matched variable expression!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
	
	private CallStatement matchCall() {
		System.out.println("Matching call statement "+cursor);
		CallStatement match = null;
		int start = cursor;
		
		CallExpression call = matchCallExpression();
		if (call != null) {
			match = new CallStatement(call);
			System.out.println("Matched call statement!");
		}
		
		if (match == null) cursor = start;
		return match;
	}
}
