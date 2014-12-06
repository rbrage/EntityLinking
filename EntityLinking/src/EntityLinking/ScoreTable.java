package EntityLinking;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.HTMLEditorKit.Parser;

public class ScoreTable {
	Reader reader;
	PrintToFile write;

	ArrayList<String> goldenfiles;
	ArrayList<String> analyzedfiles;
	ArrayList<String> analyzedfilesWords;

	String goldenText;
	String analysedText;
	String analyzedWordsString;
	HashMap<Integer, String> analyzedWords;

	public void ScoreTable(String folderPath, String prog) throws IOException {
		reader = new Reader();
		write = new PrintToFile();

		goldenfiles = reader.readFolder(folderPath + "/gold");
		analyzedfiles = reader.readFolder(folderPath + "/results_" + prog);
		analyzedfilesWords = reader.readFolder(folderPath + "/results_" + prog
				+ "/WORDS");

		Collections.sort(goldenfiles);
		Collections.sort(analyzedfiles);
		Collections.sort(analyzedfilesWords);

		System.out.println(goldenfiles);
		System.out.println(analyzedfiles);
		System.out.println(analyzedfilesWords);

		HashMap<Integer, String> analyzedWords = new HashMap<Integer, String>();

		for (int i = 0; i < goldenfiles.size(); i++) {
			goldenText = reader.readFiles(goldenfiles.get(i));
			analysedText = reader.readFiles(analyzedfiles.get(i));
			analyzedWordsString = reader.readFiles(analyzedfilesWords.get(i));

			analyzedWords = addWordsInHash(analyzedWordsString);
			
			String filename = goldenfiles.get(i).substring(
					goldenfiles.get(i).lastIndexOf("/"));

			compare(goldenText, analysedText, analyzedWords, filename);
		}

		/*
		 * P = |S ∩ A|/|S| R = |S ∩ A|/|A|
		 * 
		 * S number of spotted phrase A number of golden phrase
		 */

	}

	
	
	private HashMap<Integer, String> addWordsInHash(String analyzedWordsString2) {
		HashMap<Integer, String> temp = new HashMap<Integer, String>();
		String[] tmp = analyzedWordsString2.split("\n");
		for (int i = 0; i < tmp.length; i++) {
			String value = tmp[i].substring(0, tmp[i].indexOf(":")).trim();
			int key = Integer
					.parseInt(tmp[i].substring(tmp[i].indexOf(":") + 1));
			temp.put(key, value);
		}

		return temp;
	}

	private Object getKeyFromHashMap(HashMap hm, String value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value))
				return o;
		}

		return null;
	}

	private void compare(String goldenText, String analysedText,
			HashMap<Integer, String> analyzedWords, String filename) {
		this.goldenText = goldenText;
		this.analysedText = analysedText;
		this.analyzedWords = analyzedWords;
		ArrayList<String> notfound = new ArrayList<String>();
		ArrayList<String> found = new ArrayList<String>();

		int AWL = this.analyzedWords.size();
		int offset = 0;
		int n = 0;
		int noffset = 0;
		int offsetStart = 0;
		int offsetEnd = 0;
		double contains = 0;
		double totalwordgold = 0;
		double totalwordspotted = analyzedWords.size();
		double procentprecition = 0;
		double procentrecall = 0;
		String extracted;

		for (int i = 0; i < goldenText.length();) {
			offset = goldenText.indexOf("[", offset);
			noffset = (n*2);
			
			 if (offset == -1)
				break;

			offsetStart = goldenText.indexOf("[", offset);
			offsetEnd = goldenText.indexOf("]", offset);
			extracted = goldenText.substring(offsetStart + 1,
					offsetEnd).replaceAll("[\\   ]", " ");
			extracted = extracted.replaceAll(",", "");
			
			
			if (extracted.equals(this.analyzedWords.get(offset-noffset))){
				contains = contains + 1;
				this.analyzedWords.remove(offset-noffset);
			}
			
//			System.out.println("GO: "+goldenText.charAt(0));
//			System.out.println("OE: "+offsetEnd);
//			System.out.println("O: "+offset);
//			System.out.println("nO: "+noffset);3765 3755
			
			try {				
				int set = offsetStart + this.analyzedWords.get(offset-noffset).length()+1;
					if(offsetEnd != set){
						System.out
						.println("---------------------------------------------------");
						System.out.println(extracted + ":"+ extracted.length());
						System.out.println("OS: "+offsetStart);
						System.out.println("OE: "+offsetEnd);
						System.out.println("nO: "+noffset);
						System.out.println("wL: "+this.analyzedWords.get(offset-noffset).length());
						System.out.println("SET: "+ (set-noffset));
						
						System.out.println(this.analyzedWords.get(offset-noffset) + ":" + offset + ":"+(offset-noffset));
						System.out.println(set - noffset);
						System.out.println(this.analyzedWords.get(set - noffset) + ":" + offset + ":"+(set - noffset));
					}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			
			
			
			n = n+1;
			offset = offset + 1;
			i = offset + extracted.length();
			totalwordgold = totalwordgold + 1;

		}
		System.out
				.println("---------------------------------------------------");
		System.out.println(filename);
		System.out.println("Golden: "+goldenText.length());
		System.out.println("Analyzed text: "+analysedText.length());
		System.out.println("Analyzed word: "+analyzedWords.size());

		// System.out.println("Found: "+found);
		// System.out.println("Not found: "+notfound);

		procentprecition = (contains / totalwordspotted) * 100;
		procentrecall = (contains / totalwordgold) * 100;

		System.out.println("Precition: " + contains + "/" + totalwordspotted
				+ "|" + procentprecition);
		System.out.println("Recall: " + contains + "/" + totalwordgold + "|"
				+ procentrecall);
		System.out.println("Analyzed word 2- : "+AWL);

	}

}
