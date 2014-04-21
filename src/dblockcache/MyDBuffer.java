package dblockcache;

import common.Constants;

public class MyDBuffer extends DBuffer {

	private byte[] buffer;
	private boolean ioComplete;
	private int blockID;
	
	public MyDBuffer(int id) {
		blockID = id;
		buffer = new byte[Constants.BLOCK_SIZE];
		ioComplete = true;
	}
	
	public MyDBuffer(int id, byte[] buf) {
		blockID = id;
		buffer = buf;
		ioComplete = true;
	}
	
	
	@Override
	/* Start an asynchronous fetch of associated block from the volume */
	public void startFetch() {
		//startRequest(this, DiskOperationType.read());
	}

	@Override
	/* Start an asynchronous write of buffer contents to block on volume */
	public void startPush() {
		//push dirty blocks
	}

	@Override
	/* Check whether the buffer has valid data */ 
	public boolean checkValid() {
		return this.buffer==null;
	}

	@Override
	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
	public boolean waitValid() {
		while(!ioComplete){
		}
		return true;
	}

	@Override
	/* Check whether the buffer is dirty, i.e., has modified data written back to disk? */
	public boolean checkClean() {
		
		return false;
	}

	@Override
	/* Wait until the buffer is clean, i.e., wait until a push operation completes */
	public boolean waitClean() {
		
		return false;
	}

	@Override
	/* Check if buffer is evictable: not evictable if I/O in progress, or buffer is held */
	public boolean isBusy() {
		return !ioComplete;
	}

	@Override
	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	public int read(byte[] buffer, int startOffset, int count) {
		//Check that DBuffer has valid copy of the data
		ioComplete = false;
		int byteCount = 0;
		this.buffer = buffer;
//		startRequest(this, DiskOperationType.read());
		return byteCount;
	}

	@Override
	public int write(byte[] buffer, int startOffset, int count) {
		ioComplete = false;
		return 0;
	}

	@Override
	/* An upcall from VirtualDisk layer to inform the completion of an IO operation */
	public void ioComplete() {
		ioComplete = true;
	}

	@Override
	/* An upcall from VirtualDisk layer to fetch the blockID associated with a startRequest operation */
	public int getBlockID() {
		return blockID;
	}

	@Override
	/* An upcall from VirtualDisk layer to fetch the buffer associated with DBuffer object*/
	public byte[] getBuffer() {
		return this.buffer;
	}

}
