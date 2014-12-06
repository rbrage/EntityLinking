package EntityLinking;

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

public class DbpediaSpotlight {

	Reader reader;
	ArrayList<String> files;
	String textToAnalyze;
	HttpURLConnection connection;
	PrintToFile write;
	StringBuilder finalWords;
	StringBuilder finalString;
	StringBuilder text;
	
	public void run(String folderPath, String spotter) throws IOException {
		reader = new Reader();
		write = new PrintToFile();
		files = reader.readFolder(folderPath);
		String words;
		finalWords = new StringBuilder();
	

		long startTime = 0;
		long stopTime = 0;
		
		for (int i = 0; i < files.size(); i++) {
			
			textToAnalyze = reader.readFiles(files.get(i));
			startTime = System.currentTimeMillis();
			words = sendTextAndGetWords(textToAnalyze, spotter);
			stopTime = System.currentTimeMillis();
			String filename = files.get(i).substring(
					files.get(i).lastIndexOf("/") + 1,
					files.get(i).lastIndexOf("."));
			
			write.printToFile(words, folderPath, filename, false, "DBpedia", spotter);
			write.printToFile(finalWords.toString(), folderPath, filename+"WORDS",true, "DBpedia", spotter);
			long rtt = stopTime - startTime;
			write.printToFile(rtt, folderPath, filename, "DBpedia", spotter, "Time");
			System.out.println("Article " +filename +" is done in: " + rtt);
		}
		System.out.println("Done!");

	}

	private String sendTextAndGetWords(String textToAnalyze, String spotter) {

		
				
		text = new StringBuilder();
		finalString = new StringBuilder();
		finalWords = new StringBuilder();
		
		HashMap<Integer, String> wordshash = new HashMap<Integer, String>();
		try {

			String url = "http://127.0.0.1:2220/rest/spot"; // annotate

			URL obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("POST");
			// conn.setRequestProperty("Content-Type", "text/xml");
			// conn.setRequestProperty("Accept:", "text/xml");
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
				text.append(element.getAttribute("text") + "\n");
			}
			
			NodeList nodes = doc.getElementsByTagName("surfaceForm");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				wordshash.put(Integer.parseInt(element.getAttribute("offset")), element.getAttribute("name"));
				
			}
			
			finalString = new StringBuilder(text);
			System.out.println(text.toString().length());
			NavigableMap<Integer, String> map = new TreeMap<Integer, String>(wordshash);
			Set set2 = map.descendingMap().entrySet();
			Iterator iterator2 = set2.iterator();
			while (iterator2.hasNext()) {
				Map.Entry me2 = (Map.Entry) iterator2.next();
				String add = "[" + me2.getValue() + "]";
				int offset = (int) me2.getKey();
				finalWords.append(me2.getValue() +":" + offset+ "\n");
				finalString.insert(offset, add);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalString.toString();
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

