package scheduler;
//import java.util.*;
import java.io.*;



public class Scheduler {
	
	public static int operation = 0; // 1 = ASAP, 2 = ALAP, 3 = RC, 4 = TC1, 5 = TC2
	
	public static void main (String[] args)
	{
		
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
		
//		newCDFG.printCDFG();
		
		CDFG asapCDFG;
		asapCDFG = newCDFG.copy();
		asapCDFG = performASAP(asapCDFG);
		
		
		CDFG alapCDFG;
		alapCDFG = asapCDFG.copy();
		alapCDFG = performALAP(alapCDFG);
		
		CDFG alap2CDFG;
		alap2CDFG = asapCDFG.copy();
		alap2CDFG = performALAP2(alap2CDFG, (alapCDFG.getNumStates() + 1));
		

		
		

//		newCDFG.printCDFG("newCDFG.txt");
		asapCDFG.printCDFG("asapCDFG.txt");
		alapCDFG.printCDFG("alapCDFG.txt");
		

		
		int [] mobilities = new int [newCDFG.getNumNodes()];
		int [] alapStates = new int [newCDFG.getNumNodes()];
		int [] asapStates = new int [newCDFG.getNumNodes()];
		int [] alap2States = new int [newCDFG.getNumNodes()];
		
		
		for (int c = 0; c < asapCDFG.getNumNodes(); c++)
		{
			mobilities[c] = (alapCDFG.nodes[c].getState() - asapCDFG.nodes[c].getState());
			alapStates[c] = alapCDFG.nodes[c].getState();
			alap2States[c] = alap2CDFG.nodes[c].getState();
			asapStates[c] = asapCDFG.nodes[c].getState();
			//System.out.println("Mobility of " + c + " is :" + mobilities[c]);
//			System.out.println("ASAP of " + c + ": " + asapStates[c]);
//			System.out.println("ALAP of " + c + ": " + alapStates[c]);
//			System.out.println("ALAP2 of " + c + ": " + alap2States[c]);
		}
		
		
	

		CDFG rcCDFG;
		rcCDFG = asapCDFG.copy();
		if (operation == 3)
		{
			performRC(rcCDFG, mobilities, alapStates);
			rcCDFG.printCDFG("rcCDFG.txt");
			
		}
		
		
		if (operation == 4)
		{
			CDFG tcCDFG;
			tcCDFG = newCDFG.copy();
			tcCDFG = performTC(tcCDFG, asapCDFG.getNumStates(), mobilities, alapStates, tcCDFG.getCLK());
			tcCDFG.printCDFG("tc1CDFG.txt");
		}
		
	
		if (operation == 5)
		{
		CDFG tc2CDFG;
		tc2CDFG = newCDFG.copy();
		tc2CDFG = performTC2(tc2CDFG, alapCDFG, asapCDFG, tc2CDFG.getCLK());
		tc2CDFG.printCDFG("tc2CDFG.txt");
		}
		

	
		
		


		
	
		

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
			
//			System.out.println("Checking node:" + x);
			
			if (outCDFG.nodes[x].dependsOn(-1))
			{
				// this node has no dependencies
				outCDFG.nodes[x].setState(1); // perform in state 1
				nodesComplete[x] = outCDFG.nodes[x].getID(); // add to complete list
				nodesComplete_bool[x] = true;
//				System.out.println("Processed num =  " + count + " with state = " + 1);
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
//			System.out.println("The count is: " + count);
			
			
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
			
//			System.out.println("===================");
			
//			System.out.println("Current State: " + curState);
			
			
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
				
//					System.out.println("Now checking: " + cNode);
				//	C_FLAG = true;
					
					int [] dependOnMe = inCDFG.dependOn(cNode);
					if (dependOnMe[0] == -1)
					{
						C_FLAG = true; // no dependents -- commit this node
					} else 
					{	
						for (int x = 0; x < dependOnMe.length; x++)
						{
//							System.out.println("DEPEND LIST=" + dependOnMe[x]);
//							System.out.println("Done list of that = " + doneList[dependOnMe[x]]);
							if (!doneList[dependOnMe[x]])
							{
								C_FLAG = false;
							}
						}
						
					}
					
				
				
				// if C_FLAG is still true, then commit this node
				
				if (C_FLAG)
				{
					// commit the node
					
//					System.out.println("Committing: " + cNode + " to state " + curState);
//					
//					for (int x = 0; x < numNodes; x++)
//					{
//						System.out.println("Done " + x + ": " + doneList[x]);
//					}
					
					
					commitList[cNode] = true;
					outCDFG.nodes[cNode].setState(curState); // change state number to current state
					
					
				}
				
				
				
			} // end node loop
			
			// finalize commits
			
			 
			
			}
			
			System.arraycopy(commitList, 0, doneList, 0, numNodes);
		} // end state loop
		
		return outCDFG;
		
	}
	
	
	public static CDFG performALAP2(CDFG inCDFG, int numStates)
	{
	
		
		//int numStates = inCDFG.getNumStates(); // same number of states as ASAP
		int numNodes = inCDFG.getNumNodes();
		
		CDFG outCDFG = inCDFG;
		outCDFG.setTitle("ALAP2 CDFG");
		
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
			
//			System.out.println("===================");
			
//			System.out.println("Current State: " + curState);
			
			
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
				
//					System.out.println("Now checking: " + cNode);
				//	C_FLAG = true;
					
					int [] dependOnMe = inCDFG.dependOn(cNode);
					if (dependOnMe[0] == -1)
					{
						C_FLAG = true; // no dependents -- commit this node
					} else 
					{	
						for (int x = 0; x < dependOnMe.length; x++)
						{
//							System.out.println("DEPEND LIST=" + dependOnMe[x]);
//							System.out.println("Done list of that = " + doneList[dependOnMe[x]]);
							if (!doneList[dependOnMe[x]])
							{
								C_FLAG = false;
							}
						}
						
					}
					
				
				
				// if C_FLAG is still true, then commit this node
				
				if (C_FLAG)
				{
					// commit the node
					
//					System.out.println("Committing: " + cNode + " to state " + curState);
//					
//					for (int x = 0; x < numNodes; x++)
//					{
//						System.out.println("Done " + x + ": " + doneList[x]);
//					}
//					
					
					commitList[cNode] = true;
					outCDFG.nodes[cNode].setState(curState); // change state number to current state
					
					
				}
				
				
				
			} // end node loop
			
			// finalize commits
			
			 
			
			}
			
			System.arraycopy(commitList, 0, doneList, 0, numNodes);
		} // end state loop
		
		return outCDFG;
		
	}
	
	

	
	/*
	 * Function to perform RC scheduling given a list of mobilities and ALAP states for each node
	 */
	
	
	public static CDFG performRC(CDFG inCDFG, int [] mobilities, int[] alapState)
	{
		System.out.println("Starting RC...");
		
//		for (int b = 0; b < mobilities.length; b++)
//		{
//			System.out.println("Mobility of " + b + " : "  + mobilities[b]);
//		}

		CDFG outCDFG = inCDFG;
		outCDFG.setTitle("RC CDFG");
	
		
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
//		int numStates = inCDFG.getNumStates();
		
		// commit list
		boolean [] commitList = new boolean[numNodes];
		boolean [] doneList = new boolean[numNodes]; 
		
		try 
		{
			FileWriter fstream = new FileWriter("rc_steps.txt", true);
			BufferedWriter output = new BufferedWriter(fstream);
			
			output.write("RC Operation Steps\n");
			output.write("Title: ");
			output.write(outCDFG.getTitle());
			output.newLine();
			
			output.close();
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		// Check mobilities array
		
		if (mobilities.length != numNodes)
		{
			System.out.println("Mobilities Array should contain " + numNodes + " elements.");
			System.exit(1);
		}
		
		boolean [] readyList = new boolean[numNodes]; // create the ready list
		boolean dependency; 
		
		int [] urgency = new int [numNodes]; // urgency list
		
		// populate the ready list with nodes that do not depend on anything
		
		for (int x = 0; x < numNodes; x++)
		{
			if (outCDFG.nodes[x].dependsOn(-1))
			{
				// add this node to the ready list
				readyList[x] = true;
				//System.out.println("Added Node: " + x);
						
			}
				
		}
		
		
		/*
		 * Diagnostics: Print ready & commit lists
		 */
		
//		for (int a = 0; a < numNodes; a++)
//		{
//		System.out.println("Ready  " + a + " : " + readyList[a]);
//			
//		}
	
//		for (int b = 0; b < numNodes; b++)
//		{
//			System.out.println("Commit " + b + ": " + commitList[b]);
//		}
//		
		/*
		 * End Diagnostics
		 */
		
		
		// repeat for each state

		int curState = 1;	
		int count = 0;
		

	while (count < numNodes)
		
	{
		
		// reset resources
		
		numALU = inCDFG.getALU();
		numMUL = inCDFG.getMUL();
		numMIN = inCDFG.getMIN();
		numMAX = inCDFG.getMAX();
		numABS = inCDFG.getABS();
	
		
//		System.out.println("---------------------");
//		System.out.println("Current State: " + curState);
		
		
		// Add any ready nodes to the ready list
		
		for (int i = 0; i < numNodes; i++)
		{
			// Go through each node and add it to the ready list if all its dependencies have been committed
			dependency = false;
			
			if ((outCDFG.nodes[i].conn[0] != -1) && !doneList[i])
			{
				for (int j = 0; j < outCDFG.nodes[i].conn.length; j++)
				{
					if (!doneList[outCDFG.nodes[i].conn[j]])
					{
						dependency = true;
					}
				}
			}
			
			
//			System.out.println("Dependency = " + dependency);
			if (!dependency && !doneList[i])
			{
				// no dependencies found
				readyList[i] = true; // add to the ready list
//				System.out.println("Added " + i + " to readylist");
			}
			
		}
		
		
		// calculate urgency
		
		for (int k = 0; k < numNodes; k++)
		{
			urgency[k] = alapState[k] - curState;
		}
		
		
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
//						System.out.println("No ALUs available");
						// all ALUs used -- check mobility
						for (int y = 0; y <= numALU; y++)
						{
							
//							System.out.println("Comparing " + cNode + " and " + aluList[y]);
//							System.out.println("y=" + y);
							
							if ((mobilities[cNode] < mobilities[aluList[y]]) || (urgency[cNode] < urgency[aluList[y]]))
							{
//								System.out.println("This has higher priority - SWAP");
								// mobility of this node is less so it gets priority
								// swap its ID into the aluList and remove other node from commit lists
								
//								System.out.println("node swapped out: " + aluList[y]);
//								System.out.println("commit before: " + commitList[aluList[y]]);
								
								commitList[aluList[y]] = false; // remove old node from commit list
								aluList[y] = outCDFG.nodes[cNode].getID(); // swap ID into reservation list
								
								
//								System.out.println("commit after1: " + commitList[aluList[y]]);
								
//								System.out.println("node swapped in: " + outCDFG.nodes[cNode].getID());
//								System.out.println("alulist[y] = " + aluList[y]);
					
								
								commitList[outCDFG.nodes[cNode].getID()] = true; // add new node to commit list
								
//								System.out.println("commit after: " + commitList[aluList[y]]);
								
							}
						}
						
					}
					else
					{
						// alu available - add this node to reservation list and to commit lists
						
//						System.out.println("numALU = " + numALU);
//						System.out.println("alulistlength = " + aluList.length);
						
						aluList[aluList.length - numALU] = outCDFG.nodes[cNode].getID(); // add to reservation list
						
//						System.out.println("Added " + outCDFG.nodes[cNode].getID() + " to alu list at " + (aluList.length - numALU));
						
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
						for (int y = 0; y <= numMUL; y++)
						{
							if (mobilities[cNode] < mobilities[mulList[y]] || (urgency[cNode] < urgency[mulList[y]]))
							{
								// swap ID into mulList
								
								commitList[mulList[y]] = false; // remove old node from commit list
								mulList[y] = outCDFG.nodes[cNode].getID(); // swap ID into reservation list
								commitList[outCDFG.nodes[cNode].getID()] = true; // add new node to commit list
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
							if (mobilities[cNode] < mobilities[minList[y]] || (urgency[cNode] < urgency[minList[y]]))
							{
								// swap ID into minList
								commitList[minList[y]] = false; // remove old node from commit list
								minList[y] = outCDFG.nodes[cNode].getID(); // swap ID into reservation list
								commitList[outCDFG.nodes[cNode].getID()] = true; // add new node to commit list
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
							if (mobilities[cNode] < mobilities[maxList[y]] || (urgency[cNode] < urgency[maxList[y]]))
							{
								// swap ID into maxList
								commitList[maxList[y]] = false; // remove old node from commit list
								maxList[y] = outCDFG.nodes[cNode].getID(); // swap ID into reservation list
								commitList[outCDFG.nodes[cNode].getID()] = true; // add new node to commit list
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
							if (mobilities[cNode] < mobilities[absList[y]] || (urgency[cNode] < urgency[absList[y]]))
							{
								// swap ID into absList
								commitList[absList[y]] = false; // remove old node from commit list
								absList[y] = outCDFG.nodes[cNode].getID(); // swap ID into reservation list
								commitList[outCDFG.nodes[cNode].getID()] = true; // add new node to commit list
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
		
//		System.out.println("-------------");
//		
//		for (int a = 0; a < numNodes; a++)
//		{
//			System.out.println("Ready)  " + a + " : " + readyList[a]);
//			System.out.println("Mobility of " + a + " is: " + mobilities[a]);	
//			System.out.println("Urgency of " + a + " is: " + urgency[a]);
//		}
		
		
//		for (int b = 0; b < numNodes; b++)
//		{
//			System.out.println("Commit(before) " + b + ": " + commitList[b]);
//		}
//		
		/*
		 * End Diagnostics
		 */
		
		try 
		{
			FileWriter fstream = new FileWriter("rc_steps.txt", true);
			BufferedWriter output = new BufferedWriter(fstream);
		
			String rlist;
			String moblist;
			String urglist;
			
			output.write ("--------------------------------------------------------------------");
			output.newLine();
			output.write("State #: " + Integer.toString(curState));
			output.newLine();
			output.write("Ready List:\t");
			for (int x = 0; x < numNodes; x++)
			{
				if (readyList[x] == true)
				{
					output.write(Integer.toString(x) + '\t');
				}
			}
			output.newLine();
			output.write("Mobility: \t");
			for (int y = 0; y < numNodes; y++)
			{
				if (readyList[y] == true)
				{
					output.write(Integer.toString(mobilities[y]) + '\t');
				}
			}
			output.newLine();
			output.write("Urgency: \t");
			for (int z = 0; z < numNodes; z++)
			{
				if (readyList[z] == true)
				{
					output.write(Integer.toString(urgency[z]) + '\t');
				}
			}
			
			output.newLine();
			output.close();
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		
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
				count++;
					
			}
			
			
		}
		
		
		

		/*
		 * Diagnostics: Print ready & commit lists
		 */
		
//		System.out.println("Available ALUs = " + numALU);
//		
//		for (int a = 0; a < numNodes; a++)
//		{
//			System.out.println("Ready(after)  " + a + " : " + readyList[a]);
//			
//		}
//		
//		for (int b = 0; b < numNodes; b++)
//		{
//			System.out.println("Commit(after) " + b + ": " + commitList[b]);
//		}
//		
//		for (int c = 0; c < numNodes; c++)
//		{
//			System.out.println("Done " + c + ": " + doneList[c]);
//			
//		}
		
		/*
		 * End Diagnostics
		 */
		
		curState++;
		
	} // end state loop
		
		
		outCDFG.setState(curState - 1);
		return outCDFG;
		
		
		
	} // end method
	
	
	// TODO: TC
	/*
	 * Function to perform TC scheduling given a clock restraint
	 */
	//must use ASAPcdfg.getNumNodes as input
	public static CDFG performTC(CDFG inCDFG, int numasapstates, int [] mobilities, int[] alapState, int maxCLK)
	{
		if(maxCLK < numasapstates)	//exit if number of wanted states exceed asap - impossible scenario
		{
			System.out.println("Impossible time constraint - please increase number of required states.");
			System.exit(1);
		}
		
		System.out.println("Starting TC...");
		
//		for (int b = 0; b < mobilities.length; b++)
//		{
//			System.out.println("Mobility of " + b + " : "  + mobilities[b]);
//		}

		CDFG outCDFG = inCDFG;
		outCDFG.setTitle("TC CDFG");
		
		int numNodes = inCDFG.getNumNodes();
		
		int numALU = 0;
		int numMUL = 0;
		int numMIN = 0;
		int numMAX = 0;
		int numABS = 0;
			
		// assume resource list from nodes - iterate and set each type that exists to 1
		for(int b = 0; b < numNodes; b++)
		{
			if(inCDFG.nodes[b].getOp().equalsIgnoreCase("alu")){
				numALU = 1;
			}
			if(inCDFG.nodes[b].getOp().equalsIgnoreCase("mul")){
				numMUL = 1;
			}
			if(inCDFG.nodes[b].getOp().equalsIgnoreCase("min")){
				numMIN = 1;
			}
			if(inCDFG.nodes[b].getOp().equalsIgnoreCase("max")){
				numMAX = 1;
			}
			if(inCDFG.nodes[b].getOp().equalsIgnoreCase("abs")){
				numABS = 1;
			}
		}
		
		outCDFG.setResources(numALU, numMUL, numMIN, numMAX, numABS);
		
		//
		// reservation lists for each resource
		// 
		
	/*	int [] aluList = new int[outCDFG.getALU()];
		int [] mulList = new int[outCDFG.getMUL()];
		int [] minList = new int[outCDFG.getMIN()];
		int [] maxList = new int[outCDFG.getMAX()];	
		int [] absList = new int[outCDFG.getABS()];
	*/	
		
		
//		int numStates = inCDFG.getNumStates();
		
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
		boolean dependency; 
		
		int [] urgency = new int [numNodes]; // urgency list
		
		// populate the ready list with nodes that do not depend on anything
		
		for (int x = 0; x < numNodes; x++)
		{
			if (outCDFG.nodes[x].dependsOn(-1))
			{
				// add this node to the ready list
				readyList[x] = true;
//				System.out.println("Added Node: " + x);		
			}		
		}
		
		/*
		 * Diagnostics: Print ready & commit lists
		 */
//		
//		for (int a = 0; a < numNodes; a++)
//		{
//			System.out.println("Ready  " + a + " : " + readyList[a]);
//		}
//		
//		for (int b = 0; b < numNodes; b++)
//		{
//			System.out.println("Commit " + b + ": " + commitList[b]);
//		}
		
		/*
		 * End Diagnostics
		 */
		
		
		// repeat for each state
		
		int curState = 1;	
		

	for(curState = 1; curState <= maxCLK; curState++)		
	{
		// reset resources
		
		numALU = outCDFG.getALU();
		numMUL = outCDFG.getMUL();
		numMIN = outCDFG.getMIN();
		numMAX = outCDFG.getMAX();
		numABS = outCDFG.getABS();
		
		int [] aluList = new int[outCDFG.getALU()];
		int [] mulList = new int[outCDFG.getMUL()];
		int [] minList = new int[outCDFG.getMIN()];
		int [] maxList = new int[outCDFG.getMAX()];	
		int [] absList = new int[outCDFG.getABS()];
		
//		System.out.println("---------------------");
//		System.out.println("Current State: " + curState);
		
		
		// Add any ready nodes to the ready list
		System.out.println("-------------------------------");
		System.out.println("Ready list for state: " + curState);
		for (int i = 0; i < numNodes; i++)
		{
			// Go through each node and add it to the ready list if all its dependencies have been committed
			dependency = false;
			
			if ((outCDFG.nodes[i].conn[0] != -1) && !doneList[i])
			{
				for (int j = 0; j < outCDFG.nodes[i].conn.length; j++)
				{
					if (!doneList[outCDFG.nodes[i].conn[j]])
					{
						dependency = true;
					}
				}
			}
			
			
//			System.out.println("Dependency = " + dependency);
			if (!dependency && !doneList[i])
			{
				// no dependencies found
				readyList[i] = true; // add to the ready list
				System.out.println("Added " + i + " to readylist");
			}
			//System.out.println("Readylist of " + i + " : " + readyList[i]);			//TODO: Prints readylist
		}
		
		
		// calculate urgency
		
			
			
			for (int k = 0; k < numNodes; k++)
			{
				urgency[k] = alapState[k] - curState;
				if(doneList[k] == false)		//TODO: If node is not done, prints urgency
				{
					System.out.println("Slack of node " + k + " is " + urgency[k]);
				}
					
			}

		//Resource stuff starts here
		if(outCDFG.getALU()!=0)	//We have ALUs to allocate in general
		{	
						int aluindex = 0;
					
						for(int curnode = 0; curnode < numNodes; curnode++)	//iterate through nodes
						{
							
							////// ***************ALU!!!!**********************************////////////////////////
							if(readyList[curnode]==true&&outCDFG.nodes[curnode].getOp().equalsIgnoreCase("alu"))	//this node needs this resource, or move on
							{
								
								if(urgency[curnode]==0){ //urgency is 0 - need extra resources?
									if(numALU==0) //extra resources are needed
									{
										outCDFG.setALU(outCDFG.getALU()+1);	//create extra resource
										numALU++;	//update numALU
										int[] temp = new int[aluList.length+1];
										for(int i = 0; i<aluList.length; i++){
											temp[i] = aluList[i]; 
										}
										aluList = new int[temp.length];
										
										for(int i = 0; i<temp.length; i++){
											aluList[i] = temp[i]; 
										}
										
										System.out.println("New ALU created. Total: " + aluList.length);	//TODO: Declares an extra allocated ALU
									}
									//use alu spot for this node - change node to the state number and commit it
									outCDFG.nodes[curnode].setState(curState);
									numALU--;
									readyList[curnode]=false;
									commitList[curnode]=true;
									aluList[aluindex] = curnode;
									System.out.println("ALU #" + (aluindex + 1) + " in state " + curState + " is allocated to node " + curnode);
									aluindex++;
								}
								
								else //urgency not 0
								{
									if(numALU!=0) //we have extra resources to allocate
									{
										outCDFG.nodes[curnode].setState(curState);
										numALU--;
										readyList[curnode]=false;
										commitList[curnode]=true;
										aluList[aluindex] = curnode;
										System.out.println("ALU #" + (aluindex + 1) + " in state " + curState + " is allocated to node " + curnode);
										aluindex++;
									}
									else
									{
										for(int y = 0; y < aluList.length; y++)	//try to swap with a less urgent node
										{
											if(urgency[curnode]<urgency[aluList[y]])
											{
												int replacednode = aluList[y];
												readyList[replacednode] = true;
												commitList[replacednode] = false;
												
												outCDFG.nodes[curnode].setState(curState);
												readyList[curnode]=false;
												commitList[curnode]=true;
												aluList[aluindex] = curnode;
												System.out.println("ALU #" + (aluindex + 1) + " in state " + curState + " is re-allocated to node " + curnode);
											}
										}
									}
								}
							}
						}	
			}
		
		if(outCDFG.getMUL()!=0)	//We have muls to allocate in general
		{	
						int mulindex = 0;
					
						for(int curnode = 0; curnode < numNodes; curnode++)	//iterate through nodes
						{
							
							////// ***************mul!!!!**********************************////////////////////////
							if(readyList[curnode]==true&&outCDFG.nodes[curnode].getOp().equalsIgnoreCase("mul"))	//this node needs this resource, or move on
							{
							// TODO: Print readylist and urgency	
								if(urgency[curnode]==0){ //urgency is 0 - need extra resources?
									if(numMUL==0) //extra resources are needed
									{
										outCDFG.setMUL(outCDFG.getMUL()+1);	//create extra resource
										numMUL++;	//update nummul
										int[] temp = new int[mulList.length+1];
										for(int i = 0; i<mulList.length; i++){
											temp[i] = mulList[i]; 
										}
										mulList = new int[temp.length];
										
										for(int i = 0; i<temp.length; i++){
											mulList[i] = temp[i]; 
										}
										System.out.println("New MUL created. Total: " + mulList.length);
									}
									
									
									//use mul spot for this node - change node to the state number and commit it
									outCDFG.nodes[curnode].setState(curState);
									numMUL--;
									readyList[curnode]=false;
									commitList[curnode]=true;
									mulList[mulindex] = curnode;
									System.out.println("MUL #" + (mulindex + 1) + " in state " + curState + " is allocated to node " + curnode);
									mulindex++;
								}
								
								else //urgency not 0
								{
									if(numMUL!=0) //we have extra resources to allocate
									{
										outCDFG.nodes[curnode].setState(curState);
										numMUL--;
										readyList[curnode]=false;
										commitList[curnode]=true;
										mulList[mulindex] = curnode;
										System.out.println("MUL #" + (mulindex + 1) + " in state " + curState + " is allocated to node " + curnode);
										mulindex++;
									}
									else
									{
										for(int y = 0; y < mulList.length; y++)	//try to swap with a less urgent node
										{
											if(urgency[curnode]<urgency[mulList[y]])
											{
												int replacednode = mulList[y];
												readyList[replacednode] = true;
												commitList[replacednode] = false;
												
												outCDFG.nodes[curnode].setState(curState);
												readyList[curnode]=false;
												commitList[curnode]=true;
												mulList[mulindex] = curnode;
												System.out.println("MUL #" + (mulindex + 1) + " in state " + curState + " is re-allocated to node " + curnode);
											}
										}
									}
								}
							}
						}	
			}
		
		if(outCDFG.getMIN()!=0)	//We have mins to allocate in general
		{	
						int minindex = 0;
					
						for(int curnode = 0; curnode < numNodes; curnode++)	//iterate through nodes
						{
							
							////// ***************min!!!!**********************************////////////////////////
							if(readyList[curnode]==true&&outCDFG.nodes[curnode].getOp().equalsIgnoreCase("min"))	//this node needs this resource, or move on
							{
								
								if(urgency[curnode]==0){ //urgency is 0 - need extra resources?
									if(numMIN==0) //extra resources are needed
									{
										outCDFG.setMIN(outCDFG.getMIN()+1);	//create extra resource
										numMIN++;	//update nummin
										int[] temp = new int[minList.length+1];
										for(int i = 0; i<minList.length; i++){
											temp[i] = minList[i]; 
										}
										minList = new int[temp.length];
										
										for(int i = 0; i<temp.length; i++){
											minList[i] = temp[i]; 
										}
										
										System.out.println("New MIN created. Total: " + minList.length);
									}
									//use min spot for this node - change node to the state number and commit it
									outCDFG.nodes[curnode].setState(curState);
									numMIN--;
									readyList[curnode]=false;
									commitList[curnode]=true;
									minList[minindex] = curnode;
									System.out.println("MIN #" + (minindex + 1) + " in state " + curState + " is allocated to node " + curnode);
									minindex++;
								}
								
								else //urgency not 0
								{
									if(numMIN!=0) //we have extra resources to allocate
									{
										outCDFG.nodes[curnode].setState(curState);
										numMIN--;
										readyList[curnode]=false;
										commitList[curnode]=true;
										minList[minindex] = curnode;
										System.out.println("MIN #" + (minindex + 1) + " in state " + curState + " is allocated to node " + curnode);
										minindex++;
									}
									else
									{
										for(int y = 0; y < minList.length; y++)	//try to swap with a less urgent node
										{
											if(urgency[curnode]<urgency[minList[y]])
											{
												int replacednode = minList[y];
												readyList[replacednode] = true;
												commitList[replacednode] = false;
												
												outCDFG.nodes[curnode].setState(curState);
												readyList[curnode]=false;
												commitList[curnode]=true;
												minList[minindex] = curnode;
												System.out.println("MIN #" + (minindex + 1) + " in state " + curState + " is re-allocated to node " + curnode);
											}
										}
									}
								}
							}
						}	
			}
		
		if(outCDFG.getMAX()!=0)	//We have maxs to allocate in general
		{	
						int maxindex = 0;
					
						for(int curnode = 0; curnode < numNodes; curnode++)	//iterate through nodes
						{
							
							////// ***************max!!!!**********************************////////////////////////
							if(readyList[curnode]==true&&outCDFG.nodes[curnode].getOp().equalsIgnoreCase("max"))	//this node needs this resource, or move on
							{
								
								if(urgency[curnode]==0){ //urgency is 0 - need extra resources?
									if(numMAX==0) //extra resources are needed
									{
										outCDFG.setMAX(outCDFG.getMAX()+1);	//create extra resource
										numMAX++;	//update nummax
										int[] temp = new int[maxList.length+1];
										for(int i = 0; i<maxList.length; i++){
											temp[i] = maxList[i]; 
										}
										maxList = new int[temp.length];
										
										for(int i = 0; i<temp.length; i++){
											maxList[i] = temp[i]; 
										}
										System.out.println("New MAX created. Total: " + maxList.length);
									}
									//use max spot for this node - change node to the state number and commit it
									outCDFG.nodes[curnode].setState(curState);
									numMAX--;
									readyList[curnode]=false;
									commitList[curnode]=true;
									maxList[maxindex] = curnode;
									System.out.println("MAX #" + (maxindex + 1) + " in state " + curState + " is allocated to node " + curnode);
									maxindex++;
								}
								
								else //urgency not 0
								{
									if(numMAX!=0) //we have extra resources to allocate
									{
										outCDFG.nodes[curnode].setState(curState);
										numMAX--;
										readyList[curnode]=false;
										commitList[curnode]=true;
										maxList[maxindex] = curnode;
										System.out.println("MAX #" + (maxindex + 1) + " in state " + curState + " is allocated to node " + curnode);
										maxindex++;
									}
									else
									{
										for(int y = 0; y < maxList.length; y++)	//try to swap with a less urgent node
										{
											if(urgency[curnode]<urgency[maxList[y]])
											{
												int replacednode = maxList[y];
												readyList[replacednode] = true;
												commitList[replacednode] = false;
												
												outCDFG.nodes[curnode].setState(curState);
												readyList[curnode]=false;
												commitList[curnode]=true;
												maxList[maxindex] = curnode;
												System.out.println("MAX #" + (maxindex + 1) + " in state " + curState + " is re-allocated to node " + curnode);
											}
										}
									}
								}
							}
						}	
			}
		
		if(outCDFG.getABS()!=0)	//We have abs to allocate in general
		{	
						int absindex = 0;
					
						for(int curnode = 0; curnode < numNodes; curnode++)	//iterate through nodes
						{
							
							////// ***************abs!!!!**********************************////////////////////////
							if(readyList[curnode]==true&&outCDFG.nodes[curnode].getOp().equalsIgnoreCase("abs"))	//this node needs this resource, or move on
							{
								
								if(urgency[curnode]==0){ //urgency is 0 - need extra resources?
									if(numABS==0) //extra resources are needed
									{
										outCDFG.setABS(outCDFG.getABS()+1);	//create extra resource
										numABS++;	//update numabs
										int[] temp = new int[absList.length+1];
										for(int i = 0; i<absList.length; i++){
											temp[i] = absList[i]; 
										}
										absList = new int[temp.length];
										
										for(int i = 0; i<temp.length; i++){
											absList[i] = temp[i]; 
										}
										System.out.println("New ABS created. Total: " + absList.length);
									}
									//use abs spot for this node - change node to the state number and commit it
									outCDFG.nodes[curnode].setState(curState);
									numABS--;
									readyList[curnode]=false;
									commitList[curnode]=true;
									absList[absindex] = curnode;
									System.out.println("ABS #" + (absindex + 1) + " in state " + curState + " is allocated to node " + curnode);
									absindex++;
								}
								
								else //urgency not 0
								{
									if(numABS!=0) //we have extra resources to allocate
									{
										outCDFG.nodes[curnode].setState(curState);
										numABS--;
										readyList[curnode]=false;
										commitList[curnode]=true;
										absList[absindex] = curnode;
										System.out.println("ABS #" + (absindex + 1) + " in state " + curState + " is allocated to node " + curnode);
										absindex++;
									}
									else
									{
										for(int y = 0; y < absList.length; y++)	//try to swap with a less urgent node
										{
											if(urgency[curnode]<urgency[absList[y]])
											{
												int replacednode = absList[y];
												readyList[replacednode] = true;
												commitList[replacednode] = false;
												
												outCDFG.nodes[curnode].setState(curState);
												readyList[curnode]=false;
												commitList[curnode]=true;
												absList[absindex] = curnode;
												System.out.println("ABS #" + (absindex + 1) + " in state " + curState + " is re-allocated to node " + curnode);
											}
										}
									}
								}
							}
						}	
			}
		
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
		
		}
	return outCDFG;	
} // end TC method
	
	
	public static CDFG performTC2(CDFG inCDFG, CDFG alapCDFG, CDFG asapCDFG, int clk)
	{
		// I put in int clk here to be used as max number of states
		
		System.out.println("\nStarting TC2...");
				
		CDFG alap2CDFG;
		alap2CDFG = asapCDFG.copy();
		alap2CDFG = performALAP2(alap2CDFG, clk);
		int [] mobilities = new int [inCDFG.getNumNodes()];
		int [] alapStates = new int [inCDFG.getNumNodes()];
		int [] asapStates = new int [inCDFG.getNumNodes()];
		int [] alap2States = new int [inCDFG.getNumNodes()];
		
		
		for (int c = 0; c < asapCDFG.getNumNodes(); c++)
		{
			mobilities[c] = (alapCDFG.nodes[c].getState() - asapCDFG.nodes[c].getState());
			alapStates[c] = alapCDFG.nodes[c].getState();
			alap2States[c] = alap2CDFG.nodes[c].getState();
			asapStates[c] = asapCDFG.nodes[c].getState();
		}
		
		CDFG outCDFG = inCDFG;
		int numNodes = inCDFG.getNumNodes();
		int[] chosenstate = new int[numNodes];
		//int nodesDone = 0;
		
		//boolean [] doneList = new boolean[numNodes];
		int [] tf_start = asapStates;
		int [] tf_end = alap2States;
		
		int [] temp_tf_start = asapStates;
		int [] temp_tf_end = alap2States;
		
		double[][] nodeProb = new double[clk+1][numNodes];	//ignore position 0 for states
		double[][] nodeselfForce = new double[clk+1][numNodes];	//ignore position 0 for states
		double[][][] nodeprevForce = new double[clk+1][numNodes][numNodes];
		double[][][] nodenextForce = new double[clk+1][numNodes][numNodes];
		double[][] totnodeprevForce = new double[clk+1][numNodes];
		double[][] totnodenextForce = new double[clk+1][numNodes];
		double[][] totalForce = new double[clk+1][numNodes];
		
		int[][][][] chosenodeProb = new int[numNodes][clk+1][clk+1][numNodes];
		
		boolean[] doneList = new boolean[numNodes];
		
		for(int i = 0; i<numNodes; i++)
		{//populating prev and next prob arrays
			doneList[i] = false;
			for (int j = 0; j<clk+1; j++)
				for(int z = 0; z<clk+1; z++)
					for(int p = 0; p<clk+1; p++)
			{
				chosenodeProb[i][j][z][p] = 0;
			}
		}
		
		
		// *******MUST USE CLK+1 FOR STATES BECAUSE STATES RUN 1-TOTAL INSTEAD OF 0-(TOTAL-1)
		
		for(int i = 0; i<clk+1; i++)	//populate probabilities with zeros and forces with high negative forces
			for (int j = 0; j<numNodes; j++)
			{
				nodeProb[i][j] = 0;
				nodeselfForce[i][j] = -99;
			}
		
		for(int u = 0; u < clk+1; u++)
			for(int i = 0; i< numNodes; i++)	//populate probabilities with zeros and forces with high negative forces
				for (int j = 0; j<numNodes; j++)
				{
					nodeprevForce[u][i][j] = 0;
					nodenextForce[u][i][j] = 0;
					totnodeprevForce[u][i] = 0;
					totnodenextForce[u][i] = 0;
					totalForce[u][i] = 0;
				}
		
		double[][] q = new double[5][clk+1];	// q[resource][state] -> resource distribution.
		//*******LEGEND: 	0 = ALU		1 = MUL		2 = MIN		3 = MAX		4 = ABS	********//
		
		for (int j = 0; j<clk+1; j++)
		{
			q[0][j] = 0;
			q[1][j] = 0;
			q[2][j] = 0;
			q[3][j] = 0;
			q[4][j] = 0;
		}
		
		//double [] tf_prob = new double[numNodes];
		//while (nodesDone < numNodes)	////?????
		//{
			for (int cNode = 0; cNode < numNodes; cNode++)	//calculates nodeProb and q
			{
					for(int state=tf_start[cNode]; state <= tf_end[cNode]; state++)
					{
						nodeProb[state][cNode] = Math.abs(tf_end[cNode] - tf_start[cNode]) + 1;
						nodeProb[state][cNode] = 1/(nodeProb[state][cNode]);
						
						if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("alu")){
							//distALU[state] = distALU[state] + nodeProb[state][cNode];
							q[0][state] = q[0][state] + nodeProb[state][cNode];
						}
						if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("mul")){
							//distMUL[state] = distMUL[state] + nodeProb[state][cNode];
							q[1][state] = q[1][state] + nodeProb[state][cNode];
						}
						if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("min")){
							//distMIN[state] = distMIN[state] + nodeProb[state][cNode];
							q[2][state] = q[2][state] + nodeProb[state][cNode];
						}
						if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("max")){
							//distMAX[state] = distMAX[state] + nodeProb[state][cNode];
							q[3][state] = q[3][state] + nodeProb[state][cNode];
						}
						if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("abs")){
							//distABS[state] = distABS[state] + nodeProb[state][cNode];
							q[4][state] = q[4][state] + nodeProb[state][cNode];
						}
						//	DIAGNOSTICS
						
						  	System.out.println("STATE: " + state + " Node: " + cNode);
							System.out.println("Probability: " + nodeProb[state][cNode] + " and node op is: " + inCDFG.nodes[cNode].getOp());
						 		
					}		
			}
			
			for(int state = 1; state<=clk; state++)
			{
				System.out.println("-----------------------");
				System.out.println("Resource distribution: ");
				System.out.println("ALU in state: " + state + " is " + q[0][state]);
				System.out.println("MUL in state: " + state + " is " + q[1][state]);
				System.out.println("MIN in state: " + state + " is " + q[2][state]);
				System.out.println("MAX in state: " + state + " is " + q[3][state]);
				System.out.println("ABS in state: " + state + " is " + q[4][state]);
				
			}
				
			
			
			
			int qUsed = 0;
			
			for (int cNode = 0; cNode < numNodes; cNode++)	//calculates forces
			{
				for(int state = tf_start[cNode]; state<=tf_end[cNode]; state++)
				{
					if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("alu"))
						qUsed = 0;
					
					if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("mul"))
						qUsed = 1;
					
					if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("min"))
						qUsed = 2;
					
					if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("max"))
						qUsed = 3;
					
					if(inCDFG.nodes[cNode].getOp().equalsIgnoreCase("abs"))
						qUsed = 4;
					
					if(nodeProb[state][cNode]!=0)
					{
						if(nodeProb[state][cNode]==1)
							nodeselfForce[state][cNode] = 99;	//sets nodeForce to 99 for nodes with prob 1 to ensure scheduling
						else
							nodeselfForce[state][cNode] = 0;	//reset nodeselfForce to 0 from -99 for existing node probabilities
					}
							
					// TODO: Work on next and prev force here
					
									if(inCDFG.dependOn(cNode)[0]!=-1)	//if there are connections to next
									{

										int nqUsed = 0;
										for(int nNode = 0; nNode<inCDFG.dependOn(cNode).length; nNode++)	//iterate through nodes cNode leads to and sets the starting temp state for nNode to cNode state + 1
										{
											chosenstate[inCDFG.dependOn(cNode)[nNode]] = state + 1; //set the state of next node to 1 higher than that of cNode
											
											//Determine it's operation
											if(inCDFG.nodes[inCDFG.dependOn(cNode)[nNode]].getOp().equalsIgnoreCase("alu"))
												nqUsed = 0;
											
											if(inCDFG.nodes[inCDFG.dependOn(cNode)[nNode]].getOp().equalsIgnoreCase("mul"))
												nqUsed = 1;
											
											if(inCDFG.nodes[inCDFG.dependOn(cNode)[nNode]].getOp().equalsIgnoreCase("min"))
												nqUsed = 2;
											
											if(inCDFG.nodes[inCDFG.dependOn(cNode)[nNode]].getOp().equalsIgnoreCase("max"))
												nqUsed = 3;
											
											if(inCDFG.nodes[inCDFG.dependOn(cNode)[nNode]].getOp().equalsIgnoreCase("abs"))
												nqUsed = 4;
											
											for(int somestate = tf_start[inCDFG.dependOn(cNode)[nNode]]; somestate <= tf_end[inCDFG.dependOn(cNode)[nNode]]; somestate++)
											{
												if(somestate==chosenstate[inCDFG.dependOn(cNode)[nNode]])
												{
													nodenextForce[state][cNode][inCDFG.dependOn(cNode)[nNode]] = nodenextForce[state][cNode][inCDFG.dependOn(cNode)[nNode]] + q[nqUsed][somestate]*(1-nodeProb[chosenstate[inCDFG.dependOn(cNode)[nNode]]][inCDFG.dependOn(cNode)[nNode]]);
												}
												else
												{
													nodenextForce[state][cNode][inCDFG.dependOn(cNode)[nNode]] = nodenextForce[state][cNode][inCDFG.dependOn(cNode)[nNode]] + q[nqUsed][somestate]*(0-nodeProb[chosenstate[inCDFG.dependOn(cNode)[nNode]]][inCDFG.dependOn(cNode)[nNode]]);
												}
											}
											totnodenextForce[state][cNode] = totnodenextForce[state][cNode] + nodenextForce[state][cNode][inCDFG.dependOn(cNode)[nNode]];
											
											System.out.println("=================================");
											System.out.println("Next node force of node " + inCDFG.dependOn(cNode)[nNode] + " is:" + nodenextForce[state][cNode][inCDFG.dependOn(cNode)[nNode]]);
											System.out.println("Chosen state is: " + chosenstate[inCDFG.dependOn(cNode)[nNode]]);
											System.out.println("TOTAL NEXT FORCE: " + totnodenextForce[state][cNode]);
										}
											
									}

									if(inCDFG.nodes[cNode].getConn()[0]!=-1)	//if there are connections to previous
									{
										int pqUsed = 0;
										for(int pNode = 0; pNode<inCDFG.nodes[cNode].getConn().length; pNode++)	//iterate through nodes cNode leads to and set the chosen state to state-1
										{
											chosenstate[inCDFG.nodes[cNode].getConn()[pNode]] = state - 1; //set the state of prev node to 1 lower than that of cNode
											
											//Determine it's operation
											if(inCDFG.nodes[inCDFG.nodes[cNode].getConn()[pNode]].getOp().equalsIgnoreCase("alu"))
												pqUsed = 0;
											
											if(inCDFG.nodes[inCDFG.nodes[cNode].getConn()[pNode]].getOp().equalsIgnoreCase("mul"))
												pqUsed = 1;
											
											if(inCDFG.nodes[inCDFG.nodes[cNode].getConn()[pNode]].getOp().equalsIgnoreCase("min"))
												pqUsed = 2;
											
											if(inCDFG.nodes[inCDFG.nodes[cNode].getConn()[pNode]].getOp().equalsIgnoreCase("max"))
												pqUsed = 3;
											
											if(inCDFG.nodes[inCDFG.nodes[cNode].getConn()[pNode]].getOp().equalsIgnoreCase("abs"))
												pqUsed = 4;
											
											for(int somestate = tf_start[inCDFG.nodes[cNode].getConn()[pNode]]; somestate <= tf_end[inCDFG.nodes[cNode].getConn()[pNode]]; somestate++)
											{
												if(somestate==chosenstate[inCDFG.nodes[cNode].getConn()[pNode]])
												{
													nodeprevForce[state][cNode][inCDFG.nodes[cNode].getConn()[pNode]] = nodeprevForce[state][cNode][inCDFG.nodes[cNode].getConn()[pNode]] + q[pqUsed][somestate]*(1-nodeProb[chosenstate[inCDFG.nodes[cNode].getConn()[pNode]]][inCDFG.nodes[cNode].getConn()[pNode]]);
												}
												else
												{
													nodeprevForce[state][cNode][inCDFG.nodes[cNode].getConn()[pNode]] = nodeprevForce[state][cNode][inCDFG.nodes[cNode].getConn()[pNode]] + q[pqUsed][somestate]*(0-nodeProb[chosenstate[inCDFG.nodes[cNode].getConn()[pNode]]][inCDFG.nodes[cNode].getConn()[pNode]]);
												}
											}
											totnodeprevForce[state][cNode] = totnodeprevForce[state][cNode] + nodeprevForce[state][cNode][inCDFG.nodes[cNode].getConn()[pNode]];
											
											//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^");
											System.out.println("prev node force of node " + inCDFG.nodes[cNode].getConn()[pNode] + " is:" + nodeprevForce[state][cNode][inCDFG.nodes[cNode].getConn()[pNode]]);
											System.out.println("Chosen state is: " + chosenstate[inCDFG.nodes[cNode].getConn()[pNode]]);
											System.out.println("TOTAL PREVIOUS FORCE: " + totnodeprevForce[state][cNode]);
										}
										
										
									}
					
					// ^^ just selected states for previous and next nodes
					
					for(int fstate = tf_start[cNode]; fstate <= tf_end[cNode]; fstate++)	//Calculates total self force for chosen state for this node
					{
						if(fstate==state)
							nodeselfForce[state][cNode] = nodeselfForce[state][cNode] + q[qUsed][fstate]*(1-nodeProb[state][cNode]);
						else
							nodeselfForce[state][cNode] = nodeselfForce[state][cNode] + q[qUsed][fstate]*(0-nodeProb[state][cNode]);
					}
				
					
					
					//Diagnostics
					
					System.out.println("^^^^^^^^^^^^^^^^^^");
					System.out.println("Node: " + cNode);
					System.out.println("Used state: " + state + " Self force: " + nodeselfForce[state][cNode]);
					
					
				}
			}
			
			//Calculating total force for each possible state for each node:
			
			int[] finalstate = new int[numNodes];
			for (int cNode = 0; cNode < numNodes; cNode++)	//populate final state for each node with its default nodes
			{
				finalstate[cNode] = inCDFG.getState();
			}
			
			double[] maxForce = new double[numNodes];
			
			for (int cNode = 0; cNode < numNodes; cNode++)	//calculates forces
			{
				for(int state = tf_start[cNode]; state<=tf_end[cNode]; state++)
				{
					totalForce[state][cNode] = nodeselfForce[state][cNode] + totnodenextForce[state][cNode] + totnodeprevForce[state][cNode];
					System.out.println("");
					System.out.println("");
					System.out.println("Total force for node " + cNode + " with selected state " + state + " is " + totalForce[state][cNode]);
				}
				maxForce[cNode] = totalForce[tf_start[cNode]][cNode];
			}
			
			
			
			for (int cNode = 0; cNode < numNodes; cNode++)	//calculates maxForce for each node
			{
				for(int state = tf_start[cNode]; state<=tf_end[cNode]; state++)
				{
					if(state!=tf_start[cNode])
					{
						if(totalForce[state][cNode]>maxForce[cNode])
						{
							maxForce[cNode] = totalForce[state][cNode];
						}
					}
				}	
			}
		
			for (int cNode = 0; cNode < numNodes; cNode++)	//sets states for nodes
			{
				for(int state = tf_start[cNode]; state<=tf_end[cNode]; state++)
				{
					if(totalForce[state][cNode]==maxForce[cNode]&&doneList[cNode]==false)
					{
						finalstate[cNode] = state;
						outCDFG.setState(state);
						doneList[cNode] = true;
						System.out.println("-------------------------");
						
						System.out.println("Final state of node " + cNode + " is " + state);
						System.out.println("Maximum force is: " + maxForce[cNode]);
					}
				}	
			}

		return outCDFG;
	}
	
	
	
	
	
	
	

}
