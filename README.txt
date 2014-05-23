/**********************************************
 * Please DO NOT MODIFY the format of this file
 **********************************************/

/*************************
 * Team Info & Time spent
 *************************/

	Name1: Ariba Aboobakar
	NetId1: asa23	 
	Time spent: 25 hours 	

	Name2: Jesse Hu 	
	NetId2: jrh52
	Time spent: 25 hours 	


/******************
 * Files to submit
 ******************/

	lab4.jar // An executable jar including all the source files and test cases.
	README	// This file filled with the lab implementation details
        DeFiler.log   // (optional) auto-generated log on execution of jar file

/************************
 * Implementation details
 *************************/

DFS: In DFS, we initialize the list of available file IDs by maintaining an integer queue of the max number of 
files. We also maintain a list of the available free blocks in disk space and a map of integers (file IDs) to inodes. 
In the checkDisk method, we make sure that each existing dfile (DFileID) has exactly one inode, that the size of each dfile has a legal value,
that the block maps of all dfiles have a valid block number for every block in the dfile (as defined by its size), and that no data block in the 
VDF is listed in the block map of more than one dfile. In createDFile, we allocate a file ID to a file and create an inode for the file, and write this inode to disk.
We save it to disk by using a byte buffer. In destroyDFile, we put the file ID back into the list of available file IDs and free all of the disk blocks associated with 
the given file. In read and write, we make call to the DBufferCache to retrieve the appropriate disk files that make up the given file.

MyDBuffer: In DBufferCache, we have stored a map that keeps track of all of the buffer objects based on their block ID. We use this to keep track of which buffer objects are in the cache. We retrieve a block from the cache if it is in there, or else we evict the least-recently used block from the cache, which we keep track of through a queue,
and replace it with a new block fetched by a method call on the dbuffer object, which in turn calls on the virtual disk.  

INode: We created an inode class that keeps track of the current size of the file that the iNode is associated with. The iNode also stores the DFileID of the file that it is associated with. 
There is also an array that serves as a block map for the given file. The block map keeps track of the blocks in the virtual disk that are allocated to the given file, which is what DFS uses to read and write to files from. 
The DFS also uses the block map to free the blocks of a given file when it destroys that given file. We use the map in DFS to allocate blocks to the file, and overwrites previously written blocks. Read will go through the buffers to read in the amount of count or the size of the file. It does this with a lock on the write operation, to allow concurrency between
multiple threads. 

To run, make an instance of the VirtualDisk using a Thread and start(). DFS will need to be init() and will check the disk on instance, unless stated otherwise. Formatting is specifically done through the DFS constructor.


/************************
 * Feedback on the lab
 ************************/
More clear instructions on using Byte Buffers to read/write data to from disk would be helpful.

/************************
 * References
 ************************/

Piazza, shared test files with classmates