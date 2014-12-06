package EntityLinking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;



public class PrintToFile {
	PrintWriter writer;	
	
	public void printToFile(String text, String folderPath, String filename, Boolean words, String prog) {
		PrintWriter writer;
		try {
			if(!words){
			writer = new PrintWriter(
					folderPath + "/results_"+prog+"/" + filename + ".txt", "UTF-8");
			writer.print(text);
			writer.close();
		}else{
			writer = new PrintWriter(
					folderPath + "/results_"+prog+"/WORDS/" + filename + ".txt", "UTF-8");
			writer.print(text);
			writer.close();
		}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	/*
	 * Print DBpedia words and analyzed text
	 */
	public void printToFile(String text, String folderPath, String filename, Boolean words, String prog, String spotter ) {
		PrintWriter writer;	
		try {
			if(!words){
			writer = new PrintWriter(
					folderPath + "/results_"+prog+"/" + filename + "_with_"+spotter+".txt", "UTF-8");
			writer.print(text);
			writer.close();
			}else{
				System.out.println(folderPath + "/results_"+prog+"/WORDS/" + filename + "_with_"+spotter+".txt");
				System.out.println(text);
				writer = new PrintWriter(
						folderPath + "/results_"+prog+"/WORDS/" + filename + "_with_"+spotter+".txt", "UTF-8");
				writer.print(text);
				writer.close();
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	/*
	 * Print DBpedia time
	 */
	public void printToFile(long rtt, String folderPath, String filename,
			String prog, String spotter ,String time) throws IOException{
		
		folderPath = "/home/rbrage/Program/EntityLinking/";
		try {
			File f = new File(folderPath +prog +time+".txt");
			if(f.exists() && !f.isDirectory()) {
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
						folderPath +prog +time+".txt", true)));
				
			writer.println(filename + ": " + rtt + " ms. " + "with: "+ spotter);
			writer.close();
			}else{
				writer = new PrintWriter(
						folderPath +prog +time+".txt", "UTF-8");

				
						writer.println(filename + ": " + rtt + " ms. " + "with: "+ spotter);
				writer.close();
			}
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Print the time to a file
	 */
	public void printToFile(long rtt, String folderPath, String filename,
			String prog ,String time) throws IOException {
		folderPath = "/home/rbrage/Program/EntityLinking/";
		
		try {
			File f = new File(folderPath +prog +time+".txt");
			if(f.exists() && !f.isDirectory()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
				folderPath +prog +time+".txt", true)));
			
			writer.println(filename + ": " + rtt + " ms.");
			writer.close();
			}else{
				writer = new PrintWriter(
						folderPath +prog +time+".txt", "UTF-8");
			
						writer.println(filename + ": " + rtt + " ms.");
				writer.close();
			}
			
		} catch (FileNotFoundException|UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
}
