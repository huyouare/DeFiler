import java.util.HashMap;
import java.util.Map;


public class DFS {
	
	HashMap<Integer, DFileID> dFileMap = new HashMap<Integer, DFileID>();
	
	DBufferCache dbc;
	
	public DFS(){
		
	}

	/* creates a new dfile and returns the DFileID */ 
	public DFileID createDFile(){
		DFileID dfid = new DFileID(1);
		
		return dfid;
	}

	/* destroys the dfile named by the DFileID */ 
	public void destroyDFile(DFileID dFID)
	{	; 
	}

	/* reads contents of the dfile named by DFileID into the ubuffer 
	 * start read at dfile offset 0 
	 * starting from ubuffer offset startOffset; at most count bytes are transferred 
	 */ 
	public int read(DFileID dFID, byte[] ubuffer, int startOffset, int count); 

	/* writes to the file named by DFileID from the ubuffer 
	 * start write at dfile offset 0 
	 * starting from ubuffer offset startOffset; at most count bytes are transferred 
	 */ 
	public int write(DFileID dFID, byte[] ubuffer, int startOffset, int count); 

	/* List DFileIDs for all existing dfiles in the volume 
	 */ 
	public List<DFileID> listAllDFiles(); 

	/* Write back all dirty blocks to the volume, and wait for completion. 
	 */ 
	public void sync();
}
