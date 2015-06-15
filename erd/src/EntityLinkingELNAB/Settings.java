package EntityLinkingELNAB;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import utility.Reader;

public class Settings {

	private int threshold;
	private boolean pluralApostrophe_remove;
	private boolean symbols_remove;
	private boolean solvSuspectSpotts;
	private boolean makeLocalRelatedness;
	private float localRelatedness_threshold;
	private boolean makeRelatednesGraph;
	
	HashMap<Integer, String> hashStopWords, NONhashStopWords, hashHonorificsWords;
	String[] listOfStopWords;
	String[] NONStopWords = { "and", "of", "in" };
	String[] honorifics = { "Mr.", "Master", "Ms.", "Miss.", "Mrs.", "Mx.",
			"Sir.", "Ma'am", "Dame", "Lord", "Lady", "Dr.", "PhD.", "Phd.",
			"Ph.D.", "MD.", "DO.", "DDS.", "DMD.", "OD.", "PhramD.", "DVM",
			"MBBS.", "MBChB", "BDS", "Prof.", "Pfc.", "Br.", "Sr.", "Fr.",
			"Rev.", "Pr.", "Elder.", "Adv.", "Counsellor" };
	
	public Settings(String folderPath){
		
		setThreshold(10);
		setPluralApostrophe_remove(true);
		setSymbols_remove(true);
		setSolvSuspectSpotts(true);
		setLocalRelatedness(true);
		setLocalRelatednessThreshold(5.0f);
	
		Reader reader = new Reader();
		try {
			this.listOfStopWords = reader
					.readFilesLines(folderPath+"/en-stopwords.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.hashStopWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.listOfStopWords.length; i++) {
			this.hashStopWords.put(i, this.listOfStopWords[i]);
		}
		this.NONhashStopWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.NONStopWords.length; i++) {
			this.NONhashStopWords.put(i, this.NONStopWords[i]);
		}

		this.hashHonorificsWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.honorifics.length; i++) {
			this.hashHonorificsWords.put(i, this.honorifics[i]);
		}

		
	}
	
	public int getThreshold() {
		return threshold; //Integer.parseInt(threshold);
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public boolean isPluralApostrophe_remove() {
		return pluralApostrophe_remove;// Boolean.parseBoolean(pluralApostrophe_remove);
	}

	public void setPluralApostrophe_remove(boolean pluralApostrophe_remove) {
		this.pluralApostrophe_remove = pluralApostrophe_remove;
	}

	public boolean isSymbols_remove() {
		return symbols_remove;//Boolean.parseBoolean(symbols_remove);
	}

	public void setSymbols_remove(boolean symbols_remove) {
		this.symbols_remove = symbols_remove;
	}

	public boolean isSolvSuspectSpotts() {
		return solvSuspectSpotts;//Boolean.parseBoolean(solvSuspectSpotts);
	}

	public void setSolvSuspectSpotts(boolean solvSuspectSpotts) {
		this.solvSuspectSpotts = solvSuspectSpotts;
	}
	
	public boolean isLocalRelatedness() {
		return makeLocalRelatedness;//Boolean.parseBoolean(makeLocalRelatedness);
	}

	public void setLocalRelatedness(boolean makeLocalRelatedness) {
		this.makeLocalRelatedness = makeLocalRelatedness;
	}
	
	public float getLocalRelatednessThreshold() {
		return localRelatedness_threshold;//Integer.parseInt(localRelatedness_threshold);
	}

	public void setLocalRelatednessThreshold(float localRelatedness_threshold) {
		this.localRelatedness_threshold = localRelatedness_threshold;
	}

	public HashMap<Integer, String> getHashStopWords() {
		return this.hashStopWords;
	}

	public HashMap<Integer, String> getNONhashStopWords() {
		return this.NONhashStopWords;
	}

	public HashMap<Integer, String> getHashHonorificsWords() {
		return hashHonorificsWords;
	}

	@Override
	public String toString() {
		return "Settings [threshold=" + threshold
				+ ",\n pluralApostrophe_remove=" + pluralApostrophe_remove
				+ ",\n symbols_remove=" + symbols_remove + ",\n solvSuspectSpotts="
				+ solvSuspectSpotts + ",\n makeLocalRelatedness="
				+ makeLocalRelatedness + ",\n localRelatedness_threshold="
				+ localRelatedness_threshold + ",\n makeRelatednesGraph="
				+ makeRelatednesGraph + ",\n hashStopWords=" + hashStopWords
				+ ",\n NONhashStopWords=" + NONhashStopWords
				+ ",\n hashHonorificsWords=" + hashHonorificsWords
				+ ",\n listOfStopWords=" + Arrays.toString(listOfStopWords)
				+ ",\n NONStopWords=" + Arrays.toString(NONStopWords)
				+ ",\n honorifics=" + Arrays.toString(honorifics) + "]";
	}

	
	

}
