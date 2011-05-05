package scheduler;

import java.io.IOException;
import java.io.*;

public class CDFG {

	
	// instance variables
	public Node [] nodes;
	private String title = "Untitled";
	
	private int numALU = 0;
	private int numMUL = 0;
	private int numMIN = 0;
	private int numMAX = 0;
	private int numABS = 0;
	
	private int numCLK = 0;
	
	private int numNodes = 0;
	private static int ID = 0;
	
	private int numStates = 1; // each CDFG must have at least 1 state
	
	


	// constructor
	public CDFG(int numberOfNodes)
	{
		nodes = new Node[numberOfNodes]; // create an array of new nodes
		//nodes = nodeArray; 
		numNodes = numberOfNodes;
		ID++; // needed?
	}
	
	public CDFG copy()
	{
		int numNodes = this.getNumNodes();
		int numStates = this.getNumStates();
		
		CDFG outCDFG = new CDFG(numNodes); // create a blank CDFG with number of nodes
		
		for (int i = 0; i < numNodes; i++)
		{
			outCDFG.addNode(this.nodes[i].copy(), i); // add a copy of each node
		}
		
		outCDFG.setResources(this.getALU(), this.getMUL(), this.getMIN(), this.getMAX(), this.getABS());
		outCDFG.setCLK(this.getCLK());
		outCDFG.setTitle(this.getTitle());
		outCDFG.setState(numStates);
		
		return outCDFG;
		
	}
	
	public void setCLK(int clk)
	{
		numCLK = clk;
	}
	
	public int getCLK()
	{
		return numCLK;
	}
	
	public int getState()
	{
		return numStates;
	}
	
	public void setState(int newState)
	{
		numStates = newState;
	}
	
	
	public void setResources(int alu, int mul, int min, int max, int abs)
	{
		numALU = alu;
		numMUL = mul;
		numMIN = min;
		numMAX = max;
		numABS = abs;	
	}
	
	public void setALU(int alu)
	{
		numALU = alu;
	}

	
	public int getALU()
	{
		return numALU;
	}
	
	
	public void setMUL(int mul)
	{
		numMUL = mul;
	}
	
	public int getMUL()
	{
		return numMUL;
	}
	
	
	public void setMIN(int min)
	{
		numMIN = min;
	}
	
	public int getMIN()
	{
		return numMIN;
	}
	
	public void setMAX(int max)
	{
		numMAX = max;
	}
	
	public int getMAX()
	{
		return numMAX;
	}
	
	
	public void setABS(int abs)
	{
		numABS = abs;	
	}
	
	public int getABS()
	{
		return numABS;
	}
	
	
	public int getNumNodes()
	{
		return numNodes;
	}
	
	
	public void setTitle(String newTitle)
	{
		title = newTitle;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getNumStates()
	{
		return numStates;
	}
	
	public int addNode(Node newNode, int position)
	{
	//	System.out.println("position = " + position);
	//	System.out.println("numNodes = " + numNodes);
		if(position > (numNodes - 1))
		{
			System.out.println("Error: CDFG only has " + numNodes + " node(s).");
			return -1; // error
		}
		else
		{
			// add the node to the array at specified position
			nodes[position] = newNode;
			
			//System.out.println("Added Node at position: " + position);
			//System.out.println("Node State was: " + nodes[position].getState());
			//System.out.println("Node OP was: " + nodes[position].getOp());
			return 1; // success
		}
		
	}
	
	public void printResources()
	{
		System.out.println("# of ALUs \t=\t " + this.getALU());
		System.out.println("# of Mult \t=\t " + this.getMUL());
		System.out.println("# of MIN \t=\t " + this.getMIN());
		System.out.println("# of MAX \t=\t " + this.getMIN());
		System.out.println("# of ABS \t=\t " + this.getABS());
		System.out.println("Clock Cycles \t=\t " + this.getCLK());
	
	}
	
	public Node getNode(int y)
	{
		return nodes[y];
		
	}
	
	public void printCDFG()
	{
		System.out.println(this.getTitle());
		System.out.println("# of States: " + this.getNumStates());
		
		if (nodes[0] != null)
		{
			for (int i = 0; i < numNodes; i++)
			{
			System.out.println("ID \t= \t" + nodes[i].getID());
			System.out.println("State \t= \t" +nodes[i].getState());
			System.out.println("Op \t= \t" + nodes[i].getOp());
			nodes[i].printConn();
			}
		}
			
		this.printResources();

		
	}
	
	public void printCDFG(String filename)
	{
		
		try
		{
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter output = new BufferedWriter(fstream);
			
			output.write(this.getTitle());
			output.write('\n');
			output.write("# of States: " + this.getNumStates());
			output.write('\n');
			output.write("=====================\n");
			
			
			if (nodes[0] != null)
			{
				for (int i = 0; i < numNodes; i++)
				{
					output.write("ID \t\t= \t" + nodes[i].getID());
					output.write('\n');
					output.write("State \t= \t" + nodes[i].getState());
					output.write('\n');
					output.write("Op \t\t= \t" + nodes[i].getOp());
					output.write('\n');
					output.write(nodes[i].printConnToFile());
					output.write('\n');
					output.write("----------------------------\n");
				}
			}
			
			output.close();
			// TODO: Add Resources Printout
			
			
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	public boolean dependency(int ID_1, int ID_2)
	{
		// Given ID_1 of a node, does node with ID_2 depend on it?
		
		// Get number of connections of parent node
		int connLength = this.nodes[ID_2].numConns();
		for (int i = 0; i < connLength; i++)
		{
			if (this.nodes[ID_2].conn[i] == ID_1)
			{
				return true;
			}
		}
		
		return false;
		
		
	}

	
}
