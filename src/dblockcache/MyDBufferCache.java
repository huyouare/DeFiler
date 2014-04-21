package dblockcache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;

public class MyDBufferCache extends DBufferCache {
	
	HashMap<Integer, DBuffer> dBufferMap = new HashMap<Integer, DBuffer>(); 
	Queue<Integer> blockIDQueue = new LinkedList<Integer>();
	MyVirtualDisk myVD;
	
	/*
	 * Constructor: allocates a cacheSize number of cache blocks, each
	 * containing BLOCK-size bytes data, in memory
	 */
	public MyDBufferCache(int cacheSize, VirtualDisk myVD) {
		super(cacheSize);
		myVD = myVD;
	}

	@Override
	/*
	 * Get buffer for block specified by blockID. The buffer is "held" until the
	 * caller releases it. A "held" buffer cannot be evicted: its block ID
	 * cannot change.
	 */
	public DBuffer getBlock(int blockID) {
		//find it in the cache, otherwise fetch
		DBuffer dbuffer;
		if(dBufferMap.containsKey(blockID)){
			dbuffer = dBufferMap.get(blockID);
		}
		else{
			if(blockIDQueue.size()==this.cacheSize){
				int id = blockIDQueue.remove();
				dBufferMap.remove(id);
			}
			dbuffer = new MyDBuffer(blockID, myVD);
			dbuffer.startFetch();
			dBufferMap.put(blockID, dbuffer);
			blockIDQueue.add(blockID);
		}
		synchronized(dbuffer){
			//dbuffer.setHold();
		}
		return dbuffer;
	}

	@Override
	/* Release the buffer so that others waiting on it can use it */
	public void releaseBlock(DBuffer buf) {
//		synchronized(buf){
//			buf.removeHold();
//			notifyAll();
//		}
		dBufferMap.remove(buf.getBlockID());
		blockIDQueue.remove(buf.getBlockID());
	}

	@Override
	/*
	 * sync() writes back all dirty blocks to the volume and wait for completion.
	 * The sync() method should maintain clean block copies in DBufferCache.
	 */
	public void sync() {
		ArrayList<DBuffer> dBufferList = new ArrayList<DBuffer>();
		for(int key : dBufferMap.keySet()){
			DBuffer dBuffer = dBufferMap.get(key);
			if(dBuffer.checkValid()){
				if(!dBuffer.checkClean()){
					dBuffer.startPush();
					dBufferList.add(dBuffer);
				}
			}
		}
		while(!dBufferList.isEmpty()){
			for(DBuffer dBuffer : dBufferList){
				if(!dBuffer.isBusy())
					dBufferList.remove(dBuffer);
			}
		}
	}

}
