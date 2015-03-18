package documentMaker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONObject;

public class DocumentMaker {
	private String type;
	private String forderPath;

	private String originalText;
	private StringBuilder TXTString, HTMLString;

	public DocumentMaker(HashMap wordMap, String originalText) {
		this.type = type;
		this.originalText = originalText.trim().replaceAll("\\s+", " ");

		// Adds the words to the original text reversed.
			this.TXTString = new StringBuilder(this.originalText);
			NavigableMap<Integer, String> map = new TreeMap<Integer, String>(
					wordMap);
			Set set = map.descendingMap().entrySet();
			Iterator iterator = set.iterator();

			while (iterator.hasNext()) {
				Map.Entry me2 = (Map.Entry) iterator.next();
				String word = (String) me2.getValue();
				String add = "[" + word + "]";
				int start = (int) me2.getKey();
				this.TXTString.insert(start, add);

			}
		}

	public DocumentMaker(HashMap wordMap,String originalText, JSONObject spottedWords_obj) {
		this.type = type;
		this.originalText = originalText.trim().replaceAll("\\s+", " ");
		this.HTMLString = new StringBuilder(this.originalText);
		
		NavigableMap<Integer, String> map = new TreeMap<Integer, String>(
				wordMap);
		Set set = map.descendingMap().entrySet();
		Iterator iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry me2 = (Map.Entry) iterator.next();
			String word = (String) me2.getValue();
			JSONObject word_obj = (JSONObject) spottedWords_obj.get(word);
			
			if(word_obj.get("MaxValue") != null){
				JSONObject maxValue = (JSONObject) word_obj.get("MaxValue");
				String fbCode =  maxValue.toString().substring(maxValue.toString().indexOf("e")+1,maxValue.toString().indexOf(">"));
				String tmp = maxValue.values().toString();
				String url = "https://www.freebase.com/m/"+fbCode;
				String add = "<a href=\""+url+"\" >"+word+" </a>"+tmp+"";
				int start = (int) me2.getKey();
//				this.HTMLString.replace(start, word.length(), add);
				this.HTMLString.insert(start, add);
			}
		}
	}

	public String getTXTString() {
		return TXTString.toString();
	}
	
	public String getHTMLString() {
		return HTMLString.toString();
	}
}
