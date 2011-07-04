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
package plugins.nherve.toolbox.image.feature.region;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import plugins.nherve.toolbox.image.feature.Segmentable;



/**
 * The Class FullImageSupportRegion.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class FullImageSupportRegion extends AreaSupportRegion {

	/**
	 * Instantiates a new full image support region.
	 * 
	 * @param obj
	 *            the obj
	 */
	public FullImageSupportRegion(Segmentable obj) {
		super(obj);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.region.AreaSupportRegion#initArea()
	 */
	@Override
	public void initArea() {
		area = new Area(new Rectangle2D.Float(0, 0, getOverallWidth(), getOverallHeight()));
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.region.DefaultSupportRegion#toString()
	 */
	@Override
	public String toString() {
		return "[Full]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Pixel> iterator() {
		if (area == null) {
			initArea();
		}
		ArrayList<Pixel> px = new ArrayList<Pixel>();
		for (int x = 0; x < getOverallWidth(); x++) {
			for (int y = 0; y < getOverallHeight(); y++) {
				px.add(new Pixel(x, y));
			}
		}
		return px.iterator();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#getCenter()
	 */
	@Override
	public Pixel getCenter() {
		if (area == null) {
			initArea();
		}
		return new Pixel(getOverallWidth() / 2, getOverallHeight() / 2);
	}
}
