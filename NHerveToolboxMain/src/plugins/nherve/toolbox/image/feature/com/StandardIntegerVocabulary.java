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

import plugins.nherve.toolbox.image.feature.Distance;
import plugins.nherve.toolbox.image.feature.FeatureException;

/**
 * The Class StandardIntegerVocabulary.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class StandardIntegerVocabulary extends StandardVocabulary<Integer> {

	/**
	 * Instantiates a new standard integer vocabulary.
	 */
	public StandardIntegerVocabulary() {
		super();
		distance = new Distance<Integer>() {
			@Override
			public double computeDistance(Integer s1, Integer s2) throws FeatureException {
				return Math.abs(s1 - s2);
			}
		};
	}

	/**
	 * Builds the standard vocabulary.
	 * 
	 * @param size
	 *            the size
	 * @return the standard integer vocabulary
	 * @throws FeatureException
	 *             the feature exception
	 */
	public static StandardIntegerVocabulary buildStandardVocabulary(int size) throws FeatureException {
		StandardIntegerVocabulary vocabulary = new StandardIntegerVocabulary();
		for (int d = 0; d < size; d++) {
			vocabulary.add(d);
		}

		return vocabulary;
	}
}
