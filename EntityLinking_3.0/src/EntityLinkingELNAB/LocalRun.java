package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utility.DocumentMaker;
import utility.PrintToFile;
import utility.Reader;

public class LocalRun {
	int threshold = 100;

	public void Run(String folderPath, String outputformat) throws IOException {
		Reader reader = new Reader();
		PrintToFile write = new PrintToFile();
		DocumentMaker doc;

		ArrayList<String> files = reader
				.readFolder(folderPath + "/db/originalfiles");

		for (int i = 0; i < files.size(); i++) {

			String originalText = reader.readFiles(files.get(i)); 

			String filename = files.get(i).substring(
					files.get(i).lastIndexOf("/") + 1,
					files.get(i).lastIndexOf("."));

			ELNAB elnab = new ELNAB(folderPath);
			List<Annotation> annotations = elnab
					.analyze(filename, originalText);
			switch(outputformat){
				case "HTML":
					doc = new DocumentMaker(annotations, originalText, threshold,
							filename);
					write.printHTMLToFile(doc.getHTMLString(), folderPath ,filename);
					break;
				case "PlainText":
					doc = new DocumentMaker(annotations, originalText, threshold);
					write.printPlainTextToFile(doc.getTXTString(), folderPath, filename);
					break;
				case "TVS":
					String textTVS = encodeAnnotations(annotations);
					write.printTVSToFile(textTVS, folderPath, filename);
					break;
				default:
					System.err.println("Error in the output format");
			}

			float status = ((float) (i + 1) / files.size()) * 100;
			System.out.printf("%.2f %10s", status, "% analyzed!\n");
		}
	}

	private String encodeAnnotations(List<Annotation> annotations) {
		StringBuilder sb = new StringBuilder();
		for (Annotation a : annotations) {
			sb.append(a.toTVS()).append('\n');
		}
		return sb.toString();
	}

}
