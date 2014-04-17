package dfs;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import common.DFileID;
import dblockcache.DBufferCache;

public class RealDFS extends DFS {
	
	Queue<Integer> availableFileIDs;
	DBufferCache myDBufferCache;
	
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		availableFileIDs = new LinkedList<Integer>();
		for (int i=512; i>0; i--){
			availableFileIDs.add(i);
		}
	}

	@Override
	public DFileID createDFile() {
		// TODO Auto-generated method stub
		DFileID dfid = new DFileID(availableFileIDs.remove());
		myDBufferCache.getBlock(dfid.getID());
		return dfid;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
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
