package parser;

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
		System.out.print("Call Statement ( "+call.getOperator()+" ( ");
		
		for (int index = 0; index < call.getArgumentCount(); ++index) {
			if (index > 0) System.out.print(",");
			System.out.print(call.getArgument(index).toString());
		}
		
		System.out.println(" ) )");
	}
}
