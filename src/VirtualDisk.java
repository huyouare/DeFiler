
public class VirtualDisk {
	
	public VirtualDisk(){
	}
	/* 
	 * Start an asynchronous I/O request to the device/disk. 
	 * The blockID and buffer array are given by the DBuffer dbuf. 
	 * The operation is either READ or WRITE (DiskOperationType). 
	 */ 
	public void startRequest(DBuffer dbuf, DiskOperationType rw) throws IOException{
	}
}
