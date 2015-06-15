package EntityLinkingELNAB;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Annotation {
	private String DocId;
	private int BeginOffset;
	private int EndOffset;
	private int OriginalBeginOffset;
	private int OriginalEndOffset;
	private String PrimaryID;
	private String SecondID;
	private String MentionText;
	private String ShearchWord;
	private float Score1;
	private float Score2;
	private boolean suspect;
	private LinkedList<Integer> linebreaks;
	private float maxScore;
	
	private LinkedHashMap<String, Long> facc12;
	private LinkedHashMap<String, Double> new_facc12;
	private LinkedHashMap<String, Double> rel_facc12;
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
		return this.DocId;
	}
	public void setDocId(String docId) {
		this.DocId = docId;
	}
	
	public Integer getBeginOffset() {
		return this.BeginOffset;
	}
	public void setBeginOffset(Integer beginOffset) {
		this.BeginOffset = beginOffset;
	}
	public Integer getEndOffset() {
		return this.EndOffset;
	}
	public void setEndOffset(Integer endOffset) {
		this.EndOffset = endOffset;
	}
	public Integer getOriginalBeginOffset() {
		return this.OriginalBeginOffset;
	}
	public void setOriginalBeginOffset(Integer OriginalBeginOffset) {
		this.OriginalBeginOffset = OriginalBeginOffset;
	}
	public Integer getOriginalEndOffset() {
		return this.OriginalEndOffset;
	}
	public void setOriginalEndOffset(Integer OriginalEndOffset) {
		this.OriginalEndOffset = OriginalEndOffset;
	}
	
	public String getPrimaryID() {
		return this.PrimaryID;
	}
	public void setPrimaryID(String primaryID) {
		this.PrimaryID = primaryID;
	}
	public String getSecondID() {
		return this.SecondID;
	}
	public void setSecondID(String secondID) {
		this.SecondID = secondID;
	}
	public String getMentionText() {
		return MentionText;
	}
	public void setMentionText(String mentionText) {
		this.MentionText = mentionText;
	}
	public float getScore1() {
		return this.Score1;
	}
	public void setScore1(float score1) {
		this.Score1 = score1;
	}
	public float getScore2() {
		return this.Score2;
	}
	public void setScore2(float score2) {
		this.Score2 = score2;
	}
	public HashMap<String, Long> getFacc12() {
		return this.facc12;
	}
	public void setFacc12(LinkedHashMap<String, Long> facc12) {
		this.facc12 = facc12;
	}
	public HashMap<String, Integer> getDbpediaCandidate() {
		return this.dbpediaCandidate;
	}
	public void setDbpediaCandidate(HashMap<String, Integer> dbpediaCandidate) {
		this.dbpediaCandidate = dbpediaCandidate;
	}
	public float getMaxScore() {
		return this.maxScore;
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
	public LinkedHashMap<String, Double> getNew_facc12() {
		return new_facc12;
	}
	public void setNew_facc12(LinkedHashMap<String, Double> new_facc12) {
		this.new_facc12 = new_facc12;
	}
	public String getShearchWord() {
		return this.ShearchWord;
	}
	public void setShearchWord(String shearchWord) {
		this.ShearchWord = shearchWord;
	}
	public LinkedList<Integer> getLinebreaks() {
		return this.linebreaks;
	}
	public void setLinebreaks(LinkedList<Integer> linebreaks) {
		this.linebreaks = linebreaks;
	}
	public LinkedHashMap<String, Double> getRel_facc12() {
		return this.rel_facc12;
	}
	public void setRel_facc12(LinkedHashMap<String, Double> rel_facc12) {
		this.rel_facc12 = rel_facc12;
	}
	@Override
	public String toString() {
		return "Annotation \n  DocId=" + DocId 
				+ ",\n  BeginOffset=" + BeginOffset
				+ ",\n  EndOffset=" + EndOffset 
				+ ",\n  OriginalBeginOffset=" + OriginalBeginOffset
				+ ",\n  OriginalEndOffset=" + OriginalEndOffset
				+ ",\n  PrimaryID=" + PrimaryID
				+ ",\n  SecondID=" + SecondID 
				+ ",\n  MentionText=" + MentionText
				+ ",\n  ShearchWord="+ ShearchWord 
				+ ",\n  Score1=" + Score1 + ",\n  Score2=" + Score2
				+ ",\n  suspect=" + suspect + ",\n  maxScore=" + maxScore
				+ ",\n  facc12=" + facc12 + ",\n  new_facc12=" + new_facc12
				+ ",\n  dbpediaCandidate=" + dbpediaCandidate
				+ ",\n 	linebreaks=" + linebreaks + "\n ";
	}
	
	
	
}
