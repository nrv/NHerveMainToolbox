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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugins.nherve.toolbox.image.feature.DefaultClusteringAlgorithmImpl;
import plugins.nherve.toolbox.image.feature.Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class HierarchicalKMeans.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class HierarchicalKMeans extends DefaultClusteringAlgorithmImpl<VectorSignature> implements Distance<VectorSignature> {
	
	/** The sub display enabled. */
	private boolean subDisplayEnabled;
	
	/** The nb levels. */
	private int nbLevels;
	
	/** The each level nb classes. */
	private int eachLevelNbClasses;
	
	/** The each level nb max iterations. */
	private int eachLevelNbMaxIterations;
	
	/** The each level stabilization criterion. */
	private double eachLevelStabilizationCriterion;
	
	/** The final km. */
	private KMeans finalKM;

	/**
	 * Instantiates a new hierarchical k means.
	 * 
	 * @param nbLevels
	 *            the nb levels
	 * @param eachLevelNbClasses
	 *            the each level nb classes
	 * @param eachLevelNbMaxIterations
	 *            the each level nb max iterations
	 * @param eachLevelStabilizationCriterion
	 *            the each level stabilization criterion
	 */
	public HierarchicalKMeans(int nbLevels, int eachLevelNbClasses, int eachLevelNbMaxIterations, double eachLevelStabilizationCriterion) {
		super(false);
		this.nbLevels = nbLevels;
		this.eachLevelNbClasses = eachLevelNbClasses;
		this.eachLevelNbMaxIterations = eachLevelNbMaxIterations;
		this.eachLevelStabilizationCriterion = eachLevelStabilizationCriterion;
		setSubDisplayEnabled(false);
	}

	/**
	 * Compute next level.
	 * 
	 * @param currentLevel
	 *            the current level
	 * @return the map
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	private Map<VectorSignature, List<VectorSignature>> computeNextLevel(Map<VectorSignature, List<VectorSignature>> currentLevel) throws ClusteringException {
		Map<VectorSignature, List<VectorSignature>> nextLevel = new HashMap<VectorSignature, List<VectorSignature>>();

		for (List<VectorSignature> points : currentLevel.values()) {
			KMeans km = new KMeans(eachLevelNbClasses, eachLevelNbMaxIterations, eachLevelStabilizationCriterion);
			km.setLogEnabled(isSubDisplayEnabled());
			km.compute(points);
			
			int[] aff = km.getAffectations();
			List<VectorSignature> ct = km.getCentroids();
			ArrayList<ArrayList<VectorSignature>> ppc = new ArrayList<ArrayList<VectorSignature>>();
			for (int i = 0; i < ct.size(); i++) {
				ppc.add(new ArrayList<VectorSignature>());
			}
			int a = 0;
			for (VectorSignature vs : points) {
				ppc.get(aff[a]).add(vs);
				a++;
			}
			
			for (int i = 0; i < ct.size(); i++) {
				nextLevel.put(ct.get(i), ppc.get(i));
			}
		}

		return nextLevel;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#compute(java.util.List)
	 */
	@Override
	public void compute(List<VectorSignature> points) throws ClusteringException {
		log("Launching HierarchicalKMeans on " + points.size() + " points to produce " + nbLevels + " levels of " + eachLevelNbClasses + " classes");
		
		Map<VectorSignature, List<VectorSignature>> currentLevel = new HashMap<VectorSignature, List<VectorSignature>>();
		currentLevel.put(null, points);
		
		for (int l = 0; l < nbLevels; l++) {
			log("----------------------------------------- Computing level " + l);
			currentLevel = computeNextLevel(currentLevel);
		}
		
		finalKM = new KMeans(currentLevel.size(), 0, 0);
		finalKM.setLogEnabled(isLogEnabled());
		finalKM.setInitialCentroidsType(KMeans.PROVIDED_INTITAL_CENTROIDS);
		for (VectorSignature ct : currentLevel.keySet()) {
			finalKM.addInitialCentroid(ct, false);
		}
		finalKM.compute(points);
	}
 
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getAffectations(java.util.List)
	 */
	@Override
	public int[] getAffectations(List<VectorSignature> points) throws ClusteringException {
		return finalKM.getAffectations(points);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getCentroids()
	 */
	@Override
	public List<VectorSignature> getCentroids() throws ClusteringException {
		return finalKM.getCentroids();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getNbClasses()
	 */
	@Override
	public int getNbClasses() {
		return finalKM.getNbClasses();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Distance#computeDistance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double computeDistance(VectorSignature s1, VectorSignature s2) throws SignatureException {
		return finalKM.computeDistance(s1, s2);
	}

	/**
	 * Checks if is sub display enabled.
	 * 
	 * @return true, if is sub display enabled
	 */
	public boolean isSubDisplayEnabled() {
		return subDisplayEnabled;
	}

	/**
	 * Sets the sub display enabled.
	 * 
	 * @param subDisplayEnabled
	 *            the new sub display enabled
	 */
	public void setSubDisplayEnabled(boolean subDisplayEnabled) {
		this.subDisplayEnabled = subDisplayEnabled;
	}

}
