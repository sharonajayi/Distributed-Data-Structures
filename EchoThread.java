package server;

import java.io.IOException;
import java.lang.Thread;            // We will extend Java's base Thread class
import java.net.Socket;
import java.io.ObjectInputStream;   // For reading Java objects off of the wire
import java.io.ObjectOutputStream;  // For writing Java objects to the wire
import java.net.ServerSocket;
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
    
    private final int port;                     //The port that we are connecnted to
    /**
     * Constructor that sets up the socket we'll chat over
     *
     * @param socket The socket passed in from the server
     * @param port The port the server is connected to
     *
     */
    public EchoThread(Socket socket, int port)
    {
	this.socket = socket;
        this.port = port;
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
                
                
                if(Character.compare(command, 'A') == 0){
                    System.out.println("About to add");
                    add(msg.getVal(), true);
                    output.writeObject(msg);
//                    if(!EchoServer.status){
//                        System.out.println("Not adding correctly");
//                    }
                }
                else if(Character.compare(command, 'V') == 0){
                    System.out.println("Printing data");
                    output.writeObject(new Message(view()));
                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");
                    delete(msg.getVal(), true);
                    output.writeObject(msg);
                }
                else{
                    if(msg.theMessage.equalsIgnoreCase("EXIT"))
                        output.writeObject(new Message("Exiting Server"));
                    else
                        output.writeObject(new Message("Recieved message: "));
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
    
    public void run(Message msg, Socket sock){
        // Print incoming message
        try{
	    System.out.println("** New connection from " + sock.getInetAddress() + ":" 
                    + socket.getPort() + " **");

	    // set up I/O streams with the client
	    final ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
	    final ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());

	    // Loop to read messages
	    Message tempMsg = msg;
	    int count = 0;
	    do{
		// read and print message
		msg = (Message)input.readObject();
		System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] " + msg.theMessage);
                char command = msg.theMessage.charAt(0);
                
                
                if(Character.compare(command, 'A') == 0){
                    System.out.println("About to add");
                    add(msg.getVal(), false);
                    output.writeObject(msg);
//                    if(!EchoServer.status){
//                        System.out.println("Not adding correctly");
//                    }
                }
                else if(Character.compare(command, 'V') == 0){
                    System.out.println("Printing data");
                    output.writeObject(new Message(view()));
                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");
                    delete(msg.getVal(), false);
                    output.writeObject(msg);
                }
                else{
                    if(msg.theMessage.equalsIgnoreCase("EXIT"))
                        output.writeObject(new Message("Exiting Server"));
                    else
                        output.writeObject(new Message("Recieved message: "));
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
    }
	


    
    
    
    private void add(int value, boolean check){
        
        EchoServer.data.add(value);
        if(!EchoServer.data.contains(value)){
            System.out.println(value + " was not added in the data");
        }
        
        //sanity check
//        if(AllServers.updates.containsKey(port))
//            AllServers.updates.replace(port, EchoServer.data);
//        else System.out.println("updates does not contain this port");
       if(check) 
            updateConintueslly(port, value, -1, 'A');
    }
    
    private void delete(int value, boolean check){
        EchoServer.data.remove(value);
        if(EchoServer.data.contains(value)){
            System.out.println(value + " was not remove from data");
        }
        
        
        //sanity check
//        if(AllServers.updates.containsKey(port))
//            AllServers.updates.replace(port, EchoServer.data);
//        else System.out.println("updates does not contain this port");
        
        if(check)
            updateConintueslly(port, value, -1, 'D');
    }
    
    private String view(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < EchoServer.data.size(); i++){
            if(EchoServer.data.size() == 1){
                str.append("[").append(EchoServer.data.get(i)).append("]");
            }
            else if(i==0)
                str.append("[").append(EchoServer.data.get(i)).append(", ");
            else if(i == EchoServer.data.size() - 1)
                str.append(EchoServer.data.get(i)).append("]");
            else
                str.append(EchoServer.data.get(i)).append(", ");
        }
        
        return str.toString();
    }
    
    private boolean updateConintueslly(int port, int value, int postion, char command){
        if(AllServers.allServers.size() == 1)
            System.out.println("No updates needed");
        else{
            for (Integer ports : AllServers.allServers) {
                if(ports == port) {
                } 
                 else{
                    //Remove the data from updates and add the new value
                    if(command== 'A'){
                        update(new Message("A", value), ports);
                    }
                    else if(command == 'D'){
                        update(new Message("D", value), ports);
                    } 
                    else if(command == 'I' && postion >= 0)
                        update(new Message(postion, value), ports);
                    else
                        System.out.println("Command to update was invalid");
                    
                }
            }
        }
        
        return true;
    }
    
    private void update(Message msg, int portTO){
        Message response = null;
        try{
            final ServerSocket servSock = new ServerSocket(portTO);
            final Socket sock = servSock.accept();
	    System.out.println("Connected to " + servSock.getInetAddress().getHostAddress() + " on port " + portTO + 
                    " LocalPort number is: " +sock.getLocalPort());
	    
	    // Set up I/O streams with the server
	    final ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
	    final ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
            
            
        }
        catch(Exception e){
            System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
        }
        
        System.out.println("Close connection");
    }

} //-- end class EchoThread