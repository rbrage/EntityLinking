package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utility.MongoDB;
import utility.Reader;

import org.json.simple.JSONObject;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class LinkDetection {
	
	int threshold;
	
	Annotation a;
	List<Annotation> annotations;
	List<Annotation> tmp;
	
	MongoDB mDB;
	Settings settings;
	
	String[] listOfStopWords;
	HashMap<Integer, String> hashStopWords, NONhashStopWords;
	String originalText;

	public List<Annotation> runLinkDetection(List<Annotation> annotations, String originalText, Settings settings) throws IOException {
		this.annotations = annotations;
		this.threshold = settings.getThreshold();
		this.originalText = originalText;
		this.settings = settings;
		this.hashStopWords = settings.getHashStopWords();
		this.NONhashStopWords = settings.getNONhashStopWords();
		
		List<Annotation> annotations_extra = new ArrayList<Annotation>();
		List<Annotation> annotations_tmp = new ArrayList<Annotation>();
		
		mDB = new MongoDB("surfaceforms", "surfaceforms");
		/*
		 * Look for a match in surface forms, negative feedback is sent to solve suspect. 
		 */
		for (Annotation a : this.annotations) {
			String spottedWord = a.getMentionText();
			
			if (pluralApostrophe(spottedWord) && settings.isPluralApostrophe_remove()) {
				spottedWord = pluralApostropheRemove(spottedWord);
			}
			
			DBCursor cursor = mDB.getCandidates(spottedWord);
			if (!cursor.hasNext()) {
				if (containsSymbols(spottedWord) && settings.isSymbols_remove()) {
					spottedWord = symbolRemove(spottedWord);
				}
				cursor = mDB.getCandidates(spottedWord);
			}
			if (!cursor.hasNext() && settings.isSolvSuspectSpotts()) {
				if(a.getMentionText().split(" ").length >1){
					annotations_extra = solveSuspectSpot(a.getMentionText(), a);
					for (Annotation annotation_extra : annotations_extra) {
						if (!annotation_extra.getFacc12().isEmpty()){
							annotations_tmp.add(annotation_extra);
						}
					}
				}
			}
			
			a.setDbpediaCandidate(new HashMap<String, Integer>());
			a.setFacc12(new LinkedHashMap<String, Long>());

			try {
				while (cursor.hasNext()) {
					DBObject next = cursor.next();
					a.setShearchWord(spottedWord);
					if (next.get("facc12") != null) {
						JSONObject tmp_facc_obj = new JSONObject();
						tmp_facc_obj.putAll((Map) next.get("facc12"));
						Set<String> facc_set = tmp_facc_obj.keySet();

						for (String facc : facc_set) {
							long facc_mentions = (int) tmp_facc_obj.get(facc);

							if (facc_mentions > threshold) {
								a.getFacc12().put(facc, facc_mentions);
							}
						}
						if(a.getFacc12().isEmpty()){
							if(settings.isSolvSuspectSpotts()){
								if(spottedWord.split(" ").length >1){
									annotations_extra = solveSuspectSpot(spottedWord, a);
									for (Annotation annotation_extra : annotations_extra) {
										if (!annotation_extra.getFacc12().isEmpty()){
											annotations_tmp.add(annotation_extra);
										}
									}
								}
							}
						}
					}
				}
				a.setScore1(2f);

			} finally {
				cursor.close();
			}
		}
		/*
		 * Merge the annotations found in solve suspect with the others. And removes the note solved once. 
		 */
		for (Annotation annotation_extra : annotations_tmp) {
			this.annotations.add(annotation_extra);
		}
		
		Iterator<Annotation> annotations_iter = this.annotations.iterator();
		while (annotations_iter.hasNext()) {
			Annotation a = annotations_iter.next();
			if(a.getFacc12().isEmpty())
				annotations_iter.remove();
		}
		
		return this.annotations;
	}
	/*
	 * Function for solving suspect phrases in a window sliding technique. 
	 */
	private List<Annotation> solveSuspectSpot(String spottedWord, Annotation a) {
		DBCursor cursor = null;
		spottedWord = a.getMentionText();
		List<Annotation> tmpAnnotations = new ArrayList<Annotation>();
		String[] w = spottedWord.split(" ");

		int j = 0;
		int i = 0;
		int x = 0;
		int y = 0;
		StringBuilder testphrase = new StringBuilder();

		if (w.length >= 1) {
			while (i < w.length) {
				testphrase = new StringBuilder();
				while (j < w.length - y) {
					testphrase.append(w[j] + " ");
					j++;
				}
				i++;
				y++;
				
				if(this.hashStopWords.containsValue(testphrase.toString().trim().toLowerCase())){
					testphrase = new StringBuilder();
					y = 0;
					x = j;
					i = x;
					continue;
				}
				
				String spottedWord_testphase = testphrase.toString().trim();
				String spottedWord_testphase1 = testphrase.toString().trim();
				
				if (pluralApostrophe(spottedWord_testphase) && this.settings.isPluralApostrophe_remove()) {
					spottedWord_testphase = pluralApostropheRemove(spottedWord_testphase);
				}
				if (containsSymbols(spottedWord_testphase) && this.settings.isSymbols_remove()) {
					spottedWord_testphase = symbolRemove(spottedWord_testphase);
				}
				cursor = mDB.getCandidates(spottedWord_testphase);
					
				if (cursor.hasNext()) {
					try {
						while (cursor.hasNext()) {
							DBObject next = cursor.next();
							if (next.get("facc12") != null) {
								Annotation tmpA = new Annotation();
								tmpA.setDocId(a.getDocId());
								if(containsSymbols(spottedWord_testphase1)){
									while(endOfSentenc(spottedWord_testphase1) || startWithSymbol(spottedWord_testphase1)){
										if(endOfSentenc(spottedWord_testphase1)){
											spottedWord_testphase1 = spottedWord_testphase1.substring(0, spottedWord_testphase1.length()-1);
										}
										if(startWithSymbol(spottedWord_testphase1)){
											spottedWord_testphase1 = spottedWord_testphase1.substring(1, spottedWord_testphase1.length());
										}
									}
								}
								tmpA.setMentionText(spottedWord_testphase1);
								tmpA.setShearchWord(spottedWord_testphase);
								tmpA.setOriginalBeginOffset(a.getOriginalBeginOffset());
								tmpA.setOriginalEndOffset(a.getOriginalEndOffset());
								tmpA.setScore1(1f);
								tmpA.setFacc12(new LinkedHashMap<String, Long>());

								JSONObject tmp_facc_obj = new JSONObject();
								tmp_facc_obj.putAll((Map) next.get("facc12"));
								Set<String> facc_set = tmp_facc_obj.keySet();
								for (String facc : facc_set) {
									long facc_mentions = (int) tmp_facc_obj
											.get(facc);
									if (facc_mentions > threshold) {
										tmpA.getFacc12().put(facc,
												facc_mentions);
									}
								}
								tmpAnnotations.add(tmpA);
								testphrase = new StringBuilder();
								y = 0;
								x = j;
								i = x;
							} 
						}

					} finally {
						cursor.close();
					}
				}else if (testphrase.toString().split(" ").length <= 1) {
						testphrase = new StringBuilder();
						x++;
						y = 0;
						j = x;
						i = x;
					
				} else {
					j = x;
				}
				
			}
		}
		int newBeginOffset = 0;
		int newEndOffset = 0;
		
		for (Annotation tmpA1 : tmpAnnotations) {
			if (!tmpA1.getFacc12().isEmpty()) {
				if(a.getMentionText().indexOf(tmpA1.getMentionText().trim()) != -1){
					newBeginOffset = a.getMentionText().indexOf(tmpA1.getMentionText().trim(), newEndOffset-1);
				}else{
					newBeginOffset = a.getMentionText().indexOf(tmpA1.getMentionText(), newEndOffset-1);
				}
				newEndOffset = newBeginOffset + tmpA1.getMentionText().length();
				
				tmpA1.setSuspect(true);
				tmpA1.setBeginOffset(newBeginOffset + a.getBeginOffset());
				tmpA1.setEndOffset(newEndOffset + a.getBeginOffset());
				
			}
		}
		
		return tmpAnnotations;
	}
	private boolean containsSymbols(String wordis) {
		Pattern p = Pattern.compile("[\\,“().”?':\"]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(wordis);
		boolean b = m.find();
		return b;
	}

	private boolean pluralApostrophe(String word) {
		if (word.contains("’s") || word.contains("´s") || word.contains("'s")) {
			return true;
		} else {
			return false;
		}
	}

	private String symbolRemove(String spottedWord) {
			spottedWord = spottedWord.replaceAll("[\\,“()”?.'\"]", "");
		return spottedWord = spottedWord.trim();
	}

	private String pluralApostropheRemove(String spottedWord) {
		if (spottedWord.contains("’s"))
			return spottedWord = spottedWord.substring(0,
					spottedWord.lastIndexOf("’s"));
		if (spottedWord.contains("´s"))
			return spottedWord = spottedWord.substring(0,
					spottedWord.lastIndexOf("´s"));
		if (spottedWord.contains("'s"))
			return spottedWord = spottedWord.substring(0,
					spottedWord.lastIndexOf("'s"));
		if (spottedWord.contains("'s"))
			return spottedWord = spottedWord.substring(0,
					spottedWord.lastIndexOf("'s"));
		return spottedWord;
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
				}
			} else
				return false;
		}
		return false;

	}
	private boolean startWithSymbol(String word) {
		if (containsSymbols(word)) {
			if (word.length() > 3) {
				if (word.substring(0,1).trim()
						.matches("[\\.,?!“()”\"]")) {
					return true;
				}
			}else if (word.substring(0,1).trim()
					.matches("[\\.,?!“()”\"]")) {
				return true;
			}
		} else
			return false;
		return false;

	}
}

