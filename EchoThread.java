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
                    System.out.println("About to add");
                    add(msg.getVal());
                    output.writeObject(msg);
                    if(!EchoServer.status){
                        System.out.println("Not adding correctly");
                    }
                }
                else if(Character.compare(command, 'V') == 0){
                    System.out.println("Printing data");
                    output.writeObject(new Message(view()));
                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");
                    delete(msg.getVal());
                    output.writeObject(msg);
                }
                else if(Character.compare(command, 'I') ==0){
                    System.out.println("Inserting");
                    insert(msg.getPos(), msg.getVal());
                    output.writeObject(msg);
                }
                else if(Character.compare(command, 'R') ==0){
                    System.out.println("Calling Rollback");
                    EchoServer.data.clear();
                    for(int i=0; i<EchoServer.dataDisk.size(); i++){
                        int j = EchoServer.dataDisk.get(i);
                        EchoServer.data.add(j);
                    }
                }
                else if(Character.compare(command, 'C') ==0){
                    if(commit()){
                        EchoServer.dataDisk.clear();
                        System.out.println("Commiting To Disk");
                        for(int i=0; i<EchoServer.data.size(); i++){
                            int j = EchoServer.data.get(i);
                            EchoServer.dataDisk.add(j);
                        }
                        output.writeObject(msg);
                    }
                    else if(!commit()){
                        System.out.println("Cannot commit at this time");
                        output.writeObject(msg);
                    }
                }
                
		// Write an ACK back to the sender
		//output.writeObject(new Message("Recieved message #" + count));
		               
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
                str.append("[").append(EchoServer.data.get(i)).append(", ");
            else if(i == EchoServer.data.size() - 1)
                str.append(EchoServer.data.get(i)).append("]");
            else
                str.append(EchoServer.data.get(i)).append(", ");
        }
        
        return str.toString();
    }
    
    private void insert(int pos, int value){
        EchoServer.data.add(pos, value);
    }
    
    private boolean commit(){ //Needs a little bit of working
        if(EchoServer.data.isEmpty() &&EchoServer.dataDisk.isEmpty() ){
            return false;
        }
        else if(!EchoServer.data.isEmpty() && EchoServer.dataDisk.isEmpty()){
            return true;
        }
        else if(EchoServer.data.isEmpty() && !EchoServer.dataDisk.isEmpty()){
            return false;
        }
        else if(!EchoServer.data.isEmpty() && !EchoServer.dataDisk.isEmpty()
                && EchoServer.data.size()== EchoServer.dataDisk.size()){
            for(int i = 0; i < EchoServer.data.size(); i++){
                if(EchoServer.data.get(i) != EchoServer.dataDisk.get(i))
                    return true;
            }
        }
//        else if (EchoServer.data.size()!= EchoServer.dataDisk.size())
//            return true;
//        else if(EchoServer.data.isEmpty())
//            return false;
        
        return false;
    }
    
    private boolean rollback(){
        return false;
    }

} //-- end class EchoThread
