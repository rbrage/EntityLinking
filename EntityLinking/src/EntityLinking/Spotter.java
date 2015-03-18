package EntityLinking;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import documentMaker.DocumentMaker;


import utility.PrintToFile;
import utility.Reader;

public class Spotter { // WordSpotter
	Reader reader;
	PrintToFile write;
	ArrayList<String> files;
	String folderPath;
	long startTime = 0;
	long stopTime = 0;

	HashMap<Integer, String> hashStopWords, hashHonorificsWords;
	StringBuilder finalWords, finalString;

	String originalText;
	String filename;
	JSONObject ListOfEntityWords_obj;
	String[] listOfStopWords;

	// List of honorifics to check.
	String[] honorifics = { "Mr.", "Master", "Ms.", "Miss.", "Mrs.", "Mx.",
			"Sir.", "Ma'am", "Dame", "Lord", "Lady", "Dr.", "PhD.", "Phd.",
			"Ph.D.", "MD.", "DO.", "DDS.", "DMD.", "OD.", "PhramD.", "DVM",
			"MBBS.", "MBChB", "BDS", "Prof.", "Pfc.", "Br.", "Sr.", "Fr.",
			"Rev.", "Pr.", "Elder.", "Adv.", "Counsellor", "JAN.", "FEB.",
			"MAR.", "APR.", "MAY.", "JUN.", "JUL.", "AUG.", "SEP.", "OCT.",
			"NOV.", "DES.", "jan.", "feb.", "mar.", "apr.", "may.", "jun.",
			"jul.", "aug.", "sep.", "oct.", "nov.", "dec." };

	public void init(String folderPath) throws IOException {
		this.folderPath = folderPath;
		this.reader = new Reader();
		this.files = this.reader.readFolder(this.folderPath + "/original");
		this.write = new PrintToFile();

		this.listOfStopWords = reader
				.readFilesLines("/home/rbrage/Program/EntityLinking/en-stopwords.txt");

		this.hashStopWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.listOfStopWords.length; i++) {
			this.hashStopWords.put(i, this.listOfStopWords[i]);
		}

		this.hashHonorificsWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.honorifics.length; i++) {
			this.hashHonorificsWords.put(i, this.honorifics[i]);
		}
	}

	public void runAnnalyse() throws IOException {
		// Start the analyzing and prints the results to file
		for (int i = 0; i < this.files.size(); i++) {
			this.originalText = this.reader.readFiles(this.files.get(i));
			System.out.println(this.files.get(i));

			startTime = System.currentTimeMillis();
			String analyzedText = spottWords(this.originalText);
			stopTime = System.currentTimeMillis();

			this.filename = this.files.get(i).substring(
					this.files.get(i).lastIndexOf("/") + 1,
					this.files.get(i).lastIndexOf("."));
			
			this.write.printWordToJSON(this.ListOfEntityWords_obj, this.folderPath, this.filename, "WordSpotter");
			
			this.write.printToFile(analyzedText, this.folderPath, this.filename,
					false, "WordSpotter");
			this.write.printToFile(finalWords.toString(), this.folderPath,
					filename, true, "WordSpotter");
			long rtt = stopTime - startTime;
			this.write.printToFile(rtt, this.folderPath, filename, "Self",
					"Time");
			System.out.println("Article " + filename + " is done in: " + rtt +"ms");
		}
		System.out.println("Done Analyse!");

	}

	private String spottWords(String textToAnalyse) throws IOException {

		HashMap<Integer, String> words = new HashMap<Integer, String>();
		ArrayList<String> noncapilized = new ArrayList<String>();
		this.finalWords = new StringBuilder();

		textToAnalyse = textToAnalyse.trim().replaceAll("\\s+", " ");
		String[] wordList = textToAnalyse.split(" ");
		int offset = 0;
		this.ListOfEntityWords_obj = new JSONObject();
		
		for (int i = 0; i < wordList.length; i++) {
			String wordis = "";
			
			// Checks for honorifics word
			if (this.hashHonorificsWords.containsValue(wordList[i])) {
				int wr = countCapitalizedWord(wordList, i + 1);
				for (int j = i; j <= (i + wr); j++) {
					wordis = wordis + " " + wordList[j];
				}
				i = i + wr + 1;
				wordis = wordis.trim();
				offset = textToAnalyse.indexOf(wordis, offset);

			} else { // Checks for capitalized word
				if (capitalized(wordList[i])) {
					int wr = countCapitalizedWord(wordList, i);
					for (int j = i; j <= (i + wr - 1); j++) {
						wordis = wordis + wordList[j] + " ";
					}
					if (wr >1) {
						i = i + wr;
					}
					offset = textToAnalyse.indexOf(wordis.trim(), offset);

				}

			}

			
			if(pluralApostrophe(wordis)){
				if (wordis.contains("’s")) 
					wordis = wordis.substring(0,wordis.lastIndexOf("’s"));
				if(wordis.contains("´s"))
					wordis = wordis.substring(0,wordis.lastIndexOf("´s"));
				if(wordis.contains("'s"))
					wordis = wordis.substring(0,wordis.lastIndexOf("'s"));
			}
			
			wordis = wordis.replaceAll("[\\,“()”?:’\"]", "");
			wordis = wordis.replaceAll("\n", " ");
			wordis = wordis.trim();
			String[] w = wordis.trim().split(" ");

			// Checks if there is some capitalized stop-words in phrase
			for (int j = 0; j < w.length; j++) {
				if (this.hashStopWords.containsValue(w[j].toLowerCase())) {
					wordis = wordis.replaceAll(w[j], "");
					wordis = wordis.trim();
				}
			}
			
			
			// Checks words that has a period but is not a word.
			int endIndex = wordis.lastIndexOf(".");
			if (endIndex != -1
					&& !this.hashHonorificsWords.containsValue(wordis
							.substring(0, endIndex + 1))
					&& ((wordis.charAt(wordis.lastIndexOf(".") - 2) != ' '))) {

				if (capitalized(w[0])) {

					wordis = wordis.substring(0, endIndex);
				} else {
					continue;
				}

			}
			if (!wordis.isEmpty()) {
				if(!words.containsKey(wordis)){
					words.put(offset, wordis);
					this.finalWords.append(wordis + ":" + offset + "\n");
				}
				
				if(this.ListOfEntityWords_obj.containsKey(wordis)){
					JSONObject tmp_obj = (JSONObject) this.ListOfEntityWords_obj.get(wordis);
					JSONArray tmp_array = (JSONArray) tmp_obj.get("Offsets");
					int tmp_mentions =  (int) tmp_obj.get("Mentions") + 1;
					tmp_obj.put("Mentions", new Integer(tmp_mentions));
					tmp_array.add(new Integer(offset));
					this.ListOfEntityWords_obj.put(wordis, tmp_obj);
				}else{
					JSONObject obj = new JSONObject();
					JSONArray offset_arrey = new JSONArray();
					obj.put("Mentions", new Integer(1));
					offset_arrey.add(new Integer(offset));
					obj.put("Offsets", offset_arrey);
					this.ListOfEntityWords_obj.put(wordis, obj );
				}
				
				offset = offset + 1;
			}
			
		}
		
		DocumentMaker doc = new DocumentMaker(words, this.originalText);
		return doc.getTXTString();
		
	}

	int set = 0;

	// Method for counting capitalized words
	private int countCapitalizedWord(String[] wordList, int i) {
		set = 0;

		if (i < wordList.length) {
			if (capitalized(wordList[i])
					&& !this.hashStopWords.containsValue(wordList[i]
							.toLowerCase())
					&& !this.hashHonorificsWords.containsValue(wordList[i]
							.trim())) {

				if (endOfSentenc(wordList[i])) {
					return set + 1;
				}
				if (pluralApostrophe(wordList[i])) {
					return set + 1;
				}

				return set = (1 + countCapitalizedWord(wordList, i + 1));

			} else
				return 0;
		} else
			return set;
	}

	private boolean endOfSentenc(String word) {
		if (word.length() > 2) {
			if (word.substring(word.length() - 1).matches("[\\.,?!“()”\"]")) {
				return true;
			}
		}
		return false;
	}

	// Method that checks if there is a capitalized letter in word.
	private boolean capitalized(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isUpperCase(word.charAt(i)))
				return true;
		}
		return false;
	}

	private boolean pluralApostrophe(String word) {
		if (word.contains("’s") || word.contains("´s") || word.contains("'s")) {
			return true;
		} else {
			return false;
		}
	}

}
