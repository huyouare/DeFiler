package inode;

import java.util.ArrayList;
import java.util.HashMap;

import common.DFileID;

public class INode {
	
	public int fileSize; // IN BYTES
	public ArrayList<Integer> blockMap;
	public DFileID dfid;
	
	public INode(int size, DFileID fileID){
		this.setDFileID(fileID);
		this.setSize(size);
		this.setBlockMap(new ArrayList<Integer>());
	}

	public void addBlock(int blockID){
		this.blockMap.add(blockID);
	}
	
	public int getSize() {
		return fileSize;
	}

	public void setSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public ArrayList<Integer> getBlockMap() {
		return blockMap;
	}

	public void setBlockMap(ArrayList<Integer> blockMap) {
		this.blockMap = blockMap;
	}

	public DFileID getDFileID() {
		return dfid;
	}

	public void setDFileID(DFileID dfid) {
		this.dfid = dfid;
	}
}
