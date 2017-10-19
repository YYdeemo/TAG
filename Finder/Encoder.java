 package Finder;

import Syntax.*;
import Syntax.Process;

import com.microsoft.z3.*;

import java.util.*;
import java.util.regex.Pattern;

public class Encoder 
{
	SMTSolver solver;
	Program program;
	UnmatchedEP_Pattern pattern;
	//public LinkedList<Recv>[] recvlist;
	//public LinkedList<Send>[][] sendlist;
	int[] lastrInShape;
	int[][] lastsInShape;
	//for unmatched endpoint, make sure all sends in source endpoint should be matched
	public Hashtable<Send, LinkedList<Recv>> pattern_match;
	public Hashtable<Recv, LinkedList<Send>> match_table;
	Operation lastr = null;
	Operation lastsz = null;
	LinkedList<Send> listsfors = null;
	Hashtable<Pair<Integer,Integer>,Operation> lasttr = null;
	Hashtable<Integer, Operation> lasts = null; 
	Hashtable<String,IntExpr> var_table;
	
	//pair.first is op expr, pair.second is op time
	Hashtable<Operation, Pair<Expr, IntExpr>> operation_expr_map = null;
	
	//data structure to record the last wait on each process
	Wait[] lastwait;
	Wait[] lastwait_s;
	
	//boolean used for control the times of hb between lastwait and the following receives or sends
	boolean[] hb_ws_r;
	boolean[][] hb_ws_s; //[src][dest]
	boolean[][] hb_wr_s; //[src][dest]
	
	public boolean isZero = false;
	public boolean checkPattern = false;
	
	public Encoder(Program program) throws Z3Exception
	{
		this.match_table = program.match_table;
		lasts = new Hashtable<Integer,Operation>();
		lasttr = new Hashtable<Pair<Integer,Integer>,Operation>();
		listsfors = new LinkedList<Send>();
		var_table = new Hashtable<String,IntExpr>();
		operation_expr_map = new Hashtable<Operation,Pair<Expr,IntExpr>>();
		lastwait = new Wait[program.size()];
		lastwait_s = new Wait[program.size()];
		hb_ws_r = new boolean[program.size()];
		hb_ws_s = new boolean[program.size()][program.size()];
		hb_wr_s = new boolean[program.size()][program.size()];
		//initialize the waits to be null
		for(int i = 0; i < program.size(); i++)
		{
			lastwait[i] = null;
			lastwait_s[i]=null;
			hb_ws_r[i] = false;
			
			for(int j = 0; j < program.size(); j++)
			{
				hb_ws_s[i][j]=false;
				hb_wr_s[i][j]=false;
			}
		}
		this.program = program;
		solver = new SMTSolver(this);
		solver.definition();
	}
	
	public Encoder(Program program, 
			UnmatchedEP_Pattern pattern, int[] lastrInShape, int[][] lastsInShape) throws Z3Exception
	{
		this.pattern = pattern;
		//a pattern instance is specified when non-null value is passed
		if(pattern != null)
		{
			checkPattern = true;
		}
		//this.recvlist = program.recvlist;
		//this.sendlist = program.sendlist;
		this.match_table = program.match_table;
		this.pattern_match = program.pattern_match;
		if(!program.isPattern()&&checkPattern)
		{
			System.out.println("Program should be set to mismatched endpoint pattern available!");
			System.exit(0);
		}
		this.lastrInShape = lastrInShape;
		this.lastsInShape = lastsInShape;
		lasts = new Hashtable<Integer,Operation>();
		lasttr = new Hashtable<Pair<Integer,Integer>,Operation>();
		listsfors = new LinkedList<Send>();
		var_table = new Hashtable<String,IntExpr>();
		operation_expr_map = new Hashtable<Operation,Pair<Expr,IntExpr>>();
		lastwait = new Wait[program.size()];
		lastwait_s = new Wait[program.size()];
		hb_ws_r = new boolean[program.size()];
		hb_ws_s = new boolean[program.size()][program.size()];
		hb_wr_s = new boolean[program.size()][program.size()];
		//initialize the waits to be null
		for(int i = 0; i < program.size(); i++)
		{
			lastwait[i] = null;
			lastwait_s[i] = null;
			hb_ws_r[i] = false;
			
			for(int j = 0; j < program.size(); j++)
			{
				hb_ws_s[i][j]=false;
				hb_wr_s[i][j]=false;
			}
		}
		this.program = program;
		solver = new SMTSolver(this);
		solver.definition();
	}
	
	public void Encoding() throws Z3Exception
	{
		for(Process process: program.processes)
		{
			
			lastr = null;
			lasts.clear();
			lasttr.clear();
			listsfors.clear();
			if(checkPattern){
				for(int i = 0; i < process.indicator; i++)//up to process.indicator 
				{
					Encoding(process.get(i));
				}
			}
			else
			{
				for(int i = 0; i < process.size(); i++)//traverse all the operations 
				{
					//???????
					Encoding(process.get(i));
				}
			}
		}
		
		//System.out.println("start encoding matches...");
		
//		generateMatch();
		encodeMatch();
	}
	
	public void Encoding(Operation op) throws Z3Exception
	{
		if(op instanceof Recv)
		{
			IntExpr time = solver.MkTime("T" + op.event);
			Expr recv = solver.mkRecv("R" + op.event);
			Pair<Expr,IntExpr> recvinfo = new Pair<Expr,IntExpr>(recv,time);
			operation_expr_map.put(op, recvinfo);		
			String newName = "var" + op.event;
			//add var to var table if it is not in the table
			IntExpr var;
			if(var_table.containsKey(newName))
				var = var_table.get(newName);
			else 
			{
				var =  solver.MkTime(newName);
				var_table.put(newName, var);
			}
			// add nw when the receive is non-blocking
			IntExpr nw = null;
			if(!((Recv)op).isBlock)
			{
				 nw = solver.MkTime("T"+((Recv)op).NearestWait.event);
				 Expr wait = solver.mkWait("W" + op.event);
				 Pair<Expr, IntExpr> waitinfo = new Pair<Expr, IntExpr>(wait,nw);
				 operation_expr_map.put(((Recv)op).NearestWait,waitinfo);
				//add HB over a receive and its nw
				solver.addFormula(solver.HB(time,nw));
			}
			else
			{
				//extract the line number of op in its process
				//need to catch exception
				int loc=op.process.size();
				try
				{
					loc = Integer.parseInt(op.event.split("_")[1]);
				}
				catch(Exception e)
				{		
					System.out.println(e);
					System.exit(1);
				}
				
				//there exists a following operation after op
				if(loc < op.process.size()-1)
				{
					((Recv)op).nextOP = op.process.get(loc+1);
					//do not add hb over op and the next operation after op because the hb will be added later
				}
				
			}
			
			/*
			if(!((Recv)op).isBlock)
			{
				Wait wait = ((Recv)op).NearestWait;
				//add nearest inclosing wait for recv
				if(!operation_expr_map.containsKey(wait))
				{
					//TODO:generate the time for wait?
					IntExpr waittime = solver.MkTime("T" + op.event);
					Pair<Expr,IntExpr> waitinfo = new Pair<Expr,IntExpr>(null,waittime);
					operation_expr_map.put(wait, waitinfo);
				}
				//add HB over a receive and its nw
				solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(op).getSecond(), 
						(IntExpr)operation_expr_map.get(wait).getSecond()));		
			}
			*/
			//compare R1 and R2 if r1 or r2.tag = -1 and r1 or r2.src = -1
			//???
			solver.addFormula(solver.initRecv(recv, ((Recv)op).src, ((Recv)op).dest,((Recv)op).tag, time, var, nw));
			
			//*********
			//Pair<Integer, Integer> dtr= new Pair<Integer, Integer>(((Recv)op).dest,((Recv)op).tag);
			/*lastr = lasttr.get(dtr);*/
			
			//lastr HB r
			boolean once = false;
			if(((Recv)op).src == -1){
				once = true;
				for(Recv r : op.process.rlist){
					Pair<Integer, Integer> dtr= new Pair<Integer, Integer>(r.src,r.tag);
					lastr = lasttr.get(dtr);
					if(lastr != null)
					{
						BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lastr).getSecond(), 
								time);
						solver.addFormula(newexpr);
						
						//System.out.println(newexpr);
					}
				}
			}
			if(((Recv)op).tag == -1 && !once){
				for(Recv r : op.process.rlist){
					Pair<Integer, Integer> dtr= new Pair<Integer, Integer>(r.src,r.tag);
					lastr = lasttr.get(dtr);
					if(lastr != null && ((Recv)lastr).src==r.src)
					{
						BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lastr).getSecond(), 
								time);
						solver.addFormula(newexpr);
						//System.out.println(newexpr);
					}
				}
			}
			//
			
			
			
			//*********
			/*if(lastr != null && ((Recv)op).tag == -1 && ((Recv)op).src == -1)
			{
				BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lastr).getSecond(), 
						time);
				solver.addFormula(newexpr);
				//System.out.println(newexpr);
			}*/
			/*
			if(lastwait_s[op.process.getRank()] != null && !hb_ws_r[op.process.getRank()])
			{
				solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lastwait_s[op.process.getRank()]).getSecond(), 
						time));
				
				//only add hb from last wait w to a receive once
				hb_ws_r[op.process.getRank()]=true;
			}
			
			if(lastsz!=null && (isZero||lastsz instanceof SSend))
			{
				//only add HB over the last send and op when the send and op are in a common process
				if(lastsz.process.equals(op.process))
				{
					BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lastsz).getSecond(), 
							time);
					solver.addFormula(newexpr);
					
					//System.out.println(newexpr);
				}
			}
			*/
			//改变lastr 将其变为Hashtable
			//lastr = op;
			Pair<Integer, Integer> dtr= new Pair<Integer, Integer>(((Recv)op).src,((Recv)op).tag);
			lasttr.put(dtr, op);
			//lastr will be initilized to null at beginning of traversing each process
		}
		else if(op instanceof Wait)//assume only receives have waits
		{
			Wait w = (Wait)op;
			//HB over this wait and its next send is added when op is a send
			if(w.forR)
			{
				if(lastwait[op.process.getRank()]!=null)
					solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lastwait[op.process.getRank()]).getSecond(),
							(IntExpr)operation_expr_map.get(w).getSecond()));
				lastwait[op.process.getRank()] = w;
				for(int i = 0; i < program.size(); i++)
					hb_wr_s[op.process.getRank()][i]=false;
			}
			else
			{
				if(lastwait_s[op.process.getRank()]!=null)
					solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lastwait_s[op.process.getRank()]).getSecond(),
							(IntExpr)operation_expr_map.get(w).getSecond()));
				lastwait_s[op.process.getRank()] = w;
				hb_ws_r[op.process.getRank()] = false;
				for(int i = 0; i < program.size(); i++)
					hb_ws_s[op.process.getRank()][i]=false;
			}
				
		}
		//TODO: add HB for barriers and other operations
		else if(op instanceof Send)
		{
			IntExpr time = solver.MkTime("T" + op.event);
			Expr send = solver.mkSend("S" + op.event);
			Pair<Expr,IntExpr> sendinfo = new Pair<Expr,IntExpr>(send,time);
			operation_expr_map.put(op, sendinfo);
			IntExpr nw = null;
			if(!((Send)op).isBlock)
			{
				 nw = solver.MkTime("T"+((Send)op).NearestWait.event);
				 Expr wait = solver.mkWait("W" + op.event);
				 Pair<Expr, IntExpr> waitinfo = new Pair<Expr, IntExpr>(wait,nw);
				 operation_expr_map.put(((Send)op).NearestWait,waitinfo);
				//add HB over a send and its nw
				solver.addFormula(solver.HB(time,nw));
			}
			else
			{
				//extract the line number of op in its process
				//need to catch exception
				int loc=op.process.size();
				try
				{
					loc = Integer.parseInt(op.event.split("_")[1]);
				}
				catch(Exception e)
				{
					System.out.println(e);
					System.exit(1);
				}
				
				//there exists a following operation after op
				if(loc < op.process.size()-1)
				{
					((Send)op).nextOP = op.process.get(loc+1);
					//do not add hb over op and the next operation after op because the hb will be added later
				}
				
			}
			solver.addFormula(solver.initSend(send, ((Send)op).src, ((Send)op).dest, ((Send)op).tag, time, ((Send)op).value,nw));
			//lastr for send
			for(Recv r : op.process.rlist){
				Pair<Integer, Integer> dtr= new Pair<Integer, Integer>(r.src,r.tag);
				lastr = lasttr.get(dtr);
				if(lastr != null)
				{
					BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lastr).getSecond(), 
							time);
					solver.addFormula(newexpr);
					
					//System.out.println(newexpr);
				}else{
					System.out.println("Lastr is null!");
				}
			}
			/*
			for(Recv lastr : listrfors ){
				if(lastr != null)
				{
//					System.out.println("send: " + send + "recv: " + operation_expr_map.get(lastr).getFirst());
//					System.out.println("lastr_time: "+ (IntExpr)operation_expr_map.get(lastr).getSecond()+ " send_time: "+time);
					//if non-blocking receive is applied, should be nw < send
					solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lastr).getSecond(), 
							time));
				}
			}
			*/
			
			/*
			if(lastsz !=null)
			{
				//only add HB over the last send and op when the send and op are in a common process
				if(lastsz.process.equals(op.process))
				{
					BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lastsz).getSecond(), 
							time);
					solver.addFormula(newexpr);
					//System.out.println(newexpr);
				}
			}
			*/
			/*
			if(lasts.containsKey(((Send)op).dest))
			{
				//do not repeatedly add hb for last send and op under zero buffer
				if(!lasts.get(((Send)op).dest).equals(lastsz))
					solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lasts.get(((Send)op).dest)).getSecond(), 
						time));
			}
			*/
//			if(lastwait.length > op.process.getRank())
//			{
				if(lastwait[op.process.getRank()] != null && !hb_wr_s[op.process.getRank()][((Send)op).dest])
				{
					//does not constrain the HB for a wait and the next receive, defined in NFM paper
					solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lastwait[op.process.getRank()]).getSecond(), 
							time));
					//should remove hb from last wait w to the send s if w<hb s' exists where s'<hb s
					hb_wr_s[op.process.getRank()][((Send)op).dest] = true;
				}
//			}
				
				if(lastwait_s[op.process.getRank()] != null && !hb_ws_s[op.process.getRank()][((Send)op).dest])
				{
					//does not constrain the HB for a wait and the next receive, defined in NFM paper
					solver.addFormula(solver.HB((IntExpr)operation_expr_map.get(lastwait_s[op.process.getRank()]).getSecond(), 
							time));
					//should remove hb from last wait w to the send s if w<hb s' exists where s'<hb s
					hb_ws_s[op.process.getRank()][((Send)op).dest]=true;
				}

				//lasts HB send
				for(Send lasts:listsfors){
					if(lasts.dest == ((Send)op).dest 
							&& lasts.tag == ((Send)op).tag ){
						BoolExpr newexpr =solver.HB((IntExpr)operation_expr_map.get(lasts).getSecond(), 
								time);
						solver.addFormula(newexpr);
					}
				}
				
			//**********************	
			//Pair<Integer, Integer> dts= new Pair<Integer, Integer>(((Send)op).dest,((Send)op).tag);
			//lastrfors.put( op.process.getRank(), lastr);
			//**********************
			//add nearest inclosing send for dest
			lasts.put(((Send)op).dest,op);
			listsfors.add((Send)op);
			if(isZero || op instanceof SSend)
				lastsz = op;
		}
		else if(op instanceof Assert)
		{
			//1,iterate all the vars and ops, 
			//2, add vars not_in_table to the table, 
			//3, add negation of the assertion
			Assert ast = (Assert)op;
			
			//now only consider an expression such as a+b-c<1
			if(ast.op.length != ast.var_value.length -1 
					|| ast.var_value.length < 2)
				return;
			
			IntExpr operand1;
			if(var_table.containsKey(ast.var_value[0]))
				operand1 = var_table.get(ast.var_value[0]);
			else 
			{
				
				//if operand2 is variable, add it, otherwise, add it as a constant
				if(checkVar(ast.var_value[0]))
				{
					operand1 =  solver.MkTime(ast.var_value[0]);
					var_table.put(ast.var_value[0], operand1);
				}
				else
				{
					operand1 = solver.mkInt(Integer.valueOf(ast.var_value[0]));
				}
			}
			
			IntExpr operand2; 
			BoolExpr assertion = solver.ctx.MkTrue();
	
			for(int i = 0; i < ast.op.length; i++)
			{				
				if(var_table.containsKey(ast.var_value[i+1]))
					operand2 = var_table.get(ast.var_value[i+1]);
				else 
				{
					
					//if operand2 is variable, add it, otherwise, initialize it as a constant
					if(checkVar(ast.var_value[i+1]))
					{
						operand2 =  solver.MkTime(ast.var_value[i+1]);
						var_table.put(ast.var_value[i+1], operand2);
					}
					else
					{
						operand2 = solver.mkInt(Integer.valueOf(ast.var_value[i+1]));
					}
				}
				
				if(Pattern.matches("\\+|-|\\*|/|\\%", ast.op[i]))
				{
					operand1 = gather(operand1,operand2,ast.op[i]);
				}
				else
				{
					assertion = solver.ctx.MkNot(buildAssert(operand1,operand2,ast.op[i]));
					break;
				}
			}
			
			//System.out.println(assertion);
			solver.addFormula(assertion);
			
		}
	}
	
	public IntExpr gather(IntExpr op1, IntExpr op2, String op) throws Z3Exception
	{
		if(op.equals("+"))
		{
			return (IntExpr) solver.ctx.MkAdd(new IntExpr[]{op1,op2});
		}
		else if(op.equals("-"))
		{
			return (IntExpr) solver.ctx.MkSub(new IntExpr[]{op1,op2});
		}
		else if(op.equals("*"))
		{
			return (IntExpr) solver.ctx.MkMul(new IntExpr[]{op1,op2});
		}
		else if(op.equals("/"))
		{
			return (IntExpr) solver.ctx.MkDiv(op1,op2);
		}	
		else if(op.equals("%"))
		{
			return (IntExpr) solver.ctx.MkMod(op1,op2);
		}
		
		return solver.mkInt(0);
	}
	
	public BoolExpr buildAssert(IntExpr op1, IntExpr op2, String op) throws Z3Exception
	{
		if(op.equals("="))
		{
			return solver.ctx.MkEq(op1, op2);
		}
		else if(op.equals("<"))
		{
			return solver.ctx.MkLt(op1, op2);
		}
		else if(op.equals(">"))
		{
			return solver.ctx.MkGt(op1, op2);
		}
		else if(op.equals("<="))
		{
			return solver.ctx.MkLe(op1, op2);
		}	
		else if(op.equals(">="))
		{
			return solver.ctx.MkGe(op1, op2);
		}
		
		return solver.ctx.MkTrue();
	}
	
	public boolean checkVar(String name)
	{
		return !Pattern.matches("\\d+", name);
	}
	
	public void encodeMatch() throws Z3Exception
	{
		//two parts: first, for every receive r, there must be a match, (r,.)
		continuepoint:
		for(Recv r : match_table.keySet())
		{
			//only encode match when r is in shape
			if(checkPattern 
					&& r.rank > lastrInShape[r.dest])
				continue continuepoint;
			Expr rExpr = operation_expr_map.get(r).getFirst();
//			IntExpr rTime = operation_expr_map.get(r).getSecond();
			BoolExpr a = null;
			BoolExpr b = null;
						
			continuepoint1:
			for(Send s : match_table.get(r))
			{
				//only encode match when s is in shape
				//if s is not in shape then encode false for s and r
				if(checkPattern
						&& s.rank > lastsInShape[s.dest][s.src])
				{
					a = solver.ctx.MkFalse();
					b = (b!=null)?solver.mkOr(a, b):a;
					continue continuepoint1;
				}	
				Expr sExpr = operation_expr_map.get(s).getFirst();
//				IntExpr sTime = operation_expr_map.get(s).getSecond();
				if(rExpr != null && sExpr != null)//should not be null
				{
					if(!isZero)
					{
						if(s instanceof SSend)
							a = solver.Match_ss(rExpr, sExpr,r,s);
						else if(s instanceof RSend)
							a = solver.Match_rs(rExpr, sExpr, r,s);
						else a = solver.Match(rExpr, sExpr,r,s);
					}
					else a = solver.Match_Zero(rExpr, sExpr);
					b = (b!=null)?solver.mkOr(a, b):a;//make or for all matches for receive r
				}
			}
			
			solver.addFormula(b);
		}
	
	
		
		//second, for every send s that can match pattern receive R(c), there must be a match (s,.)
		//add matches for each send only when a pattern instance is checked
		//otherwise, match encoding is for general purpose
		if(checkPattern)
		{
			for(Send s : pattern_match.keySet())
			{
				//only encode send if dest and src is pattern requires and it is in shape
				if(s.dest != pattern.process.getRank() || s.src != pattern.determinstic.src 
					|| s.rank > lastsInShape[s.dest][s.src])
					continue; 
				Expr sExpr = operation_expr_map.get(s).getFirst();
	//			IntExpr sTime = operation_expr_map.get(s).getSecond();
				BoolExpr a = null;
				BoolExpr b = null;
				for(Recv r : pattern_match.get(s))
				{
					//only encode when r is in shape, 
					//if r is not in shape, since s is the shape, there is no way to match s with r
					if(r.rank > lastrInShape[r.dest])
					{
						//encode false for s and r;
						a = solver.ctx.MkFalse();
						b = (b!=null)?solver.mkOr(a, b):a;
						continue;
					}
					Expr rExpr = operation_expr_map.get(r).getFirst();
	//				IntExpr rTime = operation_expr_map.get(r).getSecond();
					if(rExpr != null && sExpr != null)//should not be null
					{
						a = solver.Match(rExpr, sExpr,r,s);
						b = (b!=null)?solver.mkOr(a, b):a;//make or for all matches for send s
					}
				}
				if(b!=null)
					solver.addFormula(b);
			}
		}
	}

}
