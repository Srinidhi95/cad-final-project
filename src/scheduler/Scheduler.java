package scheduler;
//import java.util.*;
import java.io.*;


public class Scheduler {
	public static void main (String[] args){
		System.out.println("CAD Final Project");
		
		System.out.println("First argument = " + args[0]);
		
		String op = "add";
		Node newNode = new Node(0,0,op);
		
		System.out.println("ID = " + newNode.getID()); 
		newNode.setID(2);		
		System.out.println("ID = " + newNode.getID());
		
		
		//readFile(args[0]);
	}
	
	
	// TODO: Parse file according to CDFG definition
	// TODO: Create a node class to store different nodes
	// TODO: Create a CDFG class that holds different nodes
	
	
	public static void readFile(String filename){
		
		System.out.println("In Readfile: " + filename);
	try{
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input); 
			String line = "";
			int count = 0;
			// read a line
			try {
				line = bufRead.readLine();
			} catch (IOException e) {
				System.out.println("Cannot Read File.");
				e.printStackTrace();
			}
			count++;
			
			// keep reading lines until end of file is reached
			while (line != null)
			{
				System.out.println("Line Read: " + line);
				System.out.println("The count is: " + count);
				line = bufRead.readLine();
				count++;
			}
			
	} catch (FileNotFoundException e){
			System.out.println("File Not Found!");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
	}
	
	

}
