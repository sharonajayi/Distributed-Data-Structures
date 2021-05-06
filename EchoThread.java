package server;

import java.io.EOFException;
import java.lang.Thread;            // We will extend Java's base Thread class
import java.net.Socket;
import java.io.ObjectInputStream;   // For reading Java objects off of the wire
import java.io.ObjectOutputStream;  // For writing Java objects to the wire
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



/**
 * Nadine Ineza and Sharon Ajayi (COSC 2354)
 * A simple server thread.  This class performs the commands the client 
 * (Echo Client) sends to the server (EchoSever)
 *
 */
public class EchoThread extends Thread
{
    private final Socket socket;                   // The socket that we'll be talking over
    

    private final int port;                     //The port that we are connecnted to
    
      
    Date time;/*time stamp variable*/

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
     * simply reads Message sent by socket and executes the command if it can or
     * prints out the message received to the client.
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
	    //int count = 0;
	    do{
		// read and print message
                msg = (Message)input.readObject();
                //Print where the socket is connected to and the message perfomed
		System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] " + msg.theMessage);
                
                //Get the command from the server
                char command = msg.theMessage.charAt(0);
                
                if(Character.compare(command, 'A') == 0){ //executes add
                    //System.out.println("About to add");
                    add(msg.getVal(), msg.check);
                    createTimeStamp(msg.theMessage); //create a timestamp for memory log
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        msg.theMessage = "EXIT"; msg.check = false;
                }
                else if(Character.compare(command, 'V') == 0){
                    //System.out.println("Printing data");
                    output.writeObject(new Message(view(), false));
                    createTimeStamp(msg.theMessage); //create a timestamp for memory log

                    
                }
                else if(Character.compare(command, 'D') == 0){
                    //System.out.println("Deleting");
                    delete(msg.getVal(), msg.check);
                    createTimeStamp(msg.theMessage); //create a timestamp for memory log
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        msg.theMessage = "EXIT"; msg.check = false;
                }
                else if(Character.compare(command, 'I') ==0){
                    //System.out.println("Inserting");
                    insert(msg.getPos(), msg.getVal(), msg.check);
                    createTimeStamp(msg.theMessage); //create a timestamp for memory log
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        msg.theMessage = "EXIT"; msg.check = false;
                }
                else if(Character.compare(command, 'R') ==0){
                    System.out.println("Calling Rollback");
                    EchoServer.data.clear();
                    for(int i=0; i<EchoServer.dataDisk.size(); i++){
                        int j = EchoServer.dataDisk.get(i);
                        EchoServer.data.add(j);
                    }
                    
                    createTimeStamp(msg.theMessage); //create a timestamp for memory log
                }
                else if(Character.compare(command, 'C') ==0){
                    if(commit()){
                        EchoServer.dataDisk.clear();
                        System.out.println("Commiting To Disk");
                        for(int i=0; i<EchoServer.data.size(); i++){
                            int j = EchoServer.data.get(i);
                            EchoServer.dataDisk.add(j);
                        }
                        
                        createTimeStamp(msg.theMessage); //create a timestamp for memory log
                        if(msg.check)
                            output.writeObject(msg);
                        else
                            msg.theMessage = "EXIT"; msg.check = false;
                        }
                    else if(!commit()){
                        System.out.println("Cannot commit at this time");
                        if(msg.check)
                            output.writeObject(msg);
                        else
                            msg.theMessage = "EXIT"; msg.check = false;
                    }
                }
                else if(Character.compare(command, 'M') == 0){
                    output.writeObject(new Message(printMemoryLog(), false));
                }
                
		// Write an ACK back to the sender
		//output.writeObject(new Message("Recieved message #" + count));

                else{
                    if(msg.theMessage.equalsIgnoreCase("EXIT")){
                        output.writeObject(new Message("Exiting Server", false));
                        createTimeStamp(msg.theMessage); //create a timestamp for memory log
                    }
                    else
                        output.writeObject(new Message("Recieved message: ", false));
                }
                
		// Write an ACK back to the sender
		

		               
	    }while(!msg.theMessage.toUpperCase().equals("EXIT"));

	    // Close and cleanup
	    System.out.println("** Closing connection with " + socket.getInetAddress() + ":" + socket.getPort() + " **");
	    socket.close();
            input.close();
            output.close();

	}
        catch(EOFException ef){
            System.out.println("** Connection was closed **"); 
        }
	catch(Exception e){
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
	}

    }  //-- end run()
    
    /**
     * Adds data to a server linked list and pushes that to the other open
     * servers.
     * @param value data that is added
     * @param check checks to see if this is a client request
     */
    private void add(int value, boolean check){
        
        EchoServer.data.add(value);
        //System.out.println(value + " was added");
        if(!EchoServer.data.contains(value)){
            System.out.println(value + " was not added in the data");
        }
        
        //pushes the update to other open servers if this is a client request
       if(check){ 
            updateConintueslly(port, value, -1, 'A');
       }
    } //--add()
    
    /**
     * Delete data from the location specified
     * @param value location of the data deleted
     * @param check if this was called by a client(true) or server (false)
     */
    private void delete(int value, boolean check){
        EchoServer.data.remove(value);
        if(EchoServer.data.contains(value)){
            System.out.println(value + " was not remove from data");
        }

        //pushes the update to other open servers if this is a client request
        if(check)
            updateConintueslly(port, value, -1, 'D');

    } //--delete()
    
    /**
     * Creates a string that would display the data currently in the server
     * @return the string version of the data
     */
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
    } //--view()
    
    /**
     * Insert data at the specified position in the server
     * @param pos where data is inserted
     * @param value data that is inserted
     * @param check checks if a client(true) or server(false) called this method
     */
    private void insert(int pos, int value, boolean check){
        EchoServer.data.add(pos, value);
        
        //pushes the update to other open servers if this is a client request
        if(check){ 
            updateConintueslly(port, value, -1, 'A');
       }
    } //--insert()
    
    /**
     * Checks if you can commit the data currently into disk
     * @return true if you can commit and false if you can't
     */
    private boolean commit(){ 
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
                && EchoServer.data.size() == EchoServer.dataDisk.size()){
            for(int i = 0; i < EchoServer.data.size(); i++){
                if(EchoServer.data.get(i) != EchoServer.dataDisk.get(i))
                    return true;
               
            }
        }
        else if (EchoServer.data.size()!= EchoServer.dataDisk.size())
            return true;
        
        return false;
    } //--commit()
    
    /**
     * Determines if server can rollback pervious version committed to disk 
     * @return 
     */
    private boolean rollback(){
        return false;
    } //--rollack()
    
    /**
     * Creates a timeStamp and puts in the memory log to keep track of'
     * commands sent to the servers by client or other server
     * @param msg 
     */
    private void createTimeStamp(String msg) {
        DateFormat form = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        HashMap<String, String> timeStamp = new HashMap<>();
        time = new Date();
        timeStamp.put(form.format(time), msg);
        EchoServer.memoryLog.add(timeStamp);
        
    } //--createTimeStamp()
    
    private String printMemoryLog(){
        StringBuilder str = new StringBuilder();
        int i = 0;
        for(HashMap<String,String> stamp : EchoServer.memoryLog){
            for(String key : stamp.keySet()){
            if(i == 0)
                str.append("{<").append(key).append(",").append(stamp.get(key)).append(">");
            if(EchoServer.memoryLog.size() == i)
                str.append("}");
            else if(i != 0)
                str.append(" : <").append(key).append(",").append(stamp.get(key)).append(">");
            }
            i++;
        }
        
        return str.toString();
    } //--printMemoryLog()
    
    /**
     * 
     * @param port that the socket connects to
     * @param value being added by the server
     * @param postion position to add the value at (-1 if there is no position specified
     * @param command what command pushed to the other servers
     */
    private void updateConintueslly(int port, int value, int postion, char command){
        if(EchoServer.allServers.size() != 1){
            for (Integer ports : EchoServer.allServers) {
                if(ports != port) {
                    
                    if(command== 'A'){        //send an add message to server
                        update(new Message("A", value, true), ports);
                    }
                    else if(command == 'D'){  //send a delete message to server
                        update(new Message("D", value, true), ports);
                    } 
                    else if(command == 'I' && postion != -1) //send an insert message to server
                        update(new Message(postion, value,true), ports);
                    else if(command == 'C'){  //send a commit command to the server
                        update(new Message("C", true), ports);
                    }
                    else if(command =='R'){  //send a rollback command to server
                        update(new Message("R", true), ports);
                    }
                    else
                        System.out.println("Command to update was invalid");
                    
                }
            }
        }
        
    } //--updateContinuseslly()
    
    /**
     * Opens a socket connection to push a message to other servers
     * @param msg message being pushed to the serve
     * @param portTO port the socket is connecting to
     */
    private void update(Message msg, int portTO){
        try{
            //Open the socket connection to the server
            final Socket serverSock = new Socket("localhost", portTO);
            System.out.println("Connected to " + serverSock.getInetAddress() + " on port " + portTO + 
                " LocalPort number is: " +serverSock.getLocalPort());
            
            //Open I/O stream
            final ObjectOutputStream out = new ObjectOutputStream(serverSock.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(serverSock.getInputStream());
            
            
            //Get the command server needs to execute
            char command = msg.theMessage.charAt(0);
            //System.out.println("Command is: " + command);
            
            //Sends an add message to the server connected to 
            if(Character.compare(command, 'A') == 0 && msg.check){
                //System.out.println("Adding");
                //System.out.println("Message sending: " + msg.theMessage);
                msg.check = false; //identify that a server sent this message
                out.writeObject(msg);
            }
            //sends an insert command to the server connected to
            else if(Character.compare(command, 'I') == 0 && msg.check){
                System.out.println("Commiting data");
                msg.check = false; //identify that a server sent this message
                out.writeObject(msg);

            }
            //sends a delete command to the server connected to
            else if(Character.compare(command, 'D') == 0 && msg.check){
                //System.out.println("Deleting");
                msg.check = false;//identify that a server sent this message
                out.writeObject(msg);
            }
            //sends a command command to the server connected to
            else if(Character.compare(command, 'C') == 0 && msg.check){
                //System.out.println("Committing");
                msg.check = false;//identify that a server sent this message
                out.writeObject(msg);
            }
            //sends a rollback command to the server connected to
            else if(Character.compare(command, 'R') == 0 && msg.check){
                System.out.println("Rollbacking");
                msg.check = false;//identify that a server sent this message
                out.writeObject(msg);
            }
            //sanity check if command is not understood by the server
            else{
                System.out.println("Message recieved is :" + msg.theMessage);
                out.writeObject(new Message("Unable to understand command sent", false)); //identify that a server sent this message
            }
            
            //Close and clean up the connections open
            System.out.println("** Closing connection with " + serverSock.getInetAddress() + ":" + serverSock.getPort() + " **");
            in.close();
            out.close();
            serverSock.close();
            
        }
        catch(EOFException ef){
            System.out.println("*Connection was closed*"); 
        }
        catch(Exception e){
            System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
        }
    } //--update()


        
        
        

    

} //-- end class EchoThread
