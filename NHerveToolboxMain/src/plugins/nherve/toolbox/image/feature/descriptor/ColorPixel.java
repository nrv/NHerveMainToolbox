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
import java.util.ArrayList;

import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;


/**
 * The Class ColorPixel.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ColorPixel extends ColorDescriptor<Signature> {
	
	/** The Constant NO_NEIGHBOUR. */
	public final static int NO_NEIGHBOUR = 1;
	
	/** The Constant CROSS_4_NEIGHBOUR. */
	public final static int CROSS_4_NEIGHBOUR = 2;
	
	/** The Constant X_4_NEIGHBOUR. */
	public final static int X_4_NEIGHBOUR = 3;
	
	/** The Constant SQUARE_9_NEIGHBOUR. */
	public final static int SQUARE_9_NEIGHBOUR = 4;
	
	/** The Constant CROSS_4_NEIGHBOUR_ONLY. */
	public final static int CROSS_4_NEIGHBOUR_ONLY = 5;
	
	/** The Constant X_4_NEIGHBOUR_ONLY. */
	public final static int X_4_NEIGHBOUR_ONLY = 6;
	
	/** The Constant SQUARE_9_NEIGHBOUR_ONLY. */
	public final static int SQUARE_9_NEIGHBOUR_ONLY = 7;
	
	/** The Constant TYPES. */
	public final static String[] TYPES = { "UNKNOWN", "NO_NEIGHBOUR", "CROSS_4_NEIGHBOUR", "X_4_NEIGHBOUR", "SQUARE_9_NEIGHBOUR", "CROSS_4_NEIGHBOUR_ONLY", "X_4_NEIGHBOUR_ONLY", "SQUARE_9_NEIGHBOUR_ONLY" };

	/** The descriptor type. */
	private int descriptorType;
	
	/** The signature size. */
	private int signatureSize;
	
	/** The kernel. */
	private ArrayList<IcyPixel> kernel;
	
	/** The rotation invariance. */
	private boolean rotationInvariance;

	/**
	 * Instantiates a new color pixel.
	 * 
	 * @param display
	 *            the display
	 */
	public ColorPixel(boolean display) {
		this(ColorSpaceTools.RGB, display);
	}
	
	/**
	 * Instantiates a new color pixel.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param display
	 *            the display
	 */
	public ColorPixel(int colorSpace, boolean display) {
		this(colorSpace, NO_NEIGHBOUR, display);
	}
	
	/**
	 * Instantiates a new color pixel.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param type
	 *            the type
	 * @param display
	 *            the display
	 */
	public ColorPixel(int colorSpace, int type, boolean display) {
		this(colorSpace, type, false, display);
	}
	
	/**
	 * Instantiates a new color pixel.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param type
	 *            the type
	 * @param rotationInvariance
	 *            the rotation invariance
	 * @param display
	 *            the display
	 */
	public ColorPixel(int colorSpace, int type,  boolean rotationInvariance, boolean display) {
		super(display);
		setDescriptorType(type);
		setColorSpace(colorSpace);
		setRotationInvariance(rotationInvariance);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public Signature extractLocalSignature(SegmentableIcyBufferedImage img, Shape shp) throws SignatureException {
		throw new RuntimeException("ColorPixel.extractSignature(SegmentableBufferedImage img, Shape shp) not implemented");
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public Signature extractLocalSignature(SegmentableIcyBufferedImage img, SupportRegion<IcyPixel> reg) throws SignatureException {
		IcyBufferedImage bimg = img.getImage();

		IcyPixel px = reg.getCenter();
		int w = img.getWidth();
		int h = img.getHeight();

		DenseVectorSignature sig = new DenseVectorSignature(getSignatureSize());
		int d = 0;
		for (IcyPixel shift : kernel) {
			double[] col = getColorComponentsManageBorders(bimg, px.plus(shift), w, h);
			for (int c = 0; c < getNbColorChannels(); c++) {
				sig.set(d++, col[c]);
			}
		}
		
		if (isRotationInvariance() && (kernel.size() > 1)) {
			int d2 = 0;
			BagOfSignatures<VectorSignature> rotSigs = new BagOfSignatures<VectorSignature>();
			rotSigs.add(sig);
			for (int shift = 2; shift < kernel.size(); shift++) {
				VectorSignature sig2 = getEmptySignature();
				d2 = 0;
				d = 0;
				for (int c = 0; c < getNbColorChannels(); c++) {
					sig2.set(d2, sig.get(d));
					d2++;
					d++;
				}
				for (d = shift * getNbColorChannels(); d < sig.getSize(); d++) {
					sig2.set(d2, sig.get(d));
					d2++;
				}
				d = getNbColorChannels();
				for (; d2 < sig2.getSize(); d2++) {
					sig2.set(d2, sig.get(d));
					d++;
				}
				rotSigs.add(sig2);
			}
			return rotSigs;
		} else {
			return sig;
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return signatureSize;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(SegmentableIcyBufferedImage img) throws SignatureException {

	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(SegmentableIcyBufferedImage img) throws SignatureException {
	}

	/**
	 * Gets the descriptor type.
	 * 
	 * @return the descriptor type
	 */
	public int getDescriptorType() {
		return descriptorType;
	}

	/**
	 * Sets the descriptor type.
	 * 
	 * @param type
	 *            the new descriptor type
	 */
	public void setDescriptorType(int type) {
		this.descriptorType = type;
		kernel = new ArrayList<IcyPixel>();
		
		switch (type) {
		case NO_NEIGHBOUR:
		case CROSS_4_NEIGHBOUR:
		case X_4_NEIGHBOUR:
		case SQUARE_9_NEIGHBOUR:
			kernel.add(new IcyPixel(0, 0));
		}
		
		switch (type) {
		case CROSS_4_NEIGHBOUR:
		case CROSS_4_NEIGHBOUR_ONLY:
		case SQUARE_9_NEIGHBOUR:
		case SQUARE_9_NEIGHBOUR_ONLY:
			kernel.add(new IcyPixel(-1, 0));
			kernel.add(new IcyPixel(+1, 0));
			kernel.add(new IcyPixel(0, -1));
			kernel.add(new IcyPixel(0, +1));
		}
		
		switch (type) {
		case X_4_NEIGHBOUR:
		case X_4_NEIGHBOUR_ONLY:
		case SQUARE_9_NEIGHBOUR:
		case SQUARE_9_NEIGHBOUR_ONLY:
			kernel.add(new IcyPixel(-1, -1));
			kernel.add(new IcyPixel(-1, +1));
			kernel.add(new IcyPixel(+1, -1));
			kernel.add(new IcyPixel(+1, +1));
		}
		
		signatureSize = getNbColorChannels() * kernel.size();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.ColorDescriptor#toString()
	 */
	@Override
	public String toString() {
		return "ColorPixel;" + TYPES[getDescriptorType()] + ";" + (isRotationInvariance() ? "RI" : "SG") + ";" + ColorSpaceTools.COLOR_SPACES[getColorSpace()];
	}

	/**
	 * Checks if is rotation invariance.
	 * 
	 * @return true, if is rotation invariance
	 */
	public boolean isRotationInvariance() {
		return rotationInvariance;
	}

	/**
	 * Sets the rotation invariance.
	 * 
	 * @param rotationInvariance
	 *            the new rotation invariance
	 */
	public void setRotationInvariance(boolean rotationInvariance) {
		this.rotationInvariance = rotationInvariance;
	}

}
