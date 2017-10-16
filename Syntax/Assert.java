package Syntax;

public class Assert extends Operation{
	
	public String[] var_value;
	public String[] op;

	public Assert(String event, Process process, String[] var_value, String[] op) {
		super(event, process);
		this.var_value = var_value;
		this.op = op;
	}
	
	public String toString()
	{
		return "Assert" + event;
	}

}
