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


class HeartBeats implements Runnable{
    List<String> dataNodeIPs;
    DataNodeInterface DataNode;

    public void run(){
        try{
            Thread.sleep(10000);        
        }
        catch(Exception e){
            System.out.println("Lol");
        }

        System.out.println("I hope the DataNodes are up!!!" + dataNodeIPs.get(0) + dataNodeIPs.get(1));    
        while(true){
            for(String ipaddr: dataNodeIPs){
                try{
                    System.out.println("Haha");
                    DataNode = (DataNodeInterface)   Naming.lookup("//"+ ipaddr + "/DataNode");
                    System.out.println("DataNode -- lookup succesful");

                    int status = 0;
                    status = DataNode.AreYouAlive();
                    if(status == 0){
                        System.out.println(ipaddr + " - i m down :(");
                    }else{
                        System.out.print("Up and Running -" + ipaddr);
                    }
                    

                }
                catch(Exception e){
                    System.out.println("Cant sleep");
                    e.printStackTrace();
                }
                
            }
        }
    }
}

public class NameNode extends UnicastRemoteObject implements NameNodeInterface 
{

    Map filetoBlock;
    HashMap blocknoToDataNodeIP;
    List<String> dataNodeIPs;
    int blockno ;
    List<String> fileNames;
    HeartBeats update;

    //Constructor
    public NameNode() throws RemoteException {
        filetoBlock = new HashMap();
        blocknoToDataNodeIP = new HashMap();
        fileNames = new ArrayList<String>();
        blockno = 0;
        dataNodeIPs = new ArrayList<String>();
        try{
        BufferedReader br = new BufferedReader(new FileReader("NameNode.config"));
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String[] hashVal = sCurrentLine.split(":");
            List<Integer>  tempHashVal;
            if(filetoBlock.containsKey(hashVal[0])){
                tempHashVal  = (List<Integer>)filetoBlock.get(hashVal[0]);
            }else{
                tempHashVal = new ArrayList<Integer>();
            }
            tempHashVal.add(Integer.parseInt(hashVal[1]));
            
            filetoBlock.put(hashVal[0], tempHashVal);
            if(tempHashVal.get(tempHashVal.size() - 1) > blockno){
                blockno = tempHashVal.get(tempHashVal.size() - 1);
            }
        }

        blockno += 1;

        br = new BufferedReader(new FileReader("setup.config"));
        while ((sCurrentLine = br.readLine()) != null) {
                dataNodeIPs.add(sCurrentLine);
        }
        }
        catch(Exception e){
            System.out.println("NameNode Constructor");
        }
        update = new HeartBeats();
        Thread t = new Thread(update);
        update.dataNodeIPs = dataNodeIPs;
        t.start();
        
        

    }

    public byte[] assignBlock(String fileName, int fileBlockId){

        System.out.println("Entered - AssignBlock");
      
        Random rand = new Random();
        int index1, index2;

        
        index1 = rand.nextInt(dataNodeIPs.size());
        index2 = rand.nextInt(dataNodeIPs.size());
        
        while(index1 == index2){ index2 = rand.nextInt(dataNodeIPs.size());}

        System.out.println("GotIpAddresses");
        
        try{

        System.out.println("Entered Try Block");
        DataNodeInterface DataNode1;
        DataNodeInterface DataNode2;
        
        DataNode1 = (DataNodeInterface) Naming.lookup( "//" + dataNodeIPs.get(index1) + "/DataNode");
        DataNode2 = (DataNodeInterface) Naming.lookup( "//" + dataNodeIPs.get(index2) + "/DataNode");
     
        System.out.println("Calling DataNode1s AssignBlock");
        DataNode1.assignBlock(blockno);
        int block1 = blockno;
        blockno = blockno + 1;
        System.out.println("Calling DataNode1s AssignBlock");
        DataNode2.assignBlock(blockno);
        int block2 = blockno;
        blockno = blockno + 1;  
        
        List<Integer> temp = new ArrayList<Integer>();
        temp.add(block1);temp.add(block2);
        filetoBlock.put(fileName+","+fileBlockId, temp);
        
        AssignBlockResponse.Builder AssignBlockResponseBuilder = AssignBlockResponse.newBuilder();

        AssignBlockResponseBuilder.setBlock1(block1);
        AssignBlockResponseBuilder.setBlock2(block2);   

        AssignBlockResponse AssignBlockResponseObject = AssignBlockResponseBuilder.build();
        
        return AssignBlockResponseObject.toByteArray();
        }

        catch(Exception e){
            System.out.println("assignBlockError"); 

        }
    
        byte[] temp = new byte[0];
        return temp;
    
    }

    public String getBlockLocations(int blockId){
        String ret = (String)blocknoToDataNodeIP.get(blockId);
        return ret;
    }

    public List<String> getList(){
        return fileNames;
    }

    public int closeWriteRequest(String fileName){
        fileNames.add(fileName);
        int filetoBlockId = 1;
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("NameNode.config", true)))) {
            while(filetoBlock.containsKey(fileName+","+filetoBlockId)){
                String temp = fileName + "," + filetoBlockId;
                List<Integer> tempList = (List<Integer>)filetoBlock.get(temp);
                for(Integer i: tempList){
                    out.println(temp+":"+ Integer.toString(i));    
                }
                filetoBlockId = filetoBlockId + 1;   
            }
        }
        catch (IOException e) {
            return 0;
        }
        return 1;
    }

    public List<Integer> readFile(String fileName){
        List<Integer> blocksList = new ArrayList<Integer>();
        int filetoBlockId = 1;
        while(filetoBlock.containsKey(fileName+","+filetoBlockId)){
            String temp = fileName + "," + filetoBlockId;
            List<Integer> tempBlock = (List<Integer>)filetoBlock.get(temp);
            blocksList.add(tempBlock.get(0));    
            filetoBlockId = filetoBlockId + 1;   
        }
        return blocksList;
    }


    public void update(List<Integer> myblocks, String ipaddr){
        for (Integer element : myblocks) {
            synchronized(blocknoToDataNodeIP) {
                System.out.println(Integer.toString(element) + ipaddr );
                blocknoToDataNodeIP.put(element.intValue(),ipaddr);
            }
        }
    }

} 