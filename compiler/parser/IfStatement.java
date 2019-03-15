package parser;

public class IfStatement implements Statement {
	private Expression condition;
	private ScopeStatement body;
	
	public IfStatement(Expression condition, ScopeStatement body) {
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
		System.out.println("If Statement ( "+condition.toString()+" )");
		
		body.visit();
	}
}
