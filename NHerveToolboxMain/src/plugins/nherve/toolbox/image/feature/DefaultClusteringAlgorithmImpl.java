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
package plugins.nherve.toolbox.image.feature;

import java.util.Arrays;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;


/**
 * The Class DefaultClusteringAlgorithmImpl.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class DefaultClusteringAlgorithmImpl<T extends Signature> extends Algorithm implements ClusteringAlgorithm<T> {
	
	/**
	 * Instantiates a new default clustering algorithm impl.
	 * 
	 * @param display
	 *            the display
	 */
	public DefaultClusteringAlgorithmImpl(boolean display) {
		super(display);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getAffectations(T[])
	 */
	public int[] getAffectations(T[] points) throws ClusteringException {
		return getAffectations(Arrays.asList(points));
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#compute(T[])
	 */
	public void compute(T[] points) throws ClusteringException {
		compute(Arrays.asList(points));
	}
}
