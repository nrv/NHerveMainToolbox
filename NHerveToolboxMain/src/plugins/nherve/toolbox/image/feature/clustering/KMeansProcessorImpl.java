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
package plugins.nherve.toolbox.image.feature.clustering;

import icy.image.IcyBufferedImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plugins.nherve.toolbox.image.feature.SegmentableBufferedImage;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.descriptor.ColorPixel;
import plugins.nherve.toolbox.image.feature.descriptor.MultiThreadedSignatureExtractor;
import plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm;
import plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessorDefaultImpl;
import plugins.nherve.toolbox.image.feature.fuzzy.PseudoFuzzyKMeans;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;


/**
 * The Class KMeansProcessorImpl.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class KMeansProcessorImpl extends FuzzyClusteringProcessorDefaultImpl implements KMeansProcessor {
	
	/**
	 * Instantiates a new k means processor impl.
	 * 
	 * @param nbClasses
	 *            the nb classes
	 * @param nbMaxIterations
	 *            the nb max iterations
	 * @param stabilizationCriterion
	 *            the stabilization criterion
	 */
	public KMeansProcessorImpl(int nbClasses, int nbMaxIterations, double stabilizationCriterion) {
		super();
		this.nbClasses = nbClasses;
		this.nbMaxIterations = nbMaxIterations;
		this.stabilizationCriterion = stabilizationCriterion;
	}

	/** The nb classes. */
	private int nbClasses;
	
	/** The nb max iterations. */
	private int nbMaxIterations;
	
	/** The stabilization criterion. */
	private double stabilizationCriterion;

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#createFuzzyClusteringAlgorithm(java.util.List)
	 */
	@Override
	public FuzzyClusteringAlgorithm createFuzzyClusteringAlgorithm(List<IcyBufferedImage> imgs)  throws ClusteringException {
		try {
			PseudoFuzzyKMeans pfkm = new PseudoFuzzyKMeans(nbClasses, nbMaxIterations, stabilizationCriterion);
			pfkm.setLogEnabled(true);
			
			List<VectorSignature> allSigs = new ArrayList<VectorSignature>();
			int i = 0;
			for (IcyBufferedImage img : imgs) {
				i++;
				//System.out.println(" ..*.. image " + i + " / " + imgs.size() + " - " + img.getWidth() + "x" + img.getHeight());
				SegmentableBufferedImage simg = new SegmentableBufferedImage(img);
				SupportRegion[] regions = getRegions(simg);
				VectorSignature[] sigs = getSignatures(simg, regions);
				allSigs.addAll(Arrays.asList(sigs));
			}
			
			//System.out.println(" ..*.. done");
			
			pfkm.compute(allSigs);
			
			return pfkm;
		} catch (SupportRegionException e) {
			throw new ClusteringException(e);
		} catch (SignatureException e) {
			throw new ClusteringException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor#getSignatures(plugins.nherve.toolbox.image.feature.SegmentableBufferedImage, plugins.nherve.toolbox.image.feature.SupportRegion[])
	 */
	@Override
	public VectorSignature[] getSignatures(SegmentableBufferedImage simg, SupportRegion[] regions) throws SignatureException {
		ColorPixel col2 = new ColorPixel(ColorSpaceTools.RGB_TO_I1H2H3, isLogEnabled());
		MultiThreadedSignatureExtractor<SegmentableBufferedImage> mex2 = new MultiThreadedSignatureExtractor<SegmentableBufferedImage>(col2);
		mex2.setLogEnabled(isLogEnabled());
		Signature[] sigs = mex2.extractSignatures(simg, regions);
		VectorSignature[] vsigs = new VectorSignature[sigs.length];
		for (int i = 0; i < sigs.length; i++) {
			VectorSignature vs = (VectorSignature) sigs[i];
			vsigs[i] = vs;
		}

		return vsigs;
	}
}
