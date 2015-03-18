package main;

import gumpaper.GumpaperArticles;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.xml.sax.SAXException;

import EntityLinking.Disambiguation;
import EntityLinking.LinkDetection;
import EntityLinking.Spotter;

import wikipediaMiner.WikipediaMiner;

import dbpediaSpotlight.DbpediaSpotlight;

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

		switch (programToUse) {
		
		// Our method
		case "WordSpotter":
			Spotter ws = new Spotter();
			ws.init(folderPath);
			ws.runAnnalyse();
			LinkDetection ld = new LinkDetection();
			ld.runLinkDetection(folderPath, flags);
			Disambiguation dis = new Disambiguation(folderPath, flags);
			dis.runDisambiguation();
			System.out.println("Done");
			break;
			
		case "DBpediaSpotlight":
			DbpediaSpotlight dbps = new DbpediaSpotlight();
			dbps.run(folderPath, flags);
			break;
			
		case "Wiki-Miner":
			WikipediaConfiguration conf = new WikipediaConfiguration(
					new File(
							"/home/rbrage/Program/workspaceWiki/wikipedia-miner-1.2.0/configs/wikipedia.xml"));
			Wikipedia wikipedia = new Wikipedia(conf, false);
			WikipediaMiner wm = new WikipediaMiner(wikipedia);
			wm.run(folderPath, wm);
			break;
			
		case "Score":
			ScoreTable st = new ScoreTable();
			if(flags.equals("Defult")){
				System.out.println("Wrong flag use: WordSpotter, DBpedia, Wiki-Miner");
			}
			st.ScoreTable(folderPath, flags);
			break;
			
		case "LinkDetection":
			ld = new LinkDetection();
			if(flags.equals("Defult")){
				System.out.println("Wrong flag use: WordSpotter, DBpedia, Wiki-Miner");
			}
			long startTime = System.currentTimeMillis();
			ld.runLinkDetection(folderPath, flags);
			long stopTime = System.currentTimeMillis();
			long rtt = stopTime - startTime;
			System.out.println(rtt);
			
			break;
			
		case "Disambiguation":
			if(flags.equals("Defult")){
				System.out.println("Wrong flag use: WordSpotter, DBpedia, Wiki-Miner");
			}
			dis = new Disambiguation(folderPath, flags);
			startTime = System.currentTimeMillis();
			dis.runDisambiguation();
			stopTime = System.currentTimeMillis();
			rtt = stopTime - startTime;
			System.out.println(rtt);
			
			break;
			
		case "Gumpaper":
			GumpaperArticles gp = new GumpaperArticles();
			gp.GumpaperArticles();
			break;
			
		default:
			System.out.println("Need a program to start!");
			System.out.println("Use: folderpath to db + program to run.");
			System.out.println("Programs: WordSpotter, DBpediaSpotlight, Wiki-Miner, Score, Gumpaper");
			System.exit(1);
		}
	}

}
