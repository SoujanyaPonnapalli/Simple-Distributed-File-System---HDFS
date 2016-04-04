import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;


public class serverCreation {
	public static void main(String args[]){
		try
		{
			//Created server instances... Designated their types
			System.setProperty("java.rmi.server.hostname","10.2.59.16");

			NameNode obj = new NameNode(); 
			Naming.rebind("NameNode", obj);  
		
		}
		catch (Exception e)
		{
			System.out.println("Error in creating Name - serverCreation.java" + e.getMessage());
			e.printStackTrace();
		}

	}
}
