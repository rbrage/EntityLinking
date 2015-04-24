package jung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;

import EntityLinkingELNAB.Annotation;
import EntityLinkingELNAB.Settings;

import utility.Freebase;
import utility.PrintToFile;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class EntityGraph {
	ArrayList<Node> nodeList;
	public DirectedSparseGraph<Node, Link> entityGraph;
	private List<Annotation> annotations;

	public EntityGraph(List<Annotation> annotations, Settings settings) {
		this.annotations = annotations;
		this.entityGraph = new DirectedSparseGraph<Node, Link>();
		this.nodeList = new ArrayList<Node>();

		for (Annotation a : this.annotations) {
			this.nodeList.add(new Node(a.getMentionText(), a.getFacc12()));
		}
	}

	public List<Annotation> makeGraph() {
		
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
							 if(!this.nodeList.get(i).id.toLowerCase().equals(this.nodeList.get(j).id.toLowerCase()) &&
									 !symbolRemove(this.nodeList.get(i).id).toLowerCase().equals(symbolRemove(this.nodeList.get(j).id).toLowerCase())){
							 
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
		}
		
		return this.annotations;
	}

	public void countEdges() {
		for (int i = 0; i < this.nodeList.size(); i++) {
			System.out.println("Edges:" + this.nodeList.get(i).getId()
					+ " - Out: " + this.nodeList.get(i).getEdegOut()
					+ " - In: " + this.nodeList.get(i).getEdgeIn());
			System.out.println(this.entityGraph.getNeighbors(this.nodeList
					.get(i)));
			System.out.println(this.entityGraph
					.getInEdges(this.nodeList.get(i)).size());

		}
	}

	public DirectedSparseGraph getEntityGraph() {
		return entityGraph;
	}

	private String symbolRemove(String spottedWord) {
			spottedWord = spottedWord.replaceAll("[\\,“()”?.'\"]", "");
		return spottedWord = spottedWord.trim();
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