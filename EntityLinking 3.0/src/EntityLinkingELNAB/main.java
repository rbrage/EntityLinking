package EntityLinkingELNAB;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utility.PrintToFile;
import utility.Reader;

public class main {
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String folderPath = args[0];
		LocalRun lr = new LocalRun();
		lr.Run(folderPath);
	}

}
