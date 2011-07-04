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

import icy.system.CPUMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.DefaultClusteringAlgorithmImpl;
import plugins.nherve.toolbox.image.feature.Distance;
import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class KMeans.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class KMeans extends DefaultClusteringAlgorithmImpl<VectorSignature> implements Distance<VectorSignature> {
	
	/**
	 * The Class ComputeAffectationWorker.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	public class ComputeAffectationWorker extends MultipleDataTask<VectorSignature, Integer> {
		
		/**
		 * Instantiates a new compute affectation worker.
		 * 
		 * @param allData
		 *            the all data
		 * @param idx1
		 *            the idx1
		 * @param idx2
		 *            the idx2
		 */
		public ComputeAffectationWorker(List<VectorSignature> allData, int idx1, int idx2) {
			super(allData, idx1, idx2);
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.concurrent.MultipleDataTask#call(java.lang.Object, int)
		 */
		@Override
		public void call(VectorSignature data, int idx) throws SignatureException {
			double minDist = Double.MAX_VALUE;
			int closestCentroid = 0;
			int c = 0;
			for (VectorSignature s : centroids) {
				double d = computeDistance(data, s);
				if (d < minDist) {
					minDist = d;
					closestCentroid = c;
				}
				c++;
			}
			affectation[idx] = closestCentroid;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.concurrent.MultipleDataTask#outputCall()
		 */
		@Override
		public Integer outputCall() {
			return 0;
		}
		
		@Override
		public void processContextualData() {
		}

	}

	/** The Constant SMART_INTITAL_CENTROIDS. */
	public final static int SMART_INTITAL_CENTROIDS = 1;
	
	/** The Constant STANDARD_INTITAL_CENTROIDS. */
	public final static int STANDARD_INTITAL_CENTROIDS = 2;
	
	/** The Constant PROVIDED_INTITAL_CENTROIDS. */
	public final static int PROVIDED_INTITAL_CENTROIDS = 3;

	/** The distance. */
	private SignatureDistance<VectorSignature> distance;
	
	/** The nb classes. */
	private int nbClasses;
	
	/** The nb max iterations. */
	private int nbMaxIterations;
	
	/** The initial centroids type. */
	private int initialCentroidsType;
	
	/** The stabilization criterion. */
	private double stabilizationCriterion;

	/** The centroids. */
	private List<VectorSignature> centroids;

	/** The initial centroids. */
	private List<VectorSignature> initialCentroids;
	
	/** The able to move. */
	private List<Boolean> ableToMove;

	/** The affectation. */
	private int[] affectation;

	/**
	 * Instantiates a new k means.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 */
	public KMeans(int nbClasses) {
		this(nbClasses, 100, 1.0);
	}

	/**
	 * Instantiates a new k means.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 * @param nbMaxIterations
	 *            the nb max iterations
	 * @param stabilizationCriterion
	 *            the stabilization criterion
	 */
	public KMeans(int nbClasses, int nbMaxIterations, double stabilizationCriterion) {
		super(false);
		distance = new L2Distance();
		setInitialCentroidsType(SMART_INTITAL_CENTROIDS);
		this.nbClasses = nbClasses;
		this.nbMaxIterations = nbMaxIterations;
		this.stabilizationCriterion = stabilizationCriterion;
		centroids = null;
		affectation = null;
	}

	/**
	 * Adds the initial centroid.
	 * 
	 * @param centroid
	 *            the centroid
	 * @param move
	 *            the move
	 */
	public void addInitialCentroid(VectorSignature centroid, boolean move) {
		initialCentroids.add(centroid);
		ableToMove.add(move);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#compute(java.util.List)
	 */
	@Override
	public void compute(final List<VectorSignature> points) throws ClusteringException {
		log("Launching KMeans on " + points.size() + " points to produce " + nbClasses + " classes");

		List<VectorSignature> oldCentroids = null;
		affectation = new int[points.size()];

		if (nbClasses < 2) {
			throw new ClusteringException("nbClasses == " + nbClasses);
		}

		if (points.size() <= nbClasses) {
			centroids = new ArrayList<VectorSignature>();
			try {
				for (VectorSignature s : points) {
					centroids.add(s.clone());
				}
				computeAffectation(points);
			} catch (CloneNotSupportedException e) {
				throw new ClusteringException(e);
			}
			return;
		}

		try {

			do {
				initialCentroids(points);
				computeAffectation(points);
			} while ((initialCentroidsType == SMART_INTITAL_CENTROIDS) && emptyCluster());

			int iteration = 0;
			double stab = 0.0;
			CPUMonitor cpu = new CPUMonitor();
			cpu.start();

			do {
				oldCentroids = centroids;
				computeCentroids(points, oldCentroids);
				stab = computeStabilizationCriterion(oldCentroids);

				computeAffectation(points);

				log("[It " + iteration + "] " + stab);

				iteration++;
			} while ((iteration < nbMaxIterations) && (stab > stabilizationCriterion));

			cpu.stop();
			log("average time per iteration : " + cpu.getElapsedTimeMilli() / iteration / 1000.0 + " s");

			if (isLogEnabled()) {
				emptyCluster();
//				for (VectorSignature c : centroids) {
//					log(c.toString());
//				}
			}

		} catch (SignatureException e) {
			throw new ClusteringException(e);
		}
	}

	/**
	 * Compute affectation.
	 * 
	 * @param points
	 *            the points
	 */
	private void computeAffectation(List<VectorSignature> points) {
		TaskManager tm = TaskManager.getSecondLevelInstance();
		try {
			tm.submitMultiForAll(points, ComputeAffectationWorker.class, this, "KMeans", 0);
		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compute centroids.
	 * 
	 * @param points
	 *            the points
	 * @param oldCentroids
	 *            the old centroids
	 * @return the list
	 * @throws SignatureException
	 *             the signature exception
	 */
	private List<VectorSignature> computeCentroids(final List<VectorSignature> points, final List<VectorSignature> oldCentroids) throws SignatureException {
		int dim = points.get(0).getSize();
		centroids = new ArrayList<VectorSignature>();
		for (int c = 0; c < nbClasses; c++) {
			if ((initialCentroidsType == PROVIDED_INTITAL_CENTROIDS) && (!ableToMove.get(c)) && (oldCentroids != null)) {
				try {
					centroids.add(oldCentroids.get(c).clone());
				} catch (CloneNotSupportedException e) {
					throw new SignatureException(e.getMessage());
				}
			} else {
				centroids.add(new DenseVectorSignature(dim));
			}
		}
		int[] cardinality = new int[nbClasses];
		Arrays.fill(cardinality, 0);

		int p = 0;
		for (VectorSignature s : points) {
			if ((initialCentroidsType != PROVIDED_INTITAL_CENTROIDS) || (ableToMove.get(affectation[p]))) {
				centroids.get(affectation[p]).add(s);
				cardinality[affectation[p]]++;
			}
			p++;
		}

		int c = 0;
		for (VectorSignature s : centroids) {
			if (cardinality[c] > 0) {
				s.multiply(1.0 / cardinality[c]);
			}
			c++;
		}

		return centroids;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Distance#computeDistance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double computeDistance(VectorSignature s1, VectorSignature s2) throws SignatureException {
		return distance.computeDistance(s1, s2);
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
			double d = computeDistance(oldCentroids.get(c), centroids.get(c));
			stab += d;
		}

		return stab / nbClasses;
	}

	/**
	 * Empty cluster.
	 * 
	 * @return true, if successful
	 */
	private boolean emptyCluster() {
		int[] cardinality = new int[nbClasses];
		Arrays.fill(cardinality, 0);
		for (int p = 0; p < affectation.length; p++) {
			cardinality[affectation[p]]++;
		}

		if (isLogEnabled()) {
			String msg = "  - ";
			for (int c = 0; c < nbClasses; c++) {
				msg += cardinality[c] + "; ";
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
	 * Gets the affectations.
	 * 
	 * @return the affectations
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	public int[] getAffectations() throws ClusteringException {
		return affectation;
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

	/**
	 * Gets the initial centroids type.
	 * 
	 * @return the initial centroids type
	 */
	public int getInitialCentroidsType() {
		return initialCentroidsType;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ClusteringAlgorithm#getNbClasses()
	 */
	public int getNbClasses() {
		return nbClasses;
	}

	/**
	 * Gets the nb max iterations.
	 * 
	 * @return the nb max iterations
	 */
	public int getNbMaxIterations() {
		return nbMaxIterations;
	}

	/**
	 * Gets the stabilization criterion.
	 * 
	 * @return the stabilization criterion
	 */
	public double getStabilizationCriterion() {
		return stabilizationCriterion;
	}
	
	/**
	 * Sanity check.
	 * 
	 * @param points
	 *            the points
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void sanityCheck(final List<VectorSignature> points) throws SignatureException {
		Random rd = new Random(System.currentTimeMillis());
		int nbTry = points.size() / 10;
		for (int t = 0; t < nbTry; t++) {
			double d = computeDistance(points.get(rd.nextInt(points.size())), points.get(rd.nextInt(points.size()))); 
			if (d > 1E-10) {
				System.out.println("sanityCheck - " + d);
				return;
			}
		}
		throw new SignatureException("sanityCheck failed");
	}
	
	/**
	 * Initial centroids.
	 * 
	 * @param points
	 *            the points
	 * @throws SignatureException
	 *             the signature exception
	 */
	private void initialCentroids(final List<VectorSignature> points) throws SignatureException {
		log("initialCentroids called (" + initialCentroidsType + ")");

		centroids = new ArrayList<VectorSignature>();

		if (initialCentroidsType == STANDARD_INTITAL_CENTROIDS) {
			int dim = points.get(0).getSize();
			double step = 1.0 / (nbClasses - 1);
			double v = 0;
			for (int c = 0; c < nbClasses; c++) {
				VectorSignature s = new DenseVectorSignature(dim);
				for (int d = 0; d < dim; d++) {
					s.set(d, v);
				}
				centroids.add(s);
				v += step;
			}
		} else {
			Random rd = new Random(System.currentTimeMillis());
			boolean[] affected = new boolean[points.size()];
			Arrays.fill(affected, false);
			int c = 0;

			if (initialCentroidsType == PROVIDED_INTITAL_CENTROIDS) {
				for (VectorSignature s : initialCentroids) {
					try {
						centroids.add(s.clone());
					} catch (CloneNotSupportedException e) {
						throw new SignatureException(e);
					}
					c++;
				}
			}

			int randomPoint = 0;
			boolean tooClose = false;
			for (; c < nbClasses; c++) {
				do {
					tooClose = false;
					do {
						randomPoint = rd.nextInt(points.size());
					} while (affected[randomPoint]);
					for (int c2 = 0; c2 < c; c2++) {
						if (computeDistance(points.get(randomPoint), centroids.get(c2)) == 0) {
							tooClose = true;
							break;
						}
					}
				} while (tooClose);
				try {
					centroids.add(points.get(randomPoint).clone());
					affected[randomPoint] = true;
					if (ableToMove != null) {
						ableToMove.add(true);
					}
				} catch (CloneNotSupportedException e) {
					throw new SignatureException(e);
				}
			}

		}

		log("initialCentroids done");
	}

	/**
	 * Sets the centroids.
	 * 
	 * @param centroids
	 *            the new centroids
	 */
	public void setCentroids(List<VectorSignature> centroids) {
		this.centroids = centroids;
	}

	/**
	 * Sets the distance.
	 * 
	 * @param distance
	 *            the new distance
	 */
	public void setDistance(SignatureDistance<VectorSignature> distance) {
		this.distance = distance;
	}

	/**
	 * Sets the initial centroids type.
	 * 
	 * @param initialCentroidsType
	 *            the new initial centroids type
	 */
	public void setInitialCentroidsType(int initialCentroidsType) {
		this.initialCentroidsType = initialCentroidsType;

		if (initialCentroidsType == PROVIDED_INTITAL_CENTROIDS) {
			initialCentroids = new ArrayList<VectorSignature>();
			ableToMove = new ArrayList<Boolean>();
		}
	}

	/**
	 * Sets the nb classes.
	 * 
	 * @param nbClasses
	 *            the new nb classes
	 */
	public void setNbClasses(int nbClasses) {
		this.nbClasses = nbClasses;
	}

	/**
	 * Sets the nb max iterations.
	 * 
	 * @param nbMaxIterations
	 *            the new nb max iterations
	 */
	public void setNbMaxIterations(int nbMaxIterations) {
		this.nbMaxIterations = nbMaxIterations;
	}

	/**
	 * Sets the stabilization criterion.
	 * 
	 * @param stabilizationCriterion
	 *            the new stabilization criterion
	 */
	public void setStabilizationCriterion(double stabilizationCriterion) {
		this.stabilizationCriterion = stabilizationCriterion;
	}

}
