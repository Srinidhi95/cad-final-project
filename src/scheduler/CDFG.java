package scheduler;

public class CDFG {

	
	// ivars
	private Node [] nodes; // make private?
	private String title = "";
	
	
	private int numNodes = 0;
	private static int ID = 0;
	private int states = 0;
	
	
	
	// constructor
	public CDFG(int numberOfNodes)
	{
		nodes = new Node[numberOfNodes]; // create an array of new nodes
		//nodes = nodeArray; 
		numNodes = numberOfNodes;
		ID++;
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
			System.out.println("Added Node at position: " + position);
			System.out.println("Node State was: " + nodes[position].getState());
			System.out.println("Node OP was: " + nodes[position].getOp());
			return 1; // success
		}
		
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
			

		
	}
	

	
}
