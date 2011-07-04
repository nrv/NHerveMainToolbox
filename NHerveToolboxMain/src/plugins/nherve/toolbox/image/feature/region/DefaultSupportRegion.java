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

import java.awt.Color;
import java.awt.Graphics2D;

import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.SupportRegion;


/**
 * The Class DefaultSupportRegion.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class DefaultSupportRegion implements SupportRegion {
	
	/** The obj. */
	private final Segmentable obj;
	
	/**
	 * Instantiates a new default support region.
	 * 
	 * @param obj
	 *            the obj
	 */
	public DefaultSupportRegion(Segmentable obj) {
		super();
		this.obj = obj;
	}
	

	/**
	 * Paint.
	 * 
	 * @param g2
	 *            the g2
	 * @param borderColor
	 *            the border color
	 * @param fillColor
	 *            the fill color
	 * @param opacity
	 *            the opacity
	 */
	public abstract void paint(Graphics2D g2, Color borderColor, Color fillColor, float opacity);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	/**
	 * Gets the overall width.
	 * 
	 * @return the overall width
	 */
	public int getOverallWidth() {
		return obj.getWidth();
	}

	/**
	 * Gets the overall height.
	 * 
	 * @return the overall height
	 */
	public int getOverallHeight() {
		return obj.getHeight();
	}
}
