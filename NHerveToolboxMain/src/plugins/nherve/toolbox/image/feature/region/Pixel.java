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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Point2d;

import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.mask.Mask;


/**
 * The Class Pixel.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class Pixel extends Point2d implements SupportRegion {
	private static final long serialVersionUID = -8821643927878896700L;

	/**
	 * Instantiates a new pixel.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public Pixel(double x, double y) {
		super(x, y);
	}
	
	/**
	 * Plus.
	 * 
	 * @param other
	 *            the other
	 * @return the pixel
	 */
	public Pixel plus(Pixel other) {
		return new Pixel(this.x + other.x, this.y + other.y);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#getCenter()
	 */
	@Override
	public Pixel getCenter() {
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Pixel> iterator() {
		ArrayList<Pixel> px = new ArrayList<Pixel>();
		px.add(this);
		return px.iterator();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#intersects(plugins.nherve.toolbox.image.mask.Mask)
	 */
	@Override
	public boolean intersects(Mask mask) throws SupportRegionException {
		return mask.contains(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "("+x+", "+y+")";
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#contains(double, double)
	 */
	@Override
	public boolean contains(double x, double y) {
		return (this.x == x) && (this.y == y);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#getBoundingBox()
	 */
	@Override
	public Rectangle2D getBoundingBox() {
		return new Rectangle2D.Double(x, y, x, y);
	}
	
}
