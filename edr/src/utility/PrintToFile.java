package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class PrintToFile {
	public void printToFile(String text, String folderPath, String filename) throws IOException {
		try {
			File f = new File(folderPath + "/erdFiles/" + filename + ".txt");
			if (f.exists() && !f.isDirectory()) {
				PrintWriter writer = new PrintWriter(
						new BufferedWriter(new FileWriter(folderPath + "/erdFiles/" + filename + ".txt", true)));

				writer.println(text);
				writer.close();
			}else{
				PrintWriter writer = new PrintWriter(folderPath + "/erdFiles/" + filename + ".txt", "UTF-8");
				writer.print(text);
				writer.close();
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	public void printToFileTSV(String text, String folderPath, String filename) throws IOException {
		try {
			File f = new File(folderPath + "/erdFilesTSV/" + filename + ".tsv");
			if (f.exists() && !f.isDirectory()) {
				PrintWriter writer = new PrintWriter(
						new BufferedWriter(new FileWriter(folderPath + "/erdFilesTSV/" + filename + ".tsv", true)));

				writer.println(text);
				writer.close();
			}else{
				PrintWriter writer = new PrintWriter(folderPath + "/erdFilesTSV/" + filename + ".tsv", "UTF-8");
				writer.print(text);
				writer.close();
		}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}
