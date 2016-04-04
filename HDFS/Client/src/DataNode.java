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
import com.google.protobuf.ByteString;

class PeriodicUpdates implements Runnable{
    List<Integer> blocks;
    public void run(){
        System.out.println("\tPeriodic Updates: Sleeping for a while, Hope the Name Node will be up");
        try{
            Thread.sleep(10000);
        }catch(Exception e){
            System.out.println("[ERROR]: PeriodicUpdates: Couldn't sleep !!");
        }
        System.out.println("\tPeriodicUpdates: Yo Woke Up Succesfully!!");
        // while(true){
        //     try{
        //         Thread.sleep(10000);
        //         BufferedReader br = new BufferedReader(new FileReader("setup.config"));
        //         String sCurrentLine;
        //         if ((sCurrentLine = br.readLine()) != null) {
        //             NameNodeInterface nameNode = (NameNodeInterface) Naming.lookup( "//" + sCurrentLine + "/NameNode");
        //             InetAddress IP=InetAddress.getLocalHost();
        //             nameNode.update(blocks,IP.getHostAddress().toString());            
        //         }
        //     }catch(Exception e){
        //         System.out.println("[ERROR]: PeriodicUpdates: Error while lookingUp and calling Update of nameNode");
        //         e.printStackTrace();
        //     }
        // }
    }
}


public class DataNode extends UnicastRemoteObject implements DataNodeInterface 
{
     HashMap MapBlockToStorage; //blockno to filename mapping...
    List<Integer> blocks;
    PeriodicUpdates update;
    int fileCnt;
    String myIp;
    //Constructor
    public DataNode(String ipAddress) throws RemoteException {
        MapBlockToStorage = new HashMap();
        blocks = new ArrayList<Integer>();
        myIp = ipAddress;
        try{
            BufferedReader br = new BufferedReader(new FileReader("DataNode.config"));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] lineArray = sCurrentLine.split(":");
                blocks.add(Integer.parseInt(lineArray[0]));
                MapBlockToStorage.put(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[1]));
                
            }    
            System.out.println("\tDatanode Constructor: Temporary DataStructures Loaded");
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("[ERROR]: DataNode: In Constructor");
        }
        fileCnt = 1;
        update = new PeriodicUpdates();
        Thread t = new Thread(update);
        update.blocks = blocks;
        System.out.println("\tThis Datanode calls PeriodicUpdates");
        t.start();
        System.out.println("\tDatanode returns from PeriodicUpdates");
    } 

    public int AreYouAlive(){
        return 1;
    }

    public int update(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("setup.config"));
            String sCurrentLine;
            if ((sCurrentLine = br.readLine()) != null) {
                NameNodeInterface nameNode = (NameNodeInterface) Naming.lookup( "//" + sCurrentLine + "/NameNode");
                InetAddress IP=InetAddress.getLocalHost();
                System.out.println("sendding -" + Integer.toString(blocks.size()));
                nameNode.update(blocks, myIp);            
            }   
            return 1;
        }
        catch(Exception e){
            System.out.println("Update - Crashed");
            e.printStackTrace();
            return 0;
        }
        
    }

    public byte[] readBlock(int blockno){
        String fileName = (String)MapBlockToStorage.get(blockno);
        Path path = Paths.get(fileName);
        byte[] data = new byte[0];
        try{
            data = Files.readAllBytes(path);
        }
        catch(Exception e){
            System.out.println("[ERROR]: readBlock: readBlock error");
        }
        ReadResponse.Builder responseBuilder = ReadResponse.newBuilder();
        responseBuilder.setData(ByteString.copyFrom(data));
        if(data!= null){
            responseBuilder.setStatus(true);    
        }
        else{
            responseBuilder.setStatus(false);
        }
        ReadResponse obj = responseBuilder.build();
        return obj.toByteArray();
    }

    public int writeBlock(byte[] msg,int blockno){
        String fileName = (String)MapBlockToStorage.get(blockno);
        try{
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(msg);
            fos.close();
        }
        catch(Exception e){
            System.out.println("[ERROR]: writeBlock");
            return 0;
        }
        return 1;
    }

    public int assignBlock(int blockno){
        System.out.println("Entered assignBlock" + Integer.toString(blockno));
        try{
            System.out.println("haha try ");
            blocks.add(blockno);
            String fileName = Integer.toString(fileCnt);
            System.out.println(fileCnt);
            MapBlockToStorage.put(blockno,fileName);   
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("DataNode.config", true)))) {
                out.println(Integer.toString(blockno) + ":" + Integer.toString(fileCnt));             
            }     
            //FileWriter fw = new FileWriter("DataNode.config");
            //BufferedWriter bw = new BufferedWriter(fw);
            //bw.write(Integer.toString(blockno) + ":" + Integer.toString(fileCnt) + "\n");    
            //bw.flush();
            //bw.close();
            //update.blocks = blocks;
            fileCnt = fileCnt + 1;
            update();
            System.out.println("AssignBlock Over without Errors");
        }
        catch(Exception e){
            System.out.println("[ERROR]: assignBlock:");
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

} 
