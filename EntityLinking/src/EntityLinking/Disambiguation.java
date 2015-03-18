package EntityLinking;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONObject;

import documentMaker.DocumentMaker;
import documentMaker.HtmlMaker;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.FileInputStream;
import java.util.Properties;

import jung.EntityGraph;
import jung.GraphVisualization;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import utility.File;
import utility.Freebase;
import utility.PrintToFile;
import utility.Reader;

public class Disambiguation {
	Reader reader;
	PrintToFile write;
	String folderPath;
	String filename;

	ArrayList<String> originalTextFiles;
	ArrayList<String> spottedWordsJSONFiles;
	ArrayList<String> spottedWordFiles;

	public Disambiguation(String folderPath, String prog) {
		this.reader = new Reader();
		this.write = new PrintToFile();
		this.folderPath = folderPath;

		this.spottedWordsJSONFiles = this.reader.readFolder(this.folderPath
				+ "/results_" + prog + "/JSON");
		this.spottedWordFiles = this.reader.readFolder(this.folderPath
				+ "/results_" + prog + "/WORDS");
		this.originalTextFiles = this.reader.readFolder(this.folderPath+"/original");
		
		Collections.sort(spottedWordsJSONFiles);
		Collections.sort(spottedWordFiles);
		Collections.sort(originalTextFiles);
	}

	public void runDisambiguation() throws IOException {
		for (int i = 0; i < this.spottedWordsJSONFiles.size(); i++) {
			JSONObject spottedWords_obj = reader
					.readJSON(this.spottedWordsJSONFiles.get(i));

			this.filename = File.getFilename(this.spottedWordsJSONFiles, i);

			Set<String> spottedWord_keys = spottedWords_obj.keySet();
			System.out.println("File: " + filename + "--------" + i + "/"
					+ (this.spottedWordsJSONFiles.size() - 1) + "-----------");

			
			/*
			 * Sort the values in facc12, high to low.
			 */
				for (String spottedWord : spottedWord_keys) {
					JSONObject spottedWord_obj = (JSONObject) spottedWords_obj
							.get(spottedWord);
					JSONObject facc_obj = (JSONObject) spottedWord_obj
							.get("Facc12");
					
					TreeMap<String, Long> facc_map = new TreeMap<String, Long>();
					facc_map.putAll(facc_obj);
					Map.Entry<String, Long> maxValue = null;

					for (Map.Entry<String, Long> value : facc_map.entrySet()) {
						if (maxValue == null
								|| value.getValue().compareTo(
										maxValue.getValue()) > 0) {
							maxValue = value;
						}
					}
					
					if (maxValue != null) {
						JSONObject max_obj = new JSONObject();
						max_obj.put(maxValue.getKey(), maxValue.getValue());
						spottedWord_obj.put("MaxValue", max_obj);

					}
					
					spottedWords_obj.put(spottedWord, spottedWord_obj);
					
					if (this.filename.equals("BAIDOA")) {
						 // Create-and-load a List of entries
						 List<Map.Entry<String, Long>> entries = new ArrayList<Map.Entry<String,
						 Long>>(facc_map.entrySet());
						 // Sort the list using a custom Comparator that compares the ages
						 Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {
						 public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
						 return o1.getValue().compareTo((long)o2.getValue());
						 }});
						
						 // Load the entries into a Map that preserves insert order
						 Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
						
						 for (Entry<String, Long> entry : entries){
						 sortedMap.put(entry.getKey(), entry.getValue());
						
						 String tmp = entry.getKey();
						 tmp = tmp.substring(tmp.toString().indexOf("e")+1,tmp.toString().indexOf(">"));
						 Freebase fb = new Freebase();
						 JSONObject result_obj = fb.shearchMid(tmp);
						 if(result_obj != null)
							 System.out.println(spottedWord+"\t"+result_obj.get("name").toString() +"\t"+result_obj.get("score").toString()+"\t"+entry.getValue()+"\t"+tmp);
						 else
							 System.out.println(spottedWord);
						 
						 }
						 
						 // All done - let's see what we got
						 //System.out.println(sortedMap);
						 System.out.println("---------");

					}
				}
				
				
					/*
					 * Making Graph
					 */
				
//					 EntityGraph eg = new EntityGraph(spottedWords_obj);
//					 String text = eg.makeGraph();
//					 PrintToFile print = new PrintToFile();
//					 print.printGraphToFile(text, folderPath, filename, "WordSpotter");
//					 GraphVisualization gv = new GraphVisualization(eg, this.filename);
//					 gv.show();
				
		
					HashMap words = addWordsInHash(reader.readFiles(spottedWordFiles.get(i)));
					DocumentMaker doc = new DocumentMaker(words, reader.readFiles(this.originalTextFiles.get(i)), spottedWords_obj);
					HtmlMaker html = new HtmlMaker(this.folderPath);
					html.makeHtmlFile(doc.getHTMLString(), this.filename);
					
					
		}
	}
	
		// Adds the spotted words in the hash.
		private HashMap<Integer, String> addWordsInHash(String spottedWordsString) {
			HashMap<Integer, String> temp = new HashMap<Integer, String>();
			String[] tmp = spottedWordsString.split("\n");
			for (int i = 0; i < tmp.length; i++) {
				String value = tmp[i].substring(0, tmp[i].lastIndexOf(":")).trim();
				int key = Integer
						.parseInt(tmp[i].substring(tmp[i].lastIndexOf(":") + 1));
				temp.put(key, value);
			}

			return temp;
		}

		
		 
}


/*
 * Making Graph
 */
// EntityGraph eg = new EntityGraph(spottedWords_obj);
// eg.makeGraph();
// eg.countEdges();
// GraphVisualization gv = new GraphVisualization(eg, this.filename);
// gv.show();


//

