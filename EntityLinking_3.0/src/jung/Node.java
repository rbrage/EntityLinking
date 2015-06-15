package jung;

import java.util.HashMap;

public class Node {
	String id;
	HashMap<String, Integer> facc12_hash;
	int edgeOut, edgeIn;
	
	public Node(String id, HashMap faccHash) {
		this.id = id;
		this.facc12_hash = faccHash;
	}
	public int getEdegOut() {
		return edgeOut;
	}
	
	public int getEdgeIn() {
		return edgeIn;
	}
	
	public String getId() {
		return id;
	}
	public HashMap<String, Integer> getFacc12() {
		return facc12_hash;
	}
	public String toString(){
		return id;
	}
}
