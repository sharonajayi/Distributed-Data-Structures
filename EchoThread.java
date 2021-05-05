package server;

import java.io.IOException;
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
    @Override
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
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        output.writeObject(new Message("Finished", false));
                }
                else if(Character.compare(command, 'V') == 0){
                    System.out.println("Printing data");
                    output.writeObject(new Message(view(), false));

                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");

                    delete(msg.getVal(), true);
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

                else{
                    if(msg.theMessage.equalsIgnoreCase("EXIT"))
                        output.writeObject(new Message("Exiting Server", false));
                    else
                        output.writeObject(new Message("Recieved message: ", false));
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
        Message waiting = null;
        try{
	    System.out.println("** New connection from " + sock.getInetAddress() + ":" 
                    + socket.getPort() + " **");

	    // set up I/O streams with the client
	    final ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
	    final ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());

	    // Loop to read messages
	    Message tempMsg = msg;
	    do{
		// read and print message
		msg = (Message)input.readObject();
		System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] " + msg.theMessage);
                char command = msg.theMessage.charAt(0);
                
                
                if(Character.compare(command, 'A') == 0){
                    System.out.println("About to add");
                    add(msg.getVal(), false);
                    output.writeObject(msg);
                    waiting = new Message("Finished", false);
                }
                else if(Character.compare(command, 'C') == 0){
                    System.out.println("Commiting data");
                    output.writeObject(new Message(view(), false));
                    waiting = new Message("Finished",false);
                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");
                    delete(msg.getVal(), false);
                    output.writeObject(msg);
                    waiting = new Message("Finished",false);
                }
                else{
                    if(msg.theMessage.equalsIgnoreCase("EXIT"))
                        output.writeObject(new Message("Exiting Server",false));
                    else
                        output.writeObject(new Message("Recieved message: ", false));
                }
                
		// Write an ACK back to the sender
		
		               
	    }while(!msg.theMessage.toUpperCase().equals("FINISHED"));

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
        System.out.println(value + " was added");
        if(!EchoServer.data.contains(value)){
            System.out.println(value + " was not added in the data");
        }
        
        //sanity check
//        if(AllServers.updates.containsKey(port))
//            AllServers.updates.replace(port, EchoServer.data);
//        else System.out.println("updates does not contain this port");
       if(check){ 
            //System.out.println("Update the other servers");
            updateConintueslly(port, value, -1, 'A');
       }
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
    private boolean updateConintueslly(int port, int value, int postion, char command){
     //   System.out.println("Staring continus update");
//        if(EchoServer.allServers.size() == 1)
//            System.out.println("No updates needed");
//        else{
            for (Integer ports : EchoServer.allServers) {
                if(ports == port) {
                } 
                 else{
                    //Remove the data from updates and add the new value
                    if(command== 'A'){
                        update(new Message("A", value, true), ports);
                    }
                    else if(command == 'D'){
                        update(new Message("D", value, true), ports);
                    } 
                    else if(command == 'I' && postion != -1)
                        update(new Message(postion, value,true), ports);
                    else
                        System.out.println("Command to update was invalid");
                    
                }
            }
        //}
        
        return true;
    }
    
    private void update(Message msg, int portTO){
        //System.out.println("Starting the update");
        boolean status = false; Message tmpMsg = null;
        try{
            final Socket sock = new Socket("localhost", portTO);
            System.out.println("Connected to " + sock.getInetAddress() + " on port " + portTO + 
                " LocalPort number is: " +sock.getLocalPort());
            final ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
            final ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
            System.out.println("About to go in to the do-while loop");
            do {
                System.out.println("***In the do-while loop**");
                char command = msg.theMessage.charAt(0);
                System.out.println("Command is: " + command);
                if(Character.compare(command, 'A') == 0){
                    System.out.println("About to add");
                    output.writeObject(new Message(msg.theMessage, false));
                    status = true;
                }
                else if(Character.compare(command, 'C') == 0){
                    System.out.println("Commiting data");
                    output.writeObject(new Message(msg.theMessage, false));
                    status = true;
                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");
                    output.writeObject(new Message(msg.theMessage, false));
                    //delete(msg.getVal(), false);
                    status = true;
                }
                else{
                    System.out.println("Message recieved is :" + msg.theMessage);
                    output.writeObject(new Message("Nonsense", false));
                    status = false;
                }
                
                tmpMsg = (Message)input.readObject();
            }
            while(!tmpMsg.theMessage.toUpperCase().equals("FINISED"));
            
            System.out.println("Close connections");
            input.close();
            output.close();
            sock.close();
            
        }
        catch(Exception e){
            System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
        }
        
        
        
        

    

} //-- end class EchoThread
