package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Spotter {
	HashMap<Integer, String> hashStopWords, NONhashStopWords,
			hashHonorificsWords;
	List<Annotation> annotations;
	Annotation a;
	
	public Spotter() throws IOException {
		annotations = new ArrayList<Annotation>();
	}
	
	public List<Annotation> spottWords(String textID, String textToAnalyse, Settings settings) {
		this.NONhashStopWords = settings.getNONhashStopWords();
		this.hashHonorificsWords = settings.getHashHonorificsWords();
		
		int startOffset = 0;
		int endOffset = 0;

		String regexp = "(?<=[\\s\\n])";
		String[] wordList = textToAnalyse.split(regexp);

		for (int i = 0; i < wordList.length; i++) {
			String wordis = "";
				/*
				 * Checks for capitalized word
				 */
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
						startOffset = textToAnalyse.indexOf(wordis, endOffset);
					} else
						startOffset = textToAnalyse.indexOf(wordis, endOffset);

					if (endOfSentenc(wordis)) {
						endOffset = startOffset + wordis.length() - 1;
					} else
						endOffset = startOffset + wordis.length();
			}

			regexp = "(?<=[\\n])";
			wordis = wordis.replaceAll(regexp, " ");
			wordis = wordis.trim();

			if (containsSymbols(wordis)) {
				while (endOfSentenc(wordis) || startWithSymbol(wordis)) {
					if (endOfSentenc(wordis)) {
						wordis = wordis.substring(0, wordis.length() - 1);
					}
					if (startWithSymbol(wordis)) {
						wordis = wordis.substring(1, wordis.length());
					}
				}
			}
			
			/*
			 * Add the capitalized word to annotation list with offsets.
			 */
			if (!wordis.isEmpty()
					&& !this.NONhashStopWords.containsValue(wordis.trim())) {
				a = new Annotation();
				a.setLinebreaks(new LinkedList<Integer>());
				a.setDocId(textID);
				a.setMentionText(wordis.trim());
				a.setBeginOffset(startOffset);
				a.setEndOffset(endOffset);
				a.setOriginalBeginOffset(startOffset);
				a.setOriginalEndOffset(endOffset);
				annotations.add(a);
				startOffset = endOffset;

			}
		}
		return annotations;
	}


	/*
	 *  Method for counting capitalized words
	 */
	private int countCapitalizedWord(String[] wordList, int i) {
		int set = 0;
		if (i < wordList.length) {
			if ((this.NONhashStopWords.containsValue(wordList[i].trim()) && capitalized(wordList[i+1])) || capitalized(wordList[i])) {
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
				return set;
		} else
			return set;
	}

	/*
	 * Function to detect linebreak
	 */
	private boolean breakLine(String word) {
		String regexp = "(?<=[\\n])";
		if (word.equals(regexp)) {
			return true;
		}
		return false;
	}
	
	/*
	 * Function to detect end of sentence
	 */
	private boolean endOfSentenc(String word) {
		String regexp = "(?<=[\\n])";
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
				}else if(word.contains(regexp)){
					return true;
				}
			} else
				return false;
		} else if(word.contains(regexp)){
			return true;
		}
		return false;

	}
	
	
	private boolean startWithSymbol(String word) {
		if (containsSymbols(word)) {
			if (word.substring(0, 1).trim().matches("[\\.,?!“()”\"]")) {
				return true;
			}
			return false;
		}
		return false;
	}

	/*
	 *  Method that checks if there is a capitalized letter in word.
	 */
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
	
	private boolean containsSymbols(String wordis) {
		Pattern p = Pattern
				.compile("[\\,“().”?':\"]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(wordis);
		boolean b = m.find();
		return b;
	}
}
