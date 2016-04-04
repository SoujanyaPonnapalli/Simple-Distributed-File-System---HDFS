import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;
import java.net.*;
import com.distributed.systems.ResultProtos.*;
import java.io.FileOutputStream;


public interface NameNodeInterface extends java.rmi.Remote
{
	 
	byte[] assignBlock(String fileName, int fileBlockId) throws RemoteException;
	String getBlockLocations(int blockId) throws RemoteException;
	List<String> getList() throws RemoteException;
	int closeWriteRequest(String fileName) throws RemoteException;
	void update(List<Integer> myblocks, String ipaddr) throws RemoteException;
	List<Integer> readFile(String fileName) throws RemoteException;
	//void hearGossip(String msg) throws RemoteException;  
}
