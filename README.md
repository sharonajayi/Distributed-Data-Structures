# Distributed-Data-Structures
COSC 2354 Team Project-Spring 2021

Classses:
-> Client Class(Might inherit from Server Class)

-> Server Class

Methods:

-> Client variables & methods: 

Update{add() -appends to the end of the linked list
       
       insert() -add int in a specified index of the liked list
       
       delete() -removes int in a specified index of the linked list}

rollback() -loads the last committed version

view() -retives the data structure

commit() -load the latest version to disk

-> Server variables & methods:

Linked List<int>

Static variable that holds all Server objects: used to find which servers need to be updated

Array (String[]): //Couldn't see what Nadine wrote

replicate() -push update to other servers and commit to the disk

check() -check if each server has the latest update (i.e compares the linked list of each server)

Others:

-> Might need to use threads.
