package server;

import java.net.Socket;             // Used to connect to the server
import java.io.ObjectInputStream;   // Used to read objects sent from the server
import java.io.ObjectOutputStream;  // Used to write objects to the server
import java.io.BufferedReader;      // Needed to read from the console (user input)
import java.io.InputStreamReader;   // Needed to read from the console (user input)
import java.util.LinkedList;
import java.util.Scanner;


/**
 * Marian Zaki (COSC 2354)
 * Simple client class.  This class connects to an EchoServer to send
 * text back and forth.  Java message serialization is used to pass
 * Message objects around.
 *
 */
public class EchoClient
{
    /**
     *IP address for the client
     */
    public String clientIP;
    
    /**
     * Server the client is connected to
     */
    //private EchoServer serv;
    
    public EchoClient(String IP){
        this.clientIP = IP;
    }
    
    /**rollback method.
     * loads the last committed version (only occurs once)
     * it can only be called once by the client
     */
    protected static Message rollback(){
        return new Message ("R",true);
    }
    
    /** * view method.Retrieves the linked list data structure and prints 
     * it out the data the user had added in
     * @return 
     */
    protected static Message view(){
        return new Message("V", true);
    }
    
//    /**commit method.
//     *loads the latest version to the disk
//     * @return true if user wants to save the new version
//     */
//    protected boolean commit(){
//        return true;
//    }
    
    protected static Message commit(){
        return new Message("C", true);
    }
    
    /**
     * insert method.adds integer at a specified index
     * @param pos Index of the linked list
     * @param value Integer to be added
     * @return 
     */
    protected static Message insert(int pos, int value){
         return new Message(pos, value, true);
    }
    
    /** * delete method.removes a specified integer
     * @param value Integer to be removed
     * @return 
     */
    protected static Message delete(int value){
         return new Message("D", value, true);
    }
    
    /**
     * add method.
     * appends to the end of the linked list and update the servers
     * @param value Integer to be appended
     * @return 
     */
    protected static Message add(int value){ 
        return new Message("A", value, true);
    }
    
    
    /**
     * Main method.
     * @param args  First argument specifies the server to connect to
     */
    public static void main(String[] args)
    {
	// Error checking for arguments
	if(args.length != 2)
        {
            System.err.println("Not enough arguments.\n");
            System.err.println("Usage:  java EchoClient <Server name or IP> <Server Port Number>\n");
            System.exit(-1);
        }
        
	try{
	    // Connect to the specified server
            String serverIP = args[0]; //first thing type
            int serverPort = Integer.valueOf(args[1]); //second thing typed
            
	    final Socket sock = new Socket(serverIP, serverPort); //final for security reason, could redirct the traffic
	    System.out.println("Connected to " + serverIP + " on port " + serverPort + 
                    " LocalPort number is: " + sock.getLocalPort());
	    
	    // Set up I/O streams with the server
	    final ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
	    final ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
            
            

	    // loop to send messages
	    Message msg = null, resp = null;
            Message resp2 = null;
//            System.out.println("Enter a line of text, or type \"EXIT\" to quit.");
//	    System.out.print(" > ");
	    do{
		// Read and send message.  Since the Message class
		// implements the Serializable interface, the
		// ObjectOutputStream "output" object automatically
		// encodes the Message object into a format that can
		// be transmitted over the socket to the server.

		//msg = readSomeText(c1,msg);

		msg = readSomeText(msg);

		output.writeObject(msg);   
                   
		// Get ACK and print.  Since Message implements
		// Serializable, the ObjectInputStream can
		// automatically read this object off of the wire and
		// encode it as a Message.  Note that we need to
		// explicitly cast the return from readObject() to the
		// type Message.
		resp = (Message)input.readObject(); //readOject() is a blocking method
		System.out.println("\nServer says: " + resp.theMessage + "\n");
                                
	    }while(!msg.theMessage.toUpperCase().equals("EXIT"));
	    
	    // shut things down
	    sock.close();

	}
	catch(Exception e){
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace(System.err);
	}

    } //-- end main(String[])


    /**
     * Simple method to print a prompt and read a line of text.
     *
     * @return A line of text read from the console
     */
    private static String readSomeText()
    {
	try{
	    System.out.println("Enter a line of text, or type \"EXIT\" to quit.");
	    System.out.print(" > ");
            System.out.println("First run");
            
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    return in.readLine();
	}
	catch(Exception e){
	    // Uh oh...
	    return "";
	}

    } //-- end readSomeText()
    

    //private static Message readSomeText(EchoClient c1, Message msg){

    private static Message readSomeText(Message msg){

        try{
        System.out.println("Enter a line of text, or type \"EXIT\" to quit.");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Scanner sc = new Scanner(System.in);

        //******
        
        System.out.println("Enter the corresponding letter to carry out an action");
        System.out.println("To ADD data - A, To DELETE - D, To VIEW - V, To INSERT - I, To COMMIT - C, To ROLLBACK - R");
        String request = sc.nextLine();
        if(request.equalsIgnoreCase("EXIT"))
                return new Message(request, true);
        else if(request.equalsIgnoreCase("A")){

            System.out.println("Input your data: ");
            String ans = in.readLine();
            if(ans.equalsIgnoreCase("EXIT"))
                return new Message(ans, true);
            else {
                int adding  = Integer.parseInt(ans);
                
                return add(adding);
            }
        }
        
        else if(request.equalsIgnoreCase("D")){

        

            System.out.println("Enter location of the data you want to delete data: ");
            String ans = in.readLine();
            if(ans.equalsIgnoreCase("EXIT"))
                return new Message(ans, true);
            else {
                int del  = Integer.parseInt(ans);
                

                return delete(del -1);
            }
        }
        
        else if(request.equalsIgnoreCase("V")){
            return view();
        
        }
        
        else if(request.equalsIgnoreCase("I")){
            System.out.println("Input your data");
            String ans = in.readLine();
            if(ans.equalsIgnoreCase("EXIT")){
                return new Message(ans, true);
            }
            else {
                System.out.println("Enter the location you want to place data");
                String pos = in.readLine();
                int ans2 = Integer.parseInt(ans);
                int pos2 = Integer.parseInt(pos);
                return insert(pos2,ans2);
            }
        }
        
        else if(request.equalsIgnoreCase("C")){
            return commit();
        }
        else if(request.equalsIgnoreCase("R")){
            return rollback();
        }


        }
        catch(Exception e){
	    // Uh oh...
            msg = new Message ("", true);
	    return msg;
        }
        return msg;
    }
    
    public static void printData(LinkedList<Integer> d){
        
    }
//    public String toString(){
//        
//        
//    }

} //-- end class EchoClient