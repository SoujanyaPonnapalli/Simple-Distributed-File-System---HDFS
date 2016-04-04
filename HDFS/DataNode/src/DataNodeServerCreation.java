import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;


public class DataNodeServerCreation {
	public static void main(String args[]){
		try
		{
			//Created server instances... Designated their types
			System.out.println(args[0] + "--- args[0]");
			System.setProperty("java.rmi.server.hostname",args[0]);
			DataNode obj = new DataNode(args[0]); 
			Naming.rebind("DataNode", obj);  
			System.out.println("Bind Succesful - DataNode Up and Running");
		
		}
		catch (Exception e)
		{
			System.out.println("Error: Couldn't create a DatNode - DataNodeServerCreation.java" + e.getMessage());
			e.printStackTrace();
		}

	}
}
