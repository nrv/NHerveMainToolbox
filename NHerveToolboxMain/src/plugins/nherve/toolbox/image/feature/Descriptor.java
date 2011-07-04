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

import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

/**
 * The Interface Descriptor.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface Descriptor<T extends Segmentable> {
	
	/**
	 * Inits the for database.
	 * 
	 * @param db
	 *            the db
	 * @throws SignatureException
	 *             the signature exception
	 */
	void initForDatabase(ImageDatabase db) throws SignatureException;
	
	/**
	 * Need to load segmentable.
	 * 
	 * @return true, if successful
	 */
	boolean needToLoadSegmentable();
	
	/**
	 * Pre process.
	 * 
	 * @param img
	 *            the img
	 * @throws SignatureException
	 *             the signature exception
	 */
	void preProcess(T img) throws SignatureException;
	
	/**
	 * Post process.
	 * 
	 * @param img
	 *            the img
	 * @throws SignatureException
	 *             the signature exception
	 */
	void postProcess(T img) throws SignatureException;
}
