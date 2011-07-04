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

import java.awt.geom.Rectangle2D;

import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.mask.Mask;


/**
 * The Interface SupportRegion.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface SupportRegion extends Iterable<Pixel> {
	
	/**
	 * Gets the center.
	 * 
	 * @return the center
	 */
	Pixel getCenter();
	
	/**
	 * Intersects.
	 * 
	 * @param mask
	 *            the mask
	 * @return true, if successful
	 * @throws SupportRegionException
	 *             the support region exception
	 */
	boolean intersects(Mask mask) throws SupportRegionException;
	
	/**
	 * Contains.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	boolean contains(double x, double y);
	
	/**
	 * Gets the bounding box.
	 * 
	 * @return the bounding box
	 */
	Rectangle2D getBoundingBox();
}
