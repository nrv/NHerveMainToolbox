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
import java.util.List;

import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.com.KernelFactory;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;


/**
 * The Class ColorPixelPair.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ColorPixelPair extends ColorDescriptor<Signature> {
	
	/** The kernel. */
	private List<IcyPixel> kernel;
	
	/** The Constant K1. */
	public static final int K1 = 1;
	
	/** The Constant K2. */
	public static final int K2 = 2;
	
	/** The Constant K3. */
	public static final int K3 = 3;
	
	/**
	 * Instantiates a new color pixel pair.
	 * 
	 * @param display
	 *            the display
	 */
	public ColorPixelPair(boolean display) {
		this(ColorSpaceTools.RGB, display);
	}
	
	/**
	 * Instantiates a new color pixel pair.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param display
	 *            the display
	 */
	public ColorPixelPair(int colorSpace, boolean display) {
		this(colorSpace, K1, display);
	}
	
	/**
	 * Instantiates a new color pixel pair.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param k
	 *            the k
	 * @param display
	 *            the display
	 */
	public ColorPixelPair(int colorSpace, int k, boolean display) {
		super(display);
		setColorSpace(colorSpace);
		setKernel(k);
	}
	
	/**
	 * Sets the kernel.
	 * 
	 * @param k
	 *            the new kernel
	 */
	public void setKernel(int k) {
		kernel = KernelFactory.getStandardKernel(k);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public Signature extractLocalSignature(SegmentableIcyBufferedImage img, Shape shp) throws SignatureException {
		throw new RuntimeException("ColorPixelPair.extractSignature(SegmentableBufferedImage img, Shape shp) not implemented");
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public Signature extractLocalSignature(SegmentableIcyBufferedImage img, SupportRegion<IcyPixel> reg) throws SignatureException {
		IcyBufferedImage bimg = img.getImage();

		BagOfSignatures<DefaultVectorSignature> sigs = new BagOfSignatures<DefaultVectorSignature>();

		IcyPixel px = reg.getCenter();
		int w = bimg.getWidth();
		int h = bimg.getHeight();

		double[] center = getColorComponentsManageBorders(bimg, px, w, h);

		for (IcyPixel shift : kernel) {
			int d = 0;
			double[] col = getColorComponentsManageBorders(bimg, px.plus(shift), w, h);
			DefaultVectorSignature sig = getEmptySignature();
			for (int c = 0; c < getNbColorChannels(); c++) {
				sig.set(d++, center[c]);
			}
			for (int c = 0; c < getNbColorChannels(); c++) {
				sig.set(d++, col[c]);
			}
			sigs.add(sig);
		}

		return sigs;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return getNbColorChannels() * 2;
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

}
