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
package plugins.nherve.toolbox.image.feature.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import plugins.nherve.toolbox.image.feature.DefaultClusteringAlgorithmImpl;
import plugins.nherve.toolbox.image.feature.Distance;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class RandomClustering.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class RandomClustering extends DefaultClusteringAlgorithmImpl<VectorSignature> implements Distance<VectorSignature> {
	
	/** The nb classes. */
	private int nbClasses;
	
	/** The centroids. */
	private List<VectorSignature> centroids;
	
	/** The distance. */
	private SignatureDistance<VectorSignature> distance;
	
	/**
	 * Instantiates a new random clustering.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 */
	public RandomClustering(int nbClasses) {
		super(false);
		this.nbClasses = nbClasses;
		distance = new L2Distance();
	}

	/**
	 * Random distinct.
	 * 
	 * @param from
	 *            the from
	 * @param nb
	 *            the nb
	 * @return the list
	 * @throws SignatureException
	 *             the signature exception
	 */
	private List<VectorSignature> randomDistinct(List<VectorSignature> from, int nb) throws SignatureException {
		try {
			List<VectorSignature> ctd = new ArrayList<VectorSignature>();
			
			Random rd = new Random(System.currentTimeMillis());
			boolean[] affected = new boolean[from.size()];
			Arrays.fill(affected, false);
			int c = 0;

			int randomPoint = 0;
			boolean tooClose = false;
			for (; c < nb; c++) {
				do {
					tooClose = false;
					do {
						randomPoint = rd.nextInt(from.size());
					} while (affected[randomPoint]);
					for (int c2 = 0; c2 < c; c2++) {
						if (computeDistance(from.get(randomPoint), ctd.get(c2)) == 0) {
							tooClose = true;
							break;
						}
					}
				} while (tooClose);
				try {
					ctd.add(from.get(randomPoint).clone());
					affected[randomPoint] = true;
				} catch (CloneNotSupportedException e) {
					throw new SignatureException(e);
				}
			}
			
			return ctd;
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#compute(java.util.List)
	 */
	@Override
	public void compute(List<VectorSignature> points) throws ClusteringException {
		try {
			log("Random clustering on " + points.size() + " points");
			centroids = randomDistinct(points, getNbClasses());
		} catch (SignatureException e) {
			throw new ClusteringException(e);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getAffectations(java.util.List)
	 */
	@Override
	public int[] getAffectations(List<VectorSignature> pts) throws ClusteringException {
		int[] othAff = new int[pts.size()];

		try {
			int p = 0;
			for (VectorSignature s : pts) {
				double minDist = Double.MAX_VALUE;
				int closestCentroid = 0;
				int c = 0;
				for (VectorSignature ct : centroids) {
					double d = computeDistance(s, ct);
					if (d < minDist) {
						minDist = d;
						closestCentroid = c;
					}
					c++;
				}
				othAff[p] = closestCentroid;
				p++;
			}
		} catch (SignatureException e) {
			throw new ClusteringException(e);
		} catch (FeatureException e) {
			throw new ClusteringException(e);
		}

		return othAff;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getCentroids()
	 */
	@Override
	public List<VectorSignature> getCentroids() throws ClusteringException {
		return centroids;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getNbClasses()
	 */
	@Override
	public int getNbClasses() {
		return nbClasses;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Distance#computeDistance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double computeDistance(VectorSignature s1, VectorSignature s2) throws FeatureException {
		return distance.computeDistance(s1, s2);
	}

}
