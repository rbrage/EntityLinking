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
	
	public void Run(String folderPath) throws IOException{
		Reader reader = new Reader();
		PrintToFile write = new PrintToFile();
		DocumentMaker doc;
		
		ArrayList<String> files = reader.readFolder(folderPath + "/erdFiles");
		
		for (int i = 0; i < files.size(); i++) {
			
			String originalText = reader.readFiles(files.get(i));
			
			String filename = files.get(i).substring(
					files.get(i).lastIndexOf("/") + 1,
					files.get(i).lastIndexOf("."));
//			if(filename.equals("mainbody-00042")){
			System.out.println("Strating: "+ filename);
			
			ELNAB elnab = new ELNAB();
			List<Annotation> annotations = elnab.ELNAB("1", filename, originalText);
				
			String textTVS = encodeAnnotations(annotations);
			
			write.printToFile(textTVS, folderPath, filename,
					false, "TVS");
			
			doc = new DocumentMaker(annotations, originalText, threshold, filename);
			write.printHTMLToFile(doc.getHTMLString(), folderPath, filename, "erd");
			
			doc = new DocumentMaker(annotations, originalText, threshold);
			write.printToFile(doc.getTXTString(), folderPath, filename,false, "erd");
			
			float status = ((float)(i+1)/files.size())*100;
			System.out.println(filename + " is done! \n"+status+"% \n------------------------ " );
			
//		}
		}
		System.out.println("All is done!");

	}
	private String encodeAnnotations(List<Annotation> annotations) {
		StringBuilder sb = new StringBuilder();
		for (Annotation a : annotations) {
			sb.append(a.toTVS()).append('\n');

		}
		return sb.toString();
	}
	
}
