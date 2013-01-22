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
package plugins.nherve.toolbox.image.feature.fuzzy;

import icy.image.IcyBufferedImage;
import icy.system.CPUMonitor;
import icy.type.TypeUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.region.GridFactory;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.mask.MaskStack;


/**
 * The Class FuzzyClusteringProcessorDefaultImpl.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class FuzzyClusteringProcessorDefaultImpl extends Algorithm implements FuzzyClusteringProcessor {
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#addToMaskStack(plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm, icy.image.IcyBufferedImage, plugins.nherve.toolbox.image.mask.MaskStack, plugins.nherve.toolbox.image.feature.SupportRegion[], plugins.nherve.toolbox.image.feature.signature.VectorSignature[])
	 */
	@Override
	public void addToMaskStack(FuzzyClusteringAlgorithm sm, IcyBufferedImage img, MaskStack seg, IcySupportRegion[] regions, VectorSignature[] sigs) throws MaskException, ClusteringException {
		int offset = seg.size();

		for (int i = 0; i < sm.getNbClasses(); i++) {
			seg.createNewMask("Segment " + i, true, Color.WHITE, 1.0f);
		}

		int[] aff = sm.getAffectations(sigs);
		int i = 0;
		for (IcySupportRegion sr : regions) {
			Mask m = seg.getByIndex(aff[i] + offset);
			BinaryIcyBufferedImage bin = m.getBinaryData();
			IcyPixel px = sr.getCenter();
			bin.set((int) px.x, (int) px.y, true);
			i++;
		}
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#getRegions(plugins.nherve.toolbox.image.feature.SegmentableBufferedImage)
	 */
	@Override
	public IcySupportRegion[] getRegions(SegmentableIcyBufferedImage simg) throws SupportRegionException {
		GridFactory factory = new GridFactory(GridFactory.ALGO_ONLY_PIXELS);
		List<IcySupportRegion> lRegions = factory.extractRegions(simg);
		IcySupportRegion[] regions = new IcySupportRegion[lRegions.size()];
		int r = 0;
		for (IcySupportRegion sr : lRegions) {
			regions[r++] = sr;
		}

		return regions;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#getAsImage(double[], plugins.nherve.toolbox.image.feature.SupportRegion[], int, int)
	 */
	@Override
	public IcyBufferedImage getAsImage(double[] data, IcySupportRegion[] regions, int w, int h) {
		IcyBufferedImage segImg = new IcyBufferedImage(w, h, 1, TypeUtil.TYPE_DOUBLE);
		int i = 0;

		CPUMonitor monitor = new CPUMonitor();
		monitor.start();

		double[] id = segImg.getDataXYAsDouble(0);
		for (IcySupportRegion sr : regions) {
			IcyPixel px = sr.getCenter();
			id[(int) px.x + (int) px.y * w] = data[i];
			i++;
		}

		monitor.stop();

		log("getAsImage CPU usage : " + monitor.getUserElapsedTimeMilli() + " ms");

		return segImg;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#createFuzzyClusteringAlgorithm(icy.image.IcyBufferedImage)
	 */
	@Override
	public FuzzyClusteringAlgorithm createFuzzyClusteringAlgorithm(IcyBufferedImage img)  throws ClusteringException {
		ArrayList<IcyBufferedImage> imgs = new ArrayList<IcyBufferedImage>();
		imgs.add(img);
		return createFuzzyClusteringAlgorithm(imgs);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#doClustering(icy.image.IcyBufferedImage, plugins.nherve.toolbox.image.mask.MaskStack, plugins.nherve.toolbox.image.feature.SupportRegion[], plugins.nherve.toolbox.image.feature.signature.VectorSignature[])
	 */
	@Override
	public FuzzyClusteringAlgorithm doClustering(IcyBufferedImage img, MaskStack seg, IcySupportRegion[] regions, VectorSignature[] sigs) throws SupportRegionException, SignatureException, MaskException, ClusteringException {
		FuzzyClusteringAlgorithm sm = createFuzzyClusteringAlgorithm(img);

		addToMaskStack(sm, img, seg, regions, sigs);

		return sm;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#doClustering(icy.image.IcyBufferedImage, plugins.nherve.toolbox.image.mask.MaskStack)
	 */
	@Override
	public FuzzyClusteringAlgorithm doClustering(IcyBufferedImage img, MaskStack seg) {
		try {
			SegmentableIcyBufferedImage simg = new SegmentableIcyBufferedImage(img);
			IcySupportRegion[] regions = getRegions(simg);
			VectorSignature[] sigs = getSignatures(simg, regions);

			return doClustering(img, seg, regions, sigs);
		} catch (Exception e) {
			e.printStackTrace();
			logError(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}
	}
}
