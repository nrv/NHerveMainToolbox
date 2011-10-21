package plugins.nherve.toolbox.image.db;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.L1Distance;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

public class QueryManager extends Algorithm {
	public class ResponseUnit implements Comparable<ResponseUnit> {
		ImageEntry entry;
		double distanceToQuery;

		@Override
		public int compareTo(ResponseUnit o) {
			return (int) Math.signum(distanceToQuery - o.distanceToQuery);
		}

		@Override
		public String toString() {
			return "ResponseUnit [entry=" + entry.getId() + ", distanceToQuery=" + distanceToQuery + "]";
		}
	}

	public class Response {
		private List<ResponseUnit> internal;

		public Response() {
			super();
			internal = new ArrayList<ResponseUnit>();
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
			String r = "Response  : \n";
			for (ResponseUnit ru : internal) {
				r += " - " + ru.toString() + "\n";
			}
			return r;
		}
		
		public void dump(BufferedWriter w) throws IOException {
			for (ResponseUnit ru : internal) {
				w.write(ru.entry.getId() + " " + ru.distanceToQuery);
				w.newLine();
			}
		}
	}

	private SignatureDistance<VectorSignature> distance;

	public QueryManager(boolean display) {
		super(display);

		distance = new L1Distance();
	}

	public Response knnQuery(final ImageDatabase db, final String desc, final VectorSignature query, final int k) throws FeatureException {
		Response result = new Response();
		for (ImageEntry e : db) {
			VectorSignature s = db.getGlobalSignature(e, desc);
			if (s != null) {
				ResponseUnit ru = new ResponseUnit();
				ru.entry = e;
				ru.distanceToQuery = distance.computeDistance(s, query);
				result.add(ru);
			}
		}

		result.sortAndTruncate(k);

		return result;
	}

	public void setDistance(SignatureDistance<VectorSignature> distance) {
		this.distance = distance;
	}

}
