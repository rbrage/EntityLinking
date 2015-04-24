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
	}

	List<Annotation> annotations = new ArrayList<Annotation>();
	Annotation a;

	public List<Annotation> spottWords(String textID, String textToAnalyse)
			throws UnsupportedEncodingException {
		int startOffset = 0;
		int startOffset2 = 0;
		int endOffset = 0;
		
		String[] wordList = textToAnalyse.split(" ");

		for (int i = 0; i < wordList.length; i++) {

			String wordis = "";
			// Checks for honorifics word
			if (this.hashHonorificsWords.containsValue(wordList[i])) {
				int wr = countCapitalizedWord(wordList, i + 1);
				for (int j = i; j <= (i + wr); j++) {
					wordis = wordis + " " + wordList[j];
				}
				i = i + wr + 1;

				if (textToAnalyse.indexOf(wordis.trim(), endOffset) != -1) {
					wordis = wordis.trim();
					startOffset = textToAnalyse.indexOf(wordis, endOffset);
				} else
					startOffset = textToAnalyse.indexOf(wordis, endOffset);

				endOffset = startOffset + wordis.length();
			

			} else { // Checks for capitalized word
				if (capitalized(wordList[i])) {
					int wr = countCapitalizedWord(wordList, i);
					for (int j = i; j <= (i + wr - 1); j++) {
						wordis = wordis + wordList[j] + " ";
					}
					if (wr > 1) {
						i = i + wr;
					}
					if (textToAnalyse.indexOf(wordis.trim(), endOffset) != -1) {
						wordis = wordis.trim();
						startOffset = textToAnalyse
								.indexOf(wordis, endOffset);
					} else
						startOffset = textToAnalyse
								.indexOf(wordis, endOffset);

					if(endOfSentenc(wordis)){
						endOffset = startOffset + wordis.length()-1;
					}else
						endOffset = startOffset + wordis.length();
				}
			}
			
			wordis = wordis.replaceAll("\n", " ");
			wordis = wordis.trim();
			
			if (!wordis.isEmpty()) {
				a = new Annotation();
				a.setDocId(textID);
				a.setMentionText(wordis.trim());
				a.setMentionText2(wordis.trim());
				a.setBeginOffset(startOffset+startOffset2);
				a.setEndOffset(endOffset+startOffset2);
				a.setBeginOffset2(startOffset+startOffset2);
				a.setEndOffset2(endOffset+startOffset2);
				
				annotations.add(a);

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
			if (capitalized(wordList[i])) {
				if (!this.hashStopWords.containsValue(wordList[i].trim()
						.toLowerCase())
						&& !this.hashStopWords.containsValue(symbolRemove(
								wordList[i]).toLowerCase())
						&& !this.hashHonorificsWords.containsValue(wordList[i]
								.trim())) {
					
					if (breakLine(wordList[i])) {
						return set + 1;
					}
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

		} else
			return set;
	}

	private boolean breakLine(String word) {
		if(word.equals("\n")){
			return true;
		}
		return false;
	}
	private boolean endOfSentenc(String word) {
			if (word.lastIndexOf(".") != -1) {
				if (!word.substring(word.lastIndexOf(".") + 1).equals(" ")){
					return false;

			} else if (word.length() > 2) {
				if (word.substring(word.trim().length() - 1).trim().matches("[\\.,?!“()”\"]")) {
					return true;
				}
			}else
				return false;
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
		if(spottedWord.substring(spottedWord.lastIndexOf(".")+1).equals(" ")){
			spottedWord = spottedWord.replaceAll("[\\,“()”?.'\"]", "");
		}
		return spottedWord = spottedWord.trim();
	}

}
