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

public class Client{
    public static void main(String args[]){
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("setup.config"));
            String sCurrentLine;
            NameNodeInterface nameNode;
            if ((sCurrentLine = br.readLine()) != null) {
                nameNode = (NameNodeInterface) Naming.lookup( "//" + sCurrentLine + "/NameNode");            
            }else{
                return;
            }
            
            while(true){
                System.out.print(">>");
                Scanner in = new Scanner(System.in);
                String command = in.nextLine();
                
                String[] cmds = command.split(" ");
                if(cmds.length >= 3){
                    break;
                }
                else if(cmds.length == 1){
                    if(command.equals("list")){
                        List<String> files = new ArrayList<String>();
                        files = nameNode.getList();
                        for (String file: files){
                            System.out.println(file);
                        }    
                    }
                    else if(command == "exit"){
                        break;
                    }
                    else{
                        System.out.println("Invalid Command");
                    }

                }else if(cmds.length == 2){
                    System.out.println(cmds[0]);
                    if(cmds[0].equals("get")){
                        List<Integer> list_of_blocks = new ArrayList<Integer>();
                        list_of_blocks = nameNode.readFile(cmds[1]);
                        for(Integer i: list_of_blocks){
                            String ipaddr = nameNode.getBlockLocations(i);
                            try{
                                DataNodeInterface dataNode = (DataNodeInterface) Naming.lookup( "//" + ipaddr + "/DataNode");
                                byte[] tempResponse = new byte[0];
                                tempResponse = dataNode.readBlock(i);
                                ReadResponse readResponse = ReadResponse.parseFrom(tempResponse);
                                if(readResponse.getStatus() == true){
                                    System.out.println(readResponse.getData());
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                System.out.println("Read Client - Error");
                            }                        


                        }
                     }
                     else if(cmds[0].equals("put")){
                        System.out.println("Put Command getting Executed");

                        br = new BufferedReader(new FileReader(cmds[1]));
                        int count = 1;
                        Path path = Paths.get(cmds[1]);
                        byte[] data = Files.readAllBytes(path);
                        int totalSize = data.length;
                        int blockSize = 5;
                        int pointer = 0;
                        System.out.println("totalSize" + Integer.toString(totalSize));

                        while (pointer <= totalSize) {
                            
                            byte[] tempResponse = nameNode.assignBlock(cmds[1],count);
                            AssignBlockResponse abResponse = AssignBlockResponse.parseFrom(tempResponse);
                            int block1 = abResponse.getBlock1();
                            int block2 = abResponse.getBlock2();
                            String ipaddr1 = nameNode.getBlockLocations(block1);
                            String ipaddr2 = nameNode.getBlockLocations(block2);

                            System.out.println("block1" + " ->" + Integer.toString(block1) + " " + ipaddr1);
                            System.out.println("block2" + " ->" + Integer.toString(block2) + " " + ipaddr2);

                            byte[] blockData = new byte[blockSize];
                            int size = blockSize;
                            if(totalSize - pointer < blockSize){
                                 size = totalSize - pointer;
                            }
                            System.arraycopy(data,pointer,blockData,0,size);


                            try{
                                 DataNodeInterface dataNode1 = (DataNodeInterface) Naming.lookup( "//" + ipaddr1 + "/DataNode");
                                 dataNode1.writeBlock(blockData,block1);
                                    
                                 DataNodeInterface dataNode2 = (DataNodeInterface) Naming.lookup( "//" + ipaddr2 + "/DataNode");
                                 dataNode2.writeBlock(blockData,block2);
                            }
                            
                            catch(Exception e){
                                e.printStackTrace();
                                System.out.println("Write Client - Error");
                            }
                            count++;
                            pointer = pointer + blockSize;
                        }

                        nameNode.closeWriteRequest(cmds[1]);
                     }
                }

            }

        }
        catch (Exception e)
        {
            System.out.println("Err in Main: " + e.getMessage());
            e.printStackTrace();
        }
    }    
}
