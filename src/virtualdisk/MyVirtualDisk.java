package virtualdisk;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class MyVirtualDisk extends VirtualDisk implements Runnable{
	private Queue<Request> requestQueue;
	
	class Request{
		DBuffer buf;
		DiskOperationType operation;
		Request(DBuffer buf, DiskOperationType operation){
			this.buf = buf;
			this.operation = operation;
		}
	}

	public MyVirtualDisk(String volName, boolean format) throws FileNotFoundException,
	IOException{
		super(volName, format);
		requestQueue = new LinkedList<Request>();
	}
	
	public MyVirtualDisk() throws FileNotFoundException,
	IOException{
		super();
		requestQueue = new LinkedList<Request>();
	}

	@Override
	public void startRequest(DBuffer buf, DiskOperationType operation){
		synchronized(requestQueue){
			
			Request r = new Request(buf, operation);
			while(!requestQueue.offer(r)) 
				continue;
			requestQueue.notify();
		}
	}
	
	public void processRequest(Request r){

		if(r.operation == DiskOperationType.READ){	
			try {
				readBlock(r.buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			r.buf.ioComplete();
		}
		else if(r.operation == DiskOperationType.WRITE){
			try {
				writeBlock(r.buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			r.buf.ioComplete();
		}
	}

	@Override
	public void run() {
		while(true){
			synchronized(requestQueue){
				while(!requestQueue.isEmpty()){
					Request r = requestQueue.poll();
					if(r==null) continue;
					processRequest(r);
				}
				

			}
		}
	}

}
