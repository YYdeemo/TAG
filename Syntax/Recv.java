package Syntax;
import java.util.BitSet;

public class Recv extends Operation {
	
	public int src;
	public int dest;
	public int rank;
	public Send match;
	public int var;
	public boolean isBlock;
	public Wait NearestWait;
	public Operation nextOP;
	public int tag;
	
	public Recv(String event, Process process, int rank, int src, 
			int dest, Send match, boolean isBlock, Wait nw,int tag) {
		super(event, process);
		this.src = src;
		this.dest = dest;
		this.match = match;
		this.rank = rank;
		//this.var = new BitSet();
		this.isBlock = isBlock;
		this.NearestWait = nw;
		this.tag = tag;
	}
	
	public String toString()
	{
		return "Recv" + event;
	}
}
