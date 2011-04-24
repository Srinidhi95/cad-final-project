package scheduler;
//import java.util.*;
import java.io.*;


public class Scheduler {
	public static void main (String[] args){
		System.out.println("CAD Final Project");
		
		System.out.println("First argument = " + args[0]);
		
		String op = "add";
		int x = 3;
		int [] connList = new int[x];
		connList[0] = 4;
		connList[1] = 9;
		connList[2] = 12;
		
		Node newNode = new Node(0, 0,op, connList);
		
		System.out.println("ID = " + newNode.getID()); 	
		
		Node secondNode = new Node(1, 0, op, connList);
		
		System.out.println("ID2 = " + secondNode.getID());
		
		//newNode.printConn();
		
		readFile(args[0]); // read and parse the file
	}
	
	
	// TODO: Parse file according to CDFG definition	
	
	public static void readFile(String filename){
		
		boolean RC_FLAG = false;
		boolean TC_FLAG = false;
		
		CDFG curCDFG = null;
		Node tempNode = null;
		int numNodes = 0;
		
		
		
		//System.out.println("In Readfile: " + filename);
	try{
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input); 
			String line = "";
			
			String delimiter = "-";
			
			// temporary variables
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
					System.out.println("#Nodes\t= \t" + numNodes);
					if(numNodes > 0)
					{
						// create a CDFG object
						
						curCDFG = new CDFG(numNodes); // initialize the CDFG
						
					}
					
				}
			
				if (!line.startsWith("."))
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
							System.out.println("Conn = " + parsedConns_int[i]);
						
						}
						
					}
					else
					{
						parsedConns_int = new int [1];
						parsedConns_int[0] = Integer.parseInt(tempConn);
						System.out.println("Conn = " + parsedConns_int[0]);
						
					}
					
					// create the node
					
					tempNode = new Node(tempID, tempState, tempOp, parsedConns_int);
					//curCDFG = new CDFG(numNodes);
					
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
				
				if (lineNum > 1 && line.startsWith("."))
				{
					System.out.println("Starts with '.'");
					temp = line.substring(1);
					
					System.out.println("temp = " + temp);
					
					if(temp.equalsIgnoreCase("e"))
					{
						
						System.out.println("numNodes = " + curCDFG.getNumNodes());
						curCDFG.printCDFG();
						// end is reached
						// TODO: Add return functionality
					}
					else if(temp.equalsIgnoreCase("ASAP"))
					{
						
					}
					else if(temp.equalsIgnoreCase("ALAP"))
					{
						
					}
					else if(temp.equalsIgnoreCase("rc"))
					{
					
						// set a flag RC
						RC_FLAG = true;
					}
					else if(temp.equalsIgnoreCase("tc1"))
					{
						
						// set a flag for TC
						TC_FLAG = true;
					}
					else if(temp.equalsIgnoreCase("tc2"))
					{
						
						// set a flag for TC
						TC_FLAG = true;
					}
					
				}
				
				System.out.println("FLAG = " + RC_FLAG);
				if (lineNum > 1 && RC_FLAG && !TC_FLAG)
				{
					// read resource constraints
					System.out.println("Reading RC constraints.");
					RC_FLAG = false;
				}
				
				if (lineNum > 1 && TC_FLAG && !RC_FLAG)
				{
					// read time constraints
					System.out.println("Reading TC constraints.");
					TC_FLAG = false;
				}
					
					
					
	
		
			
				
				
				
				
				
				
				
				
				//System.out.println("The count is: " + count);
				line = bufRead.readLine();
				lineNum++;
			}
			
	} catch (FileNotFoundException e){
			System.out.println("File Not Found!");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
	}
	
	

}
