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

import plugins.nherve.toolbox.image.feature.ClusteringAlgorithm;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class PseudoFuzzyClusteringAlgorithmImpl.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class PseudoFuzzyClusteringAlgorithmImpl extends DefaultFuzzyClusteringAlgorithmImpl {
	
	/**
	 * Instantiates a new pseudo fuzzy clustering algorithm impl.
	 * 
	 * @param display
	 *            the display
	 */
	public PseudoFuzzyClusteringAlgorithmImpl(boolean display) {
		super(display);
	}

	/** The internal algorithm. */
	protected ClusteringAlgorithm<VectorSignature> internalAlgorithm;

	/**
	 * Sets the internal algorithm.
	 * 
	 * @param internalAlgorithm
	 *            the new internal algorithm
	 */
	protected void setInternalAlgorithm(ClusteringAlgorithm<VectorSignature> internalAlgorithm) {
		this.internalAlgorithm = internalAlgorithm;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#compute(java.util.List)
	 */
	@Override
	public void compute(List<VectorSignature> points) throws ClusteringException {
		internalAlgorithm.compute(points);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getCentroids()
	 */
	@Override
	public List<VectorSignature> getCentroids() throws ClusteringException {
		return internalAlgorithm.getCentroids();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getNbClasses()
	 */
	@Override
	public int getNbClasses() {
		return internalAlgorithm.getNbClasses();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.Algorithm#setDisplayEnabled(boolean)
	 */
	@Override
	public void setLogEnabled(boolean display) {
		super.setLogEnabled(display);
		if (internalAlgorithm != null) {
			internalAlgorithm.setLogEnabled(display);
		}
	}
}
