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
 * The Interface FuzzyClusteringAlgorithm.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface FuzzyClusteringAlgorithm extends ClusteringAlgorithm<VectorSignature> {
	
	/**
	 * Gets the memberships.
	 * 
	 * @param point
	 *            the point
	 * @return the memberships
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	double[] getMemberships(VectorSignature point) throws ClusteringException;
	
	/**
	 * Gets the memberships.
	 * 
	 * @param point
	 *            the point
	 * @param clustersToConsider
	 *            the clusters to consider
	 * @return the memberships
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	double[] getMemberships(VectorSignature point, List<Integer> clustersToConsider) throws ClusteringException;
	
	/**
	 * Gets the memberships.
	 * 
	 * @param point
	 *            the point
	 * @param cluster
	 *            the cluster
	 * @return the memberships
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	double[] getMemberships(List<VectorSignature> point, int cluster) throws ClusteringException;
	
	/**
	 * Gets the memberships.
	 * 
	 * @param point
	 *            the point
	 * @param cluster
	 *            the cluster
	 * @return the memberships
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	double[] getMemberships(VectorSignature[] point, int cluster) throws ClusteringException;
	
	/**
	 * Gets the memberships.
	 * 
	 * @param point
	 *            the point
	 * @param cluster
	 *            the cluster
	 * @param clustersToConsider
	 *            the clusters to consider
	 * @return the memberships
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	double[] getMemberships(List<VectorSignature> point, int cluster, List<Integer> clustersToConsider) throws ClusteringException;
	
	/**
	 * Gets the memberships.
	 * 
	 * @param point
	 *            the point
	 * @param cluster
	 *            the cluster
	 * @param clustersToConsider
	 *            the clusters to consider
	 * @return the memberships
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	double[] getMemberships(VectorSignature[] point, int cluster, List<Integer> clustersToConsider) throws ClusteringException;
}
