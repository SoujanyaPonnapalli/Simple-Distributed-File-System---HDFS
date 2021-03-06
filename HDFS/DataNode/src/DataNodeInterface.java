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

public interface DataNodeInterface extends java.rmi.Remote
{
	 
	byte[] readBlock(int blockno) throws RemoteException;
	int writeBlock(byte[] msg, int blockno) throws RemoteException;
	int assignBlock(int blockno) throws RemoteException;
    int AreYouAlive() throws RemoteException;
}
