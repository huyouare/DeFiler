package dblockcache;

import java.io.IOException;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;
import common.Constants;
import common.Constants.DiskOperationType;

public class MyDBuffer extends DBuffer {

	private byte[] buffer;
	private boolean ioComplete;
	private int blockID;
	private MyVirtualDisk myVD;
	private boolean isValid;
	private boolean isClean;
	private boolean hold;
	
	public MyDBuffer(int id, MyVirtualDisk vd) {
		blockID = id;
		buffer = new byte[Constants.BLOCK_SIZE];
		ioComplete = true;
		myVD = vd;
		isValid = false;
		isClean = false;
		hold = false;
	}
	
	@Override
	/* Start an asynchronous fetch of associated block from the volume */
	public synchronized void startFetch() {
		ioComplete = false;
		
		myVD.startRequest(this, DiskOperationType.READ);
	}

	@Override
	/* Start an asynchronous write of buffer contents to block on volume */
	public synchronized void startPush() {
		ioComplete = false;
		
		myVD.startRequest(this, DiskOperationType.WRITE);
	}

	@Override
	/* Check whether the buffer has valid data */ 
	public boolean checkValid() {
		return this.isValid;
	}

	@Override
	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
	public synchronized boolean waitValid() {
		while(!isValid){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	/* Check whether the buffer is dirty, i.e., has modified data written back to disk? */
	public boolean checkClean() {
		return isClean;
	}

	@Override
	/* Wait until the buffer is clean, i.e., wait until a push operation completes */
	public synchronized boolean waitClean() {
		while(!isClean){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	/* Check if buffer is evictable: not evictable if I/O in progress, or buffer is held */
	public boolean isBusy() {
		return (!ioComplete || hold);
	}

	@Override
	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	public synchronized int read(byte[] buffer, int startOffset, int count) {
		//Check that DBuffer has valid copy of the data
//		System.out.println(this.blockID);
//		System.out.println(this.isValid);
		waitValid();
		int byteCount = 0;
		if(startOffset<-1)
			return -1;
		if(startOffset+count>buffer.length)
			return -1;
		for(int i=0; i<count; i++){
			if(i==buffer.length)
				break;
			buffer[i+startOffset] = this.buffer[i];
			byteCount++;
		}
		return byteCount;
	}

	@Override
	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.
	 */
	public synchronized int write(byte[] buffer, int startOffset, int count) {
		waitValid();
		int byteCount = 0;
		if(startOffset<-1)
			return -1;
		if(startOffset+count>buffer.length)
			return -1;
		for(int i=0; i<count; i++){
			if(i==buffer.length)
				break;
			this.buffer[i] = buffer[i+startOffset];
			byteCount++;
		}
		isClean = false;
		isValid = true;
		notifyAll();
		return byteCount;
	}

	@Override
	/* An upcall from VirtualDisk layer to inform the completion of an IO operation */
	public void ioComplete() {
		synchronized(this){
			ioComplete = true;
			isValid = true;
			isClean = true;
			notifyAll();
		}
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
	
	public synchronized void setHold(){
		hold = true;
	}
	
	public synchronized void removeHold(){
		hold = false;
	}

}
