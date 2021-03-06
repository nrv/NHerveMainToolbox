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

import java.awt.Shape;
import java.util.Random;

import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;


/**
 * The Class RandomHistogram.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class RandomHistogram extends GlobalAndLocalDescriptor<SegmentableIcyBufferedImage, DefaultVectorSignature> {
	
	private boolean needToLoadSegmentable;
	
	/** The rdm. */
	private Random rdm;
	
	/** The sig size. */
	private int sigSize;

	/**
	 * Instantiates a new random histogram.
	 * 
	 * @param sigSize
	 *            the sig size
	 * @param display
	 *            the display
	 */
	public RandomHistogram(int sigSize, boolean display) {
		super(display);
		
		this.sigSize = sigSize;
		rdm = new Random(System.currentTimeMillis());
		setNeedToLoadSegmentable(false);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public DefaultVectorSignature extractLocalSignature(SegmentableIcyBufferedImage img, Shape shp) throws SignatureException {
		return randomSignature();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public DefaultVectorSignature extractLocalSignature(SegmentableIcyBufferedImage img, SupportRegion<IcyPixel> reg) throws SignatureException {
		return randomSignature();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return sigSize;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return needToLoadSegmentable;
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
	 * Random signature.
	 * 
	 * @return the vector signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	public DefaultVectorSignature randomSignature() throws SignatureException {
		DefaultVectorSignature s = getEmptySignature();
		for (int d = 0; d < getSignatureSize(); d++) {
			s.set(d, rdm.nextDouble());
		}
		s.normalizeSumToOne(true);
		return s;
	}

	public void setNeedToLoadSegmentable(boolean needToLoadSegmentable) {
		this.needToLoadSegmentable = needToLoadSegmentable;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "RandomHistogram";
	}

}
