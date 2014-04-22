package tests;

import java.io.FileNotFoundException;
import java.io.IOException;

import dfs.*;
import virtualdisk.MyVirtualDisk;

public class mainTest {

	public static void main(String[] args) {
		MyVirtualDisk vd;
		vd = null;
		try {
			vd = new MyVirtualDisk();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread t = new Thread(vd);
		t.start();
		
		MyDFS fileSystem = new MyDFS(vd);
		fileSystem.init();
		for(int i = 0; i<1; i++){
			(new Thread(new FileMaker(fileSystem))).start();
		}
	}

}
