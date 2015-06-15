package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ELNAB {
	String runId;
	String textID;
	String originalText;
	String folderPath = "src/main/webapp/";
	
	Spotter spotter;
	LinkDetection ld;
	Disambiguation dis;
	Settings settings;

	List<Annotation> annotations;
	Annotation a;
	
	public List<Annotation> ELNAB(String runId, String textID, String text, Settings settings)
			throws IOException {
		// Configurations
		this.settings = settings;

		this.annotations = new ArrayList<Annotation>();
		spotter = new Spotter();
		ld = new LinkDetection();
		dis = new Disambiguation();
		
		this.annotations = spotter.spottWords(textID, text, this.settings);
		this.annotations = ld.runLinkDetection(annotations, text, this.settings);
		this.annotations = dis.runDisambiguation(annotations, this.settings);

		return this.annotations;

	}
}
