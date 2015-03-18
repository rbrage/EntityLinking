package gumpaper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;


public class GumpaperArticles {

	String urls = "http://www.gumpaper.com/api/1.0/?Action=Articles&Lang=en_US&Key=1E8F2EDD0515B0492A0BCB3010DD9CF9946F&Password=gumbp572ipaper&Count=10&Cursor=5734985392";
	HttpURLConnection connection;
	
	String forderpath = "/home/rbrage/Program/EntityLinking/db";
	
	public void GumpaperArticles() {
		
	/*
	 * /home/rbrage/Program/EntityLinking/db
	 *  http://www.gumpaper.com/api/1.0/?Action=[query type]&Lang=[language code]&Key=[your api key]&Password=[your password]&Count=[rows pr. page]&Cursor=A262E7DA030440406D0A2000BBD4D45020AE
	 *  1E8F2EDD0515B0492A0BCB3010DD9CF9946F
	 *  
	 */

		try {

			URL url = new URL(urls);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			// conn.setRequestProperty("Content-Type", "text/xml");
			// conn.setRequestProperty("Accept:", "text/xml");
			connection.setDoOutput(true);

			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			
			out.close();
			
			 BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		            System.out.println(line);
		        }
		        connection.getInputStream().close();
		        String json = sb.toString();

			
			JSONObject jsonObj = new JSONObject(json);
			System.out.println(jsonObj);
			
//			String name = jsonObj.getJSONObject("Articles").getString("Articles");
//			
//			
//			System.out.println(name);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
