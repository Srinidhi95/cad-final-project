package scheduler;
//import java.util.*;
import java.io.*;


public class Scheduler {
	
	public static int operation = 0; // 1 = ASAP, 2 = ALAP, 3 = RC, 4 = TC1, 5 = TC2
	
	public static void main (String[] args){
		
		System.out.println("CAD Final Project");
		System.out.println("Filename = " + args[0]);
		
	
		readFile(args[0]); // read and parse the file
		System.out.println("Operation = " + operation);
		
		// TODO: Get CDFG from function
	}
	
	public static void readFile(String filename){
		
		boolean RC_FLAG = false;
		boolean TC_FLAG = false;
		int rc_read = 0; // number of resources read
		
		
		CDFG curCDFG = null;
		Node tempNode = null;
		int numNodes = 0;
		String [] unitsArray; 
		
	try{
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input); 
			String line = "";
			
			String delimiter = "-";
			
			// to store temporary values when parsing
			String temp;
			String [] curNode;
			
			int lineNum = 0;
			
			// read a line
			try {
				line = bufRead.readLine();
			} catch (IOException e) {
				System.out.println("Cannot Read File.");
				e.printStackTrace();
			}
			
			lineNum++;
			
			// keep reading lines until end of file is reached
			while (line != null)
			{
				//System.out.println("Line Read: " + line);
				
				// process the line
				
				if (lineNum == 1 && line.startsWith("."))
				{
					temp = line.substring(1);
					numNodes = Integer.parseInt(temp); // Number of nodes in this CDFG
			
					if(numNodes > 0)
					{
						// create a CDFG object			
						curCDFG = new CDFG(numNodes); // initialize the CDFG
						
					}
					
				}
			
				if (!line.startsWith(".") && !RC_FLAG && !TC_FLAG)
				{
				
					curNode = line.split(delimiter);
					// variables to hold node informations
					int tempID = 0;
					int tempState = 0;
					String tempOp = "";
					String tempConn = "";
					String [] parsedConns = null;
					int [] parsedConns_int = null;
					
					tempID = Integer.parseInt(curNode[0]); 
					tempState = Integer.parseInt(curNode[1]);
					tempOp = curNode[2];
					tempConn = curNode[3];
					
					// parse the node's connections
					
					parsedConns = tempConn.split(",");
					if (parsedConns.length > 1)
					{
						parsedConns_int = new int [parsedConns.length];
						for (int i = 0; i < parsedConns.length; i++)
						{
							parsedConns_int[i] = Integer.parseInt(parsedConns[i]);
						//	System.out.println("Conn = " + parsedConns_int[i]);
						
						}
						
					}
					else
					{
						parsedConns_int = new int [1];
						parsedConns_int[0] = Integer.parseInt(tempConn);
				//		System.out.println("Conn = " + parsedConns_int[0]);
						
					}
					
					// create the node
					
					tempNode = new Node(tempID, tempState, tempOp, parsedConns_int);
					
					int success = curCDFG.addNode(tempNode, tempID);
					if (success < 0)
					{
						System.out.println("Error Adding Node!");
					}
					
//					System.out.println("ID \t= \t" + tempNode.getID());
//					System.out.println("State \t= \t" + tempNode.getState());
//					System.out.println("Op \t= \t" + tempNode.getOp());
//	//				System.out.println("Conns \t= \t" + tempConn);
					
				}	
				
				
				
				if (lineNum > 1 && RC_FLAG && !TC_FLAG)
				{
					// read resource constraints
					
					
					System.out.println("Reading RC constraints.");
					
					unitsArray = line.split("=");
					
					if (unitsArray.length != 2)
					{
						System.out.println("Error: Specify resource amount as NAME=NUM. (eg ALU=1).");
						System.exit(1);
					}else{
						
						if(unitsArray[0].equalsIgnoreCase("alu"))
						{
							System.out.println("Read ALU");
							curCDFG.setALU(Integer.parseInt(unitsArray[1]));
							rc_read++;
							
						}
						
						if (unitsArray[0].equalsIgnoreCase("mul"))
						{
							System.out.println("Read Multiplier");
							curCDFG.setMUL(Integer.parseInt(unitsArray[1]));
							rc_read++;
						}
						
						if(unitsArray[0].equalsIgnoreCase("min"))
						{
							System.out.println("Read MIN");
							curCDFG.setMIN(Integer.parseInt(unitsArray[1]));
							rc_read++;
							
						}
						
						if(unitsArray[0].equalsIgnoreCase("max"))
						{
							System.out.println("Read MAX");
							curCDFG.setMAX(Integer.parseInt(unitsArray[1]));
							rc_read++;
							
						}
						
						if(unitsArray[0].equalsIgnoreCase("abs"))
						{
							System.out.println("Read ABS");
							curCDFG.setABS(Integer.parseInt(unitsArray[1]));
							rc_read++;
							
						}
		
						
						
					}
					
					if (rc_read == 5)
					{
						RC_FLAG = false; // all resourced read, stop reading more
					}
				}
				
				if (lineNum > 1 && TC_FLAG && !RC_FLAG)
				{
					// read time constraints
					System.out.println("Reading TC constraints.");
					unitsArray = line.split("=");
					
					if (!unitsArray[0].equalsIgnoreCase("clk") || unitsArray.length != 2)
					{
						System.out.println("Error: Specify clock cycle restraint as clk=NUM");
						System.exit(1);
					}
					else
					{
						curCDFG.setCLK(Integer.parseInt(unitsArray[1]));
					}
					
					TC_FLAG = false;
				}
				
				
				
				if (lineNum > 1 && line.startsWith(".") && !RC_FLAG && !TC_FLAG)
				{
					//System.out.println("Starts with '.'");
					temp = line.substring(1);
					
					//System.out.println("temp = " + temp);
					
					if(temp.equalsIgnoreCase("e"))
					{
						if (RC_FLAG == true)
						{
							System.out.println("Error: Must specify number of all 5 resource types.");
							System.exit(1);
						}
						
						if (TC_FLAG == true)
						{
							System.out.println("Error: Must specify clock cycle limit.");
							System.exit(1);
						}
						
						System.out.println("numNodes = " + curCDFG.getNumNodes());
						curCDFG.printCDFG();
				
						System.out.println("Parsing Successfully Completed!");
						// end is reached
						// TODO: Add return functionality
					}
					else if(temp.equalsIgnoreCase("ASAP"))
					{
						operation = 1;
						
					}
					else if(temp.equalsIgnoreCase("ALAP"))
					{
						operation = 2;
					}
					else if(temp.equalsIgnoreCase("rc"))
					{
						// set a flag RC
						operation = 3;
						rc_read = 0;
						RC_FLAG = true;
					}
					else if(temp.equalsIgnoreCase("tc1"))
					{
						// set a flag for TC
						operation = 4;
						TC_FLAG = true;
					}
					else if(temp.equalsIgnoreCase("tc2"))
					{
						// set a flag for TC
						operation = 5;
						TC_FLAG = true;
					}
					
				}

				
				//System.out.println("The count is: " + count);
				line = bufRead.readLine();
				lineNum++;
			}
			
	} catch (FileNotFoundException e){
			System.out.println("File Not Found!");
	} catch (IOException e) {
			System.out.println("I/O Exception!");
		e.printStackTrace();
	}
		
		
	}
	
	

}
