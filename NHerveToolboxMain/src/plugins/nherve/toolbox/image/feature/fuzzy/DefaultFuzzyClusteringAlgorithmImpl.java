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

import java.util.Arrays;
import java.util.List;

import plugins.nherve.toolbox.image.feature.DefaultClusteringAlgorithmImpl;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class DefaultFuzzyClusteringAlgorithmImpl.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class DefaultFuzzyClusteringAlgorithmImpl extends DefaultClusteringAlgorithmImpl<VectorSignature> implements FuzzyClusteringAlgorithm {
	
	/**
	 * Instantiates a new default fuzzy clustering algorithm impl.
	 * 
	 * @param display
	 *            the display
	 */
	public DefaultFuzzyClusteringAlgorithmImpl(boolean display) {
		super(display);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getAffectations(java.util.List)
	 */
	@Override
	public int[] getAffectations(List<VectorSignature> points) throws ClusteringException {
		int[] as = new int[points.size()];
		int p = 0;
		for (VectorSignature s : points) {
			as[p] = getAffectation(s);
			p++;
		}
		return as;
	}
	
	/**
	 * Gets the affectation.
	 * 
	 * @param point
	 *            the point
	 * @return the affectation
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	public int getAffectation(VectorSignature point) throws ClusteringException {
		double[] m = getMemberships(point);
		int a = 0;
		double mx = 0;
		for (int c = 0; c < getNbClasses(); c++) {
			if (m[c] > mx) {
				mx = m[c];
				a = c;
			}
		}
		return a;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(plugins.nherve.toolbox.image.feature.signature.VectorSignature[], int)
	 */
	@Override
	public double[] getMemberships(VectorSignature[] point, int cluster) throws ClusteringException {
		return getMemberships(Arrays.asList(point), cluster);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(plugins.nherve.toolbox.image.feature.signature.VectorSignature[], int, java.util.List)
	 */
	@Override
	public double[] getMemberships(VectorSignature[] point, int cluster, List<Integer> clustersToConsider) throws ClusteringException {
		return getMemberships(Arrays.asList(point), cluster, clustersToConsider);
	}
}
