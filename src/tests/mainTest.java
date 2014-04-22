package tests;

import dfs.*;

public class mainTest {

	public static void main(String[] args) {
		OurDFS fileSystem = new OurDFS(true);
		fileSystem.init();
		for(int i = 0; i<1; i++){
			(new Thread(new FileMaker(fileSystem))).start();
		}

	}

}
