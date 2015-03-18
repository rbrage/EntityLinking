package dbpediaSpotlight;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import utility.PrintToFile;
import utility.Reader;

public class DbpediaSpotlight {

	Reader reader;
	PrintToFile write;
	
	ArrayList<String> files;
	String textToAnalyze;
	StringBuilder finalWords, finalString, originalText;
	
	public void run(String folderPath, String spotter) throws IOException {
		this.reader = new Reader();
		this.write = new PrintToFile();
		this.files = reader.readFolder(folderPath+"/original");
		String words;
		this.finalWords = new StringBuilder();
	

		long startTime = 0;
		long stopTime = 0;
		
		// Start the analyzing and prints the results to file
		for (int i = 0; i < files.size(); i++) {
			this.textToAnalyze = this.reader.readFiles(files.get(i));
			startTime = System.currentTimeMillis();
			words = sendTextAndGetWords(this.textToAnalyze, spotter);
			stopTime = System.currentTimeMillis();
			String filename = files.get(i).substring(
					this.files.get(i).lastIndexOf("/") + 1,
					this.files.get(i).lastIndexOf("."));
			
			this.write.printToFile(words, folderPath, filename, false, "DBpedia", spotter);
			this.write.printToFile(this.finalWords.toString(), folderPath, filename+"WORDS",true, "DBpedia", spotter);
			long rtt = stopTime - startTime;
			this.write.printToFile(rtt, folderPath, filename, "DBpedia", spotter, "Time");
			System.out.println("Article " +filename +" is done in: " + rtt);
		}
		System.out.println("Done spotting!");

	}

	private String sendTextAndGetWords(String textToAnalyze, String spotter) {		
		this.originalText = new StringBuilder();
		this.finalString = new StringBuilder();
		this.finalWords = new StringBuilder();
		
		HashMap<Integer, String> wordshash = new HashMap<Integer, String>();
		try {
			HttpURLConnection connection;
			String url = "http://127.0.0.1:2220/rest/spot"; // annotate
			
			URL obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			String data;
			if (spotter.isEmpty()) {
				data = "text=" + textToAnalyze;
			} else {
				data = "text=" + textToAnalyze + "&spotter=" + spotter;
			}

			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			out.write(data);
			out.close();
			

			Document doc = parseXML(connection.getInputStream());
			
			NodeList nodes1 = doc.getElementsByTagName("annotation");
			for (int i = 0; i < nodes1.getLength(); i++) {
				Element element = (Element) nodes1.item(i);
				originalText.append(element.getAttribute("text") + "\n");
			}
			
			NodeList nodes = doc.getElementsByTagName("surfaceForm");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				wordshash.put(Integer.parseInt(element.getAttribute("offset")), element.getAttribute("name"));
				
			}
			
			this.finalString = new StringBuilder(originalText);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.finalString.toString();
	}

	private Document parseXML(InputStream stream) throws Exception {
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		try {
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

			doc = objDocumentBuilder.parse(stream);
		} catch (Exception ex) {
			throw ex;
		}

		return doc;
	}

}

// curl http://localhost:2222/rest/annotate \
// -H "Accept: text/xml" \
// --data-urlencode
// "text=Brazilian state-run giant oil company Petrobras signed a three-year technology and research cooperation agreement with oil service provider Halliburton."
// \
// --data "confidence=0" \
// --data "support=0"

