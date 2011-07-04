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

import java.util.List;

import plugins.nherve.toolbox.AbleToLogMessages;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;


/**
 * The Interface ClusteringAlgorithm.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface ClusteringAlgorithm<T extends Signature> extends AbleToLogMessages {
	
	/**
	 * Gets the nb classes.
	 * 
	 * @return the nb classes
	 */
	int getNbClasses();
	
	/**
	 * Compute.
	 * 
	 * @param points
	 *            the points
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	void compute(List<T> points) throws ClusteringException;
	
	/**
	 * Compute.
	 * 
	 * @param points
	 *            the points
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	void compute(T[] points) throws ClusteringException;
	
	/**
	 * Gets the centroids.
	 * 
	 * @return the centroids
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	List<T> getCentroids() throws ClusteringException;
	
	/**
	 * Gets the affectations.
	 * 
	 * @param points
	 *            the points
	 * @return the affectations
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	int[] getAffectations(List<T> points) throws ClusteringException;
	
	/**
	 * Gets the affectations.
	 * 
	 * @param points
	 *            the points
	 * @return the affectations
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	int[] getAffectations(T[] points) throws ClusteringException;
}
