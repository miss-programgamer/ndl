package parser.statements;

import parser.expressions.Expression;

public class WhileStatement implements Statement {
	private Expression condition;
	private ScopeStatement body;
	
	public WhileStatement(Expression condition, ScopeStatement body) {
		this.condition = condition;
		this.body = body;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public ScopeStatement getBody() {
		return body;
	}
	
	@Override
	public void visit() {
		System.out.println("While Statement ( "+condition.toString()+" )");
		
		body.visit();
	}
}
