package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import EntityLinkingELNAB.Annotation;

public class DocumentMaker {

	private String originalText;
	private StringBuilder TXTString, HTMLString, HTMLDoc;

	public DocumentMaker(List<Annotation> annotations, String originalText,
			int threshold) {

		// Adds the words to the original text reversed.
		this.TXTString = new StringBuilder(originalText);
		for (int i = annotations.size() - 1; i >= 0; i--) {
			Annotation a = annotations.get(i);
			String word = a.getMentionText();
			int tmp = (int) a.getMaxScore();
			if (threshold < tmp) {
				String add = "[" + word + "]";
				int start = a.getBeginOffset();
				this.TXTString.insert(start, add);
				// this.TXTString.replace(start, (start + word.length()), add);

			}
		}
	}

	public DocumentMaker(List<Annotation> annotation, String originalText,
			int threshold, String filename) {

		this.HTMLString = new StringBuilder(originalText);

		for (int i = annotation.size() - 1; i >= 0; i--) {
			Annotation a = annotation.get(i);
			String word = a.getMentionText();

			int tmp = (int) a.getMaxScore();
			if (threshold < tmp) {
				String url = a.getSecondID();
				String info = " ["+a.isSuspect()+ ", "+ a.getBeginOffset()+", "+ a.getBeginOffset2() + ", " + a.getMentionText2()+"]";
				String add = "<a href=\"" + url + "\">" + word +"</a>";
				int start = a.getBeginOffset();
//				 System.out.println(start +" - " +a.getEndOffset() +" \t| "
//				 +a.getBeginOffset2() +" - " +a.getEndOffset2() +" \t| "
//				 +a.isSuspect() +" \t| " +a.getMentionText());
				this.HTMLString.replace(start, a.getEndOffset(), add);
//				this.HTMLString.insert(start, add);

			}
		}

		String tmp = this.HTMLString.toString().replaceAll("\n", "<br>");

		HTMLDoc = new StringBuilder();
		HTMLDoc.append("<!DOCTYPE html>");
		HTMLDoc.append("<html lang=\"en\">");
		HTMLDoc.append("<meta charset=\"utf-8\">");
		HTMLDoc.append("<head><title>" + filename + "</title></head>");
		HTMLDoc.append("<body><h1>" + filename + "</h1>" + "<p>" + tmp
				+ "</p></body>");
		HTMLDoc.append("</html>");

	}

	public String getTXTString() {
		return TXTString.toString();
	}

	public String getHTMLString() {
		return HTMLDoc.toString();
	}
}
