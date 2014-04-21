package dfs;

import inode.INode;
import common.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import common.DFileID;
import dblockcache.DBufferCache;

public class MyDFS extends DFS {
	
	Queue<Integer> availableFileIDs;
	DBufferCache myDBufferCache;
	ArrayList<INode> iNodeList;
	int numBlocks;
	int vdf[] ;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		availableFileIDs = new LinkedList<Integer>();
		for (int i=1; i<512; i++){
			availableFileIDs.add(i);
		}
		iNodeList= new ArrayList<INode>();
		int iNodesPerBlock=Constants.BLOCK_SIZE/Constants.INODE_SIZE;
		int numINodeBlocks= Constants.MAX_DFILES/iNodesPerBlock;
		int numFileBlocks= Constants.NUM_OF_BLOCKS-1-numINodeBlocks;
		vdf= new int[];
		for (int j=0; j<numINodeBlocks; j++){
			vdf[j]= 0;
		}
		
		
		
	}

	@Override
	public DFileID createDFile() {
		// TODO Auto-generated method stub
		DFileID dfid = new DFileID(availableFileIDs.remove());
		myDBufferCache.getBlock(dfid.getID());
		iNodeList.add(new INode(0, dfid));
		return dfid;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		for (INode currINode: iNodeList){
			if (currINode.getDFileID() == dFID){
				for (int item: currINode.getBlockMap()){
					vdf.put(key, 0);
				}
			}
		}
	}

	@Override
	/*
	 * reads the file dfile named by DFileID into the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		//find dBuffer based on the dFID
		//read from DBuffer into buffer
		return 0;
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DFileID> listAllDFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}

}
