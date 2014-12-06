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

public class WordSpotter {
	Reader reader;
	PrintToFile write;
	ArrayList<String> files;

	HashMap<Integer, String> hashOfNonWords;
	HashMap<Integer, String> hashOfGradeWords;

	String textToAnalyze;
	String[] listOfNonWords = { "a", "an", "and", "are", "as", "at", "be",
			"but", "by", "for", "if", "in", "into", "is", "it", "no", "not",
			"of", "on", "or", "such", "that", "the", "their", "then", "there",
			"these", "they", "this", "to", "was", "will", "with" };
	String[] listOfGrade = { "Mr.", "Mrs.", "Phd.", "Ph.D.", "JAN.", "FEB.", "OCT." };

	StringBuilder finalWords, finalString;

	public void run(String folderPath) throws IOException {
		reader = new Reader();
		files = reader.readFolder(folderPath);
		write = new PrintToFile();

		long startTime = 0;
		long stopTime = 0;

		listOfNonWords = reader
				.readFilesLines("/home/rbrage/Program/EntityLinking/en-stopwords.txt");

		hashOfNonWords = new HashMap<Integer, String>();
		for (int i = 0; i < listOfNonWords.length; i++) {
			hashOfNonWords.put(i, listOfNonWords[i]);
		}

		hashOfGradeWords = new HashMap<Integer, String>();
		for (int i = 0; i < listOfGrade.length; i++) {
			hashOfGradeWords.put(i, listOfGrade[i]);
		}

		for (int i = 0; i < files.size(); i++) {
			textToAnalyze = reader.readFiles(files.get(i));
			startTime = System.currentTimeMillis();
			String analyzedText = spottWords(textToAnalyze);
			stopTime = System.currentTimeMillis();
			String filename = files.get(i).substring(
					files.get(i).lastIndexOf("/") + 1,
					files.get(i).lastIndexOf("."));
			//System.out.println(folderPath + filename);
			write.printToFile(analyzedText, folderPath, filename, false, "mine");
			write.printToFile(finalWords.toString(), folderPath, filename, true, "mine");
			long rtt = stopTime - startTime;
			write.printToFile(rtt, folderPath, filename, "Self", "Time");
			System.out.println("Article " +filename +" is done in: " + rtt);
		}
		System.out.println("Done!");

	}

	public String spottWords(String textToAnalyse) {

		HashMap<Integer, String> words = new HashMap<Integer, String>();
		ArrayList<String> noncapilized = new ArrayList<String>();
		
		String[] word = textToAnalyse.split(" ");
		ArrayList<String> grade = new ArrayList<String>();
		int offset = 0;
		for (int i = 0; i < word.length; i++) {
			String wordis = "";
			
			if (hashOfGradeWords.containsValue(word[i])) {

				int wr = countCapilizedWord(word, i);
				for (int j = i; j <= (i + wr); j++) {
					wordis = wordis + " " + word[j];
					grade.add(wordis);
				}
				i = i + wr;
				offset = textToAnalyze.indexOf(wordis, offset);

			} else {

				if (Capilized(word[i])
						&& !hashOfNonWords.containsValue(word[i].toLowerCase())) {
					if (i != word.length - 1) {
						if (Capilized(word[i + 1])
								&& !word[i].substring(word[i].length() - 1)
										.matches("[\\,.“()”\"]")
								&& !hashOfNonWords.containsValue(word[i + 1]
										.toLowerCase()) && !hashOfGradeWords.containsValue(word[i + 1])) {
							
							if (i != word.length - 2) {
								if (Capilized(word[i + 2])
										&& !word[i + 1].substring(
												word[i + 1].length() - 1)
												.matches("[\\,.“()”\"]")
										&& !hashOfNonWords
												.containsValue(word[i + 2]
														.toLowerCase())&& !hashOfGradeWords.containsValue(word[i + 2])) {

									if (i != word.length - 3) {
										if (Capilized(word[i + 3])
												&& !word[i + 2]
														.substring(
																word[i + 2]
																		.length() - 1)
														.matches("[\\.,“()”\"]")
												&& !hashOfNonWords
														.containsValue(word[i + 3]
																.toLowerCase())&& !hashOfGradeWords.containsValue(word[i + 3])) {
											wordis = word[i] + " "
													+ word[i + 1] + " "
													+ word[i + 2] + " "
													+ word[i + 3];
											i = i + 3;
											offset = textToAnalyze.indexOf(
													wordis, offset);
										} else {

											wordis = word[i] + " "
													+ word[i + 1] + " "
													+ word[i + 2];

											i = i + 2;
											offset = textToAnalyze.indexOf(
													wordis, offset);
										}
									}
								} else {

									wordis = word[i] + " " + word[i + 1];
									i++;
									offset = textToAnalyze.indexOf(wordis,
											offset);

								}
							}

						} else {

							wordis = word[i];
							offset = textToAnalyze.indexOf(wordis, offset);
						}
					}
					
						
				}

			}
			if (!Capilized(word[i]) && !hashOfNonWords.containsValue(word[i].toLowerCase())){
				
				offset = textToAnalyze.indexOf(word[i], offset);
				noncapilized.add(word[i]+":"+ offset);
				}
			
			wordis = wordis.replaceAll("[\\,“()”:\"]", "");
			wordis = wordis.replaceAll("\n", " ");
			wordis = wordis.trim();
			String[] w = wordis.trim().split(" ");

			for (int j = 0; j < w.length; j++) {
				if (hashOfNonWords.containsValue(w[j].toLowerCase())) {
					wordis = wordis.replaceAll(w[j], "");
				}
			}
			int endIndex = wordis.lastIndexOf(".");
			
			if (endIndex != -1 && !hashOfGradeWords.containsValue(wordis.substring(0, endIndex+1)) && (wordis.charAt(wordis.lastIndexOf(".")-2) != ' ')){
				wordis = wordis.substring(0,endIndex);
			}
			if ( !wordis.isEmpty()) {
				words.put(offset, wordis);
				//System.out.println("Added: " + wordis + ":" + offset);

			}
		}
		
		System.out.println(noncapilized.size() + " | " + noncapilized);
		finalString = new StringBuilder(textToAnalyze);
		finalWords = new StringBuilder();
		NavigableMap<Integer, String> map = new TreeMap<Integer, String>(words);
		Set set = map.descendingMap().entrySet();
		Iterator iterator = set.iterator();
		int a = 0;
		while (iterator.hasNext()) {
			Map.Entry me2 = (Map.Entry) iterator.next();
			String add = "[" + me2.getValue() + "]";
			int start = (int) me2.getKey();
			finalWords.append(me2.getValue() +":" + (start)+ "\n");
			finalString.insert(start, add);
		}

		return finalString.toString();

	}

	private int countCapilizedWord(String[] word, int i) {
		if (Capilized(word[i])
				&& !hashOfNonWords.containsValue(word[i].toLowerCase())) {
			return 1 + countCapilizedWord(word, i + 1);
		} else
			return 1;
	}

	public boolean Capilized(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isUpperCase(word.charAt(i)))
				return true;
		}
		return false;
	}

}
