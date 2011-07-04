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

import icy.image.IcyBufferedImage;

/**
 * The Class SegmentableBufferedImage.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SegmentableBufferedImage implements Segmentable {
	
	/** The image. */
	private final IcyBufferedImage image;
	
	/** The name. */
	private String name;
	
	/**
	 * Instantiates a new segmentable buffered image.
	 * 
	 * @param image
	 *            the image
	 */
	public SegmentableBufferedImage(IcyBufferedImage image) {
		super();
		
		this.image = image;
		this.name = null;
	}

	/**
	 * Gets the image.
	 * 
	 * @return the image
	 */
	public IcyBufferedImage getImage() {
		return image;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Segmentable#getHeight()
	 */
	public int getHeight() {
		return image.getHeight();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Segmentable#getWidth()
	 */
	public int getWidth() {
		return image.getWidth();
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
