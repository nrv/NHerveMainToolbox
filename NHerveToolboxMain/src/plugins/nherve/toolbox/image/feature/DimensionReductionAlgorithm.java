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

import plugins.nherve.matrix.Matrix;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;

/**
 * The Class DimensionReductionAlgorithm.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class DimensionReductionAlgorithm extends Algorithm {
	
	/** The signatures. */
	protected List<DefaultVectorSignature> signatures;
	
	/** The mean. */
	protected DefaultVectorSignature mean;
	
	/** The dim. */
	protected int dim;

	/**
	 * Instantiates a new dimension reduction algorithm.
	 * 
	 * @param signatures
	 *            the signatures
	 */
	public DimensionReductionAlgorithm(List<DefaultVectorSignature> signatures) {
		super();
		this.signatures = signatures;
		this.mean = null;
	}

	/**
	 * Compute.
	 * 
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract void compute() throws SignatureException;

	/**
	 * Project.
	 * 
	 * @param toProject
	 *            the to project
	 * @return the list
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract List<DefaultVectorSignature> project(List<DefaultVectorSignature> toProject) throws SignatureException;

	/**
	 * Check.
	 * 
	 * @throws SignatureException
	 *             the signature exception
	 */
	protected void check() throws SignatureException {
		if (signatures == null) {
			throw new SignatureException("Signatures == null, can't run " + getClass().getSimpleName());
		}

		int n = signatures.size();

		if (n < 2) {
			throw new SignatureException("Not enough signatures (" + n + ") to run " + getClass().getSimpleName());
		}

		DefaultVectorSignature aSignature = signatures.get(0);
		dim = aSignature.getSize();
	}

	/**
	 * Gets the mean.
	 * 
	 * @return the mean
	 * @throws SignatureException
	 *             the signature exception
	 */
	public DefaultVectorSignature getMean() throws SignatureException {
		if (mean == null) {
			mean = new DenseVectorSignature(dim);
			for (DefaultVectorSignature s : signatures) {
				mean.add(s);
			}
			mean.multiply(1.0 / signatures.size());
			log(mean.toString());
		}
		
		return mean;
	}
	
	/**
	 * Gets the mean.
	 * 
	 * @param m
	 *            the m
	 * @return the mean
	 * @throws SignatureException
	 *             the signature exception
	 */
	public Matrix getMean(Matrix m) throws SignatureException {
		int n = m.getRowDimension();
		int dim = m.getColumnDimension();
		
		Matrix mean = new Matrix(1, dim, 0);
		for (int i = 0; i < n; i++) {
			for (int d = 0; d < dim; d++) {
				mean.set(0, d, mean.get(0, d) + m.get(i, d));
			}
		}
		
		mean.timesEquals(1.0 / (double)n);
		
		return mean;
	}
	
	/**
	 * Gets the centered matrix.
	 * 
	 * @param sigs
	 *            the sigs
	 * @return the centered matrix
	 * @throws SignatureException
	 *             the signature exception
	 */
	public Matrix getCenteredMatrix(List<DefaultVectorSignature> sigs) throws SignatureException {
		int ln = sigs.size();

		if (ln < 1) {
			throw new SignatureException("Not enough signatures (" + ln + ") to getCenteredMatrix");
		}

		DefaultVectorSignature aSignature = signatures.get(0);
		int ldim = aSignature.getSize();
		
		Matrix m = new Matrix(ln, ldim);
		DefaultVectorSignature mean = getMean();

		int c = 0;
		for (DefaultVectorSignature s : sigs) {
			for (int d = 0; d < ldim; d++) {
				m.set(c, d, s.get(d) - mean.get(d));
			}
			c++;
		}

		return m;
	}
	
	/**
	 * Gets the matrix.
	 * 
	 * @param sigs
	 *            the sigs
	 * @return the matrix
	 * @throws SignatureException
	 *             the signature exception
	 */
	public Matrix getMatrix(List<DefaultVectorSignature> sigs) throws SignatureException {
		int ln = sigs.size();

		if (ln < 1) {
			throw new SignatureException("Not enough signatures (" + ln + ") to getMatrix");
		}

		DefaultVectorSignature aSignature = signatures.get(0);
		int ldim = aSignature.getSize();
		
		Matrix m = new Matrix(ln, ldim);

		int c = 0;
		for (DefaultVectorSignature s : sigs) {
			for (int d = 0; d < ldim; d++) {
				m.set(c, d, s.get(d));
			}
			c++;
		}

		return m;
	}

	/**
	 * Gets the var cov matrix.
	 * 
	 * @param m
	 *            the m
	 * @return the var cov matrix
	 * @throws SignatureException
	 *             the signature exception
	 */
	public Matrix getVarCovMatrix(Matrix m) throws SignatureException {
		Matrix varcov = m.transpose().times(m);
		varcov.timesEquals(1.0 / (double)m.getRowDimension());
		return varcov;
	}

}
