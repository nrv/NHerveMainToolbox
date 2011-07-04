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
package plugins.nherve.toolbox.image.feature.fuzzy;

import java.util.List;

import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.clustering.KMeans;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class PseudoFuzzyKMeans.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class PseudoFuzzyKMeans extends PseudoFuzzyClusteringAlgorithmImpl {

	/**
	 * Instantiates a new pseudo fuzzy k means.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 */
	public PseudoFuzzyKMeans(int nbClasses) {
		super(false);
		
		KMeans km = new KMeans(nbClasses);
		setInternalAlgorithm(km);
	}

	/**
	 * Instantiates a new pseudo fuzzy k means.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 * @param nbMaxIterations
	 *            the nb max iterations
	 * @param stabilizationCriterion
	 *            the stabilization criterion
	 */
	public PseudoFuzzyKMeans(int nbClasses, int nbMaxIterations, double stabilizationCriterion) {
		super(false);
		KMeans km = new KMeans(nbClasses, nbMaxIterations, stabilizationCriterion);
		setInternalAlgorithm(km);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	public double[] getMemberships(VectorSignature point) throws ClusteringException {
		try {
			double[] m = new double[getNbClasses()];
			double sum = 0;
			int c = 0;
			for (VectorSignature s : getCentroids()) {
				double d = ((KMeans)internalAlgorithm).computeDistance(point, s);
				if (d > 0) {
					m[c] = 1 / d;
					sum += m[c];
				} else {
					m[c] = 0;
				}
				c++;
			}
			if (sum > 0) {
				for (c = 0; c < getNbClasses(); c++) {
					if (m[c] > 0) {
						m[c] /= sum;
					}
				}
			}
			return m;
		} catch (SignatureException e) {
			throw new ClusteringException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(java.util.List, int)
	 */
	@Override
	public double[] getMemberships(List<VectorSignature> points, int cluster) throws ClusteringException {
		double[] m = new double[points.size()];
		int pi = 0;
		for (VectorSignature p : points) {
			double[] tm = getMemberships(p);
			m[pi] = tm[cluster];
			pi++;
		}
		return m;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(java.util.List, int, java.util.List)
	 */
	@Override
	public double[] getMemberships(List<VectorSignature> points, int cluster, List<Integer> clustersToConsider) throws ClusteringException {
		throw new ClusteringException("Not yet implemented");
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(plugins.nherve.toolbox.image.feature.signature.VectorSignature, java.util.List)
	 */
	@Override
	public double[] getMemberships(VectorSignature point, List<Integer> clustersToConsider) throws ClusteringException {
		throw new ClusteringException("Not yet implemented");
	}
}
