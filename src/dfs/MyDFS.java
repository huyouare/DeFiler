package dfs;

import inode.INode;
import common.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;

import common.DFileID;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;
import dblockcache.MyDBufferCache;

public class MyDFS extends DFS {

	Queue<Integer> availableFileIDs;
	DBufferCache myDBufferCache;
//	ArrayList<INode> iNodeList;
	
	HashMap<Integer, INode> fileMap;
	VirtualDisk myVD;
	boolean[] freeList;
	
	private int numBlocks;
	private int numINodeBlocks;
	private int numFileBlocks;
	
	public int getNextFree(){
		for(int i=numINodeBlocks; i<freeList.length; i++){
			if(freeList[i]==true){
				freeList[i] = false;
				return i;
			}
		}
		return -1;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		availableFileIDs = new LinkedList<Integer>();
		for (int i=1; i<512; i++){
			availableFileIDs.add(i);
		}
		try {
			myVD = new MyVirtualDisk("test", true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		myDBufferCache = new MyDBufferCache(Constants.NUM_OF_CACHE_BLOCKS*Constants.BLOCK_SIZE, myVD);
//		iNodeList = new ArrayList<INode>();
		int iNodesPerBlock = Constants.BLOCK_SIZE/Constants.INODE_SIZE;
		numINodeBlocks = Constants.MAX_DFILES/iNodesPerBlock;
		numFileBlocks = Constants.NUM_OF_BLOCKS-1-numINodeBlocks;
		
		freeList = new boolean[1 + numINodeBlocks + numFileBlocks];
		Arrays.fill(freeList, Boolean.TRUE);
		for(int i=0; i<numINodeBlocks+1; i++){
			freeList[i] = false;
		}
		
		fileMap = new HashMap<Integer, INode>();
//		for(int i=1; i<numINodeBlocks+1; i++){
//			DBuffer buffer = myDBufferCache.getBlock(i);
//		}
		

	}

	@Override
	/* creates a new DFile and returns the DFileID, which is useful to uniquely identify the DFile*/
	public DFileID createDFile() {
		DFileID dfid = new DFileID(availableFileIDs.remove());
		fileMap.put(dfid.getID(), new INode(0, dfid));
		return dfid;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		ArrayList<Integer> blockMapItems = fileMap.get(dFID).getBlockMap(); 
		for (Integer block: blockMapItems){
			freeList[block]=true;
		}
		fileMap.remove(dFID.getID());

		//FREE ALL BLOCKS
		
//		for (int id : fileMap.keySet()){
//			if (fileMap.get(id).getDFileID() == dFID){
//				fileMap.get(id).setBlockMap(new ArrayList<Integer>());
//				fileMap.remove(id);
//				//maybe format contents
//			}
//		}
	}

	@Override
	/*
	 * reads the file dfile named by DFileID into the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		//find dBuffer based on the dFID
		//read from DBuffer into buffer
		if(startOffset<0)
			return -1;
		//More corner cases
		INode iNode = fileMap.get(dFID.getID());
		ArrayList<Integer> blockMap = iNode.getBlockMap();
		int index = 0;
		for(int blockID : blockMap){
			int index2 = 0;
			DBuffer dBuffer = myDBufferCache.getBlock(blockID);
			byte[] buffer2 = dBuffer.getBuffer();
			while(index2<Constants.BLOCK_SIZE && index>count){
				buffer[index+startOffset] = buffer2[index2];
			}
			if(index>count)
				break;
		}
		return index;
	}

	@Override
	/*
	 * writes to the file specified by DFileID from the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		if(startOffset<0)
			return -1;
		//More corner cases
		if(!fileMap.containsKey(dFID.getID()))
			return -1;
		INode iNode = fileMap.get(dFID.getID());
		ArrayList<Integer> blockMap = iNode.getBlockMap();
		int index = 0;
		for(int blockID : blockMap){
			int index2 = 0;
			DBuffer dBuffer = myDBufferCache.getBlock(blockID);
			byte[] buffer2 = dBuffer.getBuffer();
			while(index2<Constants.BLOCK_SIZE && index>count){
				buffer2[index2] = buffer[index+startOffset];
			}
			if(index>count)
				break;
		}
		while(index<count){
			int next = this.getNextFree();
			if(next==-1) return -1;
			blockMap.add(next);
			int index2 = 0;
			DBuffer dBuffer = myDBufferCache.getBlock(next);
			byte[] buffer2 = dBuffer.getBuffer();
			while(index2<Constants.BLOCK_SIZE && index>count){
				buffer2[index2] = buffer[index+startOffset];
			}
		}
		return index;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		INode dFile = fileMap.get(dFID.getID());
		return dFile.getSize();
	}

	@Override
	public List<DFileID> listAllDFiles() {
		List<DFileID> list = new ArrayList<DFileID>();
		for (int id : fileMap.keySet()){
			list.add(fileMap.get(id).getDFileID());
		}
		return list;
	}

	@Override
	/* Write back all dirty blocks to the volume, and wait for completion. */
	public void sync() {
		myDBufferCache.sync();
	}

}