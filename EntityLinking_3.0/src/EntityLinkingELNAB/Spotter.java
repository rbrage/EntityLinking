package EntityLinkingELNAB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spotter { // WordSpotter
	HashMap<Integer, String> NONhashStopWords, hashHonorificsWords;
	List<Annotation> annotations;
	Annotation a;
	int set = 0;
	public Spotter() {
		annotations = new ArrayList<Annotation>();
	}

	public List<Annotation> spottWords(String textID, String textToAnalyse,
			Settings settings) {
		this.NONhashStopWords = settings.getNONhashStopWords();
		this.hashHonorificsWords = settings.getHashHonorificsWords();

		int startOffset = 0;
		int endOffset = 0;
		int linebreakOffset = 0;
		int numberOfLineBreakRN = 0;
		int numberOfLineBreakR = 0;
		int numberOfLineBreakN = 0;
		LinkedList<Integer> linebreaks = new LinkedList<Integer>();

		if (textToAnalyse.contains("\r\n")) {
			numberOfLineBreakRN = textToAnalyse.length()
					- textToAnalyse.replaceAll("\r\n", "").length();
		}
		if (textToAnalyse.contains("\r")) {
			numberOfLineBreakR = textToAnalyse.length()
					- textToAnalyse.replaceAll("\r", "").length();
		}
		if (textToAnalyse.contains("\n")) {
			numberOfLineBreakN = textToAnalyse.length()
					- textToAnalyse.replaceAll("\n", "").length();
		}
		if ((numberOfLineBreakN + numberOfLineBreakR) == numberOfLineBreakRN) {
			int fromIndex = 0;
			for (int i = 0; i < numberOfLineBreakR; i++) {
				fromIndex = textToAnalyse.indexOf("\r", fromIndex + 1);
				linebreaks.add(fromIndex);
			}
		}

		String regexp = "(?<=[\\s\\n])";
		String[] wordList = textToAnalyse.split(regexp);
		for (int i = 0; i < wordList.length; i++) {
			String wordis = "";
			
			/*
			 * Checks for honorifics word
			 */
			if (this.hashHonorificsWords.containsValue(wordList[i])) {
				int wr = countCapitalizedWord(wordList, i + 1);
				for (int j = i; j <= (i + wr); j++) {
					wordis = wordis + wordList[j];
				}
				i = i + wr + 1;
				wordis = wordis.trim();
				startOffset = textToAnalyse.indexOf(wordis, endOffset);
				endOffset = startOffset + wordis.length();

			} else {

				/*
				 * Checks for capitalized word
				 */
				if (capitalized(wordList[i])) {
					set = 0;
					int wr = countCapitalizedWord(wordList, i);
					for (int j = i; j <= (i + wr - 1); j++) {
						wordis = wordis + wordList[j];
					}
					if (wr > 1) {
						i = i + wr - 1;
					}
					if (textToAnalyse.indexOf(wordis.trim(), startOffset) != -1) {
						wordis = wordis.trim();
						startOffset = textToAnalyse
								.indexOf(wordis, startOffset);

					} else {
						startOffset = textToAnalyse
								.indexOf(wordis, startOffset);
					}

					if (endOfSentenc(wordis)) {
						endOffset = startOffset + wordis.length() - 1;
					} else
						endOffset = startOffset + wordis.length();
				}
			}
			
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
				a.setLinebreaks(new LinkedList<Integer>(linebreaks));
				a.setDocId(textID);
				a.setMentionText(wordis.trim());
				a.setBeginOffset(startOffset + linebreakOffset);
				a.setEndOffset(endOffset + linebreakOffset);
				a.setOriginalBeginOffset(startOffset+linebreakOffset);
				a.setOriginalEndOffset(endOffset+linebreakOffset);
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
		if (i < wordList.length) {
			if ((this.NONhashStopWords.containsValue(wordList[i].trim()) && capitalized(wordList[i+1]) )
					|| capitalized(wordList[i])) {
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
		if (word.indexOf("\n") != -1 || word.indexOf("\r") != -1) {
			return true;
		}
		return false;
	}

	/*
	 * Function to detect end of sentence
	 */
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
				} else if (word.contains("\n")) {
					return true;
				}
			} else
				return false;
		} else if (word.contains("\n")) {
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

