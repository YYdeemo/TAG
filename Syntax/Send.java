package Syntax;

import java.util.BitSet;

public class Send extends Operation {
	
	public int src;
	public int dest;
	public Recv match;
	public int value;// should be bitset
	public int rank;
	public boolean isBlock;
	public Wait NearestWait;
	public Operation nextOP;	//?
	public int tag;
	
	public Send(String event, Process process, int rank, int src, int dest, Recv match, int value, 
			boolean isBlock, Wait nw,int tag) {
		super(event, process);
		this.src = src;
		this.dest = dest;
		this.match = match;
		this.rank = rank;
		this.value = value;
		this.isBlock = isBlock;
		this.NearestWait = nw;
		this.tag = tag;
	}
	
	public String toString()
	{
		return "Send" + event;
	}
}
