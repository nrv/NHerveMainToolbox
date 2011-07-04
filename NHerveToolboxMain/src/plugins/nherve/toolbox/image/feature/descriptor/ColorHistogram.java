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

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import plugins.nherve.toolbox.image.feature.SegmentableBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class ColorHistogram.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ColorHistogram extends ColorDescriptor<VectorSignature> {
	
	/** The dim. */
	private final int dim;
	
	/** The div. */
	private final int div;
	
	/** The div minus1. */
	private final int divMinus1;
	
	/**
	 * Instantiates a new color histogram.
	 * 
	 * @param div
	 *            the div
	 * @param display
	 *            the display
	 */
	public ColorHistogram(int div, boolean display) {
		super(display);
		this.div = div;
		dim = div * div * div;
		this.divMinus1 = div - 1;
	}
	
	/**
	 * Gets the index i.
	 * 
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param c
	 *            the c
	 * @return the index i
	 */
	private int getIndexI(int a, int b, int c) {
		return a + div * b + div * div * c;
	}
	
	/**
	 * Gets the index d.
	 * 
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param c
	 *            the c
	 * @return the index d
	 */
	private int getIndexD(double a, double b, double c) {
		return getIndexI((int)Math.min(a * div, divMinus1), (int)Math.min(b * div, divMinus1), (int)Math.min(c * div, divMinus1));
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public VectorSignature extractLocalSignature(SegmentableBufferedImage img, Shape shp) throws SignatureException {
		IcyBufferedImage bimg = img.getImage();
		
		int w = img.getWidth();
		int h = img.getHeight();
		int x, y, d;
		
		VectorSignature sig = getEmptySignature(dim);

		final Rectangle2D bb = shp.getBounds2D();

		for (x = (int) bb.getMinX(); x < (int) bb.getMaxX(); x++) {
			for (y = (int) bb.getMinY(); y < (int) bb.getMaxY(); y++) {
				if (shp.contains(x, y)) {
					if ((x >= 0) && (x < w) && (y >= 0) && (y < h)) {
						double[] col = getColorComponents_0_1(bimg, x, y);
						d = getIndexD(col[0], col[1], col[2]);
						try {
							sig.addTo(d, 1.0);
						} catch (ArrayIndexOutOfBoundsException e) {
							throw new SignatureException(e);
						}
					}
				}
			}
		}
		
		sig.normalizeSumToOne(true);
					
		return sig;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public VectorSignature extractLocalSignature(SegmentableBufferedImage img, SupportRegion reg) throws SignatureException {
		IcyBufferedImage bimg = img.getImage();
		
		int w = img.getWidth();
		int h = img.getHeight();
		int x, y, d;
		
		VectorSignature sig = getEmptySignature(dim);

		for (Pixel p : reg) {
			x = (int)p.x;
			y = (int)p.y;
			if ((x >= 0) && (x < w) && (y >= 0) && (y < h)) {
				double[] col = getColorComponents_0_1(bimg, x, y);
				d = getIndexD(col[0], col[1], col[2]);
				try {
					sig.addTo(d, 1.0);
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new SignatureException("ArrayIndexOutOfBoundsException " + d + "/" + dim + " ("+col[0]+", "+col[1]+", "+col[2]+")");
				}
			}
		}
		
		sig.normalizeSumToOne(true);
					
		return sig;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return dim;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(SegmentableBufferedImage img) throws SignatureException {
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(SegmentableBufferedImage img) throws SignatureException {
	}

}
