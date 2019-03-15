package compiler.parser;

public class CallStatement implements Statement {
	private CallExpression call;
	
	public CallStatement(CallExpression call) {
		this.call = call;
	}
	
	public CallExpression getCall() {
		return call;
	}
	
	@Override
	public void visit() {
		System.out.println("Calling function "+call.getOperator()+" with "+call.getArgumentCount()+" argument(s)");
	}
}
