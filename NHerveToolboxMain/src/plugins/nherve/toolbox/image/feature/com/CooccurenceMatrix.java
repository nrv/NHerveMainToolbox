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
package plugins.nherve.toolbox.image.feature.com;

import plugins.nherve.matrix.Matrix;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SparseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * The Class CooccurenceMatrix.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class CooccurenceMatrix<T> extends Matrix {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5094289804168175458L;
	
	/** The Constant SMALL_SIGNATURE_SIZE. */
	public static final int SMALL_SIGNATURE_SIZE = 4;
	
	/** The vocabulary. */
	private Vocabulary<T> vocabulary;
	
	/** The normalized. */
	private boolean normalized;

	/**
	 * Instantiates a new cooccurence matrix.
	 * 
	 * @param vs
	 *            the vs
	 */
	public CooccurenceMatrix(int vs) {
		super(vs, vs);

		vocabulary = null;
		normalized = false;
	}

	/**
	 * Instantiates a new cooccurence matrix.
	 * 
	 * @param vocabulary
	 *            the vocabulary
	 */
	public CooccurenceMatrix(Vocabulary<T> vocabulary) {
		this(vocabulary.size());

		this.vocabulary = vocabulary;
	}

	/**
	 * Normalize sum to one.
	 */
	public void normalizeSumToOne() {
		if (!normalized) {
			normalizeSumTo(1d);
			normalized = true;
		}
	}

	/**
	 * Entropy.
	 * 
	 * @return the double
	 */
	public double entropy() {
		normalizeSumToOne();
		double entropy = 0;
		for (int j = 0; j < n; j++) {
			entropy += entropy(j);
		}
		return entropy;
	}

	/**
	 * Entropy.
	 * 
	 * @param i
	 *            the i
	 * @return the double
	 */
	private double entropy(int i) {
		double entropy = 0;
		for (int j = 0; j < n; j++) {
			if (A[i][j] != 0) {
				entropy += A[i][j] * Math.log(A[i][j]);
			}
		}
		return entropy;
	}

	/**
	 * Uniformity.
	 * 
	 * @return the double
	 */
	public double uniformity() {
		normalizeSumToOne();
		double asm = 0;
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				asm += A[i][j] * A[i][j];
			}
		}
		return asm;
	}

	/**
	 * Energy.
	 * 
	 * @return the double
	 */
	public double energy() {
		return Math.sqrt(uniformity());
	}

	/**
	 * Homogeneity.
	 * 
	 * @param i
	 *            the i
	 * @return the double
	 * @throws FeatureException
	 *             the feature exception
	 */
	private double homogeneity(int i) throws FeatureException {
		double h = 0;
		for (int j = 0; j < n; j++) {
			h += A[i][j] / (1 + vocabulary.computeDistance(i, j));
		}
		return h;
	}

	/**
	 * Homogeneity.
	 * 
	 * @return the double
	 * @throws FeatureException
	 *             the feature exception
	 */
	public double homogeneity() throws FeatureException {
		normalizeSumToOne();
		double h = 0;
		for (int j = 0; j < n; j++) {
			h += homogeneity(j);
		}
		return h;
	}

	/**
	 * Contrast.
	 * 
	 * @param i
	 *            the i
	 * @return the double
	 * @throws FeatureException
	 *             the feature exception
	 */
	private double contrast(int i) throws FeatureException {
		double c = 0;
		for (int j = 0; j < n; j++) {
			c += A[i][j] * vocabulary.computeDistance(i, j);
		}
		return c;
	}

	/**
	 * Contrast.
	 * 
	 * @return the double
	 * @throws FeatureException
	 *             the feature exception
	 */
	public double contrast() throws FeatureException {
		normalizeSumToOne();
		double c = 0;
		for (int j = 0; j < n; j++) {
			c += contrast(j);
		}
		return c;
	}

	/**
	 * Gets the small signature.
	 * 
	 * @return the small signature
	 * @throws FeatureException
	 *             the feature exception
	 */
	public VectorSignature getSmallSignature() throws FeatureException {
		DenseVectorSignature sig = new DenseVectorSignature(SMALL_SIGNATURE_SIZE);
		sig.set(0, energy());
		sig.set(1, entropy());
		sig.set(2, contrast());
		sig.set(3, homogeneity());
		return sig;
	}

	/**
	 * Gets the marginalized signature.
	 * 
	 * @return the marginalized signature
	 * @throws FeatureException
	 *             the feature exception
	 */
	public VectorSignature getMarginalizedSignature() throws FeatureException {
		normalizeSumToOne();
		DenseVectorSignature sig = new DenseVectorSignature(n);
		for (int j = 0; j < n; j++) {
			sig.set(j, contrast(j));
		}
		return sig;
	}

	/**
	 * As signature.
	 * 
	 * @param dense
	 *            the dense
	 * @return the vector signature
	 * @throws FeatureException
	 *             the feature exception
	 */
	public VectorSignature asSignature(boolean dense) throws FeatureException {
		VectorSignature sig = null;
		if (dense) {
			sig = new DenseVectorSignature(n * n);
		} else {
			sig = new SparseVectorSignature(n * n);
		}
		int d = 0;
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				if (A[i][j] != 0) {
					sig.set(d, A[i][j]);
				}
				d++;
			}
		}
		return sig;
	}
	
	/**
	 * Diagonal.
	 * 
	 * @param dense
	 *            the dense
	 * @return the vector signature
	 * @throws FeatureException
	 *             the feature exception
	 */
	public VectorSignature diagonal(boolean dense) throws FeatureException {
		VectorSignature sig = null;
		if (dense) {
			sig = new DenseVectorSignature(n);
		} else {
			sig = new SparseVectorSignature(n);
		}
		for (int i = 0; i < n; i++) {
			sig.set(i, A[i][i]);
		}
		return sig;
	}

}
