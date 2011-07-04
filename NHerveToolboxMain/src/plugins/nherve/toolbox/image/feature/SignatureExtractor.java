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
package plugins.nherve.toolbox.image.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl;
import plugins.nherve.toolbox.image.feature.descriptor.GlobalDescriptor;
import plugins.nherve.toolbox.image.feature.region.GridFactory;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class SignatureExtractor.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class SignatureExtractor<T extends Segmentable> extends Algorithm {
	
	/** The descriptor. */
	private DefaultDescriptorImpl<T, ? extends Signature> descriptor;
	
	/** The display. */
	private boolean display;

	/**
	 * Cast.
	 * 
	 * @param l
	 *            the l
	 * @return the list
	 */
	public static List<VectorSignature> cast(List<Signature> l) {
		if (l == null) {
			return null;
		}
		List<VectorSignature> l2 = new ArrayList<VectorSignature>();
		for (Signature s : l) {
			l2.add((VectorSignature)s);
		}
		return l2;
	}
	
	/**
	 * Instantiates a new signature extractor.
	 * 
	 * @param descriptor
	 *            the descriptor
	 */
	public SignatureExtractor(DefaultDescriptorImpl<T, ? extends Signature> descriptor) {
		super();
		this.descriptor = descriptor;
	}
	
	/**
	 * Extract signature.
	 * 
	 * @param img
	 *            the img
	 * @return the signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	@SuppressWarnings("unchecked")
	public Signature extractSignature(T img) throws SignatureException {
		if (!(descriptor instanceof GlobalDescriptor)) {
			throw new SignatureException("Unable to extract a global signature with this descriptor");
		}
		GlobalDescriptor<T, ? extends Signature> gd = (GlobalDescriptor<T, ? extends Signature>) descriptor;
		getDescriptor().preProcess(img);
		Signature globalSignature = gd.extractGlobalSignature(img);
		getDescriptor().postProcess(img);
		return globalSignature;
	}
	
	/**
	 * Extract signatures.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @param doPreprocess
	 *            the do preprocess
	 * @return the signature[]
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract Signature[] extractSignatures(T img, SupportRegion[] regions, boolean doPreprocess) throws SignatureException;
	
	/**
	 * Extract signatures.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @return the signature[]
	 * @throws SignatureException
	 *             the signature exception
	 */
	public Signature[] extractSignatures(T img, SupportRegion[] regions) throws SignatureException {
		return extractSignatures(img, regions, true);
	}
	
	/**
	 * Extract signatures.
	 * 
	 * @param img
	 *            the img
	 * @param regions
	 *            the regions
	 * @return the list
	 * @throws SignatureException
	 *             the signature exception
	 */
	public List<Signature> extractSignatures(T img, List<? extends SupportRegion> regions) throws SignatureException {
		SupportRegion[] aRegions = (SupportRegion[])regions.toArray(new SupportRegion[regions.size()]);
		Signature[] sigs = extractSignatures(img, aRegions);
		if (sigs == null) {
			return null;
		}
		return Arrays.asList(sigs);
	}

	/**
	 * Extract signatures.
	 * 
	 * @param img
	 *            the img
	 * @return the signature[]
	 * @throws SignatureException
	 *             the signature exception
	 */
	public Signature[] extractSignatures(T img) throws SignatureException {
		try {
			GridFactory factory = new GridFactory(GridFactory.ALGO_ONLY_PIXELS);
			List<SupportRegion> regions = factory.extractRegions(img);
			SupportRegion[] aRegions = (SupportRegion[])regions.toArray(new SupportRegion[regions.size()]);
			return extractSignatures(img, aRegions);
		} catch (SupportRegionException e) {
			throw new SignatureException(e);
		}
	}

	/**
	 * Gets the descriptor.
	 * 
	 * @return the descriptor
	 */
	public DefaultDescriptorImpl<T, ? extends Signature> getDescriptor() {
		return descriptor;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.Algorithm#isDisplayEnabled()
	 */
	public boolean isLogEnabled() {
		return display;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.Algorithm#setDisplayEnabled(boolean)
	 */
	public void setLogEnabled(boolean display) {
		this.display = display;
	}
}
