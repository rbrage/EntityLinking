package jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;

import utility.Freebase;
import utility.PrintToFile;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class EntityGraph {
	ArrayList<Node> nodeList;
	public DirectedSparseGraph<Node, Link> entityGraph;

	public EntityGraph(JSONObject spottedWords_obj) {
		Set<String> spottedWord_keys = spottedWords_obj.keySet();
		this.entityGraph = new DirectedSparseGraph<Node, Link>();
		this.nodeList = new ArrayList<Node>();
		
		for (String spottedWord : spottedWord_keys) {
			JSONObject spottedWord_obj = (JSONObject) spottedWords_obj
					.get(spottedWord);
			HashMap<String, Integer> faccHash = (HashMap<String, Integer>) spottedWord_obj
					.get("Facc12");
			this.nodeList.add(new Node(spottedWord, faccHash));
		}
	}

	public String makeGraph() {
		 Freebase fb = new Freebase();
		String text = "";
		for (int i = 0; i < this.nodeList.size(); i++) {
			this.entityGraph.addVertex(this.nodeList.get(i));
			HashMap<String, Integer> hash_1 = this.nodeList.get(i).getFacc12();
			Set<String> set_1 = hash_1.keySet();
			Iterator<String> iter_1 = set_1.iterator();

			while (iter_1.hasNext()) {
				String value_1 = iter_1.next();
				
				 
			for (int j = 0; j < this.nodeList.size(); j++) {
				if (j == i)
					continue;
				HashMap<String, Integer> hash_2 = this.nodeList.get(j)
						.getFacc12();

				Set<String> set_2 = hash_2.keySet();
				Iterator<String> iter_2 = set_2.iterator();

				
					iter_2 = set_2.iterator();
					
					while (iter_2.hasNext()) {
						String value_2 = iter_2.next();
						
						if (value_1.equals(value_2)) {
							 String tmp1 = value_1.substring(value_1.toString().indexOf("e")+1,value_1.toString().indexOf(">"));
							 String tmp2 = value_2.substring(value_2.toString().indexOf("e")+1,value_2.toString().indexOf(">"));
							 
//							 JSONObject result_obj1 = fb.shearchMid(tmp1);
//							 JSONObject result_obj2 = fb.shearchMid(tmp2);
//							 
//							 if(result_obj1 != null && result_obj2 != null)
//								 text = text + this.nodeList.get(i).getId()+"\t"+result_obj1.get("name").toString()+"\t"+tmp1+"\t"+this.nodeList.get(j).getId()+"\t"+result_obj2.get("name").toString()+"\t"+tmp2 +"\n";
							 
							 
							if (this.entityGraph.containsEdge(this.entityGraph
									.findEdge(this.nodeList.get(i),
											this.nodeList.get(j)))) {
								this.entityGraph.findEdge(this.nodeList.get(i),
										this.nodeList.get(j)).weigth++;
							} else {
								this.entityGraph
										.addEdge(new Link(1.0, 100),
												this.nodeList.get(i),
												this.nodeList.get(j),
												EdgeType.DIRECTED);

							}
							this.nodeList.get(i).edgeOut++;
							this.nodeList.get(j).edgeIn++;
						}
					}
				}

			}
		}
		return text;
	}
	
	public void countEdges(){
		for(int i = 0; i<this.nodeList.size(); i++){
		System.out.println("Edges:" +this.nodeList.get(i).getId()+ " - Out: "+this.nodeList.get(i).getEdegOut()+ " - In: " +this.nodeList.get(i).getEdgeIn());
		System.out.println(this.entityGraph.getNeighbors(this.nodeList.get(i)));
		System.out.println(this.entityGraph.getInEdges(this.nodeList.get(i)).size());
		
		}
	}

	public DirectedSparseGraph getEntityGraph() {
		return entityGraph;
	}

}

// HashMap<String, String> hMap3=new HashMap<String, String>();
// Set<String> set1=hMap.keySet();
// Set<String> set2=hMap2.keySet();
//
// Iterator<String> iter1=set1.iterator();
// Iterator<String> iter2=set2.iterator();
// String val="";
// while(iter1.hasNext()) {
//
// val=iter1.next();
// System.out.println("key and value in hmap is "+val+" "+hMap.get(val));
//
// iter2=set2.iterator();
//
// while(iter2.hasNext()) {
// String val2=iter2.next();
// System.out.println("val2 value is "+val2);
//
// if(!hMap.get(val).equals(val2)) {
// hMap3.put(val, hMap.get(val));
// System.out.println("value adding");
//
// }
// }
// }
// System.out.println("hashmap3 is "+hMap3);