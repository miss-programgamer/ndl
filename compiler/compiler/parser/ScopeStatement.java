package compiler.parser;

import java.util.ArrayList;

public class ScopeStatement implements Statement {
	private ArrayList<Statement> statements;
	
	public ScopeStatement() {
		statements = new ArrayList<Statement>();
	}
	
	public boolean add(Statement statement) {
		return statements.add(statement);
	}
	
	public ArrayList<Statement> getStatements() {
		return statements;
	}
	
	@Override
	public void visit() {
		for (Statement statement : statements) {
			statement.visit();
		}
	}
}
