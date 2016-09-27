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
package plugins.nherve.toolbox.image.segmentation;

import icy.image.IcyBufferedImage;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.DefaultClusteringAlgorithmImpl;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.descriptor.MultiThreadedSignatureExtractor;
import plugins.nherve.toolbox.image.feature.descriptor.SegmentationLabelHistogram;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignatureConcatenator;

/**
 * The Class SegmentationFusionAlgorithm.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SegmentationFusionAlgorithm extends Algorithm {
	
	/** The clustering algorithm. */
	private DefaultClusteringAlgorithmImpl clusteringAlgorithm;

	/**
	 * Instantiates a new segmentation fusion algorithm.
	 */
	public SegmentationFusionAlgorithm() {
		super();
		clusteringAlgorithm = null;
		setLogEnabled(false);
	}

	/**
	 * Gets the clustering algorithm.
	 * 
	 * @return the clustering algorithm
	 */
	public DefaultClusteringAlgorithmImpl getClusteringAlgorithm() {
		return clusteringAlgorithm;
	}

	/**
	 * Segment.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @param segs
	 *            the segs
	 * @return the segmentation
	 * @throws SegmentationException
	 *             the segmentation exception
	 */
	public Segmentation segment(IcyBufferedImage img, IcySupportRegion[] regions, Segmentation[] segs) throws SegmentationException {
		try {
			SegmentationLabelHistogram slh = new SegmentationLabelHistogram(true);
			
			MultiThreadedSignatureExtractor<Segmentation> mex = new MultiThreadedSignatureExtractor<Segmentation>(slh);
			mex.setLogEnabled(isLogEnabled());
			DefaultVectorSignature[][] sigs = new DefaultVectorSignature[segs.length][regions.length];
			
			for (int s = 0; s < segs.length; s++) {
				Signature[] segSigs = mex.extractSignatures(segs[s], regions);
				for (int r = 0; r < regions.length; r++) {
					sigs[s][r] = (DefaultVectorSignature) segSigs[r];
				}
			}
			
			VectorSignatureConcatenator concat = new VectorSignatureConcatenator(DefaultVectorSignature.DENSE_VECTOR_SIGNATURE, true);
			for (int s = 0; s < segs.length; s++) {
				concat.add(sigs[s]);
			}
			DefaultVectorSignature[] csigs = concat.concatenate();
			sigs = null;
			
			DefaultSegmentationAlgorithm<SegmentableIcyBufferedImage> algo = new DefaultSegmentationAlgorithm<SegmentableIcyBufferedImage>(getClusteringAlgorithm());
			algo.setLogEnabled(isLogEnabled());
			return algo.segment(new SegmentableIcyBufferedImage(img), regions, csigs);
		} catch (SignatureException e) {
			throw new SegmentationException(e);
		}
	}

	/**
	 * Sets the clustering algorithm.
	 * 
	 * @param clusteringAlgorithm
	 *            the new clustering algorithm
	 */
	public void setClusteringAlgorithm(DefaultClusteringAlgorithmImpl clusteringAlgorithm) {
		this.clusteringAlgorithm = clusteringAlgorithm;
	}
}
