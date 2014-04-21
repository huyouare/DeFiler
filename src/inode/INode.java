package inode;

import java.util.ArrayList;
import java.util.HashMap;

import common.DFileID;

public class INode {
	
	private int fileSize;
	private ArrayList<Integer> blockMap;
	private DFileID dfid;
	
	public INode(int size, DFileID fileID){
		this.setDFileID(fileID);
		this.setFileSize(size);
		this.setBlockMap(new ArrayList<Integer>());
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
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
