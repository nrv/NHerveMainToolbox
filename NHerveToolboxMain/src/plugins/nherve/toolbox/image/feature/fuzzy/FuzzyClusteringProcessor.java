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

import java.util.List;

import plugins.nherve.toolbox.image.feature.SegmentableBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.mask.MaskStack;

/**
 * The Interface FuzzyClusteringProcessor.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface FuzzyClusteringProcessor {
	
	/**
	 * Adds the to mask stack.
	 * 
	 * @param sm
	 *            the sm
	 * @param img
	 *            the img
	 * @param seg
	 *            the seg
	 * @param regions
	 *            the regions
	 * @param sigs
	 *            the sigs
	 * @throws MaskException
	 *             the mask exception
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	void addToMaskStack(FuzzyClusteringAlgorithm sm, IcyBufferedImage img, MaskStack seg, SupportRegion[] regions, VectorSignature[] sigs) throws MaskException, ClusteringException;
	
	/**
	 * Gets the regions.
	 * 
	 * @param simg
	 *            the simg
	 * @return the regions
	 * @throws SupportRegionException
	 *             the support region exception
	 */
	SupportRegion[] getRegions(SegmentableBufferedImage simg) throws SupportRegionException;
	
	/**
	 * Gets the signatures.
	 * 
	 * @param simg
	 *            the simg
	 * @param regions
	 *            the regions
	 * @return the signatures
	 * @throws SignatureException
	 *             the signature exception
	 */
	VectorSignature[] getSignatures(SegmentableBufferedImage simg, SupportRegion[] regions) throws SignatureException;
	
	/**
	 * Gets the as image.
	 * 
	 * @param data
	 *            the data
	 * @param regions
	 *            the regions
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the as image
	 */
	IcyBufferedImage getAsImage(double[] data, SupportRegion[] regions, int w, int h);
	
	/**
	 * Creates the fuzzy clustering algorithm.
	 * 
	 * @param img
	 *            the img
	 * @return the fuzzy clustering algorithm
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	FuzzyClusteringAlgorithm createFuzzyClusteringAlgorithm(IcyBufferedImage img) throws ClusteringException;
	
	/**
	 * Creates the fuzzy clustering algorithm.
	 * 
	 * @param img
	 *            the img
	 * @return the fuzzy clustering algorithm
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	FuzzyClusteringAlgorithm createFuzzyClusteringAlgorithm(List<IcyBufferedImage> img) throws ClusteringException;
	
	/**
	 * Do clustering.
	 * 
	 * @param img
	 *            the img
	 * @param seg
	 *            the seg
	 * @param regions
	 *            the regions
	 * @param sigs
	 *            the sigs
	 * @return the fuzzy clustering algorithm
	 * @throws SupportRegionException
	 *             the support region exception
	 * @throws SignatureException
	 *             the signature exception
	 * @throws MaskException
	 *             the mask exception
	 * @throws ClusteringException
	 *             the clustering exception
	 */
	FuzzyClusteringAlgorithm doClustering(IcyBufferedImage img, MaskStack seg, SupportRegion[] regions, VectorSignature[] sigs) throws SupportRegionException, SignatureException, MaskException, ClusteringException;
	
	/**
	 * Do clustering.
	 * 
	 * @param img
	 *            the img
	 * @param seg
	 *            the seg
	 * @return the fuzzy clustering algorithm
	 */
	FuzzyClusteringAlgorithm doClustering(IcyBufferedImage img, MaskStack seg);
}
