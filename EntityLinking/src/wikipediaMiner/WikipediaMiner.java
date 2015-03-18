package wikipediaMiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.wikipedia.miner.annotation.Disambiguator;
import org.wikipedia.miner.annotation.Topic;
import org.wikipedia.miner.annotation.TopicDetector;
import org.wikipedia.miner.annotation.preprocessing.DocumentPreprocessor;
import org.wikipedia.miner.annotation.preprocessing.PreprocessedDocument;
import org.wikipedia.miner.annotation.preprocessing.WikiPreprocessor;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.Position;
import org.wikipedia.miner.util.WikipediaConfiguration;

import utility.PrintToFile;
import utility.Reader;

import com.sleepycat.je.cleaner.OffsetList;

public class WikipediaMiner {

	DecimalFormat _df = new DecimalFormat("#0%");
	DocumentPreprocessor _preprocessor;
	Disambiguator _disambiguator;
	TopicDetector _topicDetector;
	
	Reader reader;
	PrintToFile write;
	
	ArrayList<String> files;
	StringBuilder finalWords, finalString;
	String textToAnalyze;
	

	public WikipediaMiner(Wikipedia wikipedia) throws Exception {
		// TODO
		_preprocessor = new WikiPreprocessor(wikipedia);
		_disambiguator = new Disambiguator(wikipedia);
		_topicDetector = new TopicDetector(wikipedia, _disambiguator, true,
				false);
	}

	public String annotate(String originalMarkup) throws Exception {
		// TODO
		PreprocessedDocument doc = _preprocessor.preprocess(originalMarkup);
		Collection<Topic> allTopics = _topicDetector.getTopics(doc, null);
		
		HashMap<Integer, String> wordshash = new HashMap<Integer, String>();
		
		System.out.println("AllTopic size:" + allTopics.size());
		
		for (Topic t : allTopics){
			Vector<Position> position = t.getPositions();
			int offset = position.firstElement().getStart();
			wordshash.put(offset, t.getTitle());																									
		}
		
		this.finalString = new StringBuilder(doc.getPreprocessedText());
		this.finalWords = new StringBuilder();
		
		NavigableMap<Integer, String> map = new TreeMap<Integer, String>(wordshash);
		Set set2 = map.descendingMap().entrySet();
		Iterator iterator2 = set2.iterator();
		while (iterator2.hasNext()) {
			Map.Entry me2 = (Map.Entry) iterator2.next();
			String add = "[" + me2.getValue() + "]";
			int offset = (int) me2.getKey();
			this.finalWords.append(me2.getValue() +":" + offset+ "\n");
			this.finalString.insert(offset, add);
		}
		
		return this.finalString.toString();
	}

	public void run(String folderPath, WikipediaMiner annotator) throws Exception {
		this.reader = new Reader();
		this.files = this.reader.readFolder(folderPath+"/original");
		this.write = new PrintToFile();

		long startTime = 0;
		long stopTime = 0;

		// Start the analyzing and prints the results to file		
		for (int i = 0; i < files.size(); i++) {
			this.textToAnalyze = this.reader.readFiles(files.get(i));
			startTime = System.currentTimeMillis();
			String analyzedText = annotator.annotate(this.textToAnalyze);
			stopTime = System.currentTimeMillis();
			String filename = files.get(i).substring(
					this.files.get(i).lastIndexOf("/") + 1,
					this.files.get(i).lastIndexOf("."));
			
			this.write.printToFile(analyzedText, folderPath, filename, false, "wiki-miner");
			this.write.printToFile(this.finalWords.toString(), folderPath, filename, true, "wiki-miner");
			long rtt = stopTime - startTime;
			this.write.printToFile(rtt, folderPath, filename, "wiki-miner", "Time");
			System.out.println("Article " +filename +" is done in: " + rtt);
		}
		System.out.println("Done spotting!");
			
	}
}
