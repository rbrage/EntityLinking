package EntityLinkingELNAB;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import utility.Reader;

public class Settings {

	private String threshold;
	private String pluralApostrophe_remove;
	private String symbols_remove;
	private String solvSuspectSpotts;
	private String makeLocalRelatedness;
	private String localRelatedness_threshold;
	private String makeRelatednesGraph;
	
	HashMap<Integer, String> hashStopWords, NONhashStopWords,hashHonorificsWords;
	String[] listOfStopWords;
	String[] NONStopWords = { "and", "of", "in" };
	String[] honorifics = { "Mr.", "Master", "Ms.", "Miss.", "Mrs.", "Mx.",
			"Sir.", "Ma'am", "Dame", "Lord", "Lady", "Dr.", "PhD.", "Phd.",
			"Ph.D.", "MD.", "DO.", "DDS.", "DMD.", "OD.", "PhramD.", "DVM",
			"MBBS.", "MBChB", "BDS", "Prof.", "Pfc.", "Br.", "Sr.", "Fr.",
			"Rev.", "Pr.", "Elder.", "Adv.", "Counsellor", "JAN.", "FEB.",
			"MAR.", "APR.", "MAY.", "JUN.", "JUL.", "AUG.", "SEP.", "OCT.",
			"NOV.", "DES.", "jan.", "feb.", "mar.", "apr.", "may.", "jun.",
			"jul.", "aug.", "sep.", "oct.", "nov.", "dec." };
	
	public Settings(String folderPath){
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream(folderPath +"/config/config.properties");
			// load a properties file
			prop.load(input);
			setThreshold(prop.getProperty("threshold"));
			setPluralApostrophe_remove(prop.getProperty("pluralApostrophe_remove"));
			setSymbols_remove(prop.getProperty("symbols_remove"));
			setSolvSuspectSpotts(prop.getProperty("solvSuspectSpotts"));
			setLocalRelatedness(prop.getProperty("makeLocalRelatedness"));
			setLocalRelatednessThreshold(prop.getProperty("localRelatedness_threshold"));
			setRelatednesGraph(prop.getProperty("makeRelatednesGraph"));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	
		Reader reader = new Reader();
		try {
			this.listOfStopWords = reader
					.readFilesLines("/home/rbrage/Program/EntityLinking/en-stopwords.txt");
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
		return Integer.parseInt(threshold);
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public boolean isPluralApostrophe_remove() {
		return Boolean.parseBoolean(pluralApostrophe_remove);
	}

	public void setPluralApostrophe_remove(String pluralApostrophe_remove) {
		this.pluralApostrophe_remove = pluralApostrophe_remove;
	}

	public boolean isSymbols_remove() {
		return Boolean.parseBoolean(symbols_remove);
	}

	public void setSymbols_remove(String symbols_remove) {
		this.symbols_remove = symbols_remove;
	}

	public boolean isSolvSuspectSpotts() {
		return Boolean.parseBoolean(solvSuspectSpotts);
	}

	public void setSolvSuspectSpotts(String solvSuspectSpotts) {
		this.solvSuspectSpotts = solvSuspectSpotts;
	}
	
	public boolean isLocalRelatedness() {
		return Boolean.parseBoolean(makeLocalRelatedness);
	}

	public void setLocalRelatedness(String makeLocalRelatedness) {
		this.makeLocalRelatedness = makeLocalRelatedness;
	}
	
	public float getLocalRelatednessThreshold() {
		return Float.parseFloat(localRelatedness_threshold);
	}

	public void setLocalRelatednessThreshold(String localRelatedness_threshold) {
		this.localRelatedness_threshold = localRelatedness_threshold;
	}

	public boolean isRelatednesGraph() {
		return Boolean.parseBoolean(makeRelatednesGraph);
	}

	public void setRelatednesGraph(String makeRelatednesGraph) {
		this.makeRelatednesGraph = makeRelatednesGraph;
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
				+ ", pluralApostrophe_remove=" + pluralApostrophe_remove
				+ ", symbols_remove=" + symbols_remove + ", solvSuspectSpotts="
				+ solvSuspectSpotts + ", makeLocalRelatedness="
				+ makeLocalRelatedness + ", localRelatedness_threshold="
				+ localRelatedness_threshold + ", makeRelatednesGraph="
				+ makeRelatednesGraph + ", hashStopWords=" + hashStopWords
				+ ", NONhashStopWords=" + NONhashStopWords
				+ ", hashHonorificsWords=" + hashHonorificsWords
				+ ", listOfStopWords=" + Arrays.toString(listOfStopWords)
				+ ", NONStopWords=" + Arrays.toString(NONStopWords)
				+ ", honorifics=" + Arrays.toString(honorifics) + "]";
	}

}
