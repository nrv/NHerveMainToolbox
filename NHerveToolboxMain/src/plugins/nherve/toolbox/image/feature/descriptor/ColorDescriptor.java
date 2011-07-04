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
package plugins.nherve.toolbox.image.feature.descriptor;

import icy.image.IcyBufferedImage;
import plugins.nherve.toolbox.image.feature.SegmentableBufferedImage;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;

/**
 * The Class ColorDescriptor.
 * 
 * @param <S>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class ColorDescriptor<S extends Signature> extends GlobalAndLocalDescriptor<SegmentableBufferedImage, S> {
	
	/** The color space. */
	private int colorSpace;
	
	/** The use bounds. */
	private boolean useBounds;
	
	/**
	 * Instantiates a new color descriptor.
	 * 
	 * @param display
	 *            the display
	 */
	public ColorDescriptor(boolean display) {
		super(display);
		setColorSpace(ColorSpaceTools.RGB);
		setUseBounds(false);
	}

	/**
	 * Gets the color space.
	 * 
	 * @return the color space
	 */
	public int getColorSpace() {
		return colorSpace;
	}

	/**
	 * Sets the color space.
	 * 
	 * @param colorSpace
	 *            the new color space
	 */
	public void setColorSpace(int colorSpace) {
		this.colorSpace = colorSpace;
	}
	
	/**
	 * Gets the color components_0_1.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color components_0_1
	 * @throws SignatureException
	 *             the signature exception
	 */
	public double[] getColorComponents_0_1(IcyBufferedImage icyb, int x, int y) throws SignatureException {
		if (useBounds) {
			return ColorSpaceTools.getBoundedColorComponentsD_0_1(icyb, getColorSpace(), x, y);
		}
		return ColorSpaceTools.getColorComponentsD_0_1(icyb, getColorSpace(), x, y);
	}
	
	/**
	 * Gets the nb color channels.
	 * 
	 * @return the nb color channels
	 */
	public int getNbColorChannels() {
		return ColorSpaceTools.NB_COLOR_CHANNELS;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "ColorDescriptor " + ColorSpaceTools.COLOR_SPACES[getColorSpace()];
	}

	/**
	 * Gets the color components manage borders.
	 * 
	 * @param img
	 *            the img
	 * @param npx
	 *            the npx
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the color components manage borders
	 * @throws SignatureException
	 *             the signature exception
	 */
	public double[] getColorComponentsManageBorders(IcyBufferedImage img, Pixel npx, int w, int h) throws SignatureException {
		double[] col = null;
		int x = (int)npx.x;
		int y = (int)npx.y;
	
		if (x < 0) {
			x = Math.abs(x);
		} else if (x >= w) {
			x -= 2 * (x - w + 1); 
		}
		
		if (y < 0) {
			y = Math.abs(y);
		} else if (y >= h) {
			y -= 2 * (y - h + 1); 
		}
	
		col = getColorComponents_0_1(img, x, y);
	
		return col;
	}

	/**
	 * Checks if is use bounds.
	 * 
	 * @return true, if is use bounds
	 */
	public boolean isUseBounds() {
		return useBounds;
	}

	/**
	 * Sets the use bounds.
	 * 
	 * @param useBounds
	 *            the new use bounds
	 */
	public void setUseBounds(boolean useBounds) {
		this.useBounds = useBounds;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return true;
	}
}
