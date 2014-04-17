package dblockcache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class MyDBufferCache extends DBufferCache {
	
	HashMap<Integer, DBuffer> dBufferMap = new HashMap<Integer, DBuffer>(); 
	Queue<Integer> blockIDQueue = new LinkedList<Integer>();
	
	/*
	 * Constructor: allocates a cacheSize number of cache blocks, each
	 * containing BLOCK-size bytes data, in memory
	 */
	public MyDBufferCache(int cacheSize) {
		super(cacheSize);
	}

	@Override
	/*
	 * Get buffer for block specified by blockID. The buffer is "held" until the
	 * caller releases it. A "held" buffer cannot be evicted: its block ID
	 * cannot change.
	 */
	public DBuffer getBlock(int blockID) {
		//find it in the cache, otherwise fetch
		if(dBufferMap.containsKey(blockID)){
			return dBufferMap.get(blockID);
		}
		else{
			if(blockIDQueue.size()==this.cacheSize){
				int id = blockIDQueue.remove();
				dBufferMap.remove(id);
			}
			DBuffer dbuffer = new MyDBuffer(blockID);
			dbuffer.startFetch();
			dBufferMap.put(blockID, dbuffer);
			blockIDQueue.add(blockID);
			return dbuffer;
		}
	}

	@Override
	/* Release the buffer so that others waiting on it can use it */
	public void releaseBlock(DBuffer buf) {
//		Queue<Integer> tempQueue = new LinkedList<Integer>();
		dBufferMap.remove(buf.getBlockID());
		blockIDQueue.remove(buf.getBlockID());
	}

	@Override
	/*
	 * sync() writes back all dirty blocks to the volume and wait for completion.
	 * The sync() method should maintain clean block copies in DBufferCache.
	 */
	public void sync() {
		//for all blocks
		//see if valid
		//update
	}

}
