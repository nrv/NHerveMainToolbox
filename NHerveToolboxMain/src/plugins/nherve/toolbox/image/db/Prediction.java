/*
 * Copyright 2010, 2011 Institut Pasteur.
 * 
 * This file is part of NHerve Main Toolbox, which is an ICY plugin.
 * 
 * NHerve Main Toolbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NHerve Main Toolbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NHerve Main Toolbox. If not, see <http://www.gnu.org/licenses/>.
 */
package plugins.nherve.toolbox.image.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;


/**
 * The Class Prediction.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class Prediction extends Algorithm {
	
	/**
	 * The Class PredictionEntity.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	private class PredictionEntity implements Comparable<PredictionEntity> {
		
		/** The entry. */
		private ImageEntry entry;
		
		/** The score. */
		private double score;

		/**
		 * Instantiates a new prediction entity.
		 * 
		 * @param entry
		 *            the entry
		 * @param score
		 *            the score
		 */
		public PredictionEntity(ImageEntry entry, double score) {
			super();
			this.entry = entry;
			this.score = score;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(PredictionEntity o) {
			return (int) (Math.signum(score - o.score));
		}
	}

	/** The preds. */
	private List<PredictionEntity> preds;
	
	/** The predicted. */
	private String predicted;
	
	/** The sorted. */
	private boolean sorted;

	/**
	 * Instantiates a new prediction.
	 * 
	 * @param predicted
	 *            the predicted
	 */
	public Prediction(String predicted) {
		super();
		preds = new ArrayList<PredictionEntity>();
		sorted = false;
		this.predicted = predicted;
	}

	/**
	 * Adds the.
	 * 
	 * @param e
	 *            the e
	 * @param s
	 *            the s
	 */
	public void add(ImageEntry e, double s) {
		sorted = false;
		preds.add(new PredictionEntity(e, s));
	}

	/**
	 * Sort.
	 */
	private void sort() {
		if (!sorted) {
			Collections.sort(preds);
			// from score to distance
			Collections.reverse(preds);
			sorted = true;
		}
	}

	/**
	 * Average precision.
	 * 
	 * @return the double
	 */
	public double averagePrecision() {
		sort();
		int rel = 0;
		double ap = 0;
		
		for (int r = 0; r < preds.size(); r++) {
			if (preds.get(r).entry.containsClass(predicted)) {
				rel++;
				ap += (double)rel / (double)(r + 1);
			}
		}
		if (rel > 0) {
			return ap / (double) rel;
		} else {
			return 0;
		}
		
	}
}
