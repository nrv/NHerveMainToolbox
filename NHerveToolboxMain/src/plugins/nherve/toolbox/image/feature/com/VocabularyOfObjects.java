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
import java.util.List;
import java.util.Map;

import plugins.nherve.toolbox.image.feature.Distance;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;


/**
 * The Class VocabularyOfObjects.
 * 
 * @param <T>
 *            the generic type
 * @param <D>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class VocabularyOfObjects<T, D> extends Vocabulary<T> {
	
	/** The theoretical max distance. */
	private double theoreticalMaxDistance;
	
	/** The distance. */
	private Distance<D> distance;
	
	/** The distance objects. */
	private Map<Integer, D> distanceObjects;
	
	/**
	 * Instantiates a new vocabulary of objects.
	 */
	public VocabularyOfObjects() {
		super();
		distanceObjects = new HashMap<Integer, D>();
		theoreticalMaxDistance = 2;
	}
	
	/**
	 * Adds the.
	 * 
	 * @param t
	 *            the t
	 * @param d
	 *            the d
	 * @throws FeatureException
	 *             the feature exception
	 */
	public void add(T t, D d) throws FeatureException {
		int id = super.add(t);
		distanceObjects.put(id, d);
	}
	
	/**
	 * Gets the distance object.
	 * 
	 * @param index
	 *            the index
	 * @return the distance object
	 */
	public D getDistanceObject(int index) {
		return distanceObjects.get(index);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.com.Vocabulary#add(java.lang.Object)
	 */
	@Override
	public int add(T t) throws FeatureException {
		throw new FeatureException("Unable to add an object without it's associated distanceObject, use add(T t, D d) instead");
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.com.Vocabulary#computeDistance(int, int)
	 */
	@Override
	public double computeDistance(int o1, int o2) throws FeatureException {
		return distance.computeDistance(getDistanceObject(o1), getDistanceObject(o2));
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
	public double computeDistance(int o1, D o2) throws FeatureException {
		return distance.computeDistance(getDistanceObject(o1), o2);
	}
	
	/**
	 * Similarity.
	 * 
	 * @param o1
	 *            the o1
	 * @param o2
	 *            the o2
	 * @return the double
	 * @throws FeatureException
	 *             the feature exception
	 */
	public double similarity(int o1, D o2) throws FeatureException {
		double d = computeDistance(o1, o2) / theoreticalMaxDistance;
		return Math.exp(-d*d);
	}
	
	/**
	 * Builds the.
	 * 
	 * @param dobjs
	 *            the dobjs
	 * @param dst
	 *            the dst
	 * @param theoreticalMaxDistance
	 *            the theoretical max distance
	 * @return the vocabulary of objects
	 * @throws FeatureException
	 *             the feature exception
	 */
	public static VocabularyOfObjects<Integer, DefaultVectorSignature> build(List<DefaultVectorSignature> dobjs, Distance<DefaultVectorSignature> dst, double theoreticalMaxDistance) throws FeatureException {
		try {
			VocabularyOfObjects<Integer, DefaultVectorSignature> voc = new VocabularyOfObjects<Integer, DefaultVectorSignature>();
			for (int i = 0; i < dobjs.size(); i++) {
				voc.add(i, dobjs.get(i).clone());
			}
			
			voc.distance = dst;
			voc.theoreticalMaxDistance = theoreticalMaxDistance;
			
			return voc;
		} catch (CloneNotSupportedException e) {
			throw new FeatureException(e);
		}
	}

	
}
