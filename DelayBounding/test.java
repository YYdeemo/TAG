package DelayBounding;

import Syntax.Process;
import Syntax.Program;
import Syntax.Recv;
import Syntax.Send;

public class test {

	public static void main(String[] args) throws Exception {
		Program test = dlg1();
		long t1 = System.currentTimeMillis();
		System.out.println("Program starts at " + t1);
		System.out.println("Generating match pairs with delay bounding scheduler:");
		MatchGenerator generator = new MatchGenerator(test,2,true);
		generator.generate();
		long t2 = System.currentTimeMillis();
		System.out.println("Program ends at " + t2);
		System.out.println("Program executes " + ((double)(t2-t1))/(double)1000 + "seconds");
		test.displayMatch();
		System.out.println("=======================================");
		test.InitGraph();
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
	
	public static Program integrate_16core()
	{
		Program integrate = new Program(true);
		integrate.name = "Integrate (16 processes)";
		
		Process process0 = new Process(0);
		Process process1 = new Process(1);
		Process process2 = new Process(2);
		Process process3 = new Process(3);
		Process process4 = new Process(4);
		Process process5 = new Process(5);
		Process process6 = new Process(6);
		Process process7 = new Process(7);
		Process process8 = new Process(8);
		Process process9 = new Process(9);
		Process process10 = new Process(10);
		Process process11 = new Process(11);
		Process process12 = new Process(12);
		Process process13 = new Process(13);
		Process process14 = new Process(14);
		Process process15 = new Process(15);
		

		integrate.add(process0);
		integrate.add(process1);
		integrate.add(process2);
		integrate.add(process3);
		integrate.add(process4);
		integrate.add(process5);
		integrate.add(process6);
		integrate.add(process7);
		integrate.add(process8);
		integrate.add(process9);
		integrate.add(process10);
		integrate.add(process11);
		integrate.add(process12);
		integrate.add(process13);
		integrate.add(process14);
		integrate.add(process15);

		process0.add(new Send(process0.getRank() + "_" + 0, process0,0, 0, 1, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 1, process0,1, 0, 2, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 2, process0,2, 0, 3, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 3, process0,3, 0, 4, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 4, process0,4, 0, 5, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 5, process0,5, 0, 6, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 6, process0,6, 0, 7, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 7, process0,7, 0, 8, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 8, process0,8, 0, 9, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 9, process0,9, 0, 10, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 10, process0,10, 0, 11, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 11, process0,11, 0, 12, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 12, process0,12, 0, 13, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 13, process0,13, 0, 14, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 14, process0,14, 0, 15, null, 1, 
				true, null));
		process0.add(new Recv(process0.getRank() + "_" + 15, process0,15, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 16, process0,16, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 17, process0,17, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 18, process0,18, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 19, process0,19, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 20, process0,20, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 21, process0,21, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 22, process0,22, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 23, process0,23, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 24, process0,24, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 25, process0,25, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 26, process0,26, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 27, process0,27, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 28, process0,28, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 29, process0,29, -1, 
				0, null, true, null));
		
		process1.add(new Recv(process1.getRank() + "_" + 0, process1,0, -1, 
				1, null, true, null));
		process1.add(new Send(process1.getRank() + "_" + 1, process1,1, 1, 0, null, 1, 
				true, null));
		
		process2.add(new Recv(process2.getRank() + "_" + 0, process2,0, -1, 
				2, null, true, null));
		process2.add(new Send(process2.getRank() + "_" + 1, process2,1, 2, 0, null, 1, 
				true, null));
		
		process3.add(new Recv(process3.getRank() + "_" + 0, process3,0, -1, 
				3, null, true, null));
		process3.add(new Send(process3.getRank() + "_" + 1, process3,1, 3, 0, null, 1, 
				true, null));
		
		process4.add(new Recv(process4.getRank() + "_" + 0, process4,0, -1, 
				4, null, true, null));
		process4.add(new Send(process4.getRank() + "_" + 1, process4,1, 4, 0, null, 1, 
				true, null));
		
		process5.add(new Recv(process5.getRank() + "_" + 0, process5,0, -1, 
				5, null, true, null));
		process5.add(new Send(process5.getRank() + "_" + 1, process5,1, 5, 0, null, 1, 
				true, null));
		
		process6.add(new Recv(process6.getRank() + "_" + 0, process6,0, -1, 
				6, null, true, null));
		process6.add(new Send(process6.getRank() + "_" + 1, process6,1, 6, 0, null, 1, 
				true, null));
		
		process7.add(new Recv(process7.getRank() + "_" + 0, process7,0, -1, 
				7, null, true, null));
		process7.add(new Send(process7.getRank() + "_" + 1, process7,1, 7, 0, null, 1, 
				true, null));
		
		process8.add(new Recv(process8.getRank() + "_" + 0, process8,0, -1, 
				8, null, true, null));
		process8.add(new Send(process8.getRank() + "_" + 1, process8,1, 8, 0, null, 1, 
				true, null));
		
		process9.add(new Recv(process9.getRank() + "_" + 0, process9,0, -1, 
				9, null, true, null));
		process9.add(new Send(process9.getRank() + "_" + 1, process9,1, 9, 0, null, 1, 
				true, null));
		
		process10.add(new Recv(process10.getRank() + "_" + 0, process10,0, -1, 
				10, null, true, null));
		process10.add(new Send(process10.getRank() + "_" + 1, process10,1, 10, 0, null, 1, 
				true, null));
		
		process11.add(new Recv(process11.getRank() + "_" + 0, process11,0, -1, 
				11, null, true, null));
		process11.add(new Send(process11.getRank() + "_" + 1, process11,1, 11, 0, null, 1, 
				true, null));
		
		process12.add(new Recv(process12.getRank() + "_" + 0, process12,0, -1, 
				12, null, true, null));
		process12.add(new Send(process12.getRank() + "_" + 1, process12,1, 12, 0, null, 1, 
				true, null));
		
		process13.add(new Recv(process13.getRank() + "_" + 0, process13,0, -1, 
				13, null, true, null));
		process13.add(new Send(process13.getRank() + "_" + 1, process13,1, 13, 0, null, 1, 
				true, null));
		
		process14.add(new Recv(process14.getRank() + "_" + 0, process14,0, -1, 
				14, null, true, null));
		process14.add(new Send(process14.getRank() + "_" + 1, process14,1, 14, 0, null, 1, 
				true, null));
		
		process15.add(new Recv(process15.getRank() + "_" + 0, process15,0, -1, 
				15, null, true, null));
		process15.add(new Send(process15.getRank() + "_" + 1, process15,1, 15, 0, null, 1, 
				true, null));
		
		return integrate;
	}

	
	
	public static Program dlg2()
	{
		Program dlg2 = new Program(true);
		dlg2.name = "dlg2";
		Process process0 = new Process(0);
		Process process1 = new Process(1);
		Process process2 = new Process(2);
		dlg2.add(process0);
		dlg2.add(process1);
		dlg2.add(process2);
		
		process0.add(new Recv(process0.getRank() + "_" + 0, process0,0, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 1, process0,1, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 2, process0,2, -1, 
				0, null, true, null));
		process0.add(new Send(process0.getRank() + "_" + 3, process0,3, 0, 1, null, 1, 
				true, null));
		process0.add(new Recv(process0.getRank() + "_" + 4, process0,4, -1, 
				0, null, true, null));
		
		process1.add(new Send(process1.getRank() + "_" + 0, process1,0, 1, 0, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 1, process1,1, 1, 0, null, 1, 
				true, null));
		process1.add(new Recv(process1.getRank() + "_" + 2, process1,2, 0, 
				1, null, true, null));
		
		process2.add(new Send(process2.getRank() + "_" + 0, process2,0, 2, 0, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 1, process2,1, 2, 0, null, 1, 
				true, null));
		
		return dlg2;
	}
	
	
	public static Program floyd_8core()
	{
		Program floyd = new Program(true);
		floyd.name = "Floyd";
		Process process0 = new Process(0);
		Process process1 = new Process(1);
		Process process2 = new Process(2);
		Process process3 = new Process(3);
		Process process4 = new Process(4);
		Process process5 = new Process(5);
		Process process6 = new Process(6);
		Process process7 = new Process(7);
		floyd.add(process0);
		floyd.add(process1);
		floyd.add(process2);
		floyd.add(process3);
		floyd.add(process4);
		floyd.add(process5);
		floyd.add(process6);
		floyd.add(process7);
		
		process0.add(new Send(process0.getRank() + "_" + 0, process0,0, 0, 1, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 1, process0,1, 0, 2, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 2, process0,2, 0, 3, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 3, process0,3, 0, 4, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 4, process0,4, 0, 5, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 5, process0,5, 0, 6, null, 1, 
				true, null));
		process0.add(new Send(process0.getRank() + "_" + 6, process0,6, 0, 7, null, 1, 
				true, null));
		process0.add(new Recv(process0.getRank() + "_" + 7, process0,7, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 8, process0,8, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 9, process0,9, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 10, process0,10, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 11, process0,11, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 12, process0,12, -1, 
				0, null, true, null));
		process0.add(new Recv(process0.getRank() + "_" + 13, process0,13, -1, 
				0, null, true, null));
		
		process1.add(new Recv(process1.getRank() + "_" + 0, process1,0, -1, 
				1, null, true, null));
		process1.add(new Send(process1.getRank() + "_" + 1, process1,1, 1, 0, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 2, process1,2, 1, 2, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 3, process1,3, 1, 3, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 4, process1,4, 1, 4, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 5, process1,5, 1, 5, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 6, process1,6, 1, 6, null, 1, 
				true, null));
		process1.add(new Send(process1.getRank() + "_" + 7, process1,7, 1, 7, null, 1, 
				true, null));
		process1.add(new Recv(process1.getRank() + "_" + 8, process1,8, -1, 
				1, null, true, null));
		process1.add(new Recv(process1.getRank() + "_" + 9, process1,9, -1, 
				1, null, true, null));
		process1.add(new Recv(process1.getRank() + "_" + 10, process1,10, -1, 
				1, null, true, null));
		process1.add(new Recv(process1.getRank() + "_" + 11, process1,11, -1, 
				1, null, true, null));
		process1.add(new Recv(process1.getRank() + "_" + 12, process1,12, -1, 
				1, null, true, null));
		process1.add(new Recv(process1.getRank() + "_" + 13, process1,13, -1, 
				1, null, true, null));
		
		process2.add(new Recv(process2.getRank() + "_" + 0, process2,0, -1, 
				2, null, true, null));
		process2.add(new Recv(process2.getRank() + "_" + 1, process2,1, -1, 
				2, null, true, null));
		process2.add(new Send(process2.getRank() + "_" + 2, process2,2, 2, 0, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 3, process2,3, 2, 1, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 4, process2,4, 2, 3, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 5, process2,5, 2, 4, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 6, process2,6, 2, 5, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 7, process2,7, 2, 6, null, 1, 
				true, null));
		process2.add(new Send(process2.getRank() + "_" + 8, process2,8, 2, 7, null, 1, 
				true, null));
		process2.add(new Recv(process2.getRank() + "_" + 9, process2,9, -1, 
				2, null, true, null));
		process2.add(new Recv(process2.getRank() + "_" + 10, process2,10, -1, 
				2, null, true, null));
		process2.add(new Recv(process2.getRank() + "_" + 11, process2,11, -1, 
				2, null, true, null));
		process2.add(new Recv(process2.getRank() + "_" + 12, process2,12, -1, 
				2, null, true, null));
		process2.add(new Recv(process2.getRank() + "_" + 13, process2,13, -1, 
				2, null, true, null));
		
		process3.add(new Recv(process3.getRank() + "_" + 0, process3,0, -1, 
				3, null, true, null));
		process3.add(new Recv(process3.getRank() + "_" + 1, process3,1, -1, 
				3, null, true, null));
		process3.add(new Recv(process3.getRank() + "_" + 2, process3,2, -1, 
				3, null, true, null));
		process3.add(new Send(process3.getRank() + "_" + 3, process3,3, 3, 0, null, 1, 
				true, null));
		process3.add(new Send(process3.getRank() + "_" + 4, process3,4, 3, 1, null, 1, 
				true, null));
		process3.add(new Send(process3.getRank() + "_" + 5, process3,5, 3, 2, null, 1, 
				true, null));
		process3.add(new Send(process3.getRank() + "_" + 6, process3,6, 3, 4, null, 1, 
				true, null));
		process3.add(new Send(process3.getRank() + "_" + 7, process3,7, 3, 5, null, 1, 
				true, null));
		process3.add(new Send(process3.getRank() + "_" + 8, process3,8, 3, 6, null, 1, 
				true, null));
		process3.add(new Send(process3.getRank() + "_" + 9, process3,9, 3, 7, null, 1, 
				true, null));
		process3.add(new Recv(process3.getRank() + "_" + 10, process3,10, -1, 
				3, null, true, null));
		process3.add(new Recv(process3.getRank() + "_" + 11, process3,11, -1, 
				3, null, true, null));
		process3.add(new Recv(process3.getRank() + "_" + 12, process3,12, -1, 
				3, null, true, null));
		process3.add(new Recv(process3.getRank() + "_" + 13, process3,13, -1, 
				3, null, true, null));
		
		process4.add(new Recv(process4.getRank() + "_" + 0, process4,0, -1, 
				4, null, true, null));
		process4.add(new Recv(process4.getRank() + "_" + 1, process4,1, -1, 
				4, null, true, null));
		process4.add(new Recv(process4.getRank() + "_" + 2, process4,2, -1, 
				4, null, true, null));
		process4.add(new Recv(process4.getRank() + "_" + 3, process4,3, -1, 
				4, null, true, null));
		process4.add(new Send(process4.getRank() + "_" + 4, process4,4, 4, 0, null, 1, 
				true, null));
		process4.add(new Send(process4.getRank() + "_" + 5, process4,5, 4, 1, null, 1, 
				true, null));
		process4.add(new Send(process4.getRank() + "_" + 6, process4,6, 4, 2, null, 1, 
				true, null));
		process4.add(new Send(process4.getRank() + "_" + 7, process4,7, 4, 3, null, 1, 
				true, null));
		process4.add(new Send(process4.getRank() + "_" + 8, process4,8, 4, 5, null, 1, 
				true, null));
		process4.add(new Send(process4.getRank() + "_" + 9, process4,9, 4, 6, null, 1, 
				true, null));
		process4.add(new Send(process4.getRank() + "_" + 10, process4,10, 4, 7, null, 1, 
				true, null));
		process4.add(new Recv(process4.getRank() + "_" + 11, process4,11, -1, 
				4, null, true, null));
		process4.add(new Recv(process4.getRank() + "_" + 12, process4,12, -1, 
				4, null, true, null));
		process4.add(new Recv(process4.getRank() + "_" + 13, process4,13, -1, 
				4, null, true, null));
		
		process5.add(new Recv(process5.getRank() + "_" + 0, process5,0, -1, 
				5, null, true, null));
		process5.add(new Recv(process5.getRank() + "_" + 1, process5,1, -1, 
				5, null, true, null));
		process5.add(new Recv(process5.getRank() + "_" + 2, process5,2, -1, 
				5, null, true, null));
		process5.add(new Recv(process5.getRank() + "_" + 3, process5,3, -1, 
				5, null, true, null));
		process5.add(new Recv(process5.getRank() + "_" + 4, process5,4, -1, 
				5, null, true, null));
		process5.add(new Send(process5.getRank() + "_" + 5, process5,5, 5, 0, null, 1, 
				true, null));
		process5.add(new Send(process5.getRank() + "_" + 6, process5,6, 5, 1, null, 1, 
				true, null));
		process5.add(new Send(process5.getRank() + "_" + 7, process5,7, 5, 2, null, 1, 
				true, null));
		process5.add(new Send(process5.getRank() + "_" + 8, process5,8, 5, 3, null, 1, 
				true, null));
		process5.add(new Send(process5.getRank() + "_" + 9, process5,9, 5, 4, null, 1, 
				true, null));
		process5.add(new Send(process5.getRank() + "_" + 10, process5,10, 5, 6, null, 1, 
				true, null));
		process5.add(new Send(process5.getRank() + "_" + 11, process5,11, 5, 7, null, 1, 
				true, null));
		process5.add(new Recv(process5.getRank() + "_" + 12, process5,12, -1, 
				5, null, true, null));
		process5.add(new Recv(process5.getRank() + "_" + 13, process5,13, -1, 
				5, null, true, null));
		
		process6.add(new Recv(process6.getRank() + "_" + 0, process6,0, -1, 
				6, null, true, null));
		process6.add(new Recv(process6.getRank() + "_" + 1, process6,1, -1, 
				6, null, true, null));
		process6.add(new Recv(process6.getRank() + "_" + 2, process6,2, -1, 
				6, null, true, null));
		process6.add(new Recv(process6.getRank() + "_" + 3, process6,3, -1, 
				6, null, true, null));
		process6.add(new Recv(process6.getRank() + "_" + 4, process6,4, -1, 
				6, null, true, null));
		process6.add(new Recv(process6.getRank() + "_" + 5, process6,5, -1, 
				6, null, true, null));
		process6.add(new Send(process6.getRank() + "_" + 6, process6,6, 6, 0, null, 1, 
				true, null));
		process6.add(new Send(process6.getRank() + "_" + 7, process6,7, 6, 1, null, 1, 
				true, null));
		process6.add(new Send(process6.getRank() + "_" + 8, process6,8, 6, 2, null, 1, 
				true, null));
		process6.add(new Send(process6.getRank() + "_" + 9, process6,9, 6, 3, null, 1, 
				true, null));
		process6.add(new Send(process6.getRank() + "_" + 10, process6,10, 6, 4, null, 1, 
				true, null));
		process6.add(new Send(process6.getRank() + "_" + 11, process6,11, 6, 5, null, 1, 
				true, null));
		process6.add(new Send(process6.getRank() + "_" + 12, process6,12, 6, 7, null, 1, 
				true, null));
		process6.add(new Recv(process6.getRank() + "_" + 13, process6,13, -1, 
				6, null, true, null));
		
		process7.add(new Recv(process7.getRank() + "_" + 0, process7,0, -1, 
				7, null, true, null));
		process7.add(new Recv(process7.getRank() + "_" + 1, process7,1, -1, 
				7, null, true, null));
		process7.add(new Recv(process7.getRank() + "_" + 2, process7,2, -1, 
				7, null, true, null));
		process7.add(new Recv(process7.getRank() + "_" + 3, process7,3, -1, 
				7, null, true, null));
		process7.add(new Recv(process7.getRank() + "_" + 4, process7,4, -1, 
				7, null, true, null));
		process7.add(new Recv(process7.getRank() + "_" + 5, process7,5, -1, 
				7, null, true, null));
		process7.add(new Recv(process7.getRank() + "_" + 6, process7,6, -1, 
				7, null, true, null));
		process7.add(new Send(process7.getRank() + "_" + 7, process7,7, 7, 0, null, 1, 
				true, null));
		process7.add(new Send(process7.getRank() + "_" + 8, process7,8, 7, 1, null, 1, 
				true, null));
		process7.add(new Send(process7.getRank() + "_" + 9, process7,9, 7, 2, null, 1, 
				true, null));
		process7.add(new Send(process7.getRank() + "_" + 10, process7,10, 7, 3, null, 1, 
				true, null));
		process7.add(new Send(process7.getRank() + "_" + 11, process7,11, 7, 4, null, 1, 
				true, null));
		process7.add(new Send(process7.getRank() + "_" + 12, process7,12, 7, 5, null, 1, 
				true, null));
		process7.add(new Send(process7.getRank() + "_" + 13, process7,13, 7, 6, null, 1, 
				true, null));
		
		
		return floyd;
		
	}
	

}
