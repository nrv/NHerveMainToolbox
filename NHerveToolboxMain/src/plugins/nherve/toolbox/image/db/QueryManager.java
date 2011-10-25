package plugins.nherve.toolbox.image.db;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.L1Distance;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

public class QueryManager extends Algorithm {
	public class ResponseUnit implements Comparable<ResponseUnit> {
		ImageEntry entry;
		int lid;
		double distanceToQuery;

		public ResponseUnit() {
			super();
			lid = -1;
		}

		@Override
		public int compareTo(ResponseUnit o) {
			return (int) Math.signum(distanceToQuery - o.distanceToQuery);
		}

		@Override
		public String toString() {
			return "ResponseUnit [entry=" + entry.getId() + " (" + lid + "), distanceToQuery=" + distanceToQuery + "]";
		}
	}

	public class Response {
		private String queryId;
		private List<ResponseUnit> internal;

		public Response(String queryId) {
			super();
			internal = new ArrayList<ResponseUnit>();
			this.queryId = queryId;
		}

		public void sortAndTruncate(int k) {
			Collections.sort(internal);
			internal = internal.subList(0, k);
		}

		public boolean add(ResponseUnit e) {
			return internal.add(e);
		}

		@Override
		public String toString() {
			String r = "Response (" + queryId + ") : \n";
			for (ResponseUnit ru : internal) {
				r += " - " + ru.toString() + "\n";
			}
			return r;
		}

		public void dump(BufferedWriter w) throws IOException {
			w.write("# " + queryId);
			w.newLine();
			for (ResponseUnit ru : internal) {
				if (ru.lid >= 0) {
					w.write(DatabaseManager.getUniqueId(ru.entry.getId(), ru.lid) + " " + ru.distanceToQuery);
				} else {
					w.write(ru.entry.getId() + " " + ru.distanceToQuery);
				}
				w.newLine();
			}
		}
	}

	private SignatureDistance<VectorSignature> distance;

	public QueryManager(boolean display) {
		super(display);

		distance = new L1Distance();
	}

	public Response knnQuery(final String queryId, final ImageDatabase db, final String desc, final VectorSignature query, final int k) throws FeatureException {
		Response result = new Response(queryId);
		if (db.containsGlobalDescriptor(desc)) {
			for (ImageEntry e : db) {
				VectorSignature s = db.getGlobalSignature(e, desc);
				if (s != null) {
					ResponseUnit ru = new ResponseUnit();
					ru.entry = e;
					ru.distanceToQuery = distance.computeDistance(s, query);
					result.add(ru);
				}
			}
		} else if (db.containsLocalDescriptor(desc)) {
			for (ImageEntry e : db) {
				BagOfSignatures<VectorSignature> bag = db.getLocalSignature(e, desc);
				if (bag != null) {
					int lid = 0;
					for (VectorSignature s : bag) {
						ResponseUnit ru = new ResponseUnit();
						ru.entry = e;
						ru.lid = lid;
						ru.distanceToQuery = distance.computeDistance(s, query);
						result.add(ru);
						lid++;
					}
				}
			}
		}

		result.sortAndTruncate(k);

		return result;
	}

	public void setDistance(SignatureDistance<VectorSignature> distance) {
		this.distance = distance;
	}

}
