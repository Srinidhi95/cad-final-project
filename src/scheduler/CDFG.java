package scheduler;

public class CDFG {

	
	// ivars
	public Node [] nodes; // make private?
	private String title = "";
	
	
	private int numNodes = 0;
	private static int ID = 0;
	
	
	
	// constructor
	public CDFG(int numberOfNodes)
	{
		Node [] nodeArray = new Node[numberOfNodes]; // create an array of new nodes
		nodes = nodeArray; 
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
	
	public int addNode(int position)
	{
		if(position > numNodes)
		{
			System.out.println("Error: CDFG only has " + numNodes + " node(s).");
			return -1; // error
		}
		else
		{
			// add the node to the array at specified position
			return 1; // success
		}
		
	}
	

	
}
