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
package plugins.nherve.toolbox.image.feature.signature;

import java.text.DecimalFormat;

import plugins.nherve.toolbox.image.feature.Signature;


/**
 * The Class VectorSignature.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class VectorSignature implements Signature, Iterable<Integer> {
	
	/** The Constant DENSE_VECTOR_SIGNATURE. */
	public final static int DENSE_VECTOR_SIGNATURE = 1;
	
	/** The Constant SPARSE_VECTOR_SIGNATURE. */
	public final static int SPARSE_VECTOR_SIGNATURE = 2;

	/** The Constant df. */
	private final static DecimalFormat df = new DecimalFormat("0.000");
	
	/** The additional information. */
	private Object additionalInformation;

	/**
	 * Instantiates a new vector signature.
	 */
	public VectorSignature() {
		super();
		setAdditionalInformation(null);
	}

	/**
	 * Gets the empty signature.
	 * 
	 * @param type
	 *            the type
	 * @param size
	 *            the size
	 * @return the empty signature
	 */
	public static VectorSignature getEmptySignature(int type, int size) {
		switch (type) {
		case DENSE_VECTOR_SIGNATURE:
			return new DenseVectorSignature(size);
		case SPARSE_VECTOR_SIGNATURE:
			return new SparseVectorSignature(size);
		default:
			return null;
		}
	}

	/**
	 * Adds the.
	 * 
	 * @param other
	 *            the other
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void add(VectorSignature other) throws SignatureException {
		for (int d = 0; d < getSize(); d++) {
			addTo(d, other.get(d));
		}
	}

	/**
	 * Adds the.
	 * 
	 * @param other
	 *            the other
	 * @param mult
	 *            the mult
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void add(VectorSignature other, double mult) throws SignatureException {
		for (int d = 0; d < getSize(); d++) {
			addTo(d, other.get(d) * mult);
		}
	}

	/**
	 * Adds the to.
	 * 
	 * @param idx
	 *            the idx
	 * @param val
	 *            the val
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void addTo(int idx, double val) throws SignatureException {
		set(idx, get(idx) + val);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract VectorSignature clone() throws CloneNotSupportedException;

	/**
	 * Gets the.
	 * 
	 * @param idx
	 *            the idx
	 * @return the double
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract double get(int idx) throws SignatureException;

	/**
	 * Gets the additional information.
	 * 
	 * @return the additional information
	 */
	public Object getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * Gets the non zero bins.
	 * 
	 * @return the non zero bins
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract int getNonZeroBins() throws SignatureException;

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public abstract int getSize();

	/**
	 * Multiply.
	 * 
	 * @param coef
	 *            the coef
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void multiply(double coef) throws SignatureException {
		for (int d = 0; d < getSize(); d++) {
			multiply(d, coef);
		}
	}

	/**
	 * Multiply.
	 * 
	 * @param idx
	 *            the idx
	 * @param coef
	 *            the coef
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void multiply(int idx, double coef) throws SignatureException {
		set(idx, get(idx) * coef);
	}

	/**
	 * Normalize sum to.
	 * 
	 * @param n
	 *            the n
	 * @param force
	 *            the force
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void normalizeSumTo(double n, boolean force) throws SignatureException {
		double sum = sum();

		if (sum != 0.0) {
			multiply(n / sum);
		} else if (force) {
			setAll(n / getSize());
		}
	}

	/**
	 * Normalize sum to one.
	 * 
	 * @param force
	 *            the force
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void normalizeSumToOne(boolean force) throws SignatureException {
		normalizeSumTo(1.0, force);
	}

	/**
	 * Sets the.
	 * 
	 * @param idx
	 *            the idx
	 * @param val
	 *            the val
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract void set(int idx, double val) throws SignatureException;

	/**
	 * Concat.
	 * 
	 * @param other
	 *            the other
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract void concat(VectorSignature other) throws SignatureException;

	/**
	 * Sets the additional information.
	 * 
	 * @param additionalInformation
	 *            the new additional information
	 */
	public void setAdditionalInformation(Object additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * Sets the all.
	 * 
	 * @param val
	 *            the new all
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void setAll(double val) throws SignatureException {
		for (int d = 0; d < getSize(); d++) {
			set(d, val);
		}
	}

	/**
	 * Sum.
	 * 
	 * @return the double
	 * @throws SignatureException
	 *             the signature exception
	 */
	public double sum() throws SignatureException {
		double sum = 0.0;

		for (int d = 0; d < getSize(); d++) {
			sum += get(d);
		}

		return sum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			String str = getClass().getSimpleName() + "(" + getSize() + " - " + df.format(sum()) + ")[";
			boolean first = true;
			for (int d = 0; d < getSize(); d++) {
				if (first) {
					first = false;
				} else {
					str += "  ";
				}
				str += df.format(get(d));
			}
			str += "]";
			return str;
		} catch (SignatureException e) {
			return "SignatureException : " + e.getMessage();
		}
	}

}
