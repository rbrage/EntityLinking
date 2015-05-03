package EntityLinkingELNAB;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import utility.Reader;

public class Spotter { // WordSpotter
	Reader reader;
	ArrayList<String> files;
	String folderPath;
	long startTime = 0;
	long stopTime = 0;

	HashMap<Integer, String> hashStopWords, NONhashStopWords,
			hashHonorificsWords;
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

	String[] NONStopWords = { "and", "of", "in" };

	public Spotter() throws IOException {
		this.reader = new Reader();

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

		this.NONhashStopWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.NONStopWords.length; i++) {
			this.NONhashStopWords.put(i, this.NONStopWords[i]);
		}

	}

	List<Annotation> annotations = new ArrayList<Annotation>();
	Annotation a;

	public List<Annotation> spottWords(String textID, String textToAnalyse) {
		int startOffset = 0;
		int endOffset = 0;
		int linebreakOffset = 0;
		int numberOfLineBreak = 0;
		LinkedList<Integer> linebreaks = new LinkedList<Integer>();


		if (!textID.subSequence(0, 3).equals("msn")) {
			if (textToAnalyse.contains("\n")) {
				numberOfLineBreak = textToAnalyse.length()
						- textToAnalyse.replaceAll("\n", "").length();
				int fromIndex = 0;
				for (int i = 0; i < numberOfLineBreak; i++) {
					fromIndex = textToAnalyse.indexOf("\n", fromIndex + 1);
					linebreaks.add(fromIndex);
				}
			}
		}
		String regexp = "(?<=[\\s\\n])";
		String[] wordList = textToAnalyse.split(regexp);
		for (int i = 0; i < wordList.length; i++) {
			
			String wordis = "";
			// Checks for capitalized word
			if (capitalized(wordList[i])) {
				int wr = countCapitalizedWord(wordList, i);
				for (int j = i; j <= (i + wr - 1); j++) {
					wordis = wordis + wordList[j];
				}
				if (wr > 1) {
					i = i + wr - 1;
				}
				if (textToAnalyse.indexOf(wordis.trim(), startOffset) != -1) {
					wordis = wordis.trim();
					startOffset = textToAnalyse.indexOf(wordis, startOffset);
					
				} else{
					startOffset = textToAnalyse.indexOf(wordis, startOffset);
//					System.out.println("Wis: "+wordis+ "--:"+startOffset);
				}

				if (endOfSentenc(wordis)) {
					endOffset = startOffset + wordis.length() - 1;
				} else
					endOffset = startOffset + wordis.length();
			}
			
//			System.out.println("Wis: "+wordis);
			
			
				int numberOfLineBreak2 = 0;

				if (!linebreaks.isEmpty()) {
					if (wordis.contains("\n")) {
						numberOfLineBreak2 = wordis.length()
								- wordis.replaceAll("\n", "").length();
						int fromIndex = 0;
						for (int i2 = 0; i2 < numberOfLineBreak2; i2++) {
							fromIndex = wordis.indexOf("\n", fromIndex + 1);
							a.getLinebreaks().add(fromIndex);
						}
					}

					

					if (startOffset > linebreaks.getFirst()) {
						while (startOffset > linebreaks.getFirst()) {
							linebreakOffset = linebreakOffset + 1;
							linebreaks.removeFirst();
						}
					}
				}

				wordis = wordis.replaceAll("\n", " ");
				wordis = wordis.trim();
				
				if (containsSymbols(wordis)) {
					while (endOfSentenc(wordis) || startWithSymbol(wordis)) {
						if (endOfSentenc(wordis)) {
							wordis = wordis.substring(0,
									wordis.length() - 1);
						}
						if (startWithSymbol(wordis)) {
							wordis = wordis.substring(1, wordis.length());
						}
					}
				}
			if (!wordis.isEmpty()) {
				a = new Annotation();
					a.setLinebreaks(new LinkedList<Integer>());
				a.setDocId(textID);
				a.setMentionText(wordis.trim());
				a.setMentionText2(wordis.trim());
				a.setBeginOffset(startOffset + linebreakOffset);
				a.setEndOffset(endOffset + linebreakOffset);
				a.setBeginOffset2(startOffset);
				a.setEndOffset2(endOffset);
				a.setBeginOffset3(startOffset + linebreakOffset);
				a.setEndOffset3(endOffset + linebreakOffset);
				annotations.add(a);
//				System.out.println(a.toString());
				startOffset = endOffset;
			}
		}
		return annotations;
	}

	int set = 0;

	// Method for counting capitalized words
	private int countCapitalizedWord(String[] wordList, int i) {
		set = 0;
		if (i < wordList.length) {
			if (capitalized(wordList[i])
					|| (this.NONhashStopWords.containsKey(wordList[i]) && set > 1)) {
//				 if (!this.hashStopWords.containsValue(wordList[i].trim()
//				 .toLowerCase())
//				 && !this.hashStopWords.containsValue(symbolRemove(
//				 wordList[i]).toLowerCase())
//				 && !this.hashHonorificsWords.containsValue(wordList[i]
//				 .trim())) {

				if (startWithSymbol(wordList[i])) {
					return set;
				}
				if (breakLine(wordList[i])) {
					return set+1;
				}
				if (endOfSentenc(wordList[i])) {
					return set+1;
				}
				if (pluralApostrophe(wordList[i])) {
					return set + 1;
				}

				return set = (1 + countCapitalizedWord(wordList, i + 1));

			} else
				return set;
			 } else
			 return set;

//		} else
//			return set;
	}

	private boolean breakLine(String word) {
		if (word.equals("\n")) {
			return true;
		}
		return false;
	}

	private boolean endOfSentenc(String word) {
		if (containsSymbols(word)) {
			if (word.substring(word.length()).equals(".")
					|| word.substring(word.length()).equals(",")) {
				return false;
			} else if (word.length() > 3) {
				if (word.lastIndexOf(".") != -1 && word.lastIndexOf(".") > 2) {
					if (word.substring(word.lastIndexOf(".") - 2,
							word.lastIndexOf(".") - 1).equals(".")) {
						return false;
					} else if (word.substring(word.trim().length() - 1).trim()
							.matches("[\\.,?!“()”\"]")) {
						return true;
					}
				} else if (word.substring(word.trim().length() - 1).trim()
						.matches("[\\.,?!“()”\"]")) {
					return true;
				}else if(word.contains("\n")){
					return true;
				}
			} else
				return false;
		} else if(word.contains("\n")){
			return true;
		}
		return false;

	}

	private boolean startWithSymbol(String word) {
		if (containsSymbols(word)) {
			if (word.substring(0, 1).trim().matches("[\\.,?!“()”\"]")) {
					return true;
				}
			} else if (word.substring(0, 1).trim().matches("[\\.,?!“()”\"]")) {
				return true;
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

	private String symbolRemove(String spottedWord) {
		if (spottedWord.substring(spottedWord.lastIndexOf(".") + 1).equals(" ")) {
			spottedWord = spottedWord.replaceAll("[\\,“()”?.'\"]", "");
		}
		return spottedWord = spottedWord.trim();
	}

	private boolean containsSymbols(String wordis) {
		Pattern p = Pattern
				.compile("[\\,“().”?':\"]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(wordis);
		boolean b = m.find();
		return b;
	}
}
/*
 * // Checks for honorifics word if
 * (this.hashHonorificsWords.containsValue(wordList[i])) { int wr =
 * countCapitalizedWord(wordList, i + 1); for (int j = i; j <= (i + wr); j++) {
 * wordis = wordis + " " + wordList[j]; } i = i + wr + 1;
 * 
 * if (textToAnalyse.indexOf(wordis.trim(), endOffset) != -1) { wordis =
 * wordis.trim(); startOffset = textToAnalyse.indexOf(wordis, endOffset); } else
 * startOffset = textToAnalyse.indexOf(wordis, endOffset);
 * 
 * endOffset = startOffset + wordis.length();
 * 
 * } else {
 */
