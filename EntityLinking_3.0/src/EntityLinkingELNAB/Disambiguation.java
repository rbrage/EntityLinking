package EntityLinkingELNAB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.DBCursor;

import jung.EntityGraph;
import jung.GraphVisualization;
import utility.MongoDB;

public class Disambiguation {

	int threshold;
	boolean makeRelatednesGraph;
	boolean localRelatedness;
	float localRelatednessThreshold;

	List<Annotation> annotations;

	HashMap<String, Integer> candidatelist;
	HashMap<String, Long> facc12list;
	HashMap<Integer, String> hashStopWords, NONhashStopWords;
	MongoDB mDB;

	public List<Annotation> runDisambiguation(List<Annotation> annotations,
			Settings settings) throws IOException {
		this.annotations = annotations;
		this.threshold = settings.getThreshold();
		this.makeRelatednesGraph = settings.isRelatednesGraph();
		this.localRelatedness = settings.isLocalRelatedness();
		this.localRelatednessThreshold = settings
				.getLocalRelatednessThreshold();
		this.hashStopWords = settings.getHashStopWords();
		this.NONhashStopWords = settings.getNONhashStopWords();

		for (Annotation a : this.annotations) {
			/*
			 * Sort the values in facc12, high to low.
			 */
			Map<String, Long> facc_map = new HashMap<String, Long>();
			facc_map = a.getFacc12();

			List<Map.Entry<String, Long>> entries = new ArrayList<Map.Entry<String, Long>>(
					facc_map.entrySet());
			Collections.sort(entries,
					new Comparator<Map.Entry<String, Long>>() {
						public int compare(Entry<String, Long> o1,
								Entry<String, Long> o2) {
							return o2.getValue()
									.compareTo((long) o1.getValue());
						}
					});

			Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();

			for (Entry<String, Long> entry : entries) {
				sortedMap.put(entry.getKey(), entry.getValue());
			}
			a.setFacc12((LinkedHashMap<String, Long>) sortedMap);

			Entry<String, Long> max = a.getFacc12().entrySet().iterator()
					.next();
			Long maxValue = max.getValue();
			String maxKey = max.getKey();
			String tmp = maxKey.substring(maxKey.indexOf("e") + 1,
					maxKey.indexOf(">"));
			String primaryID = "/m/" + tmp;
			String secondID = "https://www.freebase.com/m/" + tmp;
			a.setPrimaryID(primaryID);
			a.setSecondID(secondID);
			a.setMaxScore(maxValue);
			a.setScore1(1);
		}

		Collections.sort(this.annotations, new Comparator<Annotation>() {

			@Override
			public int compare(Annotation o1, Annotation o2) {
				return Integer.compare(o1.getBeginOffset(), o2.getBeginOffset());
			}
		});

		if (localRelatedness)
			this.annotations = makeLocalRelatedness(this.annotations);

		if (makeRelatednesGraph) {
			EntityGraph eg = new EntityGraph(this.annotations, settings);
			eg.makeGraph();
			GraphVisualization gv = new GraphVisualization(eg, this.annotations
					.get(0).getDocId());
			gv.show();
		}

		/*
		 * Removes unwanted entities from the annotation list
		 */
		mDB = new MongoDB("entityforms", "entityforms");
		Iterator<Annotation> annotations_iter = this.annotations.iterator();

		while (annotations_iter.hasNext()) {
			boolean remove = false;
			Annotation a = annotations_iter.next();
			if (a.getPrimaryID() == null) {
				remove = true;
			} else if ((!StringUtils.isAllUpperCase(a.getMentionText()) && this.hashStopWords
					.containsValue(a.getMentionText().trim().toLowerCase()))
					|| this.NONhashStopWords.containsValue(a.getMentionText()
							.trim().toLowerCase())) {
				remove = true;
			} else {
				DBCursor cursor = mDB.getCandidates(a.getPrimaryID());
				try {
					if (!cursor.hasNext()) {
						remove = true;
					}
				} finally {
					cursor.close();
				}
			}

			if (remove) {
				annotations_iter.remove();
			}
		}

		/*
		 * Uses the local relatedness score to change the primary id, if it
		 * above the threshold
		 */
		for (Annotation a : this.annotations) {
			if (a.getNew_facc12() != null) {
				Iterator<Entry<String, Double>> newFacc = a.getNew_facc12()
						.entrySet().iterator();
				String fbMID = a.getPrimaryID().substring(
						a.getPrimaryID().lastIndexOf("/") + 1,
						a.getPrimaryID().length());
				long test = 0;

				while (newFacc.hasNext()) {
					Entry<String, Double> newFacc_obj = newFacc.next();
					String tmp_fbMID = newFacc_obj.getKey();
					if (a.getNew_facc12().size() <= 1) {
						if (a.getFacc12().get("<fb:m\\u002e" + tmp_fbMID + ">") > localRelatednessThreshold) {
							fbMID = tmp_fbMID;
							if (!a.getPrimaryID().equals("/m/" + tmp_fbMID)) {
								a.setMaxScore(a.getFacc12().get(
										"<fb:m\\u002e" + fbMID + ">"));
							}
						}

					} else {
						tmp_fbMID = newFacc_obj.getKey();
						if (test < a.getFacc12().get(
								"<fb:m\\u002e" + tmp_fbMID + ">")
								&& a.getFacc12().get(
										"<fb:m\\u002e" + tmp_fbMID + ">") > localRelatednessThreshold) {
							test = a.getFacc12().get(
									"<fb:m\\u002e" + tmp_fbMID + ">");
							fbMID = tmp_fbMID;
							if (!a.getPrimaryID().equals("/m/" + tmp_fbMID)) {
								a.setMaxScore(a.getFacc12().get(
										"<fb:m\\u002e" + fbMID + ">"));
							}
						}
					}

					String primaryID = "/m/" + fbMID;
					String secondID = "https://www.freebase.com/m/" + fbMID;
					a.setPrimaryID(primaryID);
					a.setSecondID(secondID);

				}
			}
		}

		return this.annotations;

	}

	/*
	 * Function for making local relatedness score
	 */
	public List<Annotation> makeLocalRelatedness(List<Annotation> annotations) {
		for (int i = 0; i < annotations.size(); i++) {

			HashMap<String, Long> hash_1 = annotations.get(i).getFacc12();
			Set<Map.Entry<String, Long>> set_1 = hash_1.entrySet();// hash_1.keySet();
			Iterator<Map.Entry<String, Long>> iter_1 = set_1.iterator();
			Map<String, Double> l = new HashMap<String, Double>();

			while (iter_1.hasNext()) {
				Entry<String, Long> entry1 = iter_1.next();
				String value_1 = entry1.getKey();
				if (entry1.getValue() >= (int) (annotations.get(i)
						.getMaxScore() * (this.localRelatednessThreshold / 100.0f))) {
					for (int j = 0; j < annotations.size(); j++) {
						if (j == i)
							continue;
						HashMap<String, Long> hash_2 = annotations.get(j)
								.getFacc12();
						if (hash_2.containsKey(value_1)) {
							if (!annotations
									.get(i)
									.getMentionText()
									.toLowerCase()
									.equals(annotations.get(j).getMentionText()
											.toLowerCase())
									&& !symbolRemove(
											annotations.get(i).getMentionText())
											.toLowerCase()
											.equals(symbolRemove(
													annotations.get(j)
															.getMentionText())
													.toLowerCase())) {
								String tmp1 = value_1.substring(value_1
										.toString().indexOf("e") + 1, value_1
										.toString().indexOf(">"));
								if (l.containsKey(tmp1)) {
									l.put(tmp1, l.get(tmp1) + 1.0);
								} else {
									l.put(tmp1, (double) 1);
								}
							}
						}
						List<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(
								l.entrySet());
						Collections.sort(entries,
								new Comparator<Map.Entry<String, Double>>() {
									public int compare(
											Entry<String, Double> o1,
											Entry<String, Double> o2) {
										return o2.getValue().compareTo(
												o1.getValue());
									}
								});

						Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
						Double highest = 0.0;
						for (Entry<String, Double> entry : entries) {
							if (entry.getValue() >= highest) {
								highest = entry.getValue();
								sortedMap.put(entry.getKey(), entry.getValue());
							}
						}

						this.annotations.get(i).setNew_facc12(
								(LinkedHashMap<String, Double>) sortedMap);
					}
				}

			}
		}

		return this.annotations;
	}

	private String symbolRemove(String spottedWord) {
		spottedWord = spottedWord.replaceAll("[\\,“()”?.'\"]", "");
		return spottedWord = spottedWord.trim();
	}
}

