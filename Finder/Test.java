package Finder;

import Syntax.*;
import Syntax.Process;

public class Test {

	public static void main(String[] args) throws Exception {
		Program program;
		Process process0,process1,process2;
		UnmatchedEP_Finder finder1;
		//test case 1
		//R(1,2)    S(0,1)
		//R(1,1)    S(0,2)
		
		program = new Program(true);
		process0 = new Process(0);
		process1 = new Process(1);
		program.add(process0);
		program.add(process1);
		process0.add(new Recv(process0.getRank()+ "_" + 0, process0,0, 1, 
			0, null, true, null,2));
		process0.add(new Recv(process0.getRank()+ "_" + 1, process0,1, 1, 
				0, null, true, null,1));
		
		process1.add(new Send(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 2, 
				true, null,1));
		process1.add(new Send(process1.getRank() + "_" + 1, process1,1, 1, 0, null, 3, 
				true, null,2));
	    finder1 = new UnmatchedEP_Finder(program);
	    
	    if(!finder1.graphInitialized)
			program.InitGraph();
	    AssertionViolation_Checker checker = new AssertionViolation_Checker(program);
		checker.Run();//简单地筛选出可能的match_pair
		
		
		System.out.println("=========================================================");
	
		//-----------------------------------------------
		//test case 2
		//R(1,1)    S(0,2)
		//R(1,*)    S(0,1)
		program = new Program(true);
		process0 = new Process(0);
		process1 = new Process(1);
		program.add(process0);
		program.add(process1);
		process0.add(new Recv(process0.getRank()+ "_" + 0, process0, 0, 1, 
			0, null, true, null,1));
		process0.add(new Recv(process0.getRank()+ "_" + 1, process0, 1,1, 
				0, null, true, null,-1));
		
		process1.add(new Send(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 2, 
				true, null,2));
		process1.add(new Send(process1.getRank() + "_" + 1, process1,1, 1, 0, null, 3, 
				true, null,1));
		finder1 = new UnmatchedEP_Finder(program);
		
		if(!finder1.graphInitialized)
			program.InitGraph();
		checker = new AssertionViolation_Checker(program);
		checker.Run();
		
		
		System.out.println("=========================================================");
		
		
		//test case 3
				//R(1,*)    S(0,1)				//R(*)	 S(0)	S(0)
				//R(1,2)    S(0,2)				//R(2)	
				program = new Program(true);
				process0 = new Process(0);
				process1 = new Process(1);
				program.add(process0);
				program.add(process1);
				process0.add(new Recv(process0.getRank()+ "_" + 0, process0, 0, 1, 
					0, null, true, null,-1));
				process0.add(new Recv(process0.getRank()+ "_" + 1, process0, 1,1, 
						0, null, true, null,2));
				
				process1.add(new Send(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 2, 
						true, null,1));
				process1.add(new Send(process1.getRank() + "_" + 1, process1,1, 1, 0, null, 3, 
						true, null,2));
				finder1 = new UnmatchedEP_Finder(program);
				
				if(!finder1.graphInitialized)
					program.InitGraph();
				checker = new AssertionViolation_Checker(program);
				checker.Run();
				
				
				System.out.println("=========================================================");
		//test case 4
		//R(2,1)   S(0,3)    S(0,2)
		//R(2,*)             S(0,1)
		//R(1,3)   
		
		program = new Program(true);
		process0 = new Process(0);
		process1 = new Process(1);
		process2 = new Process(2);
		program.add(process0);
		program.add(process1);
		program.add(process2);
		process0.add(new Recv(process0.getRank()+ "_" + 0, process0, 0,2, 
			0, null, true, null,1));
		process0.add(new Recv(process0.getRank()+ "_" + 1, process0, 0,2, 
				0, null, true, null,-1));
		process0.add(new Recv(process0.getRank()+ "_" + 2, process0, 0,1, 
						0, null, true, null,3));
		process1.add(new Send(process1.getRank() + "_" + 0, process1, 0,1, 0, null, 3, 
				true, null,3));
		process2.add(new Send(process2.getRank() + "_" + 0, process1, 0,2, 0, null, 3, 
				true, null,2));
		process2.add(new Send(process2.getRank() + "_" + 1, process1,1, 2, 0, null, 4, 
				true, null,1));
		finder1 = new UnmatchedEP_Finder(program);	
		if(!finder1.graphInitialized)
			program.InitGraph();
		checker = new AssertionViolation_Checker(program);
		checker.Run();
		
		System.out.println("=========================================================");
		
		//--------------------------------------------------
		//test case 5
		//R(1,2)   S(0,2)    S(0,1)
		//R(1,*)   S(0,1)    
		//R(*,1)   
		//--------------------
		//<R(1,1)>          
		//return may deadlock (actually no deadlock, need SMT encoding to verify)
		program = new Program(true);
		process0 = new Process(0);
		process1 = new Process(1);
		process2 = new Process(2);
		program.add(process0);
		program.add(process1);
		program.add(process2);
		process0.add(new Recv(process0.getRank()+ "_" + 0, process0, 0,1, 
				0, null, true, null,2));
		process0.add(new Recv(process0.getRank()+ "_" + 1, process0, 0,1, 
				0, null, true, null,-1));
		process0.add(new Recv(process0.getRank()+ "_" + 2, process0, 0,-1, 
				0, null, true, null,1));
		process1.add(new Send(process1.getRank() + "_" + 0, process1, 0,1, 0, null, 3, 
				true, null,2));
		process1.add(new Send(process1.getRank() + "_" + 1, process1, 0,1, 0, null, 3, 
				true, null,1));
		process2.add(new Send(process2.getRank() + "_" + 0, process1,1, 2, 0, null, 4, 
				true, null,1));
		finder1 = new UnmatchedEP_Finder(program);	
		if(!finder1.graphInitialized)
			program.InitGraph();
		checker = new AssertionViolation_Checker(program);
		checker.Run();
		
	}

}
