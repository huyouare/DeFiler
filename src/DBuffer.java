
public class DBuffer {
	
	public DBuffer(){
	}
	
	/* Start an asynchronous fetch of associated block from the volume */ 
	public void startFetch(); 

	/* Start an asynchronous write of buffer contents to block on volume */ 
	public void startPush(); 

	/* Check whether the buffer has valid data*/ 
	public boolean checkValid(); 

	/* Wait until the buffer has valid data (i.e., wait for fetch to complete) */ 
	public boolean waitValid(); 

	/* Check whether the buffer is dirty, i.e., has modified data to be written back */ 
	public boolean checkClean(); 

	/* Wait until the buffer is clean (i.e., wait for push to complete) */ 
	public boolean waitClean(); 

	/* Check if buffer is evictable: not evictable if I/O in progress, or buffer is held. */ 
	public boolean isBusy(); 

	/* Reads into the ubuffer[ ] from the contents of this Dbuffer dbuf. 
	 * Check first that dbuf has a valid copy of the data! 
	 * startOffset is for the ubuffer, not for dbuf. 
	 * Reads begin at offset 0 in dbuf and move at most count bytes. 
	 */ 
	public int read(byte[] ubuffer, int startOffset, int count); 

	/* Writes into this Dbuffer dbuf from the contents of ubuffer[ ]. 
	 * Mark dbuf dirty! startOffset is for the ubuffer, not for dbuf. 
	 * Writes begin at offset 0 in dbuf and move at most count bytes. 
	 */ 
	public int write(byte[] ubuffer, int startOffset, int count);
}
