package tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import common.DFileID;

import dfs.MyDFS;
import virtualdisk.MyVirtualDisk;

public class AribaTest implements Runnable{
    
	DFileID conc;
	public int number;
	private MyDFS dfs;
	private int clientID;
	
	public AribaTest(MyDFS dfs, DFileID file, int id){
		this.conc=file;
		this.dfs=dfs;
		this.clientID=id;
		
	}
	@Override
	public void run() {
		extTest();
	}
	
    private void extTest() {

	 Print("Started", "Running");
	 System.out.println("sdf");
     Print("Write INITIAL", "Concurrent " + conc.getID());
     WriteTest(conc, "INTIAL");
     Print("Read Concurrent", ReadTest(conc));
     Print("Write INITIALS", "Concurrent " + conc.getID());
     WriteTest(conc, "INTIALS");
     Print("Read Concurrent", ReadTest(conc));

     DFileID nf = dfs.createDFile();

     Print("Created DFile", Integer.toString(nf.getID()));
     Print("Writing", "Test Two");
     WriteTest(nf, "TEST TWO");
     Print("Read", ReadTest(nf));

     WriteTest(nf, "TEST PART");
    }
     
    private void Print(String op, String mes) {
        System.out.println("Client #" + clientID + "\t Op: " + op + "\t \t "
                + mes);
    }
	private void WriteTest(DFileID f, String t) {
        byte[] data = t.getBytes();
        dfs.write(f, data, 0, data.length);
    }

    private void WriteLong (DFileID f) {
        byte[] data = new byte[2048];
        for (int i = 0; i < 2048; i++) {
            data[i] = (byte) ('a' + (i % 26));
        }
        dfs.write(f, data, 0, 2048);
    }

    private String ReadLong (DFileID f) {
        byte[] data = new byte[2048];
        dfs.read(f, data, 0, 2048);
        return new String(data).trim();
    }

    private String ReadTest (DFileID f) {
        byte[] read = new byte[100];
        dfs.read(f, read, 0, 50);
        // Print("Read bytes", Integer.toString(bytes));
        return new String(read).trim();
    }
	
	public static void main(String args[]) throws FileNotFoundException, IOException{

		MyVirtualDisk vdf= new MyVirtualDisk();
		Thread t = new Thread(vdf);
		t.start();

		ArrayList<Thread> clients = new ArrayList<Thread>();
		// Run NUM_WORKERS threads
		for (int i = 0; i < 3; i++) {
			MyDFS currDFS= new MyDFS(vdf);
			currDFS.init();
			DFileID file = currDFS.createDFile();

			AribaTest tc = new AribaTest(currDFS, file, i);
			Thread f = new Thread(tc);
			f.start();
		}
	}
}
