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
 * The Class StandardVocabulary.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class StandardVocabulary<T> extends Vocabulary<T> {
	
	/** The distance. */
	protected Distance<T> distance;

	/**
	 * Instantiates a new standard vocabulary.
	 */
	public StandardVocabulary() {
		super();
		
		distance = null;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.com.Vocabulary#computeDistance(int, int)
	 */
	@Override
	public double computeDistance(int o1, int o2) throws FeatureException {
		return distance.computeDistance(getObject(o1), getObject(o2));
	}

}

