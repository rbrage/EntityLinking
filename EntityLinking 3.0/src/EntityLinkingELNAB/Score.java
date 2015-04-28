package EntityLinkingELNAB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utility.DocumentMaker;
import utility.PrintToFile;
import utility.Reader;

public class Score {
	int threshold = 100;
	String gold_tvs_path = "/home/rbrage/Program/EntityLinking/erd/Long_dev_data.tsv";

	public void run(String folderPath) throws IOException {
		Reader reader = new Reader();
		PrintToFile write = new PrintToFile();
		DocumentMaker doc;
		HashMap LOGF = new HashMap();
		List<Annotation> annotations = new ArrayList<Annotation>();
		List<Annotation> goldenAnnotations = new ArrayList<Annotation>();

		String tmp = "mainbody-00003";

		BufferedReader TSVFile = new BufferedReader(new FileReader(
				gold_tvs_path));
		String dataRow = TSVFile.readLine(); // Read first line.

		while (dataRow != null) {
			String[] dataArray = dataRow.split("\t");
			if (!dataArray[0].equals(tmp)) {
				LOGF.put(tmp, annotations);
				tmp = dataArray[0];
				annotations = new ArrayList<Annotation>();
			}
				Annotation a = new Annotation();
				a.setDocId(dataArray[0]);
				a.setBeginOffset(Integer.parseInt(dataArray[1]));
				a.setEndOffset(Integer.parseInt(dataArray[2]));
				a.setPrimaryID(dataArray[3]);
				a.setMentionText(dataArray[5]);
				annotations.add(a);
			
			dataRow = TSVFile.readLine();
		}
		TSVFile.close();

		ArrayList<String> files = reader
				.readFolder(folderPath + "/results_TVS");
		for (int i = 0; i < files.size(); i++) {
			String filename = files.get(i).substring(
					files.get(i).lastIndexOf("/") + 1,
					files.get(i).lastIndexOf("."));

			if (LOGF.containsKey(filename)) {
				List<Annotation> selfAnnotations = new ArrayList<Annotation>();
				BufferedReader TSVFile1 = new BufferedReader(new FileReader(
						files.get(i)));
				String dataRow1 = TSVFile1.readLine(); // Read first line.
				while (dataRow1 != null) {
					String[] dataArray1 = dataRow1.split("\t");
					Annotation a_self = new Annotation();
					a_self.setDocId(dataArray1[0]);
					a_self.setBeginOffset(Integer.parseInt(dataArray1[1]));
					a_self.setEndOffset(Integer.parseInt(dataArray1[2]));
					a_self.setPrimaryID(dataArray1[3]);
					a_self.setMentionText(dataArray1[5]);
					selfAnnotations.add(a_self);
					dataRow1 = TSVFile1.readLine();
				}
				TSVFile1.close();

				goldenAnnotations = (List<Annotation>) LOGF.get(filename);

				int length = 0;
				if(selfAnnotations.size()>goldenAnnotations.size())
					length = selfAnnotations.size();
				else 
					length = goldenAnnotations.size();

				System.out.println(filename + " - " + length + "||"+ goldenAnnotations.size()+"|"+selfAnnotations.size());
				
				int goldenK = 0;
				int selfL = 0;
				StringBuilder output = new StringBuilder();
				output.append("GoldenW \t FBmid \t BOffset \t BOffset \t FBmid \t SpottW\n");
				for (int j = 0; j < length; j++) {
//					System.out.println(goldenK+":|"+ goldenAnnotations.size()+"||"+selfAnnotations.size()+ "|:"+selfL);
//					System.out.println(goldenAnnotations.get(goldenK).getBeginOffset() +"<->"+  selfAnnotations.get(selfL).getBeginOffset()+
//							"=" +(goldenAnnotations.get(goldenK).getBeginOffset().intValue() == selfAnnotations.get(selfL).getBeginOffset().intValue()));
						
					if(goldenAnnotations.get(goldenK).getBeginOffset().intValue() == selfAnnotations.get(selfL).getBeginOffset().intValue()){
							output.append(goldenAnnotations.get(goldenK).getMentionText()+ "\t" +goldenAnnotations.get(goldenK).getPrimaryID()
									+ "\t" + goldenAnnotations.get(goldenK).getBeginOffset()
									+ "\t" + selfAnnotations.get(selfL).getBeginOffset() 
									+ "\t" + selfAnnotations.get(selfL).getPrimaryID()+ "\t" + selfAnnotations.get(selfL).getMentionText()+"\n");
							if(goldenK<goldenAnnotations.size()-1){
								goldenK++;}
							if(selfL<selfAnnotations.size()-1){
								selfL++;}
						}
						
						if(goldenAnnotations.get(goldenK).getBeginOffset().intValue() < selfAnnotations.get(selfL).getBeginOffset().intValue()){
							output.append(goldenAnnotations.get(goldenK).getMentionText()+ "\t" +goldenAnnotations.get(goldenK).getPrimaryID()
									+ "\t" + goldenAnnotations.get(goldenK).getBeginOffset()
									+ "\t  NULL \t  NULL \t  NULL" +"\n");
							if(goldenK<goldenAnnotations.size()-1){
								goldenK++;}
							}
						if(selfAnnotations.get(selfL).getBeginOffset().intValue() < goldenAnnotations.get(goldenK).getBeginOffset().intValue()){
							output.append("NULL \t  NULL \t  NULL \t" + selfAnnotations.get(selfL).getBeginOffset() 
									+ "\t" + selfAnnotations.get(selfL).getPrimaryID()+ "\t" + selfAnnotations.get(selfL).getMentionText()+"\n");
							if(selfL<selfAnnotations.size()-1){
								selfL++;}
						}
						

					

				}
//				System.out.println(output);
				write.printToFile(output.toString(), folderPath, filename,
						true, "CompareTVS");
				System.out
						.println("-----------------------------------------------------------");
			}
		}

	}
}
