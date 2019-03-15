package compiler.parser;

import java.util.ArrayList;

import compiler.lexer.IndentToken;
import compiler.lexer.NameToken;
import compiler.lexer.Token;

public class Parser {
	private ArrayList<Token> tokens;
	private ScopeStatement root;
	private int cursor;
	
	private int currentIndent;
	
	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
		
		root = new ScopeStatement();
		cursor = 0;
	}
	
	public void parse() {
		currentIndent = 0;
		root = matchScope();
	}
	
	public void visit() {
		root.visit();
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
	
	private ScopeStatement matchScope() {
		System.out.println("Matching scope");
		ScopeStatement result = new ScopeStatement();
		
		while (cursor < tokens.size()) {
			Statement statement;
			if (matchScopeSame()) {
				// Consume indent tokens that are of the same indent level
			} else if (matchScopeUp()) {
				result.add(matchScope());
			} else if (matchScopeDown()) {
				break;
			} else if ((statement = matchVariable()) != null) {
				result.add(statement);
			} else if ((statement = matchIf()) != null) {
				result.add(statement);
			} else if ((statement = matchOpCall()) != null) {
				result.add(statement);
			} else {
				// Invalid token sequence
				System.out.println("Invalid token sequence encountered "+getCursorToken().toString());
				break;
			}
		}
		
		return result;
	}
	
	private boolean matchScopeUp() {
		System.out.println("Matching scope up"+cursor);
		// TODO: Handle more than one scope up at once
		
		boolean result = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() - currentIndent == 1) {
			result = true;
			++cursor;
			++currentIndent;
			System.out.println("Matched scope up!");
		}
		
		return result;
	}
	
	private boolean matchScopeDown() {
		System.out.println("Matching scope down"+cursor);
		// TODO: Handle more than one scope down at once
		
		boolean result = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() - currentIndent == -1) {
			result = true;
			++cursor;
			--currentIndent;
			System.out.println("Matched scope down!");
		}
		
		return result;
	}
	
	private boolean matchScopeSame() {
		System.out.println("Matching scope same" + cursor);
		boolean result = false;
		
		if (getCursorToken() instanceof IndentToken && ((IndentToken)getCursorToken()).getSize() == currentIndent) {
			result = true;
			++cursor;
			System.out.println("Matched scope same!");
		}
		
		return result;
	}
	
	private VariableStatement matchVariable() {
		System.out.println("Matching variable"+cursor);
		VariableStatement result = null;
		
		if (getCursorToken() instanceof NameToken) {
			String name = ((NameToken)getCursorToken()).getName();
			if (getCursorToken(1) instanceof NameToken && ((NameToken)getCursorToken(1)).getName().equals("var")) {
				if (getCursorToken(2) instanceof NameToken) {
					String type = ((NameToken)getCursorToken(2)).getName();
					result = new VariableStatement(name, type, "");
					cursor += 3;
					System.out.println("Matched variable!");
				}
			}
		}
		
		return result;
	}
	
	private IfStatement matchIf() {
		System.out.println("Matching if"+cursor);
		IfStatement result = null;
		
		if (getCursorToken() instanceof NameToken && ((NameToken)getCursorToken()).getName().equals("if")) {
			++cursor;
			Expression condition = matchExpression();
			if (condition != null && matchScopeUp()) {
				result = new IfStatement(condition, matchScope());
				System.out.println("Matched if!");
			}
			else {
				--cursor;
			}
		}
		
		return result;
	}
	
	private CallExpression matchIsOp() {
		System.out.println("Matching is op");
		CallExpression result = null;
		
		if (getCursorToken() instanceof NameToken) {
			String lhs = ((NameToken)getCursorToken()).getName();
			if (getCursorToken(1) instanceof NameToken && ((NameToken)getCursorToken(1)).getName().equals("is")) {
				if (getCursorToken(2) instanceof NameToken) {
					String rhs = ((NameToken)getCursorToken(2)).getName();
					cursor += 3;
					result = new CallExpression("is", new Expression[]{new VariableExpression(lhs), new VariableExpression(rhs)});
					System.out.println("Matched is op!");
				}
			}
		}
		
		return result;
	}
	
	private CallStatement matchOpCall() {
		System.out.println("Matching op call"+cursor);
		CallStatement result = null;
		
		if (getCursorToken() instanceof NameToken) {
			String operator = ((NameToken)getCursorToken()).getName();
			System.out.println("OP "+operator);
			if (getCursorToken(1) instanceof NameToken) {
				String varName = ((NameToken)getCursorToken(1)).getName();
				cursor += 2;
				result = new CallStatement(new CallExpression(operator, new Expression[]{new VariableExpression(varName)}));
				System.out.println("Matched op call!");
			}
		}
		
		return result;
	}
	
	private Expression matchExpression() {
		System.out.println("Matching expression");
		Expression result = null;
		
		result = matchIsOp();
		
		if (result != null) {
			System.out.println("Matched expression!");
		}
		
		return result;
	}
}
