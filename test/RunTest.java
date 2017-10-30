package test;

import Syntax.*;
import Finder.*;



public class RunTest {

	public void run(Benchmark b) throws Exception
	{
		UnmatchedEP_Finder finder1;
		//Circle_Finder finder2;
		//ZeroSemantics_Finder finder3;

		for(Program testprogram : b.list)
		{
			System.out.println("Program " + testprogram.name + " is on test:");
			long t1 = System.currentTimeMillis();
			System.out.println("Program starts at " + t1);
			System.out.println("Test on mismatched send-receive pattern:");
		    finder1 = new UnmatchedEP_Finder(testprogram);
			//finder1.Run();
			System.out.println("====================================");
			//System.out.println("Test on circular dependency pattern:");
			if(!finder1.graphInitialized)
			testprogram.InitGraph();//?
			//finder2 = new Circle_Finder(testprogram);
			//finder2.Run();
					
			System.out.println("====================================");
			System.out.println("Assertion violation check:");
			AssertionViolation_Checker checker = new AssertionViolation_Checker(testprogram);
			checker.Run();//简单地筛选出可能的match_pair
			long t2;
			if(finder1.endtime !=0)
				t2 = finder1.endtime;
			//if(finder2.endtime !=0)
			//	t2 = finder2.endtime;
			if(checker.endtime !=0)
				t2 = checker.endtime;
			else t2 = System.currentTimeMillis();
			System.out.println("Program ends at " + t2);
			System.out.println("Program executes " + ((double)(t2-t1))/(double)1000 + "seconds");
			System.out.println("====================================\n");
			
			
			/*long t1 = System.currentTimeMillis();
			System.out.println("Program starts at " + t1);
			System.out.println("Test on zero buffer semantics:");
			finder3 = new ZeroSemantics_Finder(testprogram);
			finder3.Run();
			long t2 = System.currentTimeMillis();
			System.out.println("Program ends at " + t2);
			System.out.println("Program executes " + ((double)(t2-t1))/(double)1000 + "seconds");
			System.out.println("====================================\n");*/
		}
	}
	
	public static void main(String[] args) throws Exception {

		Benchmark benchmark = new Benchmark();
		RunTest test = new RunTest();
		test.run(benchmark);
	}

}
