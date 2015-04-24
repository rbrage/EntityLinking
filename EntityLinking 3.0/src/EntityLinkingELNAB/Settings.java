package EntityLinkingELNAB;

public class Settings {

	/*
	 * Defult settings
	 */
	
	private int threshold = 100;
	private boolean pluralApostrophe_remove = false;
	private boolean symbols_remove = false;
	private boolean solvSuspectSpotts = false;
	
	boolean makegraf = false;
	boolean makefreebaseCalls = false;
	
	public Settings(){
		
	}
	
	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public boolean isPluralApostrophe_remove() {
		return pluralApostrophe_remove;
	}

	public void setPluralApostrophe_remove(boolean pluralApostrophe_remove) {
		this.pluralApostrophe_remove = pluralApostrophe_remove;
	}

	public boolean isSymbols_remove() {
		return symbols_remove;
	}

	public void setSymbols_remove(boolean symbols_remove) {
		this.symbols_remove = symbols_remove;
	}

	public boolean isSolvSuspectSpotts() {
		return solvSuspectSpotts;
	}

	public void setSolvSuspectSpotts(boolean solvSuspectSpotts) {
		this.solvSuspectSpotts = solvSuspectSpotts;
	}

	public boolean isMakegraf() {
		return makegraf;
	}

	public void setMakegraf(boolean makegraf) {
		this.makegraf = makegraf;
	}

	public boolean isMakefreebaseCalls() {
		return makefreebaseCalls;
	}

	public void setMakefreebaseCalls(boolean makefreebaseCalls) {
		this.makefreebaseCalls = makefreebaseCalls;
	}

}
