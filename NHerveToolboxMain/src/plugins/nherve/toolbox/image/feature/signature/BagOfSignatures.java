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
import java.util.Iterator;
import java.util.List;

import plugins.nherve.toolbox.image.feature.Signature;


/**
 * The Class BagOfSignatures.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class BagOfSignatures<T extends Signature> implements Signature, Iterable<T> {
	
	/** The signatures. */
	private List<T> signatures;

	/**
	 * Instantiates a new bag of signatures.
	 */
	public BagOfSignatures() {
		super();
		signatures = new ArrayList<T>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BagOfSignatures<T> clone() throws CloneNotSupportedException {
		BagOfSignatures<T> c = new BagOfSignatures<T>();
		for (T s : this) {
			c.add((T)s.clone());
		}
		return c;
	}

	/**
	 * Adds the.
	 * 
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	public boolean add(T e) {
		return signatures.add(e);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return signatures.iterator();
	}

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size() {
		return signatures.size();
	}

	/**
	 * Gets the signatures.
	 * 
	 * @return the signatures
	 */
	public List<T> getSignatures() {
		return signatures;
	}

}
