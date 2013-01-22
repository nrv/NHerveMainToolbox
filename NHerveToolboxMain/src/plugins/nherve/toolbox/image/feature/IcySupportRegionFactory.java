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

import java.util.List;

import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.mask.Mask;


/**
 * The Interface SupportRegionFactory.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface IcySupportRegionFactory extends SupportRegionFactory<IcySupportRegion> {
	/**
	 * Extract regions.
	 * 
	 * @param img
	 *            the img
	 * @param mask
	 *            the mask
	 * @return the list
	 * @throws SupportRegionException
	 *             the support region exception
	 */
	public abstract List<IcySupportRegion> extractRegions(Segmentable img, Mask mask) throws SupportRegionException;

}