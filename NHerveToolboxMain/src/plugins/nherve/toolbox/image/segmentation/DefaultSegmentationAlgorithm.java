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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import plugins.nherve.toolbox.image.DifferentColorsMap;
import plugins.nherve.toolbox.image.feature.DefaultClusteringAlgorithmImpl;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.SegmentationAlgorithm;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.IcySupportRegionFactory;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl;
import plugins.nherve.toolbox.image.feature.descriptor.MultiThreadedSignatureExtractor;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;


/**
 * The Class DefaultSegmentationAlgorithm.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class DefaultSegmentationAlgorithm<T extends Segmentable> extends SegmentationAlgorithm<T> {
	
	/** The clustering. */
	private DefaultClusteringAlgorithmImpl<VectorSignature> clustering;
	
	/** The descriptor. */
	private DefaultDescriptorImpl<T, ? extends Signature> descriptor;
	
	/** The factory. */
	private IcySupportRegionFactory factory;

	/**
	 * Instantiates a new default segmentation algorithm.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param factory
	 *            the factory
	 * @param clustering
	 *            the clustering
	 */
	public DefaultSegmentationAlgorithm(DefaultDescriptorImpl<T, ? extends Signature> descriptor, IcySupportRegionFactory factory, DefaultClusteringAlgorithmImpl<VectorSignature> clustering) {
		super();
		this.descriptor = descriptor;
		this.clustering = clustering;
		this.factory = factory;
	}

	/**
	 * Instantiates a new default segmentation algorithm.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param clustering
	 *            the clustering
	 */
	public DefaultSegmentationAlgorithm(DefaultDescriptorImpl<T, ? extends Signature> descriptor, DefaultClusteringAlgorithmImpl<VectorSignature> clustering) {
		this(descriptor, null, clustering);
	}

	/**
	 * Instantiates a new default segmentation algorithm.
	 * 
	 * @param clustering
	 *            the clustering
	 */
	public DefaultSegmentationAlgorithm(DefaultClusteringAlgorithmImpl<VectorSignature> clustering) {
		this(null, null, clustering);
	}

	/**
	 * Creates the segmentation.
	 * 
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param c
	 *            the c
	 * @param regions
	 *            the regions
	 * @param affectation
	 *            the affectation
	 * @return the segmentation
	 * @throws MaskException
	 *             the mask exception
	 */
	private Segmentation createSegmentation(int w, int h, int c, IcySupportRegion[] regions, int[] affectation) throws MaskException {
		Segmentation seg = new Segmentation(w, h);
		DifferentColorsMap colorMap = new DifferentColorsMap(c, 1);

		for (int m = 0; m < c; m++) {
			seg.createNewMask("Segment " + m, true, colorMap.get(m), 1.0f);
		}

		int p = 0;
		for (IcySupportRegion sr : regions) {
			Mask mask = seg.getByIndex(affectation[p]);
			IcyPixel px = sr.getCenter();
			mask.getBinaryData().set((int)px.x, (int)px.y, true);
			p++;
		}

		return seg;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SegmentationAlgorithm#segment(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public Segmentation segment(T img) throws SegmentationException {
		if (factory == null) {
			throw new SegmentationException("DefaultSegmentationAlgorithm : SupportRegionFactory not initialized");
		}
		try {
			factory.setLogEnabled(isLogEnabled());

			List<IcySupportRegion> regions = factory.extractRegions(img);
			IcySupportRegion[] aRegions = (IcySupportRegion[])regions.toArray(new IcySupportRegion[regions.size()]);

			return segment(img, aRegions);

		} catch (SupportRegionException e) {
			throw new SegmentationException(e);
		}
	}

	/**
	 * Segment.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @return the segmentation
	 * @throws SegmentationException
	 *             the segmentation exception
	 */
	@SuppressWarnings("unchecked")
	public Segmentation segment(T img, IcySupportRegion[] regions) throws SegmentationException {
		if (descriptor == null) {
			throw new SegmentationException("DefaultSegmentationAlgorithm : Descriptor not initialized");
		}
		try {
			descriptor.setLogEnabled(isLogEnabled());

			MultiThreadedSignatureExtractor<T> mex = new MultiThreadedSignatureExtractor<T>(descriptor);
			mex.setLogEnabled(isLogEnabled());

			Signature[] sigs = mex.extractSignatures(img, regions);

			if (sigs.length > 0) {
				if (sigs[0] instanceof VectorSignature) {
					VectorSignature[] vs = new VectorSignature[sigs.length];
					for (int i = 0; i < sigs.length; i++) {
						vs[i] = (VectorSignature)sigs[i];
					}
					return segment(img, regions, vs);
				} else if (sigs[0] instanceof BagOfSignatures<?>) {
					BagOfSignatures<VectorSignature>[] bs = new BagOfSignatures[sigs.length];
					for (int i = 0; i < sigs.length; i++) {
						bs[i] = (BagOfSignatures<VectorSignature>)sigs[i];
					}
					return segmentBag(img, regions, bs);
				}
			}

			return null;
		} catch (SignatureException e) {
			throw new SegmentationException(e);
		}
	}

	/**
	 * Segment.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @param sigs
	 *            the sigs
	 * @return the segmentation
	 * @throws SegmentationException
	 *             the segmentation exception
	 */
	public Segmentation segment(T img, IcySupportRegion[] regions, VectorSignature[] sigs) throws SegmentationException {
		if (clustering == null) {
			throw new SegmentationException("DefaultSegmentationAlgorithm : ClusteringAlgorithm not initialized");
		}
		try {
			clustering.setLogEnabled(isLogEnabled());
			clustering.compute(sigs);

			List<VectorSignature> centroids = clustering.getCentroids();
			int[] affectation = clustering.getAffectations(sigs);

			return createSegmentation(img.getWidth(), img.getHeight(), centroids.size(), regions, affectation);
		} catch (ClusteringException e) {
			throw new SegmentationException(e);
		} catch (MaskException e) {
			throw new SegmentationException(e);
		}
	}
	
	/**
	 * Segment using previous quantization.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @param sigs
	 *            the sigs
	 * @return the segmentation
	 * @throws SegmentationException
	 *             the segmentation exception
	 */
	public Segmentation segmentUsingPreviousQuantization(T img, IcySupportRegion[] regions, VectorSignature[] sigs) throws SegmentationException {
		if (clustering == null) {
			throw new SegmentationException("DefaultSegmentationAlgorithm : ClusteringAlgorithm not initialized");
		}
		try {
			List<VectorSignature> centroids = clustering.getCentroids();
			int[] affectation = clustering.getAffectations(sigs);

			return createSegmentation(img.getWidth(), img.getHeight(), centroids.size(), regions, affectation);
		} catch (ClusteringException e) {
			throw new SegmentationException(e);
		} catch (MaskException e) {
			throw new SegmentationException(e);
		}
	}
	
	/**
	 * Segment bag.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @param sigs
	 *            the sigs
	 * @return the segmentation
	 * @throws SegmentationException
	 *             the segmentation exception
	 */
	public Segmentation segmentBag(T img, IcySupportRegion[] regions, BagOfSignatures<VectorSignature>[] sigs) throws SegmentationException {
		if (clustering == null) {
			throw new SegmentationException("DefaultSegmentationAlgorithm : ClusteringAlgorithm not initialized");
		}
		try {
			clustering.setLogEnabled(isLogEnabled());
			List<VectorSignature> lsigs = new ArrayList<VectorSignature>();
			for (int i = 0; i < sigs.length; i++) {
				for (VectorSignature vs : sigs[i]) {
					lsigs.add(vs);
				}
			}
			clustering.compute(lsigs);

			List<VectorSignature> centroids = clustering.getCentroids();
			int[] affectation = clustering.getAffectations(lsigs);

			Segmentation seg = new Segmentation(img.getWidth(), img.getHeight());
			DifferentColorsMap colorMap = new DifferentColorsMap(centroids.size(), 1);

			for (int m = 0; m < centroids.size(); m++) {
				seg.createNewMask("Segment " + m, true, colorMap.get(m), 1.0f);
			}

			int a = 0;
			for (int r = 0; r < sigs.length; r++) {
				HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
				for (VectorSignature vs : sigs[r]) {
					int aff = affectation[a++];
					if (count.containsKey(aff)) {
						count.put(aff, count.get(aff) + 1);
					} else {
						count.put(aff, 1);
					}
				}
				int max = 0;
				int affect = 0;
				for (int c : count.keySet()) {
					if (count.get(c) > max) {
						affect = c;
						max = count.get(c);
					}
				}
				Mask mask = seg.getByIndex(affect);
				IcyPixel px = regions[r].getCenter();
				mask.getBinaryData().set((int)px.x, (int)px.y, true);
			}

			return seg;
		} catch (ClusteringException e) {
			throw new SegmentationException(e);
		} catch (MaskException e) {
			throw new SegmentationException(e);
		}
	}
}
