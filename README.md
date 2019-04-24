# Java-Socket-Project-Post-It-Notes
This is a program that uses java sockets to simulate a bulletin board where a user can post virtual post it notes. A board is generated and the user must give coordinates / a message to attach posts to the board. Addition commandsd are used to do things like remove all the current post it notes or retrieve all the messages in a certain section of the bulletin board. 

Server.java
-------------
A file that runs the server side and handles commands 

Client.java 
-------------
A file that handles the GUI / input from the user 

Commands
-------------
POST(noteX,noteY,noteWidth,noteHeight,noteColor,noteMessage) 
posts note with given information 

PIN(x,y)
Pins the notes in the given coordinate (pins are unpinned by default) only prioritized messages are pinned. 

UNPIN(x,y)
Unpin the notes in a given coordinate 

GET(x,y)
Retrieve the post at this coordinate and display its message 

CLEAR 
Remove all the posts on the board 


