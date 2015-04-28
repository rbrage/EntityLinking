package main;

import java.io.IOException;

import EntityLinkingELNAB.LocalRun;
import EntityLinkingELNAB.Score;

public class start {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		String folderPath = args[0];
		LocalRun lr = new LocalRun();
		lr.Run(folderPath);
		
//		Score s = new Score();
//		s.run(folderPath);
		System.out.println("Done!");
	}
}
