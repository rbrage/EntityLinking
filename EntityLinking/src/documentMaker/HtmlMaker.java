package documentMaker;

import utility.PrintToFile;

public class HtmlMaker {
	private PrintToFile write;
	
	private String folderPath; 
	private String filename;
	
	public HtmlMaker(String folderPath){
		this.folderPath = folderPath;
	}
	
	public void makeHtmlFile(String txt, String filename){
		this.filename = filename;
		write = new PrintToFile();
		
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>");
		builder.append("<html lang=\"en\">");
		builder.append("<meta charset=\"utf-8\">");
		builder.append("<head><title>"+this.filename+"</title></head>");
		builder.append("<body><h1>"+this.filename+"</h1><p>"+txt+"</p></body>");
		builder.append("</html>");
		String html = builder.toString();
		
		this.write.printHTMLToFile(html, this.folderPath, this.filename,"WordSpotter");
	}
}
