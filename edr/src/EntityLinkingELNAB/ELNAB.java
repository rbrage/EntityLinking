package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.mongodb.DBCursor;

import utility.MongoDB;
import utility.PrintToFile;

public class ELNAB {
	String runId;
	String textID;
	String originalText;
	String folderPath = "/home/rbrage/Program/EntityLinking/db";

	Spotter spotter;
	LinkDetection ld;
	Disambiguation dis;
	Settings settings;

	List<Annotation> annotations;
	Annotation a;
	
	public List<Annotation> ELNAB(String runId, String textID, String text)
			throws IOException {
		this.annotations = new ArrayList<Annotation>();
		spotter = new Spotter();
		ld = new LinkDetection();
		dis = new Disambiguation();

		// Configurations
		settings = new Settings();
		settings.setThreshold(100);
		settings.setPluralApostrophe_remove(true);
		settings.setSymbols_remove(true);
		settings.setSolvSuspectSpotts(true);
		settings.setMakeGraphDisambigutation(true);
		
		settings.setMakefreebaseCalls(false);
		settings.setMakegraf(false);

		this.annotations = spotter.spottWords(textID, text);
		this.annotations = ld.runLinkDetection(annotations, text, settings);
		this.annotations = dis.runDisambiguation(annotations, settings);

		

		return this.annotations;

	}
}
