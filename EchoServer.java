package server_sample;

import java.net.ServerSocket;  // The server uses this to bind to a port
import java.net.Socket;        // Incoming connections are represented as sockets
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
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
    /** The server will listen on this port for client connections */
    public static final int SERVER_PORT = 8754;
    
    /*disk hashtable that contains Integer and linked list of integers*/
    private Hashtable<Integer,LinkedList<Integer>> disk;
    
    /*time stamp variable*/
    Date timeStamp;
    
    /*availablitity check*/
    protected boolean available;
    
    /*array list that contains all the servers*/
    private ArrayList<EchoServer> allServers;
    
    /*array list that contains memory logs*/
    private ArrayList<String[]> memoryLog;
    
    /*linked list of integers*/
    private LinkedList<Integer> data;
    
    /**replicate method.
     * push update to other servers
     */
    private void replicate(){
        
    }
    
    /**check method.
     * checks if each server has the latest update
     * (i.e compares the linked list of each server)
     * @return true if the linked list of each server is the same
     */
    private boolean check(){
        return true;
        
    }
    
    /**updateServer method.
     * updates EchoClient command to  server and use replicate() to update the other servers
     */
    public void updateServer(){
        replicate();
    }
    
    /**
     * Main routine.  Just a dumb loop that keeps accepting new
     * client connections.
     *
     */
    public static void main(String[] args)
    {
	try{
	    // This is basically just listens for new client connections
	    final ServerSocket serverSock = new ServerSocket(SERVER_PORT);
	    System.out.println("Server started successfully .... ");
	    // A simple infinite loop to accept connections
	    Socket sock = null;
	    EchoThread thread = null;
	    while(true){
                System.out.println("Waiting for a new connection .... ");
		sock = serverSock.accept();     // Accept an incoming connection
		thread = new EchoThread(sock);  // Create a thread to handle this connection
		thread.start();                 // Fork the thread
	    }                                   // Loop to work on new connections while this
                                                // the accept()ed connection is handled

	}
	catch(Exception e){
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
	}

    }  //-- end main(String[])

} //-- End class EchoServer
