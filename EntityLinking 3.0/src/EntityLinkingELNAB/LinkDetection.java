package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	Reader reader;
	Settings settings;
	
	String[] listOfStopWords;
	HashMap<Integer, String> hashStopWords;
	String originalText;
	

	public List<Annotation> runLinkDetection(List<Annotation> annotations, String originalText, Settings settings) throws IOException {
		this.annotations = annotations;
		this.threshold = settings.getThreshold();
		this.originalText = originalText;
		this.settings = settings;
		
		List<Annotation> annotations_extra = new ArrayList<Annotation>();
		List<Annotation> annotations_tmp = new ArrayList<Annotation>();
		
		mDB = new MongoDB("surfaceforms", "surfaceforms");
		reader = new Reader();

		this.listOfStopWords = reader
				.readFilesLines("/home/rbrage/Program/EntityLinking/en-stopwords.txt");
		
		this.hashStopWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.listOfStopWords.length; i++) {
			this.hashStopWords.put(i, this.listOfStopWords[i]);
		}
		
		for (Annotation a : this.annotations) {
			String spottedWord = a.getMentionText();
			DBCursor cursor = mDB.getCandidates(spottedWord);
			
			if (!cursor.hasNext()) {
				if (pluralApostrophe(spottedWord) && settings.isPluralApostrophe_remove()) {
					spottedWord = pluralApostropheRemove(spottedWord);
				}
				if (containsSymbols(spottedWord) && settings.isSymbols_remove()) {
					spottedWord = symbolRemove(spottedWord);
				}
				cursor = mDB.getCandidates(spottedWord);
			}
			if (!cursor.hasNext() && settings.isSolvSuspectSpotts()) {
				annotations_extra = solveSuspectSpot(a.getMentionText(), a);
				for (Annotation annotation_extra : annotations_extra) {
					if (!annotation_extra.getFacc12().isEmpty()){
						annotations_tmp.add(annotation_extra);
					}
				}
			}
			
			a.setDbpediaCandidate(new HashMap<String, Integer>());
			a.setFacc12(new LinkedHashMap<String, Long>());

			try {
				while (cursor.hasNext()) {
					DBObject next = cursor.next();
					a.setShearchWord(spottedWord);
					if (next.get("<foaf:name>") != null) {
						a.getDbpediaCandidate().putAll(
								(Map) next.get("<foaf:name>"));
					}
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
					}
				}

			} finally {
				cursor.close();
			}
		}
		for (Annotation annotation_extra : annotations_tmp) {
			this.annotations.add(annotation_extra);
		}

		return this.annotations;
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

		return spottedWord;
	}

	private List<Annotation> solveSuspectSpot(String spottedWord, Annotation a) {
		DBCursor cursor = null;
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
								tmpA.setMentionText(testphrase.toString().trim());
								tmpA.setShearchWord(spottedWord_testphase);
								tmpA.setMentionText2(a.getMentionText2());
								tmpA.setBeginOffset2(a.getBeginOffset2());
								tmpA.setEndOffset2(a.getEndOffset2());
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
				}else if (testphrase.toString().split(" ").length == 1) {
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
					newBeginOffset = a.getMentionText().indexOf(tmpA1.getMentionText().trim(), newBeginOffset);
				}else{
					newBeginOffset = a.getMentionText().indexOf(tmpA1.getMentionText().substring(0, tmpA1.getMentionText().indexOf(" ")), newBeginOffset);
				}
				newEndOffset = newBeginOffset + tmpA1.getMentionText().length();
				
				tmpA1.setSuspect(true);
//				tmpA1.setScore1(2f);
				tmpA1.setBeginOffset(newBeginOffset + a.getBeginOffset());
				tmpA1.setEndOffset(newEndOffset + a.getBeginOffset());
				
			}
		}
		
		return tmpAnnotations;
	}
}
