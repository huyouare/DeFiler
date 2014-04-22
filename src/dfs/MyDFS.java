package dfs;

import inode.INode;
import common.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;

import common.DFileID;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;
import dblockcache.MyDBufferCache;

public class MyDFS extends DFS {

	Queue<Integer> availableFileIDs;
	Set<Integer> allocFileIDs;
	DBufferCache myDBufferCache;
	
	HashMap<Integer, INode> fileMap; //FileIDs
	MyVirtualDisk myVD;
	boolean[] freeBlockList; //Blocks 
	
	private int numBlocks;
	private int numINodeBlocks;
	private int numFileBlocks;
	private int iNodesPerBlock;
	
	public MyDFS(){
		availableFileIDs = new LinkedList<Integer>();
		for (int i=1; i<=Constants.MAX_DFILES; i++){
			availableFileIDs.add(i);
		}
		
		try {
			myVD = new MyVirtualDisk();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myDBufferCache = new MyDBufferCache(Constants.NUM_OF_CACHE_BLOCKS * Constants.BLOCK_SIZE, myVD);
		System.out.println(myDBufferCache.cacheSize);
	}
	
	public MyDFS(MyVirtualDisk vd){
		this();
		myVD = vd;
	}
	
	public int getNextFree(){
		synchronized(freeBlockList){
			for(int i=numINodeBlocks; i<freeBlockList.length; i++){
				if(freeBlockList[i]==true){
					freeBlockList[i] = false;
					return i;
				}
			}
			return -1;
		}
	}

	@Override
	public void init() {
		iNodesPerBlock = Constants.BLOCK_SIZE/Constants.INODE_SIZE;
		numINodeBlocks = Constants.MAX_DFILES/iNodesPerBlock;
		numFileBlocks = Constants.NUM_OF_BLOCKS-1-numINodeBlocks;
		
		freeBlockList = new boolean[1 + numINodeBlocks + numFileBlocks];
		synchronized(freeBlockList){
			Arrays.fill(freeBlockList, Boolean.TRUE);
			for(int i=0; i<numINodeBlocks+1; i++){
				freeBlockList[i] = false;
			}
		}
		fileMap = new HashMap<Integer, INode>();
		
		checkDisk();
	}
	
	public void checkDisk(){
		//Check sizes of inodes
		//Check valid blockIDs
		//Check for collisions
		
		boolean[] blockIDFound = new boolean[Constants.NUM_OF_BLOCKS];
		
		for(int i=1; i<Constants.MAX_DFILES+1; i++){
			DBuffer dBuffer = myDBufferCache.getBlock(i);
			byte[] buffer = dBuffer.getBuffer();

			int idInt = ByteBuffer.wrap(buffer, 0, 4).getInt();
			
			//CHECK UNIQUE INODES
			if(i!=idInt && idInt!=0){
				try {
					throw new Exception("Invalid fileID");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//CHECK FileID Bounds
			if(idInt<0 || idInt>Constants.MAX_DFILES){
				try {
					throw new Exception("fileID out of bounds");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(idInt!=0){
				int sizeInt = ByteBuffer.wrap(buffer, 4, 4).getInt();
				
				Integer[] blockMap = new Integer[Constants.MAX_BLOCKS_PER_FILE];
				for(int j=0; j<Constants.MAX_BLOCKS_PER_FILE; j++){
					blockMap[j] = ByteBuffer.wrap(buffer, 4*j+8, 4).getInt();
					
					//CHECK BlockID Bounds
					if(blockMap[j]<numINodeBlocks+1 || blockMap[j]>Constants.NUM_OF_BLOCKS){
						try {
							throw new Exception("blockID out of bounds");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					//CHECK BlockID Uniqueness
					if(blockIDFound[ blockMap[j] ]){
						try {
							throw new Exception("blockID already used");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					blockIDFound[ blockMap[j] ] = true;
					
				}
				ArrayList<Integer> blockMapList = new ArrayList<Integer>(Arrays.asList(blockMap));
				
				DFileID dfid = new DFileID(idInt);
				INode dfile = new INode(sizeInt, dfid);
				
				dfile.setBlockMap(blockMapList);
				fileMap.put(i, dfile);
			}
		}
	}

	@Override
	/* creates a new DFile and returns the DFileID, which is useful to uniquely identify the DFile*/
	public DFileID createDFile() {
		DFileID dfid = new DFileID(availableFileIDs.remove());
		synchronized(fileMap){
			fileMap.put(dfid.getID(), new INode(0, dfid));
		}
		
		saveToDisk(fileMap.get(dfid.getID()));
		return dfid;
	}
	
	public void saveToDisk(INode inode){
		byte[] buffer = new byte[Constants.INODE_SIZE];
		byte[] arr1 = ByteBuffer.allocate(4).putInt(inode.getSize()).array();
		byte[] arr2 = ByteBuffer.allocate(4).putInt(inode.getDFileID().getID()).array();
		ByteBuffer target = ByteBuffer.wrap(arr1);
		target.put(arr2);
		
		ArrayList<Integer> blockMap = inode.getBlockMap();
		for(int i:blockMap){
			target.putInt(i);
		}
		
		byte[] bytes = new byte[target.remaining()];
		target.get(bytes, 0, bytes.length);
		target.clear();
		target.get(buffer, 0, target.capacity());
		System.out.println(buffer.toString());
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		ArrayList<Integer> blockMapItems;
		synchronized(fileMap){
			blockMapItems = fileMap.get(dFID).getBlockMap(); 
			fileMap.remove(dFID.getID());
		}
		synchronized(freeBlockList){
			for (Integer block: blockMapItems){
				freeBlockList[block]=true;
			}
		}
		availableFileIDs.add(dFID.getID());
	}

	@Override
	/*
	 * reads the file dfile named by DFileID into the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		synchronized(dFID){
			if(startOffset<0)
				return -1;
			if(dFID==null) return -1;
			if(buffer==null) return -1;
			System.out.println("DFileID: " + dFID.getID());
			INode iNode = fileMap.get(dFID.getID());
			int size = iNode.getSize();
			if(iNode==null) return -1;
			ArrayList<Integer> blockMap = iNode.getBlockMap();
			
			int index = 0;
			for(int blockID : blockMap){
				int index2 = 0;
				DBuffer dBuffer = myDBufferCache.getBlock(blockID);
				byte[] buffer2 = dBuffer.getBuffer();
				while(index2<Constants.BLOCK_SIZE && index<count && index<size){
					buffer[index+startOffset] = buffer2[index2];
					index++;
					index2++;
				}
				if(index>=count || index>=size)
					break;
			}
			return index;
		}
	}

	@Override
	/*
	 * writes to the file specified by DFileID from the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		synchronized(dFID){
			if(startOffset<0)
				return -1;
			if(dFID==null) return -1;
			if(buffer==null) return -1;
			if(!fileMap.containsKey(dFID.getID()))
				return -1;
			INode iNode = fileMap.get(dFID.getID());
			if(iNode==null) return -1;
			ArrayList<Integer> blockMap = iNode.getBlockMap();
			
			int index = 0;
			for(int blockID : blockMap){
				int index2 = 0;
				DBuffer dBuffer = myDBufferCache.getBlock(blockID);
				byte[] buffer2 = dBuffer.getBuffer();
				while(index2<Constants.BLOCK_SIZE && index<count){
					buffer2[index2] = buffer[index+startOffset];
					index++;
					index2++;
				}
				if(index>=count)
					break;
			}
			
			while(index<count){
				int next = this.getNextFree();
				if(next==-1) return -1;
				blockMap.add(next);
				int index2 = 0;
				DBuffer dBuffer = myDBufferCache.getBlock(next);
				byte[] buffer2 = dBuffer.getBuffer();
				while(index2<Constants.BLOCK_SIZE && index<count){
					buffer2[index2] = buffer[index+startOffset];
					index++;
					index2++;
				}	
			}
			iNode.setSize(index);
			return index;
		}
	}

	@Override
	public int sizeDFile(DFileID dFID) {
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