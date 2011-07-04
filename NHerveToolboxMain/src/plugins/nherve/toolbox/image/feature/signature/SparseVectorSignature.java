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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class SparseVectorSignature.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SparseVectorSignature extends VectorSignature  {
	
	/** The size. */
	private int size;
	
	/** The data. */
	private Map<Integer, Double> data;

	/**
	 * Instantiates a new sparse vector signature.
	 * 
	 * @param size
	 *            the size
	 */
	public SparseVectorSignature(int size) {
		super();
		this.size = size;
		data = new TreeMap<Integer, Double>();
	}
	
//	public SparseVectorSignature() {
//		this(0);
//	}

	/* (non-Javadoc)
 * @see plugins.nherve.toolbox.image.feature.signature.VectorSignature#clone()
 */
@Override
	public SparseVectorSignature clone() throws CloneNotSupportedException {
		SparseVectorSignature ns = new SparseVectorSignature(getSize());
		for (int d = 0; d < getSize(); d++) {
			try {
				ns.set(d, get(d));
			} catch (SignatureException e) {
				throw new CloneNotSupportedException("SignatureException : " + e.getMessage());
			}
		}
		return ns;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.signature.VectorSignature#concat(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	public void concat(VectorSignature other) throws SignatureException {
		int newSize = size + other.getSize();
		Map<Integer, Double> newData = new TreeMap<Integer, Double>();

		int d = 0;
		for (int i = 0; i < size; i++) {
			if (data.containsKey(i)) {
				newData.put(d, data.get(i));
			}
			d++;
		}
		for (int i = 0; i < other.getSize(); i++) {
			if (other.get(i)!= 0) {
				newData.put(d, other.get(i));
			}
			d++;
		}

		size = newSize;
		data = newData;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.signature.VectorSignature#get(int)
	 */
	@Override
	public double get(int idx) throws SignatureException {
		return data.containsKey(idx) ? data.get(idx) : 0;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.signature.VectorSignature#getNonZeroBins()
	 */
	@Override
	public int getNonZeroBins() throws SignatureException {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.signature.VectorSignature#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.signature.VectorSignature#set(int, double)
	 */
	@Override
	public void set(int idx, double val) throws SignatureException {
		if ((idx < 0) || (idx >= size)) {
			throw new SignatureException("Invalid signature index (" + idx + ")");
		}

		if (val == 0) {
			data.remove(idx);
		} else {
			data.put(idx, val);
		}
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public Map<Integer, Double> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Integer> iterator() {
		return data.keySet().iterator();
	}

}
