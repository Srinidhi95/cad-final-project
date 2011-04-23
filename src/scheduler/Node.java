package scheduler;

public class Node {

	public int ID; // ID of each node
	public int state; // State that it's in
	public String op; // Operation that it performs
	public int conn; // TODO: connections should be an array of ints.
	
	
	// Constructor for Node
	public Node (int initID, int initState, String initOp )
	{
		ID = initID;
		state = initState;
		op = initOp;
	
	}
	
	public void setID (int newID)
	{
		ID = newID;
	}
	
	public void setState (int newState)
	{
		state = newState;
	}
	
	public void setOp (String newOP)
	{
		op = newOP;
	}
	
	public void addConn (int newConn)
	{
		// TODO: Add connection to existing array
	}
	
	public int getID()
	{
		return ID;
	}
	
	public int getState()
	{
		return state;
	}
	
	public String getOp()
	{
		return op;
	}
	
	// TODO: Write getter for connections
	
	
}
