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

import java.util.Arrays;

import plugins.nherve.toolbox.image.feature.Signature;



/**
 * The Class IndexSignature.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class IndexSignature implements Signature {
	
	/** The size. */
	private int size;
	
	/** The data. */
	private int[] data;
	
	/**
	 * Instantiates a new index signature.
	 * 
	 * @param size
	 *            the size
	 * @param initialValue
	 *            the initial value
	 */
	public IndexSignature(int size, int initialValue) {
		super();
		this.size = size;
		data = new int[size];
		Arrays.fill(data, initialValue);
	}
	
	/**
	 * Instantiates a new index signature.
	 * 
	 * @param size
	 *            the size
	 */
	public IndexSignature(int size) {
		this(size, 0);
	}
	
	/**
	 * Instantiates a new index signature.
	 */
	public IndexSignature() {
		super();
		size = 0;
		data = null;
	}
	
	/**
	 * Gets the.
	 * 
	 * @param idx
	 *            the idx
	 * @return the int
	 * @throws SignatureException
	 *             the signature exception
	 */
	public int get(int idx) throws SignatureException {
		return data[idx];
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
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
	public void set(int idx, int val) throws SignatureException {
		data[idx] = val;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IndexSignature clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Not yet needed");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String k = "";
		
		if (size > 0) {
			k += data[0];
			for (int i = 1; i < size; i++) {
				k += "-" + data[i];
			}
		}
		return k;
	}
}
