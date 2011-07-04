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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.mask.Mask;


/**
 * The Class AreaSupportRegion.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class AreaSupportRegion extends DefaultSupportRegion {
	
	/** The area. */
	protected Area area;
	
	/**
	 * Instantiates a new area support region.
	 * 
	 * @param obj
	 *            the obj
	 */
	public AreaSupportRegion(Segmentable obj) {
		super(obj);
		area = null;
	}

	/**
	 * Inits the area.
	 */
	protected abstract void initArea();
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#intersects(plugins.nherve.toolbox.image.mask.Mask)
	 */
	public boolean intersects(Mask mask) throws SupportRegionException {
		return mask.intersects(area);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#contains(double, double)
	 */
	@Override
	public boolean contains(double x, double y) {
		return area.contains(x, y);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.region.DefaultSupportRegion#paint(java.awt.Graphics2D, java.awt.Color, java.awt.Color, float)
	 */
	@Override
	public void paint(Graphics2D g2, Color borderColor, Color fillColor, float opacity) {
		Color backupColor = g2.getColor();
		Composite backupComposite = g2.getComposite();
		
		if (fillColor != null) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
			g2.setColor(fillColor);
			g2.fill(area);
			g2.setComposite(backupComposite);
		}
		
		g2.setColor(borderColor);
		g2.draw(area);
		
		g2.setColor(backupColor);
	}

	/**
	 * Gets the area.
	 * 
	 * @return the area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * Gets the bounds.
	 * 
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return area.getBounds();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegion#getBoundingBox()
	 */
	@Override
	public Rectangle2D getBoundingBox() {
		return area.getBounds2D();
	}

}
