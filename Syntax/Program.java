package Syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import DelayBounding.MatchGenerator;

public class Program {
	public String name = "";
	public LinkedList<Process> processes;
	public Hashtable<Recv, LinkedList<Send>> match_table;
	public Hashtable<Send, LinkedList<Recv>> pattern_match;
	boolean isPattern;		
	//public LinkedList<Recv>[][] rlistwithtag;
	//public LinkedList<Send>[][] SwithtagforR;
	
//	public LinkedList<Recv>[] recvlist;
	//public LinkedList<Send>[][] sendlist;
	
	//input for delay bounding scheduler
	public boolean useDelayBound = false;
	public boolean Overapproximate = true;
	int delay = 1;
	int phraseSize = 1;
	boolean preemptive = true;
	
	int testedprocess = 0; //for printing the info for a specific process
	int sectionNforP = 0; //for printing the section number for a specific process
	
	public Program(boolean mismatchedEndpoint)
	{
		isPattern = mismatchedEndpoint;
		processes = new LinkedList<Process>();
		match_table = new Hashtable<Recv, LinkedList<Send>>();
		pattern_match = new Hashtable<Send, LinkedList<Recv>>();
	}
	
	
	public int getMatchSize()
	{
		int num = 0;
		
		for(Recv r : match_table.keySet())
		{
			for(Send s : match_table.get(r))
			{
				num++;
			}
		}
		
		return num;
	}
	
	public int getMatchSize(int p,int q)
	{
		int num = 0;
		
		for(Recv r : match_table.keySet())
		{
			//***************************
			if(r.dest == p && r.tag == q){
				for(Send s : match_table.get(r))
				{
					num++;
				}
			}
		}
		
		return num;
	}
	
	public boolean isPattern()
	{
		return isPattern;
	}
	
	public void add(Process proc)
	{
		processes.add(proc);
	}
	
	
	
	public HashSet<UnmatchedEP_Pattern> getUnmatchedEP_Pattern()
	{
		HashSet<UnmatchedEP_Pattern> set = new HashSet<UnmatchedEP_Pattern>();
		for(Process p: processes)
		{
			//**********
			if(p.hasDeterminsticRecv() && p.hasTagRecv())
			{
				set.addAll(p.getUnmatchedEPPatternSet());
			}
		}
		
		return set;
	}
	
	public Process get(int i) throws Exception
	{
		if(i >= processes.size())
			throw new Exception(i + " is too large!");
		
		return processes.get(i);
	}
	
	public int size()
	{
		return processes.size();
	}
	
	public void InitGraph() throws Exception
	{
		for(Process process : processes)
		{
			process.generateVE();
		}
		
		generateMatch();
		displayMatch();
		//System.out.println("Match pair size for p"+ testedprocess + ": " + this.getMatchSize(testedprocess));
		//System.out.println("Section number for p"+ testedprocess + ": " + this.sectionNforP);
		//System.out.println("Match pair size: " + this.getMatchSize());
		
	}
	
	public void displayMatch()
	{
		int index = 0;

		for(Recv r : match_table.keySet())
		{
			
			LinkedList<Send> slist = match_table.get(r);
			int count = 0;
			System.out.print("[" + index +"] ");
			index++;
			for(Send s : slist)
			{
				System.out.print("(r" + r.event + ", s" + s.event + ") or ");
				count++;
			}
			System.out.print(count);
			System.out.println();
		}
	}
	
	public void generateMatch() throws Exception
	{
		if(useDelayBound)
		{
			long t1 = System.currentTimeMillis();
			System.out.println("Generating match pairs with delay bounding scheduler:");
			MatchGenerator generator = new MatchGenerator(this,delay,preemptive);
			generator.generate();
			long t2 = System.currentTimeMillis();
			System.out.println("Match pair generation executes " + ((double)(t2-t1))/(double)1000 + "seconds");
		}
		//use overapproximate to match pairs 
		else if(Overapproximate)
		{
			long t1 = System.currentTimeMillis();//get time1
			System.out.println("Generating overapproximated match pairs with counting:");
			//Match_overapproximate();//get match pairs
			//Match_withtag();
			third_matchpair();
			long t2 = System.currentTimeMillis();//get time2
			System.out.println("Match pair generation executes " + ((double)(t2-t1))/(double)1000 + "seconds");
		}
		else
		{
			//split the program into pieces and verify each piece;
			
			long t1 = System.currentTimeMillis();
			System.out.println("Generating underapproximated match pairs with counting:");
			Match_underapproximate(phraseSize);
			long t2 = System.currentTimeMillis();
			System.out.println("Match pair generation executes " + ((double)(t2-t1))/(double)1000 + "seconds");
		}
	}
	
	public void Match_underapproximate(int phraseSize)
	{
		LinkedList<Recv>[] Rlist = new LinkedList[this.size()];
		//LinkedList<Recv>[][] recvlist = new LinkedList[this.size()][this.size()+1];
		Hashtable<Integer, Hashtable<Integer, LinkedList<Send>>> sendlist = new Hashtable<Integer,Hashtable<Integer,LinkedList<Send>>>();
		for(Process process: processes)
		{
			Rlist[process.getRank()] = (LinkedList<Recv>) process.rlist.clone();
			for(Integer dest : process.slist.keySet())
			{
				if(!sendlist.containsKey(dest))
				{
					sendlist.put(dest, new Hashtable<Integer, LinkedList<Send>>());
				}

				sendlist.get(dest).put(process.getRank(), (LinkedList<Send>)process.slist.get(dest).clone());
				 
			}
		}
		
		
		
		
		for(int i = 0; i < Rlist.length;i++)
		{
			//iteratively extract match pairs for the receives on process i
			//each iteration is one phrase
			//use index to indicate the end of the current phrase
			//each iteration a receive is removed from Rlist when the match pairs for this receive are extracted
			//each iteration a send is removed from sendlist when the match pairs for this send are extracted 
			Process p = this.processes.get(i);
			while(Rlist[i].size() > 0
					&& sendlist.get(i).size()>0)
			{
				//for printing
				if(i == testedprocess)
					sectionNforP++;
				
				
				//n is the number of the senders to process i
				int n = sendlist.get(i).size();
				//if(sendlist.get(i).size()>0)
					//System.out.println(sendlist.get(i));
				//N is the number of receives (sends) for each phrase, 
				int N = Integer.min(n*phraseSize, Rlist[i].size());
				//hash table to maps source to the count of receives from beginning to endR of Rlist
				Hashtable<Integer, Integer> count_r = new Hashtable<Integer, Integer>();
				//Hashtable<Integer, Integer> count_s = new Hashtable<Integer, Integer>();
				Pair<Integer,Integer>[] count_s = new Pair[n];
				int index = 0;
				for(Integer src: sendlist.get(i).keySet())
				{
					count_s[index] = new Pair<Integer,Integer>(src,0);
					index++;
				}
				
				int distribute_n = n;
				int distributed = 0;
				//group the sends and receives for the current iteration
				for(int j = 0; j < N; j++)
				{
					Recv r = Rlist[i].get(j);
					//System.out.println(r);
					if(!count_r.containsKey(r.src))
						count_r.put(r.src, 0);
					count_r.put(r.src, count_r.get(r.src)+1);
					//the count of a send from src >= the count of receive from src
					if(r.src !=-1)
					{
						for(int m = 0; m < count_s.length;m++)
						{
							if(count_s[m].getFirst() == r.src)
							{
								//update count_s
									count_s[m].setSecond(count_s[m].getSecond()+1);
									distributed += 1;
								//count_r never gets larger than send size
								if(count_r.get(r.src) == sendlist.get(i).get(count_s[m].getFirst()).size())
									distribute_n--;
								break;
							}
						}

					}
				}
				
				//sort the count_s, then distribute the remainder to count_s
				int remainder = N-distributed;
				//int average = (distribute_n == 0) ? remainder : remainder/distribute_n;
				
				while(remainder > 0)
				{
					//sort count_s, distribute from the least count to most count
					sort(count_s);
					for(int j =0; j < count_s.length;j++)
					{
						int src = count_s[j].getFirst();
						int count = count_s[j].getSecond();
						if(count < sendlist.get(i).get(src).size())
						{
							count_s[j].setSecond(count+1);
							break;
						}
					}
					remainder -= 1;
					
					/*
					//used for each iteration that a count(>phrasesize) is only distributed further if all the prior counts cannot be distributed
					int distributable = 0;
					for(int j = 0; j < count_s.length; j++)
					{
						int src = count_s[j].getFirst();
						int count = count_s[j].getSecond();
						//distribute the send list that can be distributed
						if(count < sendlist.get(i).get(src).size() 
								&& (count<phraseSize || distributable == 0))
						{
							if(average < (sendlist.get(i).get(src).size() - count))
							{
								count_s[j].setSecond(count+average);
								distributed += average; 
								distributable ++;
							}
							else
							{
								count_s[j].setSecond(sendlist.get(i).get(src).size());
								distributed += (sendlist.get(i).get(src).size() - count); 
							}
						}
					}
					distribute_n -= distributable;
					remainder -= distributed;
					average = (distribute_n == 0) ? remainder : remainder/distribute_n;
					*/
				}
				
				generateMatchwithRanking(Rlist[i], sendlist.get(i), N, count_s);
				
				
			}
		}
		
	}
	
	void sort(Pair<Integer,Integer>[] a)
	{
		Arrays.sort(a, new Comparator<Pair<Integer,Integer>>() 
			{
				public int compare(Pair<Integer,Integer> o1, Pair<Integer,Integer> o2) {
					return o1.getSecond().compareTo(o2.getSecond());
				}
			});
	} 
	
	void generateMatchwithRanking(LinkedList<Recv> Rs, Hashtable<Integer, LinkedList<Send>> sendlist,
			int N,Pair<Integer,Integer>[] count_s)
	{
			
		for(int rank_r = 0; rank_r < N; rank_r++)
		{
			Recv r = Rs.get(rank_r);
			LinkedList<Send> sendlistforR = new LinkedList<Send>();

			for(int j = 0; j< count_s.length; j++)
			{
				int src = count_s[j].getFirst();
				int count = count_s[j].getSecond();
				for(int rank_s = 0; rank_s < count; rank_s++)
				{
					Send s = sendlist.get(src).get(rank_s);
					if(rank_r >= rank_s //rule 1
							&&rank_r <= rank_s + (N - count)//rule2
							&&(r.src == -1 || r.src == s.src)
							&&(r.tag == -1 || r.tag == s.tag))//***********
					{ 
						//either it is a wildcard receive or the endpoints commit
						sendlistforR.add(s);
						
						//System.out.println("("+r+","+s+")");
						
						//generate pattern_match
						if(isPattern)
						{

							if(!pattern_match.containsKey(s))
							{
								pattern_match.put(s, new LinkedList<Recv>());
							}
							pattern_match.get(s).add(r);
						}
					}
				}
			}
			//add the list with r to the match table
			if(!sendlistforR.isEmpty())
				match_table.put(r, sendlistforR);			
		}
		
		//TODO:remove receives and sends
		for(int i = 0; i < N; i++)
			Rs.removeFirst();
		//if(N>0) System.out.println(Rs);
		
		for(int i = 0; i < count_s.length; i++)
		{
			int src = count_s[i].getFirst();
			int count = count_s[i].getSecond();
			if(sendlist.get(src).size() == count)
				sendlist.remove(src);
			else
			{
				for(int j = 0; j < count; j++)
					sendlist.get(src).removeFirst();
			}
		}
	}
	
	public void Match_overapproximate()
	{
		 LinkedList<Recv>[] recvlist = new LinkedList[this.size()];//all receives 
		 LinkedList<Send>[][] sendlist = new LinkedList[this.size()][this.size()];//all sends
		 //store the rlist and slist of each process 
		for(Process process: processes)
		{
			recvlist[process.getRank()] = process.rlist;//recvlist[rank] = all recv in the process
			for(Integer dest : process.slist.keySet())
			{
				sendlist[dest][process.getRank()] = process.slist.get(dest);
			}//sendlist[1][2] = all send whose destination is 1 and come from process 2
			
		}
		
		
		for(int i = 0; i < recvlist.length; i ++)//i = ranks
		{
		
			 Iterator<Recv> ite_r = recvlist[i].iterator();
			 
			 //calculate # of sends from any source to i
			 int sendstoi = 0;
			 for(int j = 0; j < sendlist[i].length; j++){
				 if(sendlist[i][j] != null)
					 sendstoi += sendlist[i][j].size();
			 }
			 
			 while(ite_r.hasNext()){
				 Recv r = ite_r.next();
				 
				//	 System.out.println("recv in thread i = " + i + ": " + r.exp +" with rank " + r.rank);
				 LinkedList<Send> sendlistforR = new LinkedList<Send>();
				 for(int j = 0; j < sendlist[i].length; j++){
					 //no sends in sendlist[i][j]
					 if(sendlist[i][j] == null)
						 continue;
					 Iterator<Send> ite_s = sendlist[i][j].iterator();
					 
					 while(ite_s.hasNext()){
						 Send s = ite_s.next();
						 //compare and set
						 if(r.rank >= s.rank //rule 1
								 &&r.rank <= s.rank + (sendstoi - sendlist[i][j].size())//rule2
								 //???????r.src == s.dest
								 &&(r.src == -1 || r.src == s.src)
								 &&(r.tag == -1 || r.tag ==s.tag)){ //either it is a wildcard receive or the endpoints commit
							 sendlistforR.add(s);
							 
							 //generate pattern_match
							 if(isPattern)
							 {
//								 //check if the send is pattern send
//								 if(s.dest == pattern.process.getRank() 
//										 && s.src == pattern.determinstic.src)
//								 {
								 if(!pattern_match.containsKey(s))
								 {
									 pattern_match.put(s, new LinkedList<Recv>());
								 }
								 pattern_match.get(s).add(r);
							 }
						 }
					 }
				 }
				 //add the list with r to the match table
				 if(!sendlistforR.isEmpty())
					 match_table.put(r, sendlistforR);
			 }
		 }
	}
	
	
	public void third_matchpair(){

		ArrayList<Integer> Rtag = new ArrayList<>();
		//the key is a pair<X,Y> X means tag,Y means the process's rank 
		//the value is also a hashtable, the value is a linkedlist which has receivers(tag and endpoint is same and belong to same process),
		//the key is endpoint of receives. 
		Hashtable<Pair<Integer,Integer>,Hashtable<Integer,LinkedList<Recv>>> rlwithtag = new Hashtable<Pair<Integer,Integer>,Hashtable<Integer,LinkedList<Recv>>>();
		Hashtable<Pair<Integer,Integer>,Hashtable<Integer,LinkedList<Send>>> slwithtag = new Hashtable<Pair<Integer,Integer>,Hashtable<Integer,LinkedList<Send>>>();
		
		for(Process process: processes){
			if(process.slist.isEmpty())
				continue;
			for(Integer dest : process.slist.keySet()){
				if(process.slist.get(dest).isEmpty())
					continue;
				for(Send s : process.slist.get(dest)){
					Rtag.add(s.tag);
					//System.out.println("slist:"+s);
				}
			}
		}
		for(Process process:processes){
			for(Recv r:process.rlist){
				//System.out.println("rlist"+r);
			}
		}
		 
		for(Process process: processes){
			for(Recv r : process.rlist){
				Pair<Integer,Integer> tagandrank = new Pair<Integer, Integer>(r.tag, process.getRank());
				//while rtag == -1 ,every key about hashtable rlwithtag should add r
				if(r.tag==-1){
					if(r.src == -1){//R（tag = *, endpoint = *）
						for(Process processr : processes){
							for(Integer rdtag : Rtag){
								Pair<Integer,Integer> rtagpair = new Pair<Integer, Integer>(rdtag, process.getRank());
								if(rlwithtag.containsKey(rtagpair)){
									if((rlwithtag.get(rtagpair)).containsKey(processr.getRank())){
										if( !rlwithtag.get(rtagpair).get(processr.getRank()).contains(r))
											rlwithtag.get(rtagpair).get(processr.getRank()).add(r);
										r.rank = rlwithtag.get(rtagpair).get(processr.getRank()).indexOf(r);
									}else{
										LinkedList<Recv> recv = new LinkedList<Recv>();
										recv.add(r);
										rlwithtag.get(rtagpair).put(processr.getRank(), recv);
										r.rank = rlwithtag.get(rtagpair).get(processr.getRank()).indexOf(r);
									}
								}else
								{
									LinkedList<Recv> recv = new LinkedList<Recv>();
									recv.add(r);
									Hashtable<Integer,LinkedList<Recv>> hash = new Hashtable<>();
									hash.put(processr.getRank(), recv);
									rlwithtag.put(rtagpair , hash);	
									r.rank = rlwithtag.get(rtagpair).get(processr.getRank()).indexOf(r);
								}
							}
						}
					}else{//R(tag=* , endpoint != *)
						for(Integer rdtag : Rtag){
							Pair<Integer,Integer> rtagpair = new Pair<Integer, Integer>(rdtag, process.getRank());
							if(rlwithtag.containsKey(rtagpair)){
								if((rlwithtag.get(rtagpair)).containsKey(r.src)){
									if(!rlwithtag.get(rtagpair).get(r.src).contains(r))
										rlwithtag.get(rtagpair).get(r.src).add(r);
									r.rank = rlwithtag.get(rtagpair).get(r.src).indexOf(r);
								}else{
									LinkedList<Recv> recv = new LinkedList<Recv>();
									recv.add(r);
									rlwithtag.get(rtagpair).put(r.src, recv);
									r.rank = rlwithtag.get(rtagpair).get(r.src).indexOf(r);
								}
							}else
							{
								LinkedList<Recv> recv = new LinkedList<Recv>();
								recv.add(r);
								Hashtable<Integer,LinkedList<Recv>> hash = new Hashtable<>();
								hash.put(r.src, recv);
								rlwithtag.put(rtagpair , hash);	
								r.rank = rlwithtag.get(rtagpair).get(r.src).indexOf(r);
							}
						}
					}
				}else{
					if(r.src == -1){//R(tag!=*, endpoint=*)
						for(Process processr : processes){
							Pair<Integer,Integer> rtagpair = new Pair<Integer, Integer>(r.tag, process.getRank());
							if(rlwithtag.containsKey(rtagpair)){
								if((rlwithtag.get(rtagpair)).containsKey(processr.getRank())){
									if(!rlwithtag.get(rtagpair).get(processr.getRank()).contains(r))
										rlwithtag.get(rtagpair).get(processr.getRank()).add(r);
									r.rank = rlwithtag.get(rtagpair).get(processr.getRank()).indexOf(r);
								}else{
									LinkedList<Recv> recv = new LinkedList<Recv>();
									recv.add(r);
									rlwithtag.get(rtagpair).put(processr.getRank(), recv);
									r.rank = rlwithtag.get(rtagpair).get(processr.getRank()).indexOf(r);
								}
							}else
							{
								LinkedList<Recv> recv = new LinkedList<Recv>();
								recv.add(r);
								Hashtable<Integer,LinkedList<Recv>> hash = new Hashtable<>();
								hash.put(processr.getRank(), recv);
								rlwithtag.put(rtagpair , hash);	
								r.rank = rlwithtag.get(rtagpair).get(processr.getRank()).indexOf(r);
							}
						}
					}else{//R(tag!=*, endpoint!=*)
						if(rlwithtag.containsKey(tagandrank)){
							if((rlwithtag.get(tagandrank)).containsKey(r.src)){
								if(!rlwithtag.get(tagandrank).get(r.src).contains(r))
									rlwithtag.get(tagandrank).get(r.src).add(r);
								r.rank = rlwithtag.get(tagandrank).get(r.src).indexOf(r);
							}else{
								LinkedList<Recv> recv = new LinkedList<Recv>();
								recv.add(r);
								rlwithtag.get(tagandrank).put(r.src, recv);
								r.rank = rlwithtag.get(tagandrank).get(r.src).indexOf(r);
							}
						}
						else{
							LinkedList<Recv> recv = new LinkedList<Recv>();
							recv.add(r);
							Hashtable<Integer,LinkedList<Recv>> hash = new Hashtable<>();
							hash.put(r.src, recv);
							rlwithtag.put(tagandrank , hash);	
							r.rank = rlwithtag.get(tagandrank).get(r.src).indexOf(r);
						}
					}
				}
			}
			// generate the send list 
			if(process.slist.isEmpty()){
				continue;
			}
			for(Integer dest : process.slist.keySet()){
				if(process.slist.get(dest).isEmpty())
					continue;
				for(Send s : process.slist.get(dest)){
					Pair<Integer,Integer> tagandrank = new Pair<Integer, Integer>(s.tag, dest);
					if(slwithtag.containsKey(tagandrank)){
						if(slwithtag.get(tagandrank).containsKey(process.getRank())){
							if(!slwithtag.get(tagandrank).get(process.getRank()).contains(s))
								slwithtag.get(tagandrank).get(process.getRank()).add(s);
							s.rank = slwithtag.get(tagandrank).get(process.getRank()).indexOf(s);
						}else{
							LinkedList<Send> send = new LinkedList<Send>();
							send.add(s);
							slwithtag.get(tagandrank).put(process.getRank(), send);
							s.rank = slwithtag.get(tagandrank).get(process.getRank()).indexOf(s);
						}
					}else{
						LinkedList<Send> send = new LinkedList<Send>();
						send.add(s);
						Hashtable<Integer,LinkedList<Send>> shash = new Hashtable<>();
						shash.put(process.getRank(), send);
						slwithtag.put(tagandrank, shash);
						s.rank = slwithtag.get(tagandrank).get(process.getRank()).indexOf(s);
						}
					}
				}
		}
		
//check the linkedlist
//if there has a R(tag and endpoint = *) all the receive's rank +1,
		for(int i = 0;i<processes.size();i++){
			for(Integer tag : Rtag){
				Pair<Integer,Integer> tt = new Pair<Integer, Integer>(tag, i);
				for(int j = 0; j<processes.size(); j++){
					if(rlwithtag.containsKey(tt)){
						if(rlwithtag.get(tt).containsKey(j)){
							LinkedList<Recv> rl = rlwithtag.get(tt).get(j);
							if(!rl.isEmpty()){
								System.out.println("the linkedlist is tag="+tag+"process="+i+"src="+j);
								for(Recv receive : rl){
									System.out.println("receive:"+receive);
									if(receive.src==-1){
										if(receive.tag==-1){
											for(Process process: processes){
												for(Recv r : process.rlist){
													if(r.src!=-1&&r.tag!=-1){
														r.rank=r.rank+1;
													}
												}
										}
									}else{
										for(Process process: processes){
											for(Recv r : process.rlist){
												if(r.tag==receive.tag){
													r.rank=r.rank+1;
												}
											}
									}
									}
								}else{
									if(receive.tag==-1){
										for(Process process: processes){
											for(Recv r : process.rlist){
												if(r.src==receive.src){
													r.rank=r.rank+1;
												}
											}
									}
									}
								}
								}
							}else{
								//System.out.println("tag="+tag+"process="+i+"is empty!");
							}
						}
					}
					if(!slwithtag.containsKey(tt)){
						continue;
					}
					if(!slwithtag.get(tt).containsKey(j)){
						continue;
					}
					LinkedList<Send> sl = slwithtag.get(tt).get(j);
					if(!sl.isEmpty()){
						System.out.println("the linkedlist is tag="+tag+"process="+i+"src="+j);
						for(Send send : sl){
							System.out.println("send:"+send);
						}
					}else{
						//System.out.println("tag="+tag+"process="+i+"is empty!");
					}
				}
			}
		}
		
		for(int i = 0;i<processes.size();i++){
			for(Integer rtag : Rtag){
				Pair<Integer,Integer> tt = new Pair<Integer, Integer>(rtag, i);
				if(!rlwithtag.containsKey(tt)){
					continue;
				}
					for(int j = 0 ; j<processes.size() ; j++){
						if(!rlwithtag.get(tt).containsKey(j))
							continue;
						Iterator<Recv> ite_r = rlwithtag.get(tt).get(j).iterator();
						int lastrr = 0;
						//LinkedList<Send> SforR = new LinkedList<>();

						while(ite_r.hasNext()){
							Recv r = ite_r.next();
							Pair<Integer,Integer> stt = new Pair<Integer, Integer>(rtag, i);

							if(!slwithtag.containsKey(stt)){
								continue;
							}
							LinkedList<Send> SforR = new LinkedList<>();
							if(!slwithtag.get(stt).containsKey(j)){
								continue;
							}
							Iterator<Send> ite_s = slwithtag.get(stt).get(j).iterator();
							//Send lastss = null;
							while(ite_s.hasNext()){
								 Send s = ite_s.next();
								 if(r.rank >= s.rank&& //rule 1
									 //&&r.rank <= s.rank + (sendstoi - std[rtag][i].size())//rule2
									 (r.src == -1 || r.src == s.src)
									 &&(r.tag == -1 || r.tag ==s.tag)){ //either it is a wildcard receive or the endpoints commit
										 if(!SforR.contains(s)){
											 SforR.add(s);
										 }
										//generate pattern_match
										 if(isPattern)
										 {
											 if(!pattern_match.containsKey(s))
											 {
												 pattern_match.put(s, new LinkedList<Recv>());
											 }
											 if(!(pattern_match.get(s)).contains(r)){
												 pattern_match.get(s).add(r);
											 }
										 }
									 }
								}
									 //while r.tag = -1 ,the next r add other send
									 /*
									 for(int k = 0;k<lastrr;k++){
										 if(r.tag == -1)
											 break;
										 int sindex = std[rtag][i].indexOf(s);
										 if(sindex > 0){
											 lastss = std[rtag][i].get(sindex-k);
										 }
										 if(r.src == lastss.src
												&&r.tag ==lastss.tag){ 
											 if(!SforR.contains(lastss))
												 SforR.add(lastss);
											 //generate pattern_match
											 if(isPattern)
											 {
												 if(!pattern_match.containsKey(lastss))
												 {
													 pattern_match.put(lastss, new LinkedList<Recv>());
												 }
												 if(!(pattern_match.get(s)).contains(r)){
													 pattern_match.get(lastss).add(r);
												 }
											 }
										 }
									 }
									 */
						//add the list with r to the match table
							 if(!SforR.isEmpty()){
								 if(!match_table.containsKey(r)||match_table.get(r)==null)
									 match_table.put(r, SforR);
								 else match_table.get(r).addAll(SforR);
								 //
								 System.out.println("R:"+r);
								 for(Send ss : SforR){
									 System.out.println("SforR:"+ss);
								 }
								 //
							}
							 if(r.tag == -1){
								 lastrr += 1 ;
							 }
						}	
				}
			}
		}
	}
}
	
