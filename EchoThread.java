
package server;

import java.io.EOFException;
import java.io.IOException;
import java.lang.Thread;            // We will extend Java's base Thread class
import java.net.Socket;
import java.io.ObjectInputStream;   // For reading Java objects off of the wire
import java.io.ObjectOutputStream;  // For writing Java objects to the wire



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
	    //int count = 0;
	    do{
		// read and print message
                msg = (Message)input.readObject();

		System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] " + msg.theMessage);
                char command = msg.theMessage.charAt(0);

                
                
                if(Character.compare(command, 'A') == 0){
                    System.out.println("About to add");
                    add(msg.getVal(), msg.check);
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        msg.theMessage = "EXIT"; msg.check = false;
                }
                else if(Character.compare(command, 'V') == 0){
                    System.out.println("Printing data");
                    output.writeObject(new Message(view(), false));

                    
                }
                else if(Character.compare(command, 'D') == 0){
                    System.out.println("Deleting");

                    delete(msg.getVal(), msg.check);
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        output.writeObject(new Message("Finished", false));
                }
                else if(Character.compare(command, 'I') ==0){
                    System.out.println("Inserting");
                    insert(msg.getPos(), msg.getVal(), msg.check);
                    if(msg.check)
                        output.writeObject(msg);
                    else
                        output.writeObject(new Message("Finished", false));
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
                        String fileName = "commit.ser";
                        FileOutputStream file = new FileOutputStream(fileName);
                        ObjectOutputStream out = new ObjectOutputStream(file);
                        out.writeObject(EchoServer.dataDisk);
                        out.close();
                        file.close();
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
    
    
    private void add(int value, boolean check){
        
        EchoServer.data.add(value);
        System.out.println(value + " was added");
        if(!EchoServer.data.contains(value)){
            System.out.println(value + " was not added in the data");
        }
        
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
    

    private void insert(int pos, int value, boolean check){
        EchoServer.data.add(pos, value);
        
        if(check){ 
            //System.out.println("Update the other servers");
            updateConintueslly(port, value, -1, 'A');
       }
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
                && EchoServer.data.size() == EchoServer.dataDisk.size()){
            for(int i = 0; i < EchoServer.data.size(); i++){
                if(EchoServer.data.get(i) != EchoServer.dataDisk.get(i))
                    return true;
               
            }
        }
        else if (EchoServer.data.size()!= EchoServer.dataDisk.size())
            return true;
        
        return false;
    }
    
   private boolean rollback(){
        if (EchoServer.dataDisk.isEmpty()){
            return false;
        }
        
        return false;
        }
    
    private boolean commitCheck(){
        if (EchoServer.dataDisk.isEmpty()){
            return true;
        }
        return false;
    }
    
    private boolean rollbackCheck(){
        return false;
    }
    private void updateConintueslly(int port, int value, int postion, char command){
        if(EchoServer.allServers.size() != 1){
            for (Integer ports : EchoServer.allServers) {
                if(ports != port) {
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
        }
        
    }
    
    private void update(Message msg, int portTO){
        //System.out.println("Starting the update");
        boolean status = false; Message tmpMsg = null;
        try{
            final Socket serverSock = new Socket("localhost", portTO);
            System.out.println("Connected to " + serverSock.getInetAddress() + " on port " + portTO + 
                " LocalPort number is: " +serverSock.getLocalPort());
            final ObjectOutputStream out = new ObjectOutputStream(serverSock.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(serverSock.getInputStream());
 
            char command = msg.theMessage.charAt(0);
            System.out.println("Command is: " + command);
            if(Character.compare(command, 'A') == 0 && msg.check){
                System.out.println("Adding");
                System.out.println("Message sending: " + msg.theMessage);
                msg.check = false;
                out.writeObject(msg);
                status = true;
                System.out.println("status = " + status);
            }
            else if(Character.compare(command, 'I') == 0 && msg.check){
                System.out.println("Commiting data");
                msg.check = false;
                out.writeObject(msg);
                status = true;

            }
            else if(Character.compare(command, 'D') == 0 && msg.check){
                System.out.println("Deleting");
                msg.check = false;
                out.writeObject(msg);
                status = true;
            }
            else{
                System.out.println("Message recieved is :" + msg.theMessage);
                out.writeObject(new Message("Nonsense", false));
                status = false;
                System.out.println("status = " + status);
            }
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
    }    

} //-- end class EchoThread

