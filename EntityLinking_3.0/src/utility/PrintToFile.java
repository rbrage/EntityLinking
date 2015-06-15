package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.json.simple.JSONObject;

public class PrintToFile {
	PrintWriter writer;

	public void printTVSToFile(String text, String folderPath, String filename) {
		try {
			File file = new File(folderPath + "/db/results_TVS/" + filename + ".tsv");
			file.getParentFile().mkdirs();
			if (!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.print(text);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void printPlainTextToFile(String text, String folderPath,
			String filename) {
		try {
			File file = new File(folderPath+ "/db/results_Plaintext/" + filename + ".txt");
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.print(text);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void printHTMLToFile(String text, String folderPath, String filename) {
		try {
			File file = new File(folderPath + "/db/results_HTML/"+ filename + ".html");
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.print(text);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Print the time to a file
	 */
	public void printToFile(long rtt, String folderPath, String filename,
			String prog, String time) throws IOException {
		folderPath = "/home/rbrage/Program/EntityLinking/";

		try {
			File f = new File(folderPath + prog + time + ".txt");
			if (f.exists() && !f.isDirectory()) {
				PrintWriter writer = new PrintWriter(
						new BufferedWriter(new FileWriter(folderPath + prog
								+ time + ".txt", true)));

				writer.println(filename + ": " + rtt + " ms.");
				writer.close();
			} else {
				writer = new PrintWriter(folderPath + prog + time + ".txt",
						"UTF-8");

				writer.println(filename + ": " + rtt + " ms.");
				writer.close();
			}

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Print score to file
	 */
	public void printScoreToFile(String folderPath, String filename,
			String type, double precition, double recall) throws IOException {
		try {
			File f = new File(folderPath + "/score/score_" + type + ".txt");
			if (f.exists() && !f.isDirectory()) {
				PrintWriter writer = new PrintWriter(new BufferedWriter(
						new FileWriter(folderPath + "/score/score_" + type
								+ ".txt", true)));

				writer.println(filename + "\t" + precition + "\t" + recall);
				writer.close();
			} else {
				writer = new PrintWriter(folderPath + "/score/score_" + type
						+ ".txt", "UTF-8");
				writer.println(filename + "\t" + precition + "\t" + recall);
				writer.close();
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public void printWordToJSON(JSONObject obj, String folderPath,
			String filename, String prog) throws IOException {
		PrintWriter writer;
		try {

			writer = new PrintWriter(folderPath + "/results_" + prog + "/JSON/"
					+ filename + ".json", "UTF-8");

			writer.print(obj.toString());
			writer.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public void printGraphToFile(String text, String folderPath,
			String filename, String prog) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(folderPath + "/results_" + prog
					+ "/GRAPH/" + filename + ".txt", "UTF-8");
			writer.print(text);
			writer.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Print DBpedia words and analyzed text
	 */
	public void printToFile(String text, String folderPath, String filename,
			Boolean words, String prog, String spotter) {
		PrintWriter writer;
		try {
			if (!words) {
				writer = new PrintWriter(folderPath + "/results_" + prog + "/"
						+ filename + "_with_" + spotter + ".txt", "UTF-8");
				writer.print(text);
				writer.close();
			} else {
				System.out.println(folderPath + "/results_" + prog + "/WORDS/"
						+ filename + "_with_" + spotter + ".txt");
				System.out.println(text);
				writer = new PrintWriter(folderPath + "/results_" + prog
						+ "/WORDS/" + filename + "_with_" + spotter + ".txt",
						"UTF-8");
				writer.print(text);
				writer.close();
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Print DBpedia time
	 */
	public void printToFile(long rtt, String folderPath, String filename,
			String prog, String spotter, String time) throws IOException {

		folderPath = "/home/rbrage/Program/EntityLinking/";
		try {
			File f = new File(folderPath + prog + time + ".txt");
			if (f.exists() && !f.isDirectory()) {
				PrintWriter writer = new PrintWriter(
						new BufferedWriter(new FileWriter(folderPath + prog
								+ time + ".txt", true)));

				writer.println(filename + ": " + rtt + " ms. " + "with: "
						+ spotter);
				writer.close();
			} else {
				writer = new PrintWriter(folderPath + prog + time + ".txt",
						"UTF-8");

				writer.println(filename + ": " + rtt + " ms. " + "with: "
						+ spotter);
				writer.close();
			}

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}
