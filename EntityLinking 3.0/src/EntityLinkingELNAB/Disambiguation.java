package EntityLinkingELNAB;

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

import com.mongodb.DBCursor;

import edu.uci.ics.jung.graph.util.EdgeType;

import jung.EntityGraph;
import jung.GraphVisualization;
import jung.Link;
import utility.MongoDB;
import utility.PrintToFile;

public class Disambiguation {

	int threshold;
	boolean makegraf;
	boolean makefreebaseCalls;

	List<Annotation> annotations;

	HashMap<String, Integer> candidatelist;
	HashMap<String, Long> facc12list;

	MongoDB mDB;

	public List<Annotation> runDisambiguation(List<Annotation> annotations,
			Settings settings) {
		this.annotations = annotations;
		this.threshold = settings.getThreshold();
		this.makegraf = settings.isMakegraf();
		this.makefreebaseCalls = settings.isMakefreebaseCalls();

		for (Annotation a : this.annotations) {
			/*
			 * Sort the values in facc12, high to low.
			 */
			Map<String, Long> facc_map = new HashMap<String, Long>();
			facc_map = a.getFacc12();
			Map.Entry<String, Long> maxValue = null;

			for (Map.Entry<String, Long> value : facc_map.entrySet()) {
				if (maxValue == null
						|| value.getValue().compareTo(maxValue.getValue()) > 0) {
					maxValue = value;
				}
			}
			if (maxValue != null && (maxValue.getValue() > threshold)) {
				String tmp = maxValue.getKey().substring(
						maxValue.getKey().indexOf("e") + 1,
						maxValue.getKey().indexOf(">"));
				String primaryID = "/m/" + tmp;
				String secondID = "https://www.freebase.com//m/" + tmp;
				a.setPrimaryID(primaryID);
				a.setSecondID(secondID);
				// a.setScore1(3);
			}

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
			if (!a.getFacc12().isEmpty())
				a.setMaxScore(maxValue.getValue());
		}

		mDB = new MongoDB("entityforms", "entityforms");
		Iterator<Annotation> annotations_iter = this.annotations.iterator();

		while (annotations_iter.hasNext()) {
			boolean remove = false;
			Annotation a = annotations_iter.next();
			if (a.getPrimaryID() == null) {
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

		Collections.sort(this.annotations, new Comparator<Annotation>() {

			@Override
			public int compare(Annotation o1, Annotation o2) {
				return Integer.compare(o1.getBeginOffset(), o2.getBeginOffset());
			}
		});

		this.annotations = makeGraphDisambigutation(this.annotations);

		if (makegraf) {
			EntityGraph eg = new EntityGraph(this.annotations, settings);
			eg.makeGraph();
			GraphVisualization gv = new GraphVisualization(eg, this.annotations
					.get(0).getDocId());
			gv.show();
		}

		for (Annotation a : this.annotations) {
			if (a.getNew_facc12() != null) {
				Iterator<Entry<String, Integer>> newFacc = a.getNew_facc12()
						.entrySet().iterator();
				String fbMID = null;
				long test = 0;

				while (newFacc.hasNext()) {
					String tmp = newFacc.next().toString();
					if (a.getNew_facc12().size() <= 1) {
						fbMID = tmp.substring(0, tmp.indexOf("=")).trim();
					} else {
						String tmp_fbMID = tmp.substring(0, tmp.indexOf("="))
								.trim();
						if (test < a.getFacc12().get(
								"<fb:m\\u002e" + tmp_fbMID + ">")) {
							test = a.getFacc12().get(
									"<fb:m\\u002e" + tmp_fbMID + ">");
							fbMID = tmp_fbMID;
						}
					}
					String primaryID = "/m/" + fbMID;
					String secondID = "https://www.freebase.com//m/" + fbMID;
					a.setPrimaryID(primaryID);
					a.setSecondID(secondID);
				}
			}
		}
		return this.annotations;

	}

	public List<Annotation> makeGraphDisambigutation(
			List<Annotation> annotations) {
		for (int i = 0; i < annotations.size(); i++) {

			HashMap<String, Long> hash_1 = annotations.get(i).getFacc12();
			Set<String> set_1 = hash_1.keySet();
			Iterator<String> iter_1 = set_1.iterator();
			Map<String, Integer> l = new HashMap<String, Integer>();

			while (iter_1.hasNext()) {
				String value_1 = iter_1.next();
				for (int j = 0; j < annotations.size(); j++) {
					if (j == i)
						continue;

					HashMap<String, Long> hash_2 = annotations.get(j)
							.getFacc12();
					Set<String> set_2 = hash_2.keySet();
					Iterator<String> iter_2 = set_2.iterator();
					iter_2 = set_2.iterator();

					while (iter_2.hasNext()) {
						String value_2 = iter_2.next();

						if (value_1.equals(value_2)) {
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
									l.put(tmp1, l.get(tmp1) + 1);
								} else {
									l.put(tmp1, 1);
								}

							}
						}
					}
					List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(
							l.entrySet());
					Collections.sort(entries,
							new Comparator<Map.Entry<String, Integer>>() {
								public int compare(Entry<String, Integer> o1,
										Entry<String, Integer> o2) {
									return o2.getValue().compareTo(
											o1.getValue());
								}
							});

					Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
					int highest = 0;
					for (Entry<String, Integer> entry : entries) {
						if (entry.getValue() >= highest) {
							highest = entry.getValue();
							sortedMap.put(entry.getKey(), entry.getValue());
						}
					}

					this.annotations.get(i).setNew_facc12(
							(LinkedHashMap<String, Integer>) sortedMap);
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
// float bestWordScore = 0;
// for (Annotation tmpA2 : tmpAnnotations) {
// if (tmpA2.getMaxScore() > bestWordScore) {
// bestWordScore = tmpA2.getMaxScore();
//
// System.out.println("Get best-- " + a.getMentionText() +
// " - "+a.getBeginOffset() );
// int newBeginOffset = 0;
// int newEndOffset = 0;
// newBeginOffset = a.getBeginOffset() +
// spottedWord.indexOf(a.getMentionText());
// newEndOffset = newBeginOffset + a.getMentionText().length();
//
// tmpA2.setBeginOffset(newBeginOffset);
// tmpA2.setEndOffset(newEndOffset);
//
// }
// }

