package virtualdisk;
import java.io.FileNotFoundException;
import java.io.IOException;
import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class MyVirtualDiskOLD extends VirtualDisk{

	public MyVirtualDiskOLD(String volName, boolean format) throws FileNotFoundException,
	IOException{
		super(volName, format);
	}

	@Override
	public void startRequest(DBuffer buf, DiskOperationType operation){
		if(operation == DiskOperationType.READ){	
			Reader r = new Reader(buf);
			(new Thread(r)).start();
		}
		else{
			Writer w = new Writer(buf);
			(new Thread(w)).start();
		}
	}
	
	
	class Reader implements Runnable{

		DBuffer myBuf;

		Reader(DBuffer dbuf){
			myBuf = dbuf;
		}
		public void run() {
			System.out.println("Running ");
			try {
				readBlock(myBuf);
				myBuf.ioComplete();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Thread exiting.");
		}
	}
	
	class Writer implements Runnable{
		
		DBuffer myBuf;

		Writer(DBuffer dbuf){
			myBuf = dbuf;
		}
		public void run() {
			System.out.println("Running ");
			try {
				writeBlock(myBuf);
				myBuf.ioComplete();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Thread exiting.");
		}
		
	}

}
