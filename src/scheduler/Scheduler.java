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
		newCDFG.setTitle("Original CDFG");
		
		
		System.out.println("Number of states = " + newCDFG.getState()); // Should be 1 before ASAP/ALAP
		
		CDFG asapCDFG;
		asapCDFG = newCDFG.copy();
		
		asapCDFG = performASAP(asapCDFG);
		//CDFG alapCDFG = performALAP(newCDFG);
		
		
		newCDFG.printCDFG();
		asapCDFG.printCDFG();
		//alapCDFG.printCDFG();
		
		
		// TODO: Shift ALAP schedule up if needed
	
		
		// calculates mobilities
		
//		int [] mobilities = new int [newCDFG.getNumNodes()];
//		
//		for (int c = 0; c < asapCDFG.getNumNodes(); c++)
//		{
//			mobilities[c] = (alapCDFG.nodes[c].getState() - asapCDFG.nodes[c].getState());
//			System.out.println("Mobility of " + c + "= " + mobilities[c]);
//			
//		}
		
		
		
		int [] mobilities = new int[newCDFG.getNumNodes()];
		
		mobilities[0] = 1;
		
		
//		performRC(newCDFG, mobilities);
		
	
		
		
		
		//System.out.println("Operation = " + operation);
		
	}
	
	
	
	/*
	 * Function to read and parse input file
	 * Takes in a String filename and returns a CDFG object
	 */
	
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
					//int tempState = 0;
					String tempOp = "";
					String tempConn = "";
					String [] parsedConns = null;
					int [] parsedConns_int = null;
					
					tempID = Integer.parseInt(curNode[0]); 
					//tempState = Integer.parseInt(curNode[1]);
					tempOp = curNode[1];
					tempConn = curNode[2];
					
					// parse the node's connections
					
					parsedConns = tempConn.split(",");
					if (parsedConns.length > 1)
					{
						parsedConns_int = new int [parsedConns.length];
						for (int i = 0; i < parsedConns.length; i++)
						{
							parsedConns_int[i] = Integer.parseInt(parsedConns[i]);
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
				
					}
					
					// create the node
					
					tempNode = new Node(tempID, 1, tempOp, parsedConns_int);
					
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
					
					unitsArray = line.split("=");
					
					if (unitsArray.length != 2)
					{
						System.out.println("Error: Specify resource amount as NAME=NUM. (eg ALU=1).");
						System.exit(1);
					}else
						{
						
							if(unitsArray[0].equalsIgnoreCase("alu"))
							{
								curCDFG.setALU(Integer.parseInt(unitsArray[1]));
								rc_read++;							
							}
							
							if (unitsArray[0].equalsIgnoreCase("mul"))
							{
								curCDFG.setMUL(Integer.parseInt(unitsArray[1]));
								rc_read++;
							}
							
							if(unitsArray[0].equalsIgnoreCase("min"))
							{					
								curCDFG.setMIN(Integer.parseInt(unitsArray[1]));
								rc_read++;							
							}
							
							if(unitsArray[0].equalsIgnoreCase("max"))
							{
								curCDFG.setMAX(Integer.parseInt(unitsArray[1]));
								rc_read++;	
							}
							
							if(unitsArray[0].equalsIgnoreCase("abs"))
							{
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
					temp = line.substring(1);
					
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
	
	
	/*
	 * Function to perform ASAP scheduling
	 * Takes in a CDFG object and returns an ASAP scheduled CDFG object
	 */
	
	public static CDFG performASAP(CDFG inCDFG)
	{
		
		// int numStates = inCDFG.getNumStates();
		int numNodes = inCDFG.getNumNodes();
		int numStates = 0; // number of states initially 0
		
		int [] nodesComplete = new int [numNodes];
		boolean [] nodesComplete_bool = new boolean [numNodes];
		
		int [] nodesComplete_commit = new int [numNodes];
		boolean [] nodesComplete_bool_commit = new boolean [numNodes];
		
		//	System.out.println("NumStates = " + numStates);
		//	System.out.println("NumNodes = " + numNodes);
	
		int count = 0; // number of nodes completed
		boolean dependency = false;
		
		boolean ACTION_FLAG = false; // true if a node was committed
		
		
		
		CDFG outCDFG = inCDFG;
		outCDFG.setTitle("ASAP CDFG");
		
		for (int x = 0; x < numNodes; x++)
		{
			// first check for nodes with no dependencies and perform them in the first state
			if (outCDFG.nodes[x].dependsOn(-1))
			{
				// this node has no dependencies
				outCDFG.nodes[x].setState(1); // perform in state 1
				nodesComplete[count] = outCDFG.nodes[x].getID(); // add to complete list
				nodesComplete_bool[count] = true;
//				System.out.println("Processed num =  " + count + " with state = " + (numStates));
				count++;
				ACTION_FLAG = true;
				
				
			}
				
		}
		
		
		
		// commit changes
		
		//System.out.println("first node commit (before) = " + nodesComplete_bool[0]);
		
	
		if (ACTION_FLAG)
		{
			System.arraycopy(nodesComplete, 0, nodesComplete_commit, 0, nodesComplete.length);
			System.arraycopy(nodesComplete_bool, 0, nodesComplete_bool_commit, 0, nodesComplete_bool.length);
		}
	
		
		//	System.out.println("first node commit = " + nodesComplete_bool[0]);
		
		// at this point, the first state (1) has been allocated.
		
		numStates = 2;
		
		
		
		while (count < numNodes)
		{
			System.out.println("The count is: " + count);
			
			
			ACTION_FLAG = false;
//			System.out.println("Currently at State =  " + numStates);
			
			for (int j = 0; j < numNodes; j++)
			{
				dependency = false; // reset flag for each node
				
//				System.out.println("j = " + j);
//				System.out.println("Conn length = " + outCDFG.nodes[j].conn.length);
				
				if ((outCDFG.nodes[j].conn[0] != -1) && !nodesComplete_bool_commit[j])
				{
					for (int y = 0; y < outCDFG.nodes[j].conn.length; y++) // go through connection list
					{
					
						if (!nodesComplete_bool_commit[outCDFG.nodes[j].conn[y]]) // check committed list
						{
							dependency = true; // found a connection that is not in completed list
						}
						
					}
					
				}
				
				
				if (count == -1) // disabled
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
		
					outCDFG.nodes[j].setState(numStates); // set to current state
//					System.out.println("Just completed " + j + " with state = " + numStates);
//					System.out.println("Count before committing: " + count);
//					System.out.println("j = " + j);
					
					nodesComplete[j] = outCDFG.nodes[j].getID();
					nodesComplete_bool[j] = true;
					ACTION_FLAG = true;
				
					count++;
					
					
					
				}
				
				
			} // end node looping
			
			// commit changes
			
			if (ACTION_FLAG)
			{
				System.arraycopy(nodesComplete, 0, nodesComplete_commit, 0, nodesComplete.length);
				System.arraycopy(nodesComplete_bool, 0, nodesComplete_bool_commit, 0, nodesComplete_bool.length);
				numStates++;
			}
			
		
		} // end while loop
		
		
		
		// Check to make sure that count = numNodes (all nodes processed)
		
		System.out.println("count = " + count);
		System.out.println("numNodes = " + numNodes);
		System.out.println("Number of states = " + numStates);
		
		if (count == (numNodes))
		{
			outCDFG.setState(numStates - 1);
			return outCDFG;
		}
		else
		{
			System.out.println("Error performing ASAP Optimization!");
			System.exit(1);
		}
		
		return outCDFG; // to satisfy compiler
		
	} // end method
	
	
	
	
	
	/*
	 * Function to perform ALAP scheduling
	 * Takes in a CDFG object and returns an ALAP scheduled CDFG object
	 */
	
	public static CDFG performALAP(CDFG inCDFG)
	{
	
		
		int numStates = inCDFG.getNumStates(); // same number of states as ASAP
		int numNodes = inCDFG.getNumNodes();
		
		CDFG outCDFG = inCDFG;
		outCDFG.setTitle("ALAP CDFG");
		
		// commit and done lists
		boolean [] commitList = new boolean[numNodes];
		boolean [] doneList = new boolean[numNodes];
		
		boolean C_FLAG = true; // commit if this flag is true
		
		
		for (int curState = numStates; curState > 0; curState--)
		{
			/*
			 * For every state starting with the last, go through every node
			 * and only commit those that do not have anything depending on them that
			 * hasn't already been committed
			 */
			System.out.println("===================");
			
			System.out.println("Current State: " + curState);
			
			
			// reset the flag
			C_FLAG = true;
			
			for (int cNode = 0; cNode < numNodes; cNode++)
			{
				/*
				 * For this node, loop through nodes and check if they depend on me
				 * If none do (or if they do and have been committed) then commit this node 
				 */
				
				// reset the flag
				C_FLAG = true;
				
				if (!doneList[cNode])
				{
				
				for (int i = 0; i < numNodes; i++)
				{
					// call dependsOn(i) and check to see if it's true
					if (outCDFG.dependency(cNode, i))
					{
						if (doneList[i] == false)
						{
							// this node is not complete
							C_FLAG = false; // cannot commit this node
						}
					}
					
					
				}
				
				// if C_FLAG is still true, then commit this node
				
				if (C_FLAG)
				{
					// commit the node
					
					System.out.println("Committing: " + cNode + " to state " + curState);
					
					for (int x = 0; x < numNodes; x++)
					{
						System.out.println("Done " + x + ": " + doneList[x]);
					}
					
					
					commitList[cNode] = true;
					outCDFG.nodes[cNode].setState(curState); // change state number to current state
					
					
				}
				
				
				
			} // end node loop
			
			// finalize commits
			
			System.arraycopy(commitList, 0, doneList, 0, numNodes); 
			
			}
			
		} // end state loop
		
		return outCDFG;
		
	}
	
	

	
	
	
	
	public static void performRC(CDFG inCDFG, int [] mobilities)
	{
		// TODO: Implement URGENCY to break ties
		
		System.out.println("Starting RC...");
		
		for (int b = 0; b < mobilities.length; b++)
		{
			System.out.println("Mobility of " + b + " : "  + mobilities[b]);
		}
		
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
		int numStates = inCDFG.getNumStates();
		
		// commit list
		boolean [] commitList = new boolean[numNodes];
		boolean [] doneList = new boolean[numNodes]; 
		
		
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
		 * Diagnostics: Print ready & commit lists
		 */
		
		for (int a = 0; a < numNodes; a++)
		{
			System.out.println("Ready  " + a + " : " + readyList[a]);
			
		}
		
		for (int b = 0; b < numNodes; b++)
		{
			System.out.println("Commit " + b + ": " + commitList[b]);
		}
		
		/*
		 * End Diagnostics
		 */
		
		
		// repeat for each state
		
		// TODO: need to consider if number of states increases, perhaps use while loop
		
		int curState;
		
	for (curState = 1; curState <= numStates; curState++)
	{
		
		// reset resources
		
		numALU = inCDFG.getALU();
		numMUL = inCDFG.getMUL();
		numMIN = inCDFG.getMIN();
		numMAX = inCDFG.getMAX();
		numABS = inCDFG.getABS();
		
		System.out.println("Current State: " + curState);
		
		// TODO: Add any ready nodes to the ready list
		
		// go through readyList and figure out what resources you need
		
		for (int cNode = 0; cNode < numNodes; cNode++)
		{
			if (readyList[cNode] == true)
			{
				
				if (outCDFG.nodes[cNode].getOp().equalsIgnoreCase("alu"))
				{
					// this node needs an ALU
					if (numALU == 0)
					{
						System.out.println("No ALUs available");
						// all ALUs used -- check mobility
						for (int y = 0; y <= numALU; y++)
						{
							
							System.out.println("Comparing " + cNode + " and " + aluList[y]);
							
							if (mobilities[cNode] < mobilities[aluList[y]])
							{
								System.out.println("This has higher priority - SWAP");
								// mobility of this node is less so it gets priority
								// swap its ID into the aluList and remove other node from commit lists
								
								System.out.println("node swapped out: " + aluList[y]);
								System.out.println("commit before: " + commitList[aluList[y]]);
								
								aluList[y] = outCDFG.nodes[cNode].getID(); // swap ID into reservation list
								commitList[aluList[y]] = false; // remove old node from commit list
								
								
								
								System.out.println("commit after1: " + commitList[aluList[y]]);
								
								// TODO: Problem is that cNode.getID is = to aluList[y]
							
								System.out.println("node swapped in: " + outCDFG.nodes[cNode].getID());
								System.out.println("alulist[y] = " + aluList[y]);
					
								
								commitList[outCDFG.nodes[cNode].getID()] = true; // add new node to commit list
								
								System.out.println("commit after: " + commitList[aluList[y]]);
								
							}
						}
						
					}
					else
					{
						// alu available - add this node to reservation list and to commit lists
						
						System.out.println("numALU = " + numALU);
						System.out.println("alulistlength = " + aluList.length);
						
						aluList[aluList.length - numALU] = outCDFG.nodes[cNode].getID(); // add to reservation list
						
						System.out.println("Added " + outCDFG.nodes[cNode].getID() + " to alu list at " + (aluList.length - numALU));
						
						numALU--; // reduce number of available ALUs
						
						commitList[outCDFG.nodes[cNode].getID()] = true; // add to commit list
						
						
					}
					
				}
				
				if (outCDFG.nodes[cNode].getOp().equalsIgnoreCase("mul"))
				{
					// this node needs a MUL
					if (numMUL == 0)
					{
						// all multipliers used -- check mobility
						for (int y = 0; y < numMUL; y++)
						{
							if (mobilities[cNode] < mobilities[mulList[y]])
							{
								// swap ID into mulList
							}
						}
					}
					else
					{
						// multiplier available - add this node to reservation list
						mulList[mulList.length - numMUL] = outCDFG.nodes[cNode].getID();
						
						numMUL--;
						
						commitList[outCDFG.nodes[cNode].getID()] = true;
						
						
					}
					
					
					
				}
				
				if (outCDFG.nodes[cNode].getOp().equalsIgnoreCase("min"))
				{
					// this node needs a MIN
					if (numMIN == 0)
					{
						// all mins used used -- check mobility
						for (int y = 0; y < numMIN; y++)
						{
							if (mobilities[cNode] < mobilities[minList[y]])
							{
								// swap ID into minList
							}
						}
					}
					else
					{
						// min available - add this node to reservation list
						minList[minList.length - numMIN]  = outCDFG.nodes[cNode].getID();
						
						numMIN--;
						
						commitList[outCDFG.nodes[cNode].getID()] = true;
						
					}
					
					
				}
				
				if (outCDFG.nodes[cNode].getOp().equalsIgnoreCase("max"))
				{
					// this node needs a MAX
					if (numMAX == 0)
					{
						// all max used -- check mobility
						for (int y = 0; y < numMAX; y++)
						{
							if (mobilities[cNode] < mobilities[maxList[y]])
							{
								// swap ID into maxList
							}
						}
					}
					else
					{
						// max available - add this node to reservation list
						maxList[maxList.length - numMAX] = outCDFG.nodes[cNode].getID();
						
						numMAX--;
						
						commitList[outCDFG.nodes[cNode].getID()] = true;
						
					}
					
					
				}
				
				if (outCDFG.nodes[cNode].getOp().equalsIgnoreCase("abs"))
				{
					// this node needs a ABS
					if (numABS == 0)
					{
						// all abs used -- check mobility
						for (int y = 0; y < numABS; y++)
						{
							if (mobilities[cNode] < mobilities[absList[y]])
							{
								// swap ID into absList
							}
						}
					}
					else
					{
						// abs available - add this node to reservation list
						absList[absList.length - numABS] = outCDFG.nodes[cNode].getID();
						numABS--;
						commitList[outCDFG.nodes[cNode].getID()] = true;
						
						
					}
					
					
				}
				
				
				
			}
		}
		
		
		/*
		 * Diagnostics: Print ready & commit lists
		 */
		
	//	System.out.println("Available ALUs = " + numALU);
		
		for (int a = 0; a < numNodes; a++)
		{
			System.out.println("Ready(before)  " + a + " : " + readyList[a]);
			
		}
		
		for (int b = 0; b < numNodes; b++)
		{
			System.out.println("Commit(before) " + b + ": " + commitList[b]);
		}
		
		/*
		 * End Diagnostics
		 */
		
		// TODO: commit nodes that can be performed this state and remove them from the ready list
		
		// To commit, change state of each node in the commit list to the current state
		// Then remove it from ready & commit lists and add it to the done list
		
		for (int x = 0; x < numNodes; x++)
		{
			if (commitList[x] == true)
			{
				// commit this node
				outCDFG.nodes[x].setState(curState);
				commitList[x] = false;
				readyList[x] = false;
				doneList[x]	 = true;
					
			}
			
			
		}
		
		
		

		/*
		 * Diagnostics: Print ready & commit lists
		 */
		
		System.out.println("Available ALUs = " + numALU);
		
		for (int a = 0; a < numNodes; a++)
		{
			System.out.println("Ready(after)  " + a + " : " + readyList[a]);
			
		}
		
		for (int b = 0; b < numNodes; b++)
		{
			System.out.println("Commit(after) " + b + ": " + commitList[b]);
		}
		
		for (int c = 0; c < numNodes; c++)
		{
			System.out.println("Done " + c + ": " + doneList[c]);
			
		}
		
		/*
		 * End Diagnostics
		 */
		
		
		
	} // end state loop
		
		
		
		
		
		
		
	} // end method
	
	public void performTC1()
	{
		
	}
	
	public void performTC2()
	{
		
	}

}
