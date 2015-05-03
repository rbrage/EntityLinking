package EntityLinkingELNAB;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Annotation {
	private String DocId;
	private int BeginOffset;
	private int EndOffset;
	private int BeginOffset2;
	private int EndOffset2;
	private int BeginOffset3;
	private int EndOffset3;
	private String PrimaryID;
	private String SecondID;
	private String MentionText;
	private String MentionText2;
	private String ShearchWord;
	private float Score1;
	private float Score2;
	private boolean suspect;
	private LinkedList<Integer> linebreaks;
	private float maxScore;
	
	private LinkedHashMap<String, Long> facc12;
	private LinkedHashMap<String, Integer> new_facc12;
	private HashMap<String, Integer> dbpediaCandidate;

	
	public Annotation(){
		
	}
	public String toTVS(){
		StringBuilder sb = new StringBuilder();
		sb.append(DocId).append("\t");
		sb.append(BeginOffset).append("\t");
		sb.append(EndOffset).append("\t");
		sb.append(PrimaryID).append("\t");
		sb.append(SecondID).append("\t");
		sb.append(MentionText).append("\t");
		sb.append(Score1).append("\t");
		sb.append(Score2).append("\t");
		return sb.toString();
	}
	
	public String getDocId() {
		return DocId;
	}
	public void setDocId(String docId) {
		DocId = docId;
	}
	
	public Integer getBeginOffset() {
		return BeginOffset;
	}
	public void setBeginOffset(Integer beginOffset) {
		BeginOffset = beginOffset;
	}
	public Integer getEndOffset() {
		return EndOffset;
	}
	public void setEndOffset(Integer endOffset) {
		EndOffset = endOffset;
	}
	public Integer getBeginOffset2() {
		return BeginOffset2;
	}
	public void setBeginOffset2(Integer beginOffset2) {
		BeginOffset2 = beginOffset2;
	}
	public Integer getEndOffset2() {
		return EndOffset2;
	}
	public void setEndOffset2(Integer endOffset2) {
		EndOffset2 = endOffset2;
	}
	public Integer getBeginOffset3() {
		return BeginOffset3;
	}
	public void setBeginOffset3(Integer beginOffset3) {
		BeginOffset3 = beginOffset3;
	}
	public Integer getEndOffset3() {
		return EndOffset3;
	}
	public void setEndOffset3(Integer endOffset3) {
		EndOffset3 = endOffset3;
	}
	public String getPrimaryID() {
		return PrimaryID;
	}
	public void setPrimaryID(String primaryID) {
		PrimaryID = primaryID;
	}
	public String getSecondID() {
		return SecondID;
	}
	public void setSecondID(String secondID) {
		SecondID = secondID;
	}
	public String getMentionText() {
		return MentionText;
	}
	public void setMentionText(String mentionText) {
		MentionText = mentionText;
	}
	public float getScore1() {
		return Score1;
	}
	public void setScore1(float score1) {
		Score1 = score1;
	}
	public float getScore2() {
		return Score2;
	}
	public void setScore2(float score2) {
		Score2 = score2;
	}
	
	public HashMap<String, Long> getFacc12() {
		return facc12;
	}
	public void setFacc12(LinkedHashMap<String, Long> facc12) {
		this.facc12 = facc12;
	}
	public HashMap<String, Integer> getDbpediaCandidate() {
		return dbpediaCandidate;
	}
	public void setDbpediaCandidate(HashMap<String, Integer> dbpediaCandidate) {
		this.dbpediaCandidate = dbpediaCandidate;
	}
	public float getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(float maxScore) {
		this.maxScore = maxScore;
	}
	
	public boolean isSuspect() {
		return suspect;
	}
	public void setSuspect(boolean suspect) {
		this.suspect = suspect;
	}
	public String getMentionText2() {
		return MentionText2;
	}
	public void setMentionText2(String mentionText2) {
		MentionText2 = mentionText2;
	}
	public LinkedHashMap<String, Integer> getNew_facc12() {
		return new_facc12;
	}
	public void setNew_facc12(LinkedHashMap<String, Integer> new_facc12) {
		this.new_facc12 = new_facc12;
	}
	public String getShearchWord() {
		return ShearchWord;
	}
	public void setShearchWord(String shearchWord) {
		ShearchWord = shearchWord;
	}
	public LinkedList<Integer> getLinebreaks() {
		return linebreaks;
	}
	public void setLinebreaks(LinkedList<Integer> linebreaks) {
		this.linebreaks = linebreaks;
	}
	
	@Override
	public String toString() {
		return "Annotation \n  DocId=" + DocId + ",\n  BeginOffset=" + BeginOffset
				+ ",\n  EndOffset=" + EndOffset + ",\n  BeginOffset2=" + BeginOffset2
				+ ",\n  EndOffset2=" + EndOffset2 +",\n  BeginOffset3=" + BeginOffset3
				+ ",\n  PrimaryID=" + PrimaryID
				+ ",\n  SecondID=" + SecondID + ",\n  MentionText=" + MentionText
				+ ",\n  MentionText2=" + MentionText2 + ",\n  ShearchWord="
				+ ShearchWord + ", Score1=" + Score1 + ",\n  Score2=" + Score2
				+ ",\n  suspect=" + suspect + ",\n  maxScore=" + maxScore
				+ ",\n  facc12=" + facc12 + ",\n  new_facc12=" + new_facc12
				+ ",\n  dbpediaCandidate=" + dbpediaCandidate
				+ ",\n 	linebreaks=" + linebreaks + "\n ";
	}
	
	
	
}