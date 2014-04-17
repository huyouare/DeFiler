
public class DBufferCache {
	/* Get buffer for block specified by blockID 
	 The buffer is “held” until the caller releases it. 
	 A “held” buffer cannot be evicted: its block ID cannot change. 
	 */ 
	public DBuffer getBlock(int blockID); 

	/* Release the buffer so that it may be eligible for eviction. 
	 */ 
	public void releaseBlock(DBuffer dbuf); 

	/* Write back all dirty blocks to the volume, and wait for completion. 
	 */ 
	public void sync();
}
