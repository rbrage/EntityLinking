package EntityLinking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utility.MongoDB;
import utility.PrintToFile;
import utility.Reader;
import org.json.simple.JSONObject;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class LinkDetection {
	Reader reader;
	PrintToFile write;
	String folderPath;

	ArrayList<String> analyzedfiles;
	ArrayList<String> spottedWordsJSONFiles;

	String goldenText;
	String analysedText;
	String spottedWordsString;
	HashMap<Integer, String> spottedWords, hashHonorificsWords;
	
	// List of honorifics to check.
	String[] honorifics = { "Mr.", "Master", "Ms.", "Miss.", "Mrs.", "Mx.",
				"Sir.", "Ma'am", "Dame", "Lord", "Lady", "Dr.", "PhD.", "Phd.",
				"Ph.D.", "MD.", "DO.", "DDS.", "DMD.", "OD.", "PhramD.", "DVM",
				"MBBS.", "MBChB", "BDS", "Prof.", "Pfc.", "Br.", "Sr.", "Fr.",
				"Rev.", "Pr.", "Elder.", "Adv.", "Counsellor", "JAN.", "FEB.",
				"MAR.", "APR.", "MAY.", "JUN.", "JUL.", "AUG.", "SEP.", "OCT.",
				"NOV.", "DES.", "jan.", "feb.", "mar.", "apr.", "may.", "jun.",
				"jul.", "aug.", "sep.", "oct.", "nov.", "dec." };

	/**
	 * @param args
	 * @throws IOException
	 */
	public void runLinkDetection(String folderPath, String prog)
			throws IOException {
		this.reader = new Reader();
		this.write = new PrintToFile();
		this.folderPath = folderPath;

		this.analyzedfiles = this.reader.readFolder(this.folderPath + "/results_" + prog);
		this.spottedWordsJSONFiles = this.reader.readFolder(this.folderPath + "/results_"
				+ prog + "/JSON");
		
		this.hashHonorificsWords = new HashMap<Integer, String>();
		for (int i = 0; i < this.honorifics.length; i++) {
			this.hashHonorificsWords.put(i, this.honorifics[i]);
		}

		MongoDB mDB = new MongoDB();
		//
		for (int i = 0; i < this.spottedWordsJSONFiles.size(); i++) {
			JSONObject spottedWords_obj = reader.readJSON(this.spottedWordsJSONFiles
					.get(i));

			String filename = this.spottedWordsJSONFiles.get(i).substring(
					this.spottedWordsJSONFiles.get(i).lastIndexOf("/") + 1,
					this.spottedWordsJSONFiles.get(i).lastIndexOf("."));

			Set<String> spottedWord_keys = spottedWords_obj.keySet();
			System.out.println("File: " + filename + "\t--------" + i + "/"
					+ (this.spottedWordsJSONFiles.size() - 1) + "-----------");

			for (String spottedWord : spottedWord_keys) {
				JSONObject spottedWord_obj = (JSONObject) spottedWords_obj.get(spottedWord);
				JSONObject candidate_obj = new JSONObject();
				JSONObject lable_obj = new JSONObject();
				JSONObject facc_obj = new JSONObject();

				DBCursor cursor = mDB.getCandidates(spottedWord);
				
				try {
					if(!cursor.hasNext()){
//						String[] tmpSW = spottedWord.split(" ");
//						
//						if(tmpSW.length > 1 && this.hashHonorificsWords.containsValue(tmpSW[0])){
//							System.out.println(tmpSW[0]+"|"+tmpSW[1]);
//								cursor = mDB.getCandidates(tmpSW[1]);
//						}
//						else
								System.out.println("Word not fond:\t" + spottedWord);
						
					}
					while (cursor.hasNext()) {
						DBObject next = cursor.next();
						
						if(next.get("<rdfs:label>") != null)
							lable_obj.putAll((Map) next.get("<rdfs:label>"));
						
						if (next.get("<foaf:name>") != null)
							candidate_obj.putAll((Map) next.get("<foaf:name>"));
						
						if (next.get("facc12") != null) {
							JSONObject tmp_facc_obj = new JSONObject();
							tmp_facc_obj.putAll((Map) next.get("facc12"));
							Set<String> facc_set = tmp_facc_obj.keySet();
							
							for (String facc : facc_set) {
								int facc_mentions = (int) tmp_facc_obj
										.get(facc);
								
								if (facc_mentions > 500) {
									facc_obj.put(facc, facc_mentions);
								}
							}
						}
					}
				} finally {
					cursor.close();
				}
				spottedWord_obj.put("Lable", lable_obj);
				spottedWord_obj.put("Candidate", candidate_obj);
				spottedWord_obj.put("Facc12", facc_obj);
			}
			this.write.printWordToJSON(spottedWords_obj, this.folderPath,
					filename, "WordSpotter");

		}

	}

}

