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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class VectorSignatureConcatenator.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class VectorSignatureConcatenator {
	
	/** The all. */
	private List<VectorSignature[]> all;
	
	/** The coef. */
	private List<Double> coef;
	
	/** The type. */
	private int type;
	
	/** The size. */
	private int size;
	
	/** The dim. */
	private int dim;
	
	/** The normalization. */
	private boolean normalization;

	/**
	 * Instantiates a new vector signature concatenator.
	 * 
	 * @param type
	 *            the type
	 * @param normalization
	 *            the normalization
	 */
	public VectorSignatureConcatenator(int type, boolean normalization) {
		super();
		this.all = new ArrayList<VectorSignature[]>();
		this.coef = new ArrayList<Double>();
		this.size = 0;
		this.dim = 0;
		this.normalization = normalization;
		this.type = type;
	}
	
	/**
	 * Adds the.
	 * 
	 * @param vsa
	 *            the vsa
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void add(VectorSignature[] vsa) throws SignatureException {
		add(vsa, 1.0);
	}
	
	/**
	 * Adds the.
	 * 
	 * @param vsa
	 *            the vsa
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void add(VectorSignature vsa) throws SignatureException {
		add(new VectorSignature[]{vsa}, 1.0);
	}

	/**
	 * Adds the.
	 * 
	 * @param vsa
	 *            the vsa
	 * @param c
	 *            the c
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void add(VectorSignature[] vsa, double c) throws SignatureException {
		if (size == 0) {
			size = vsa.length;
		} else if (vsa.length == 0) {
			throw new SignatureException("Empty VectorSignature array");
		} else if (size != vsa.length) {
			throw new SignatureException("Array sizes mismatch (" + size + " != " + vsa.length + ")");
		}
		dim += vsa[0].getSize();
		all.add(vsa);
		coef.add(c);
	}

	/**
	 * Concatenate.
	 * 
	 * @return the vector signature[]
	 * @throws SignatureException
	 *             the signature exception
	 */
	public VectorSignature[] concatenate() throws SignatureException {
		if ((all.size() == 0) || (size == 0)) {
			throw new SignatureException("No vector to concatenate");
		}
		VectorSignature[] result = new VectorSignature[size];
		for (int s = 0; s < size; s++) {
			VectorSignature sig = VectorSignature.getEmptySignature(type, dim);
			int c = 0;
			int d = 0;
			for (VectorSignature[] vsa : all) {
				VectorSignature vs = vsa[s];
				for (int ld = 0; ld < vs.getSize(); ld++) {
					sig.set(d, vs.get(ld) * coef.get(c));
					d++;
				}
				c++;
			}
			if (normalization) {
				sig.normalizeSumToOne(true);
			}
			result[s] = sig;
		}
		
		return result;
	}

	/**
	 * Gets the dim.
	 * 
	 * @return the dim
	 */
	public int getDim() {
		return dim;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
}
