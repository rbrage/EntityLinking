package main;

import java.io.IOException;

import EntityLinkingELNAB.LocalRun;
import EntityLinkingELNAB.Score;

public class start {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if(args.length!=2){
			System.out.println("There is not enough input arguments! \nFirst arguments is folderpath, the second arguments need to bee either: \nHTML, PlainText, TSV");
			System.exit(0);
		}
		
		String folderPath = args[0];
		String outputformat = args[1];
		if(outputformat.equals("HTML") || outputformat.equals("PlainText") || outputformat.equals("TSV") ){
			LocalRun lr = new LocalRun();
			lr.Run(folderPath, outputformat);
		}else{		
			System.out.println("The second arg need to bee either: \nHTML, PlainText, TSV");
			System.exit(0);
		}
	}
}
