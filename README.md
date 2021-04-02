# Distributed-Data-Structures
## COSC 2354 Team Project-Spring 2021

### Classses:

-> EchoClient Class

-> EchoServer Class

-> GlobalClock Class (not really needed)

-> Message Class

### Methods & Variables:

**-> Client variables & methods:** 

public int ClientIP

Update:
       
       
       protected void add(int value) 
       
              -appends to the end of the linked list
       
       protected void insert(int post, int value) 
       
              -add int in a specified index of the liked list
       
       protected void delete(int value) 
       
              -removes int in a specified index of the linked list
       

protected void rollback() 
       
       -loads the last committed version (only occurs one time)
       -it can only be called once by the client

protected LinkedList<integer> view() 
       
       -retives the data structure

protected boolean commit() 
       
       -load the latest version to disk

**-> Server variables & methods:**

private LinkedList<Integer> data
       
       - hold all the data the client wants

private Hastable<Integer, LinkedList<Integer>> 
       
       - hold the data each client commited
       
private Date timeStamp

protected boolean available

private static ArrayList<EchoServers> allServers
       
       - Static variable that holds all Server objects: used to find which servers need to be updated


private ArrayList<String[]> memoryLog

       Array (String[]) of size 3
       - [0] = IP address 
       - [1] = update that occured
       - [2] = time stamp

private void replicate()

       -push update to other servers and commit to the disk

private boolean check() 
       
       -check if each server has the latest update (i.e compares the linked list of each server)

public void updateServer()

       - updates EchoClient command to  server and use replicate() to update the other serevers 

**-> GlobalClock variables & methods:**

--Might not need this [class](https://stackabuse.com/how-to-get-current-date-and-time-in-java/)--

**-> Message variables & methods: **
Variables:
       [] public String message

Constructors:

       Message(String met, int value){
       
              switch(met){
              
                     case "A": message = "Update: add " + value; break;
                     
                     case "D": message = "Update: delete " + value; break;
                     
                     }
       
       }
       
       Message(String met,{
       
              switch(met){
              
                     case "C": message = "Commit to disk "; break;
                     
                     case "R": message = "Rollback"; break;
                     
                     }
       
       }
       
       Message(int pos, int value){
       
              message = "Update: " + value + " at position " + pos;
       
       }
Others:


