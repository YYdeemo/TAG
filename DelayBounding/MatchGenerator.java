package DelayBounding;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import Syntax.*;
import Syntax.Process;

public class MatchGenerator 
{
	private Program ctp;
	private boolean Preemptive;
	//private int[] pointer;
	private int D; //delay
	private LinkedList<Recv>[] RQueue;
	private LinkedList<Send>[] SQueue;
		
	public MatchGenerator(Program ctp_i, int delay,boolean preemptive)
	{
		D = delay;
		ctp = ctp_i;
		Preemptive = preemptive;
		//pointer = new int[ctp_i.size()];
		RQueue = new LinkedList[ctp.size()];
		SQueue = new LinkedList[ctp.size()];
		for(int i = 0; i < ctp_i.size(); i++)
		{
			RQueue[i] = new LinkedList<Recv>();
			SQueue[i] = new LinkedList<Send>();
			//pointer[i] = 0;
		}
	}
	
	public void generate() throws Exception
	{
		Hashtable<Recv, LinkedList<Send>> matches = ctp.match_table;
		//LinkedList<Recv>[] RQueue = new LinkedList[ctp.size()];
		//LinkedList<Send>[] SQueue = new LinkedList[ctp.size()];
		int[] pointer = new int[ctp.size()];
		/*for(int i = 0; i < pointer.length; i++)
		{
			RQueue[i] = new LinkedList<Recv>();
			SQueue[i] = new LinkedList<Send>();
			pointer[i] = 0;
		}*/
		schedule(D,matches,0,pointer,0);
	}
	
	void schedule(int delay,Hashtable<Recv, LinkedList<Send>> matches, int current,
			//LinkedList<Recv>[] RQueue, LinkedList<Send>[] SQueue, 
			int[] pointer,int printindex) throws Exception
	{
		boolean x = true;
		for(int i = 0; i < ctp.size(); i++)
		{
			if(!isEnd(i,pointer))
			{
				x = false;
				break;
			}
		}
		
		if(x)
		{
			//add match resolution or match pairs
			//System.out.println("end on delay " + delay);
			return;
		}
		
		//may initialize until when they are used
		LinkedList<Recv> rlist = new LinkedList<Recv>();
		LinkedList<Send> slist = new LinkedList<Send>();
		
		//addedr only needs to use one counter because all the processed receives have common destination "current"
		int[] addedr = new int[1];
		int[] addeds = new int[ctp.size()];
		
		if(!isEnd(current,pointer))
	 	{
			
			//System.out.println(current+" "+isEnd(current,pointer) + ", delay = " + delay);
			
			//delay
			if(delay > 0)
			{
				//LinkedList<Recv>[] RQueue_p = new LinkedList[ctp.size()];
				//LinkedList<Send>[] SQueue_p = new LinkedList[ctp.size()];
				int[] pointer_p = new int[ctp.size()];
				for(int i = 0; i < ctp.size(); i++)
				{
					//RQueue_p[i] = (LinkedList<Recv>) RQueue[i].clone();
					//SQueue_p[i] = (LinkedList<Send>) SQueue[i].clone();
					//System.out.println(SQueue_p[i]);

					pointer_p[i] = pointer[i];
				}
				
				schedule(delay-1,matches,(current+1)%ctp.size(),
						//RQueue_p,SQueue_p,
						pointer_p,printindex+1);
			}
				//System.out.println(SQueue[0]);
				//System.out.println(RQueue[0]);
/*
			System.out.print("before[" + printindex+"]: ");
			for(int i = 0; i < SQueue.length; i++)
			{
				System.out.println("(" + i+")" + SQueue[i]);
			}
			*/
			traverse(matches,current,rlist,slist,addedr,addeds,pointer);
		}
		
		
		
		schedule(delay,matches,(current+1)%ctp.size(),pointer,printindex+1);
		
		//insert r back to queue at the beginning
		if(!rlist.isEmpty())
		{
			RQueue[current].addAll(0, rlist);
		}
		//inseart s back to queue at the beginning
		if(!slist.isEmpty())
		{
			SQueue[current].addAll(0, slist);
		}
		
		if(addedr[0]>0)
		{
			for(int i = 0; i < addedr[0]; i++)
			{
				RQueue[current].removeLast();
			}
		}
		
		//System.out.println("current = " + current + " adds before: " + addeds[0]+" " + addeds[1] + " " + addeds[2]);
		for(int i = 0; i < addeds.length; i++)
		{
			if(addeds[i] > 0){
				for(int j = 0; j < addeds[i]; j++)
					SQueue[i].removeLast();
			}
		}
		
		/*
		System.out.print("after[" + printindex +"]: ");
		for(int i = 0; i < SQueue.length; i++)
		{
			System.out.println("(" + i+")" + SQueue[i]);
		}
		*/
		
	}
	
	void traverse(Hashtable<Recv, LinkedList<Send>> matches, int current,
			LinkedList<Recv> removedr, LinkedList<Send> removeds,
			int[] addedr,int[] addeds,
			//LinkedList<Recv>[] RQueue, LinkedList<Send>[] SQueue, 
			int[] pointer) throws Exception
	{
		boolean yield = false;
		Process process = ctp.get(current); 
		while(!yield && process.get(pointer[current])!=null)
	 	{
			//System.out.println("point:" + pointer[current]);

			if(process.get(pointer[current]) instanceof Recv)
			{
				Recv R = (Recv)process.get(pointer[current]);
				if(R.isBlock || Preemptive)
				{
					//stop traversing the next operation, preemption point
					//need to match all the previous receives before trying to match R
					Recv R_top = RQueue[current].isEmpty() ? null :	RQueue[current].getFirst();
					Send S_match = getmatch(R_top,current,pointer);
					//System.out.println("("+R_top+", "+S_match+")");

					while(S_match != null)
					{
						//add S_match to removed list
						removeds.addLast(S_match);
						Recv rmdR = RQueue[current].removeFirst();
						//add rmdR to removed list
						removedr.addLast(rmdR);
						if(!matches.contains(R_top))
						{
							matches.put(R_top, new LinkedList<Send>());
						}
						
						//System.out.println("("+R_top+", "+S_match+")");
						
						if(!matches.get(R_top).contains(S_match)){
							
							matches.get(R_top).add(S_match);
						}
						R_top = RQueue[current].isEmpty() ? null :	RQueue[current].getFirst();
						S_match = getmatch(R_top,current,pointer);						
					}
					
					if(RQueue[current].isEmpty())
					{
						S_match = getmatch(R,current,pointer);
						//System.out.println("("+R+", "+S_match+")");
						
						if(S_match !=null)
						{
							//adds S_match to removed list
							removeds.addLast(S_match);
							
							if(!matches.containsKey(R))
							{
								//System.out.println(matches);
								//System.out.println("not included: " + R);
								matches.put(R, new LinkedList<Send>());
							}
							if(!matches.get(R).contains(S_match))
								matches.get(R).add(S_match);
							
							//R is matched, so the pointer can be incremented.
							pointer[current]++;
						}
						else{yield = true;}
					}
					else	yield = true;					
				}
				else 
				{
					RQueue[current].addLast(R);
					//increase added receive
					addedr[0]++;
					pointer[current]++;
				}
				
			}

			if(process.get(pointer[current]) instanceof Send)
			{
				Send S = (Send)process.get(pointer[current]);
				if(Preemptive)
				{
					//stop traversing the next operation, preemption point
					yield = true;
				}
				//enqueue S, regardless if S is a preemption point or not
				SQueue[S.dest].addLast(S);
				//increase added send
				addeds[S.dest]++;
				//System.out.println("adds " + addeds);
				pointer[current]++;
			}

			if(process.get(current) instanceof Wait)
			{
				Wait wait = (Wait)process.get(current);
				
				Recv R_top = RQueue[current].isEmpty() ? null :	RQueue[current].getFirst();
				Recv R_w = (Recv)wait.op;
				Send S_match = getmatch(R_top,current,pointer);
				while(RQueue[current].contains(R_w) && S_match != null)
				{
					//add S_match to removed list
					removeds.addLast(S_match);
					Recv rmdR = RQueue[current].removeFirst();
					//add rmdR to removed list
					removedr.addLast(rmdR);
					if(!matches.containsKey(R_top))
					{
						matches.put(R_top, new LinkedList<Send>());
					}
					if(!matches.get(R_top).contains(S_match))
						matches.get(R_top).add(S_match);
					
					R_top = RQueue[current].isEmpty() ? null :	RQueue[current].getFirst();
					S_match = getmatch(R_top,current,pointer);					
					
				}

				yield = true;
				if(!RQueue[current].contains(R_w))
					pointer[current]++;
			}
		}
	}
	
	Send getmatch(Recv R, int dest, int[] pointer)
	{
		if(R == null)
			return null;
		
		if(SQueue[dest].isEmpty())
			return null;
		
		if (R.src == -1) //wildcard receive
		{
			Send s_match = SQueue[dest].removeFirst();
			return s_match;
		}
		else //deterministic receive
		{
			for(int i = 0; i < SQueue[dest].size(); i++)
			{
				Send s = SQueue[dest].get(i);
				if(s.src == R.src)
				{
					SQueue[dest].remove(i);
					return s;
				}
			}
			
			//no sends found
			return null;
		}
	}
	
	boolean isEnd(int i,int[] pointer) throws Exception
	{
		return pointer[i] >= ctp.get(i).size();
	}

}
