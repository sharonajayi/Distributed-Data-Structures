# Distributed-Data-Structures
## COSC 2354 Team Project-Spring 2021

### Classses:

-> Client Class

-> Server Class

-> GlobalClock Class

-> Message Class

### Methods & Variables:

**-> Client variables & methods:** 

Update:
       
       
       add() -appends to the end of the linked list
       
       insert() -add int in a specified index of the liked list
       
       delete() -removes int in a specified index of the linked list
       

rollback() -loads the last committed version

view() -retives the data structure

commit() -load the latest version to disk

**-> Server variables & methods:**

Linked List<int>

Static variable that holds all Server objects: used to find which servers need to be updated

Array (String[]) of size 3
- [0] = IP address 
- [1] = update that occured
- [2] = time stamp
replicate() -push update to other servers and commit to the disk

check() -check if each server has the latest update (i.e compares the linked list of each server)

**-> GlobalClock variables & methods:**

.......

**-> Message variables & methods: **
Variables:
       [] public String message

Constructors:
       Message(String met, int value){
              switch(met){
                     case "A": message = "Update: add " + value; break;
                     case "D": message = "Updare: delete " + value; break;
                     }
       }

Others:


