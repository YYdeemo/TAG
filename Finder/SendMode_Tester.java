
package Finder;

import Syntax.*;
import Syntax.Process;
import Finder.*;

import com.microsoft.z3.*;


public class SendMode_Tester {
	
	public static void main(String[] args) throws Exception {
		Program test = rsend1();
		long t1 = System.currentTimeMillis();
		System.out.println("Program starts at " + t1);
		test.InitGraph();
		//test.displayMatch();
		SendMode_Checker checker = new SendMode_Checker(test);
		checker.Run();
		long t2 = System.currentTimeMillis();
		System.out.println("Program executes " + ((double)(t2-t1))/(double)1000 + "seconds");
		//test.displayMatch();
	}
	
	public static Program rsend1()
	{
		Program rsend = new Program(true);
		rsend.name = "ssend test case with three processes, unsat";
		Process process0 = new Process(0);
		Process process1 = new Process(1);
		Process process2 = new Process(2);
		rsend.add(process0);
		rsend.add(process1);
		rsend.add(process2);
		
		process0.add(new Recv(process0.getRank() + "_" + 0, process0,0, 2, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 1, process0,1, 1, 
				0, null, true, null));
		
		process1.add(new SSend(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 1, process1,0, 1, 2, null, 1, 
				true, null));
		
		process2.add(new Recv(process2.getRank() + "_" + 0, process2,0, 1, 
				2, null, true, null));
		process2.add(new Send(process2.getRank() + "_" + 1, process2,0, 2, 0, null, 1, 
				true, null));
		
		
		return rsend;
	}

}

