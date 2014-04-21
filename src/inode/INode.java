package inode;

import java.util.HashMap;

import common.DFileID;

public class INode {
	
	public int fileSize;
	public HashMap<Integer, Integer> blockMap;
	public DFileID dfid;
	
	public INode(int mySize, DFileID myFileID){
		this.dfid=myFileID;
		this.fileSize=mySize;
		this.blockMap= new HashMap<Integer, Integer>();
	}
}
