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

import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.segmentation.Segmentation;


//FIXME : A REVOIR POUR LE MULTITHREAD !!!
/**
 * The Class SegmentationLabelHistogram.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SegmentationLabelHistogram extends GlobalAndLocalDescriptor<Segmentation, VectorSignature> {
	
	/** The dim. */
	private int dim;
	
	/**
	 * Instantiates a new segmentation label histogram.
	 * 
	 * @param display
	 *            the display
	 */
	public SegmentationLabelHistogram(boolean display) {
		super(display);
		dim = 0;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public VectorSignature extractLocalSignature(Segmentation img, Shape shp) throws SignatureException {
		throw new RuntimeException("SegmentationLabelHistogram.extractSignature(Segmentation img, Shape shp) not implemented");
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public VectorSignature extractLocalSignature(Segmentation seg, SupportRegion<IcyPixel> reg) throws SignatureException {
		try {
			int w = seg.getWidth();
			int h = seg.getHeight();
			int x, y, l;
			
			if (dim == 0) {
				throw new SignatureException("SegmentationLabelHistogram : initialization problem, dim == 0");
			}
			
			VectorSignature sig = getEmptySignature(dim);
			
			for (IcyPixel p : reg) {
				x = (int)p.x;
				y = (int)p.y;
				if ((x >= 0) && (x < w) && (y >= 0) && (y < h)) {
					l = seg.getMaskId(x, y);
					sig.addTo(l, 1.0);
				}
			}
			
			sig.normalizeSumToOne(true);
						
			return sig;
		} catch (MaskException e) {
			throw new SignatureException(e);
		}
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
	public void postProcess(Segmentation seg) throws SignatureException {
		dim = 0;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(Segmentation seg) throws SignatureException {
		dim = seg.size();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "SegmentationLabelHistogram";
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return true;
	}

}
