package scheduler;

public class Node {

	
	// TODO: Should variables all be private?
	
	private int ID; // ID of the node
	public int state; // State that the node is in
	public String op; // Operation that it performs
	public int [] conn; // Array of connections (each entry is the ID of another node)
	
	// TODO: Add mobility variable and methods
	
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
	
	
	// Second constructor with no connList (set to -1 by default)
	public Node (int initID, int initState, String initOp)
	{
		state = initState;
		op = initOp;
		ID = initID;
		
		int[] connList = new int[1];
		connList[0] = -1;
		conn = connList;
		
	}
	
	public Node copy()
	{
		
		int [] newList = new int [conn.length];
		
		for (int i = 0; i < conn.length; i++)
		{
			newList[i] = this.conn[i];
		}
		
		
		Node outNode = new Node (this.getID(), this.getState(), this.getOp(), newList);
		return outNode;
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
	
	
	public int numConns()
	{
		return this.conn.length;
		
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
			System.out.println("No Dependencies.");
		}
		else
		{
			list = "Dep.: \t\t(";
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
			System.out.println("# of Dependencies = " + conn.length);	
		}
		
	}

	
	
	public String printConnToFile() // for diagnostic purposes
	{
		String list = "";
		
		if (conn[0] == -1)
		{
			list = "No Dependencies.";
			return list;
		}
		else
		{
			list = "Dep.\t=\t(";
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
			list += "\n# of Dependencies = " + conn.length;
			return list;
			
		}
		
	}
	
}
