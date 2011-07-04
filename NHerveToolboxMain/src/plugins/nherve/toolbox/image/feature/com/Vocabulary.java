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

import java.util.HashMap;
import java.util.Map;

import plugins.nherve.toolbox.image.feature.FeatureException;


/**
 * The Class Vocabulary.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class Vocabulary<T> {
	
	/** The size. */
	private int size;
	
	/** The indexes. */
	private Map<T, Integer> indexes;
	
	/** The objects. */
	private Map<Integer, T> objects;
	
	/**
	 * Instantiates a new vocabulary.
	 */
	public Vocabulary() {
		super();
		
		size = 0;
		indexes = new HashMap<T, Integer>();
		objects = new HashMap<Integer, T>();
	}
	
	/**
	 * Compute distance.
	 * 
	 * @param o1
	 *            the o1
	 * @param o2
	 *            the o2
	 * @return the double
	 * @throws FeatureException
	 *             the feature exception
	 */
	public abstract double computeDistance(int o1, int o2) throws FeatureException;

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size() {
		return size;
	}

	/**
	 * Contains.
	 * 
	 * @param t
	 *            the t
	 * @return true, if successful
	 */
	public boolean contains(T t) {
		return indexes.containsKey(t);
	}

	/**
	 * Adds the.
	 * 
	 * @param t
	 *            the t
	 * @return the int
	 * @throws FeatureException
	 *             the feature exception
	 */
	public int add(T t) throws FeatureException {
		if (contains(t)) {
			throw new FeatureException("Object '" + t.toString() + "' is already in this vocabulary");
		}
		
		int id = size;
		indexes.put(t, id);
		objects.put(id, t);
		size++;
		
		return id;
	}

	/**
	 * Gets the index.
	 * 
	 * @param object
	 *            the object
	 * @return the index
	 */
	public int getIndex(T object) {
		return indexes.get(object);
	}
	
	/**
	 * Gets the object.
	 * 
	 * @param index
	 *            the index
	 * @return the object
	 */
	public T getObject(int index) {
		return objects.get(index);
	}
}
