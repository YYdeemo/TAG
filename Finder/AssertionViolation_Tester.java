
package Finder;

import Syntax.Process;
import Syntax.Program;
import Syntax.Recv;
import Syntax.SSend;
import Syntax.Send;

import com.microsoft.z3.*;


public class AssertionViolation_Tester {
	
	public static void main(String[] args) throws Exception {
		Program test = ssend1();
		long t1 = System.currentTimeMillis();
		System.out.println("Program starts at " + t1);
		test.InitGraph();
		test.displayMatch();
		AssertionViolation_Checker checker = new AssertionViolation_Checker(test);
		checker.Run();
		long t2 = System.currentTimeMillis();
		System.out.println("Program executes " + ((double)(t2-t1))/(double)1000 + "seconds");
		test.displayMatch();
	}
	
	public static Program dlg1()
	{
		Program dlg1 = new Program(true);
		dlg1.name = "dlg1";
		Process process0 = new Process(0);
		Process process1 = new Process(1);
		Process process2 = new Process(2);
		dlg1.add(process0);
		dlg1.add(process1);
		dlg1.add(process2);	
	
		process0.add(new Recv(process0.getRank() + "_" + 0, process0,0, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 1, process0,1, 2, 
				0, null, true, null));
		
		process1.add(new Send(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 1, 
				true, null));
		
		process2.add(new Send(process2.getRank() + "_" + 0, process2,0, 2, 0, null, 1, 
				true, null));
		
		
		return dlg1;
	}
	
	public static Program ssend1()
	{
		Program ssend = new Program(true);
		ssend.name = "ssend test case with two processes";
		Process process0 = new Process(0);
		Process process1 = new Process(1);
		ssend.add(process0);
		ssend.add(process1);
		
		process0.add(new Recv(process0.getRank() + "_" + 0, process0,0, -1, 
				0, null, true, null));
		
		process1.add(new SSend(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 1, 
				true, null));
		
		return ssend;
	}

}
