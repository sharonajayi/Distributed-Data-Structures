package server;

import java.lang.Thread;            // We will extend Java's base Thread class
import java.net.Socket;
import java.io.ObjectInputStream;   // For reading Java objects off of the wire
import java.io.ObjectOutputStream;  // For writing Java objects to the wire
import java.util.LinkedList;


/**
 * Marian Zaki (COSC 2354)
 * A simple server thread.  This class just echoes the messages sent
 * over the socket until the socket is closed.
 *
 */
public class EchoThread extends Thread
{
    private final Socket socket;                   // The socket that we'll be talking over
    
    //private LinkedList<Integer> data;
    /**
     * Constructor that sets up the socket we'll chat over
     *
     * @param socket The socket passed in from the server
     *
     */
    public EchoThread(Socket socket)
    {
	this.socket = socket;
    }

    /**
     * run() is basically the main method of a thread.  This thread
     * simply reads Message objects off of the socket.
     *
     */
    public void run()
    {
	try{
	    // Print incoming message
	    System.out.println("** New connection from " + socket.getInetAddress() + ":" 
                    + socket.getPort() + " **");

	    // set up I/O streams with the client
	    final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
	    final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

	    // Loop to read messages
	    Message msg = null;
	    int count = 0;
	    do{
		// read and print message
		msg = (Message)input.readObject();
		System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] " + msg.theMessage);
                char command = msg.theMessage.charAt(0);
                count++;
                if(Character.compare(command, 'A') == 0){
                    add(msg.getVal());
                    output.writeObject(msg.theMessage);
                    if(!EchoServer.status){
                        System.out.println("Not adding correctly");
                    }
                }
                else if(Character.compare(command, 'V') == 0){
                    System.out.println("Printing data");
                    output.writeChars(view());
                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");
                    delete(msg.getVal());
                    output.writeObject(msg.theMessage);
                }
                
		// Write an ACK back to the sender
		
		               
	    }while(!msg.theMessage.toUpperCase().equals("EXIT"));

	    // Close and cleanup
	    System.out.println("** Closing connection with " + socket.getInetAddress() + ":" + socket.getPort() + " **");
	    socket.close();

	}
	catch(Exception e){
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
	}

    }  //-- end run()
    
    
    private void add(int value){
        
        EchoServer.data.add(value);
        if(EchoServer.data.contains(value)){
            EchoServer.status = true;
        }
//        else
//            System.out.println("Value: " + value + " was not added" );
    }
    
    private void delete(int value){
        EchoServer.data.remove(value);
    }
    
    private String view(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < EchoServer.data.size(); i++){
            if(i==0)
                str.append("[").append(EchoServer.data.get(i)).append(" ");
            else if(i == EchoServer.data.size() - 1)
                str.append(EchoServer.data.get(i)).append("]");
            else
                str.append(EchoServer.data.get(i)).append(", ");
        }
        
        return str.toString();
    }

} //-- end class EchoThread