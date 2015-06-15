package utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Freebase {

	public JSONObject shearchMid(String mid) {
		 
		 try {
		      HttpTransport httpTransport = new NetHttpTransport();
		      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		      JSONParser parser = new JSONParser();
		      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
		      url.put("mid", "/m/"+mid);
		      url.put("key", "AIzaSyBtFEznKyvz4awbJwThwqwF07OJuwtpHGI");
		      HttpRequest request = requestFactory.buildGetRequest(url);
		      HttpResponse httpResponse = request.execute();
		      JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
		      JSONArray results = (JSONArray)response.get("result");
		      
		      for (int n=0;n<results.size();n++) {
		    	  JSONObject result_obj = (JSONObject) results.get(n);
		    	  return result_obj;
//		    	  System.out.println(result_obj.get("name").toString() +"\t"+result_obj.get("score").toString());
		      }
		      
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }
		 return null;
		
		  }
}
