package Syntax;

public class RSend extends Send {

	public RSend(String event, Process process, int rank, int src, int dest,
			Recv match, int value, boolean isBlock, Wait nw, int tag) {
		super(event, process, rank, src, dest, match, value, isBlock, nw, tag);
		// TODO Auto-generated constructor stub
	}
	
	public String toString()
	{
		return "RSend" + event;
	}

}
