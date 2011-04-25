package scheduler;

public class Node {

	
	// TODO: Should variables all be private?
	
	private int ID; // ID of the node
	public int state; // State that the node is in
	public String op; // Operation that it performs
	public int [] conn; // Array of connections (each entry is the ID of another node)
	
	public static int numOfNodes = 0;
	
	// TODO: Some kind of reset function for numOfNodes?
	
	
	// Constructor for Node
	public Node (int initID, int initState, String initOp, int [] connList )
	{
		//ID = initID;
		state = initState;
		op = initOp;
		conn = connList;
		ID = initID;
		//numOfNodes++;
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
	
	
	// Return true if node with ID depends on the calling node
	public boolean dependsOn(int ID)
	{
		for (int i = 0; i < this.conn.length; i++)
		{
			if (conn[i] == ID)
			{
				return true;
			}
		}
		
		return false;
		
	}
	
	public void printConn() // for diagnostic purposes
	{
		String list = "";
		
		if (conn[0] == -1)
		{
			System.out.println("No Connections.");
		}
		else
		{
			list = "Conns: \t\t(";
			for (int i = 0; i < conn.length; i++)
			{
				if (i == (conn.length - 1))
				{
					list += conn[i] + ")";
				}
				else{
					list += conn[i] + ",";	
				}
	
			}
			System.out.println(list);
			System.out.println("Number of connections = " + conn.length);	
		}
		
	}

	
}
