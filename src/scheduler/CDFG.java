package scheduler;

public class CDFG {

	
	// ivars
	private Node [] nodes; // make private?
	private String title = "";
	
	private int numALU = 0;
	private int numMUL = 0;
	private int numMIN = 0;
	private int numMAX = 0;
	private int numABS = 0;
	
	private int numCLK = 0;
	
	private int numNodes = 0;
	private static int ID = 0;
	//private int states = 0;
	
	
	
	// constructor
	public CDFG(int numberOfNodes)
	{
		nodes = new Node[numberOfNodes]; // create an array of new nodes
		//nodes = nodeArray; 
		numNodes = numberOfNodes;
		ID++; // needed?
	}
	
	
	public void setCLK(int clk)
	{
		numCLK = clk;
	}
	
	public int getCLK()
	{
		return numCLK;
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
	
	public int addNode(Node newNode, int position)
	{
		if(position > numNodes)
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
		System.out.println("Number of ALUs \t=\t " + this.getALU());
		System.out.println("Number of Mult \t=\t " + this.getMUL());
		System.out.println("Number of MIN \t=\t " + this.getMIN());
		System.out.println("Number of MAX \t=\t " + this.getMIN());
		System.out.println("Number of ABS \t=\t " + this.getABS());
		System.out.println("Clock Cycles \t=\t " + this.getCLK());
	
	}
	
	public void printCDFG()
	{
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
	

	
}
