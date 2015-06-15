package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Reader {
	private ArrayList<String> fileList;
	private String context;
	private String[] words;
	
	public ArrayList<String> readFolder(String path){
		fileList = new ArrayList<String>();
		final File folder = new File(path);
		System.out.println(path);
		System.out.println(folder);
	    for (final File fileEntry : folder.listFiles()){
	      if (fileEntry.isFile()) {
	    	  fileList.add(path+"/"+fileEntry.getName());
	        } else {
	            System.out.println("Not a file");
	        }
	    }
	    return fileList;
	}
	
	public String readFiles(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	            
	        }
	        context = sb.toString();
	    } finally {
	        br.close();
	    }
	    
		return context;
	}
	
	public String[] readFilesLines(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	            
	        }
	        context = sb.toString();
            
	        
	    } finally {
	        br.close();
	    }
	    words = context.split("\n");
		return words;
	}
	
	public JSONObject readJSON(String path){
		JSONParser parser = new JSONParser();
		 
		try {
	 
			Object obj = parser.parse(new FileReader(path));
	 
			JSONObject jsonObject = (JSONObject) obj;
			
			return jsonObject;
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	 
	     }

	}
	

