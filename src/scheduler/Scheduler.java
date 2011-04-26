package scheduler;
//import java.util.*;
import java.io.*;


public class Scheduler {
	
	public static int operation = 0; // 1 = ASAP, 2 = ALAP, 3 = RC, 4 = TC1, 5 = TC2
	
	public static void main (String[] args){
		
		System.out.println("CAD Final Project");
		if (args.length == 0)
		{
			System.out.println("You must specify and input file.");
			System.exit(1);
		}
		
		System.out.println("Filename = " + args[0]);
		

		
		CDFG newCDFG; // = new CDFG();
		
		newCDFG = readFile(args[0]); // read and parse the file
		newCDFG.setTitle("Incoming CDFG");
		
		int [] mobilities = new int[newCDFG.getNumNodes()];
		
		
		performRC(newCDFG, mobilities);
		
	//	newCDFG.printCDFG();
		
		//CDFG asapCDFG;
		//asapCDFG = performASAP(newCDFG);
		
		//asapCDFG.printCDFG();
		
		//System.out.println("Operation = " + operation);
		
	}
	
	// Function to read and parse input file
	
	public static CDFG readFile(String filename){
		
		boolean RC_FLAG = false; // set to TRUE when reading resources
		boolean TC_FLAG = false; // set to TRUE when reading clock cycle constraint
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
						if (tempConn.equalsIgnoreCase("x"))
						{
							parsedConns_int[0] = -1;
						}
						else
						{
							parsedConns_int[0] = Integer.parseInt(tempConn);	
						}
						
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
//					System.out.println("Conns \t= \t" + tempConn);
					
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
						//curCDFG.printCDFG();
				
						System.out.println("Parsing Successfully Completed!");
						// end is reached
						
						return curCDFG;
		
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
		
		return curCDFG;
	}
	
	
	public static CDFG performASAP(CDFG inCDFG)
	{
		
		int numStates = inCDFG.getNumStates();
		int numNodes = inCDFG.getNumNodes();
		
		int [] nodesComplete = new int [numNodes];
		boolean [] nodesComplete_bool = new boolean [numNodes];
		
		int [] nodesComplete_commit = new int [numNodes];
		boolean [] nodesComplete_bool_commit = new boolean [numNodes];
		
		System.out.println("NumStates = " + numStates);
		System.out.println("NumNodes = " + numNodes);
	
		int count = 0; // number of nodes completed
		boolean dependency = false;
		
		// TODO: Make sure next statement returns a copy of actual object instead of just reference
		
		CDFG outCDFG = inCDFG; // return a different graph.
		
		for (int x = 0; x < numNodes; x++)
		{
			// first check for nodes with no dependencies and perform them in the first state
			if (outCDFG.nodes[x].dependsOn(-1))
			{
				// this node has no dependencies
				outCDFG.nodes[x].setState(1); // perform in state 1
				nodesComplete[count] = outCDFG.nodes[x].getID(); // add to complete list
				nodesComplete_bool[count] = true;
				System.out.println("Processed num =  " + count);
				count++;
				
				
			}
				
		}
		
		// commit changes
		
		System.out.println("first node commit (before) = " + nodesComplete_bool[0]);
		
		
		// TODO: Fix copy of boolean array
		
		System.arraycopy(nodesComplete, 0, nodesComplete_commit, 0, nodesComplete.length);
		System.arraycopy(nodesComplete_bool, 0, nodesComplete_bool_commit, 0, nodesComplete_bool.length);
		
	
		
		System.out.println("first node commit = " + nodesComplete_bool[0]);
		
		// at this point, the first state (1) has been allocated.
		
		for (int i = 2; i <= numStates; i++) // start with state 2
		{
			/*
			 * For every state, see if you can perform the OP at that node
			 * Node can be performed if all nodes that it depends on are complete 
			 */
			
			System.out.println("Processing State =  " + i);
			
			for (int j = 0; j < numNodes; j++)
			{
				dependency = false; // reset flag for each node
				
				System.out.println("j = " + j);
				System.out.println("Conn length = " + outCDFG.nodes[j].conn.length);
				
				if (outCDFG.nodes[j].conn[0] != -1)
				{
					for (int y = 0; y < outCDFG.nodes[j].conn.length; y++) // go through connection list
					{
					
						if (!nodesComplete_bool_commit[outCDFG.nodes[j].conn[y]]) // check committed list
						{
							dependency = true; // found a connection that is not in completed list
						}
						
					}
					
				}
				
				
				if (i == 2)
				{
					System.out.println("=======================");
					System.out.println("dependency = " + dependency);
					System.out.println("j =  " + j);
					System.out.println("nodecomplete_commit?  " + nodesComplete_bool_commit[j]);
					System.out.println("nodecomplete?  " + nodesComplete_bool[j] + "\n");
				}
				
				if (dependency == false && !nodesComplete_bool_commit[j] && count < numNodes)
				{
					// perform this node
					outCDFG.nodes[j].setState(i); // set to current state
					System.out.println("Just DONE with count = " + count);
					System.out.println("j = " + j);
					nodesComplete[count] = outCDFG.nodes[j].getID();
					nodesComplete_bool[count] = true;
				
					count++;
					
				}
				
				
			} // end node looping
			
			// commit changes
			
			System.arraycopy(nodesComplete, 0, nodesComplete_commit, 0, nodesComplete.length);
			System.arraycopy(nodesComplete_bool, 0, nodesComplete_bool_commit, 0, nodesComplete_bool.length);
			
			
		
		} // end state looping
		
		// Check to make sure that count = numNodes (all nodes processed)
		
		System.out.println("count = " + count);
		System.out.println("numNodes = " + numNodes);
		
		if (count == (numNodes))
		{
			return outCDFG;
		}
		else
		{
			System.out.println("Error performing ASAP Optimization!");
			System.exit(1);
		}
		
		return outCDFG; // to satisfy compiler
		
	} // end method
	
	public static void performALAP(CDFG inCDFG)
	{
		// TODO: Change return type to CDFG
	}
	
	
	
	public static void performRC(CDFG inCDFG, int [] mobilities)
	{
		// TODO: Implement URGENCY to break ties
		
		System.out.println("Starting RC...");
		
		CDFG outCDFG = inCDFG;  // return a different CDFG
	
		// read in the resource list
		int numALU = inCDFG.getALU();
		int numMUL = inCDFG.getMUL();
		int numMIN = inCDFG.getMIN();
		int numMAX = inCDFG.getMAX();
		int numABS = inCDFG.getABS();
		
		//
		// reservation lists for each resource
		// 
		
		int [] aluList = new int[numALU];
		int [] mulList = new int[numMUL];
		int [] minList = new int[numMIN];
		int [] maxList = new int[numMAX];	
		int [] absList = new int[numABS];
		
		
		
		int numNodes = inCDFG.getNumNodes();
		
		// Check mobilities array
		
		if (mobilities.length != numNodes)
		{
			System.out.println("Mobilities Array should contain " + numNodes + " elements.");
			System.exit(1);
		}
		
		boolean [] readyList = new boolean[numNodes]; // create the ready list
		
		// populate the ready list with nodes that do not depend on anything
		
		
		for (int x = 0; x < numNodes; x++)
		{
			if (outCDFG.nodes[x].dependsOn(-1))
			{
				// add this node to the ready list
				readyList[x] = true;
				System.out.println("Added Node: " + x);
						
			}
				
		}
		
		
		/*
		 * Diagnostics: Print ready list
		 */
		
		for (int a = 0; a < numNodes; a++)
		{
			System.out.println("At " + a + " : " + readyList[a]);
		}
		
		/*
		 * End Diagnostics
		 */
		
		
		
		// TODO: Repeat for each state
		
		// go through readyList and figure out what resources you need
		
		for (int x = 0; x < numNodes; x++)
		{
			if (readyList[x] == true)
			{
				
				if (outCDFG.nodes[x].getOp().equalsIgnoreCase("alu"))
				{
					// this node needs an ALU
					if (numALU == 0)
					{
						// all ALUs used -- check mobility
						for (int y = 0; y < numALU; y++)
						{
							if (mobilities[x] < mobilities[aluList[y]])
							{
								// mobility of this node is less so it gets priority
								// swap its ID into the aluList and remove other node from commit lists
								
							}
						}
						
					}
					else
					{
						// alu available - add this node to reservation list and to commit lists
					}
					
				}
				
				if (outCDFG.nodes[x].getOp().equalsIgnoreCase("mul"))
				{
					// this node needs a MUL
					if (numMUL == 0)
					{
						// all multipliers used -- check mobility
						for (int y = 0; y < numMUL; y++)
						{
							if (mobilities[x] < mobilities[mulList[y]])
							{
								// swap ID into mulList
							}
						}
					}
					else
					{
						// multiplier available - add this node to reservation list
					}
					
					
					
				}
				
				if (outCDFG.nodes[x].getOp().equalsIgnoreCase("min"))
				{
					// this node needs a MIN
					if (numMIN == 0)
					{
						// all mins used used -- check mobility
						for (int y = 0; y < numMIN; y++)
						{
							if (mobilities[x] < mobilities[minList[y]])
							{
								// swap ID into minList
							}
						}
					}
					else
					{
						// min available - add this node to reservation list
					}
					
					
				}
				
				if (outCDFG.nodes[x].getOp().equalsIgnoreCase("max"))
				{
					// this node needs a MAX
					if (numMAX == 0)
					{
						// all max used -- check mobility
						for (int y = 0; y < numMAX; y++)
						{
							if (mobilities[x] < mobilities[maxList[y]])
							{
								// swap ID into maxList
							}
						}
					}
					else
					{
						// max available - add this node to reservation list
					}
					
					
				}
				
				if (outCDFG.nodes[x].getOp().equalsIgnoreCase("abs"))
				{
					// this node needs a ABS
					if (numABS == 0)
					{
						// all abs used -- check mobility
						for (int y = 0; y < numABS; y++)
						{
							if (mobilities[x] < mobilities[absList[y]])
							{
								// swap ID into absList
							}
						}
					}
					else
					{
						// abs available - add this node to reservation list
					}
					
					
				}
				
				
				
			}
		}
		
		
		
		
		
		
		
		
	}
	
	public void performTC1()
	{
		
	}
	
	public void performTC2()
	{
		
	}

}
