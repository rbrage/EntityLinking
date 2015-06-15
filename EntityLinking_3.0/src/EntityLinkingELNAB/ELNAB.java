package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ELNAB {
	String textID;
	String originalText;

	Spotter spotter;
	LinkDetection ld;
	Disambiguation dis;
	Settings settings;

	List<Annotation> annotations;
	Annotation a;

	public ELNAB(String folderPath) {
		// Configurations
			this.settings = new Settings(folderPath);
	}

	public List<Annotation> analyze(String textID, String text)
			throws IOException {
		this.annotations = new ArrayList<Annotation>();
		spotter = new Spotter();
		ld = new LinkDetection();
		dis = new Disambiguation();

		this.annotations = spotter.spottWords(textID, text,settings);
		this.annotations = ld.runLinkDetection(annotations, text, settings);
		this.annotations = dis.runDisambiguation(annotations, settings);

		return this.annotations;

	}
}
