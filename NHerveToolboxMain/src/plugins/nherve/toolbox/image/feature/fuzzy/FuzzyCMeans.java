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

import icy.system.CPUMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


// TODO A tester
/**
 * The Class FuzzyCMeans.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class FuzzyCMeans extends DefaultFuzzyClusteringAlgorithmImpl {
	
	/**
	 * The Class ComputeMembershipWorker.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	public class ComputeMembershipWorker extends MultipleDataTask<VectorSignature, Integer> {
		
		/**
		 * Instantiates a new compute membership worker.
		 * 
		 * @param allData
		 *            the all data
		 * @param idx1
		 *            the idx1
		 * @param idx2
		 *            the idx2
		 */
		public ComputeMembershipWorker(List<VectorSignature> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.concurrent.MultipleDataTask#call(java.lang.Object, int)
		 */
		@Override
		public void call(VectorSignature vs, int idx) throws Exception {
			double nexp = 2d / (fuzzyfier - 1);
			double[] dst = new double[nbClasses];
			int c = 0;
			for (VectorSignature s : centroids) {
				dst[c] = distance(vs, s);
				c++;
			}

			for (int c1 = 0; c1 < nbClasses; c1++) {
				double m = 0;
				double num = dst[c1];
				for (int c2 = 0; c2 < nbClasses; c2++) {
					m += Math.pow(num / dst[c2], nexp);
				}
				memberships[idx][c1] = 1d / m;
			}

		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.concurrent.MultipleDataTask#outputCall()
		 */
		@Override
		public Integer outputCall() throws Exception {
			return 0;
		}
		
		@Override
		public void processContextualData() {
		}

	}

	/** The distance. */
	private SignatureDistance<VectorSignature> distance;
	
	/** The nb classes. */
	private int nbClasses;
	
	/** The nb max iterations. */
	private int nbMaxIterations;
	
	/** The stabilization criterion. */
	private double stabilizationCriterion;
	
	/** The centroids. */
	private List<VectorSignature> centroids;
	
	/** The nb points. */
	private int nbPoints;
	
	/** The memberships. */
	private double[][] memberships;
	
	/** The fuzzyfier. */
	private double fuzzyfier;

	/**
	 * Instantiates a new fuzzy c means.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 * @param nbMaxIterations
	 *            the nb max iterations
	 * @param stabilizationCriterion
	 *            the stabilization criterion
	 */
	public FuzzyCMeans(int nbClasses, int nbMaxIterations, double stabilizationCriterion) {
		super(false);

		this.nbClasses = nbClasses;
		this.nbMaxIterations = nbMaxIterations;
		this.stabilizationCriterion = stabilizationCriterion;

		distance = new L2Distance();
		centroids = null;
		nbPoints = 0;
		memberships = null;
		fuzzyfier = 2d;
	}

	/**
	 * Empty cluster.
	 * 
	 * @return true, if successful
	 */
	private boolean emptyCluster() {
		double[] cardinality = new double[nbClasses];
		Arrays.fill(cardinality, 0);
		for (int p = 0; p < nbPoints; p++) {
			for (int c = 0; c < nbClasses; c++) {
				cardinality[c] += memberships[p][c];
			}
		}

		if (isLogEnabled()) {
			String msg = "  - ";
			for (int c = 0; c < nbClasses; c++) {
				msg += cardinality[c] + " ";
			}
			log(msg);
		}

		for (int c = 0; c < nbClasses; c++) {
			if (cardinality[c] == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compute stabilization criterion.
	 * 
	 * @param oldCentroids
	 *            the old centroids
	 * @return the double
	 * @throws SignatureException
	 *             the signature exception
	 */
	private double computeStabilizationCriterion(final List<VectorSignature> oldCentroids) throws SignatureException {
		if (oldCentroids == null) {
			return nbClasses * stabilizationCriterion * 100.0;
		}

		double stab = 0.0;

		for (int c = 0; c < nbClasses; c++) {
			double d = distance(oldCentroids.get(c), centroids.get(c));
			stab = Math.max(d, stab);
		}

		return stab;
	}

	/**
	 * Compute memberships.
	 * 
	 * @param points
	 *            the points
	 */
	private void computeMemberships(List<VectorSignature> points) {
		TaskManager tm = TaskManager.getSecondLevelInstance();
		try {
			tm.submitMultiForAll2(points, ComputeMembershipWorker.class, this, "FCM", 0);
		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#compute(java.util.List)
	 */
	@Override
	public void compute(List<VectorSignature> points) throws ClusteringException {
		nbPoints = points.size();

		log("Launching FuzzyCMeans on " + nbPoints + " points to produce " + nbClasses + " classes");

		if (nbClasses < 2) {
			throw new ClusteringException("nbClasses == " + nbClasses);
		}

		List<VectorSignature> oldCentroids = null;
		memberships = new double[nbPoints][nbClasses];

		if (nbPoints <= nbClasses) {
			centroids = new ArrayList<VectorSignature>();
			try {
				for (VectorSignature s : points) {
					centroids.add(s.clone());
				}
				computeMemberships(points);
			} catch (CloneNotSupportedException e) {
				throw new ClusteringException(e);
			}
			return;
		}

		try {

			do {
				initialMemberships();
			} while (emptyCluster());

			int iteration = 0;
			double stab = 0.0;
			CPUMonitor cpu = new CPUMonitor();
			cpu.start();

			do {
				oldCentroids = centroids;
				computeCentroids(points);
				stab = computeStabilizationCriterion(oldCentroids);

				computeMemberships(points);

				log("[It " + iteration + "] " + stab);

				iteration++;
			} while ((iteration < nbMaxIterations) && (stab > stabilizationCriterion));

			cpu.stop();
			log("average time per iteration : " + cpu.getElapsedTimeMilli() / iteration / 1000.0 + " s");

			if (isLogEnabled()) {
				emptyCluster();
			}

		} catch (SignatureException e) {
			throw new ClusteringException(e);
		}
	}

	/**
	 * Initial memberships.
	 */
	private void initialMemberships() {
		Random rd = new Random(System.currentTimeMillis());
		for (int p = 0; p < nbPoints; p++) {
			double left = 1d;
			for (int c = 0; c < nbClasses - 1; c++) {
				double m = rd.nextDouble() * left;
				memberships[p][c] = m;
				left -= m;
			}
			memberships[p][nbClasses - 1] = left;
		}
	}

	/**
	 * Compute centroids.
	 * 
	 * @param points
	 *            the points
	 * @return the list
	 * @throws SignatureException
	 *             the signature exception
	 */
	private List<VectorSignature> computeCentroids(final List<VectorSignature> points) throws SignatureException {
		int dim = points.get(0).getSize();
		centroids = new ArrayList<VectorSignature>();
		for (int c = 0; c < nbClasses; c++) {
			centroids.add(new DenseVectorSignature(dim));
		}

		double[] nrm = new double[nbClasses];
		Arrays.fill(nrm, 0);

		int p = 0;
		for (VectorSignature vs : points) {
			for (int c = 0; c < nbClasses; c++) {
				VectorSignature cs = centroids.get(c);
				double m = Math.pow(memberships[p][c], fuzzyfier);
				nrm[c] += m;
				for (int d = 0; d < dim; d++) {
					cs.addTo(d, vs.get(d) * m);
				}
			}
			p++;
		}

		int c = 0;
		for (VectorSignature cs : centroids) {
			cs.multiply(1.0 / nrm[c]);
			c++;
		}

		return centroids;
	}

	/**
	 * Distance.
	 * 
	 * @param s1
	 *            the s1
	 * @param s2
	 *            the s2
	 * @return the double
	 * @throws SignatureException
	 *             the signature exception
	 */
	private double distance(VectorSignature s1, VectorSignature s2) throws SignatureException {
		return distance.computeDistance(s1, s2);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getCentroids()
	 */
	@Override
	public List<VectorSignature> getCentroids() throws ClusteringException {
		return centroids;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm#getMemberships(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	public double[] getMemberships(VectorSignature point) throws ClusteringException {
		try {
			double nexp = 2d / (fuzzyfier - 1);
			double[] dst = new double[nbClasses];
			double[] m = new double[nbClasses];
			int c = 0;
			for (VectorSignature s : centroids) {
				dst[c] = distance(point, s);
				c++;
			}

			for (int c1 = 0; c1 < nbClasses; c1++) {
				double im = 0;
				double num = dst[c1];
				for (int c2 = 0; c2 < nbClasses; c2++) {
					im += Math.pow(num / dst[c2], nexp);
				}
				m[c1] = 1d / im;
			}

			return m;
		} catch (SignatureException e) {
			throw new ClusteringException(e);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getNbClasses()
	 */
	@Override
	public int getNbClasses() {
		return nbClasses;
	}

	/**
	 * Sets the fuzzyfier.
	 * 
	 * @param fuzzyfier
	 *            the new fuzzyfier
	 */
	public void setFuzzyfier(double fuzzyfier) {
		this.fuzzyfier = fuzzyfier;
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
