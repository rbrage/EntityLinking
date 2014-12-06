package EntityLinking;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.xml.sax.SAXException;

public class main {

	
	// /home/rbrage/Program/EntityLinking/db
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String folderPath = args[0];
		String programToUse = args[1];
		String flags = "Default"; // LingPipeSpotter, AtLeastOneNounSelector, NESpotter, ---- WikiMarkupSpotter, OpenNLPChunkerSpotter, KeyphraseSpotter
		if (args.length > 2)
			flags = args[2];
		
		System.out.println(folderPath + "|" + programToUse);

		if (programToUse.equals("Self")) {
			WordSpotter ws = new WordSpotter();
			ws.run(folderPath);
		} 
		else if (programToUse.equals("DBpedia")) {
			DbpediaSpotlight dbps = new DbpediaSpotlight();
			dbps.run(folderPath, flags);
		} 
		else if (programToUse.equals("wiki-miner")) {
			
			WikipediaConfiguration conf = new WikipediaConfiguration(
					new File(
							"/home/rbrage/Program/workspaceWiki/wikipedia-miner-1.2.0/configs/wikipedia.xml"));
			Wikipedia wikipedia = new Wikipedia(conf, false);
			
			WikiMiner wm = new WikiMiner(wikipedia);
			
			wm.run(folderPath, wm);
			
		} 
		else if(programToUse.equals("Score")){
			ScoreTable st = new ScoreTable();
			if(flags.equals("Defult")){
				System.out.println("Wrong flag use: mine, DBpedia, wiki-miner");
			}
			st.ScoreTable(folderPath, flags);
			
		}else {
			System.out.println("Need a program to start!");
			System.exit(1);
		}
	}

}
