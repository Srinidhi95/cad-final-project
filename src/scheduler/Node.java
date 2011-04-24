package scheduler;

public class Node {

	
	// TODO: Should variables all be private?
	
	private int ID; // ID of each node
	public int state; // State that it's in
	public String op; // Operation that it performs
	public int [] conn; // Array of connections (each entry is the ID of another node)
	
	public static int numOfNodes = 0;
	
	// TODO: Some kind of reset function for numOfNodes?
	
	
	// Constructor for Node
	public Node (int initState, String initOp, int [] connList )
	{
		//ID = initID;
		state = initState;
		op = initOp;
		conn = connList;
		ID = numOfNodes++;
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
	
	public int [] getConn()
	{
		return conn;
	}
	
	public void printConn() // for diagnostic purposes
	{
		System.out.println("Number of connections = " + conn.length);
		
		for (int i = 0; i < conn.length; i++)
		{
			System.out.println(conn[i]);
		}
	}

	
}
