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
import java.util.ArrayList;
import java.util.List;

import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignatureConcatenator;


/**
 * The Class FusionDescriptor.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class FusionDescriptor<T extends Segmentable> extends GlobalAndLocalDescriptor<T, DefaultVectorSignature> {
	
	/** The descriptors. */
	private List<DefaultDescriptorImpl<T, DefaultVectorSignature>> descriptors;
	
	/** The coef. */
	private List<Double> coef;
	
	/** The total signature dim. */
	private int totalSignatureDim;
	
	/** The normalization. */
	private boolean normalization;

	/**
	 * Instantiates a new fusion descriptor.
	 * 
	 * @param normalization
	 *            the normalization
	 * @param display
	 *            the display
	 */
	public FusionDescriptor(boolean normalization, boolean display) {
		super(display);
		totalSignatureDim = 0;
		descriptors = new ArrayList<DefaultDescriptorImpl<T, DefaultVectorSignature>>();
		coef = new ArrayList<Double>();
		setNormalization(normalization);
	}
	
	/**
	 * Adds the.
	 * 
	 * @param e
	 *            the e
	 */
	public void add(DefaultDescriptorImpl<T, DefaultVectorSignature> e) {
		add(e, 1);
	}
	
	/**
	 * Adds the.
	 * 
	 * @param e
	 *            the e
	 * @param c
	 *            the c
	 */
	public void add(DefaultDescriptorImpl<T, DefaultVectorSignature> e, double c) {
		descriptors.add(e);
		totalSignatureDim += e.getSignatureSize();
		coef.add(c);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DefaultVectorSignature extractLocalSignature(T img, SupportRegion<IcyPixel> reg) throws SignatureException {
		VectorSignatureConcatenator concatenator = new VectorSignatureConcatenator(DefaultVectorSignature.DENSE_VECTOR_SIGNATURE, isNormalization());
		int dsc = 0;
		double cf = 1;
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			LocalDescriptor<T, DefaultVectorSignature, IcyPixel> ld = (LocalDescriptor<T, DefaultVectorSignature, IcyPixel>) d;
			cf = coef.get(dsc);
			DefaultVectorSignature vs = (DefaultVectorSignature)(ld.extractLocalSignature(img, reg));
			concatenator.add(new DefaultVectorSignature[]{vs}, cf);
			dsc++;
		}
		return concatenator.concatenate()[0];
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DefaultVectorSignature extractLocalSignature(T img, Shape shp) throws SignatureException {
		VectorSignatureConcatenator concatenator = new VectorSignatureConcatenator(DefaultVectorSignature.DENSE_VECTOR_SIGNATURE, isNormalization());
		int dsc = 0;
		double cf = 1;
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			LocalDescriptor<T, DefaultVectorSignature, IcyPixel> ld = (LocalDescriptor<T, DefaultVectorSignature, IcyPixel>) d;
			cf = coef.get(dsc);
			DefaultVectorSignature vs = (DefaultVectorSignature)(ld.extractLocalSignature(img, shp));
			concatenator.add(new DefaultVectorSignature[]{vs}, cf);
			dsc++;
		}
		return concatenator.concatenate()[0];
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.GlobalAndLocalDescriptor#extractGlobalSignature(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DefaultVectorSignature extractGlobalSignature(T img) throws SignatureException {
		VectorSignatureConcatenator concatenator = new VectorSignatureConcatenator(DefaultVectorSignature.DENSE_VECTOR_SIGNATURE, isNormalization());
		int dsc = 0;
		double cf = 1;
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			GlobalDescriptor<T, DefaultVectorSignature> gd = (GlobalDescriptor<T, DefaultVectorSignature>) d;
			cf = coef.get(dsc);
			DefaultVectorSignature vs = (DefaultVectorSignature)(gd.extractGlobalSignature(img));
			concatenator.add(new DefaultVectorSignature[]{vs}, cf);
			dsc++;
		}
		return concatenator.concatenate()[0];
	}

	/**
	 * Gets the.
	 * 
	 * @param index
	 *            the index
	 * @return the default descriptor impl
	 */
	public DefaultDescriptorImpl<T, DefaultVectorSignature> get(int index) {
		return descriptors.get(index);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return totalSignatureDim;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(T img) throws SignatureException {
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			d.postProcess(img);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(T img) throws SignatureException {
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			d.preProcess(img);
		}
	}

	/**
	 * Checks if is normalization.
	 * 
	 * @return true, if is normalization
	 */
	public boolean isNormalization() {
		return normalization;
	}

	/**
	 * Sets the normalization.
	 * 
	 * @param normalization
	 *            the new normalization
	 */
	public void setNormalization(boolean normalization) {
		this.normalization = normalization;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		String res = "FusionDescriptor(";
		boolean first = true;
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			if (!first) {
				res += " | ";
			}
			res += d.toString();
			first = false;
		}
		res += ")";
		return res;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		for (DefaultDescriptorImpl<T, DefaultVectorSignature> d : descriptors) {
			if (d.needToLoadSegmentable()) {
				return true;
			}
		}
		return false;
	}
}
