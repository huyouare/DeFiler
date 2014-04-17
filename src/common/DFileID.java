package common;

/* typedef DFileID to int */
public class DFileID {

	private int dFID;

	DFileID(int dFID) {
		this.dFID = dFID;
	}

	public int getDFileID() {
		return dFID;
	}
	    
	public boolean equals(Object other){
		DFileID otherID =  (DFileID) other;
		if(otherID.getID() == dFID){
			return true;
		}
		return false;
	}
	    
	public String toString(){
		return dFID+"";
	}
	
	public int getID(){
		return dFID;
	}
	
	public void setID(int id){
		dFID = id;
	}
}
