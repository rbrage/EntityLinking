package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import utility.PrintToFile;
import utility.Reader;

public class ScoreTable {
	Reader reader;
	PrintToFile write;
	String folderPath;

	ArrayList<String> goldenfiles;
	ArrayList<String> analyzedfiles;
	ArrayList<String> spottedFilesWords;

	String goldenText;
	String analysedText;
	String spottedWordsString;
	HashMap<Integer, String> spottedWords;

	public void ScoreTable(String folderPath, String prog) throws IOException {
		reader = new Reader();
		write = new PrintToFile();
		this.folderPath = folderPath;

		goldenfiles = reader.readFolder(this.folderPath + "/gold");
		analyzedfiles = reader.readFolder(this.folderPath + "/results_" + prog);
		spottedFilesWords = reader.readFolder(this.folderPath + "/results_" + prog
				+ "/WORDS");

		Collections.sort(goldenfiles);
		Collections.sort(analyzedfiles);
		Collections.sort(spottedFilesWords);


		HashMap<Integer, String> spottedWords = new HashMap<Integer, String>();

		for (int i = 0; i < goldenfiles.size(); i++) {
			goldenText = reader.readFiles(goldenfiles.get(i));
			analysedText = reader.readFiles(analyzedfiles.get(i));
			spottedWordsString = reader.readFiles(spottedFilesWords.get(i));

			spottedWords = addWordsInHash(spottedWordsString);

			String filename = goldenfiles.get(i).substring(
					goldenfiles.get(i).lastIndexOf("/")+1);

			compare(goldenText, analysedText, spottedWords, filename);
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

	// Private method to get out the offset stored as the key of the hash. 
	private Object getKeyFromHashMap(HashMap hm, String value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value))
				return o;
		}

		return null;
	}

	private void compare(String goldenText, String analysedText,
			HashMap<Integer, String> spottedWords, String filename) {
		
		this.goldenText = goldenText.trim();
		this.analysedText = analysedText;
		this.spottedWords = spottedWords;

		int offset = 0;
		int n = 0;
		int noffset = 0;
		int offsetStartGoldenText = 0;
		int offsetEndGoldenText = 0;
		int wordInPhrase = 0;
		double contains = 0;
		double totalwordgold = 0;
		double totalwordspotted = spottedWords.size();
		double procentprecition = 0;
		double procentrecall = 0;
		boolean foundWord = false;
		String extractedGoldenWord;
		
		goldenText = goldenText.replaceAll("\\s+", " ");
		System.out.println(goldenText);
		
		for (int i = 0; i < goldenText.length();) {
			offset = goldenText.indexOf("[", offset);
			noffset = (n * 2);

			if (offset == -1)
				break;
			
			// Finds the golden word inside [] in the golden text.
			offsetStartGoldenText = goldenText.indexOf("[", offset);
			offsetEndGoldenText = goldenText.indexOf("]", offset);
			extractedGoldenWord = goldenText.substring(offsetStartGoldenText + 1, offsetEndGoldenText);
			extractedGoldenWord = extractedGoldenWord.replaceAll(",", "");
			
			
			
			if (this.spottedWords.get(offset - noffset) == null){
				System.out.println(extractedGoldenWord + " : " +offsetStartGoldenText + " == " + this.spottedWords.get(offset - noffset) + " : " + (offset-noffset));
			}
			// Compare it the golden word to the spotted word.
			if (extractedGoldenWord.equals(this.spottedWords.get(offset - noffset))) {
				contains = contains + 1;
				this.spottedWords.remove(offset - noffset);
			} else {
				// Compare it the golden word to the spotted word. In some files the offset is +-4 but checked manually it is the same word.   
				if (getKeyFromHashMap(this.spottedWords, extractedGoldenWord) != null) {
					int keyOffset = Integer.parseInt(getKeyFromHashMap(this.spottedWords, extractedGoldenWord).toString());
					if((0 < (offset - noffset) - keyOffset) && ((offset - noffset) - keyOffset)<4 ){
						contains = contains + 1;
						this.spottedWords.remove(offset - noffset - ((offset - noffset) - keyOffset));
					}else{

					}
				}
			}

			// Finds spotted word in golden phrase. 
			try {
				int tmpOffset = offsetStartGoldenText
						+ this.spottedWords.get(offset - noffset).length() + 1;
				if (offsetEndGoldenText != tmpOffset) {
					
					if(extractedGoldenWord.contains("'"))
						extractedGoldenWord = extractedGoldenWord.substring(0,extractedGoldenWord.lastIndexOf("'"));
					if (extractedGoldenWord.equals(this.spottedWords.get(offset - noffset))) {
						wordInPhrase = wordInPhrase + 1;
						this.spottedWords.remove(offset - noffset);
					}
					String[] extractedList = extractedGoldenWord.split(" ");
					int extractedNumberOfWords = extractedGoldenWord.split(" ").length;

					for (int x = 0; x < extractedNumberOfWords; x++) {

						int y = x + extractedList[x].length();

						String word = this.spottedWords.get((tmpOffset + x)
								- noffset);
						String word2 = this.spottedWords.get((tmpOffset + x + y)
								- noffset);
						
						if (word != null) {
							if (extractedGoldenWord.contains(word)) {
								System.out.println("W1:"+word);
								wordInPhrase = wordInPhrase + 2;
								foundWord = true;
								break;
							}
						} else if (word2 != null) {
							word = this.spottedWords.get((tmpOffset + x + y)
									- noffset);
							if (extractedGoldenWord.contains(word2)) {
								System.out.println("W2:"+word2);
								wordInPhrase = wordInPhrase + 2;
								foundWord = true;
								break;
							}

						} else {
							foundWord = false;
							continue;
						}
					}
					if (!foundWord
							&& extractedList[0].equals(this.spottedWords
									.get(offset - noffset))) {
						wordInPhrase++;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}

			n = n + 1;
			offset = offset + 1;
			i = offset + extractedGoldenWord.length();
			totalwordgold++;

		}
	
		/* Calculation of score
		 * Precision = |C ∩ G|/|C| R = |C ∩ G|/|G|
		 * C = number of spotted words
		 * G = number of golden words
		 */
		
		/*
		 * Calculate precise match
		 */
		procentprecition = (contains / totalwordspotted) * 100;
		procentrecall = (contains / totalwordgold) * 100;
		
		
		System.out.println("\nContains: "+contains);
		System.out.println("totalwordgold: "+totalwordgold);
		System.out.println("totalwordspotted: "+totalwordspotted);
		
		System.out.println("GT length: " + goldenText.length() + " | " + (goldenText.length() + (totalwordgold*2)));
		System.out.println("AT length: " + analysedText.length() +"\n");
		
		
		
		
		/*
		 * Calculate if word is in phrase match
		 */
		double procentprecition2 = ((contains + wordInPhrase) / totalwordspotted) * 100;
		double procentrecall2 = ((contains + wordInPhrase) / totalwordgold) * 100;
 
		/*
		 * Print the score to a local file. 
		 */
		try {
			this.write.printScoreToFile(this.folderPath, filename.substring(0, filename.lastIndexOf(".")), "precise", procentprecition, procentrecall);
			this.write.printScoreToFile(this.folderPath, filename.substring(0, filename.lastIndexOf(".")), "2orMore", procentprecition2, procentrecall2);
			this.write.printScoreToFile(this.folderPath, filename.substring(0, filename.lastIndexOf(".")), "goldenword", totalwordgold, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
