package server;

import java.net.ServerSocket;  // The server uses this to bind to a port
import java.net.Socket;        // Incoming connections are represented as sockets
import java.util.ArrayList;
import java.util.HashMap;

import java.util.LinkedList;

/**
 * Marian Zaki (COSC 2354)
 * A simple server class.  Accepts client connections and forks
 * EchoThreads to handle the bulk of the work.
 *
 * 
 */
public class EchoServer
{
   

    protected static boolean status = false;
    
    /*availablitity check*/
    protected boolean[] available; //might not need this
    
     /** The server will listen on this port for client connections */
    protected static ArrayList<Integer> allServers = new ArrayList<Integer>(){
        {
            add(7951);
            add(6536);
        }
    };
    
    /*array list that contains memory logs*/
    protected static ArrayList<HashMap<String, String>> memoryLog = new ArrayList<>();
    
    /*linked list of integers*/

    protected static LinkedList<Integer> data = new LinkedList<>();
    
    protected static LinkedList<Integer> dataDisk = new LinkedList<>();

//    
//    /**check method.
//     * checks if each server has the latest update
//     * (i.e compares the linked list of each server)
//     * @return true if the linked list of each server is the same
//     */
//    private boolean check(){
//        return true;
//        
//    }
  
    /**
     * Main routine.Just a dumb loop that keeps accepting new
     * client connections.
     *
     * @param args
     */
    public static void main(String[] args)
    {
	try{
	    // This is basically just listens for new client connections
            if(args.length != 1)
            {
                System.err.println("Not enough arguments.\n");
                System.err.println("Usage:  java EchoServer <Server Port Number>\n");
                System.out.println("Port number sould be 7951, 6536, or 9113");
                System.exit(-1);
            }
            int serverPort = Integer.parseInt(args[0]);
	    
            if(!allServers.contains(serverPort)){
                System.out.println("Invaild port, use one of the following 7951, 6536, or 9113");
                System.exit(-1);
            }
                
            final ServerSocket serverSock = new ServerSocket(serverPort);
	    // A simple infinite loop to accept connections
	    Socket sock = null;
	    EchoThread thread = null;
            //LinkedList<Integer> info = this.
	    while(true){
                System.out.println("Waiting for a new connection .... ");
		sock = serverSock.accept();     // Accept an incoming connection (blocking method)
		thread = new EchoThread(sock, serverPort);  // Create a thread to handle this connection
		thread.start();                 // Fork the thread
	    }                                   // Loop to work on new connections while this
                                                // the accept()ed connection is handled

	}
	catch(Exception e){
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
	}
        

       

    }  //-- end main(String[])
    

    //Makes sure that all the servers are updated
//    protected static void intialUpdate(){
//        if(allServers.size() == 1)
//            System.out.println("No updates needed");
//        else{
//            
//            for(int i: original){
//                data.add(i);
//            }
//            
//            System.out.println("Inital Updates completed");
//        }
//        
//        
//    }
    
} //-- End class EchoServer
