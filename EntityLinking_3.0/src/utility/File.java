package utility;

import java.util.ArrayList;

public class File {

	static String filename;
	
	public static String getFilename(ArrayList<String> list, int i) {
		filename = ((String) list.get(i)).substring(((String) list.get(i)).lastIndexOf("/") + 1, ((String) list.get(i)).lastIndexOf("."));
		return filename;
	}
	
	
}
