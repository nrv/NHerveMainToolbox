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

import plugins.nherve.toolbox.image.db.ImageEntry;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.SegmentableImage;
import plugins.nherve.toolbox.image.feature.com.VocabularyOfObjects;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * The Class BagOfWordDescriptor.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class BagOfWordDescriptor<T extends SegmentableImage> extends DefaultDescriptorImpl<ImageEntry<T>, VectorSignature> implements GlobalDescriptor<ImageEntry<T>, VectorSignature> {
	
	/** The vocabulary. */
	private VocabularyOfObjects<Integer, VectorSignature> vocabulary;
	
	/** The descriptor. */
	private String descriptor;

	/**
	 * Instantiates a new bag of word descriptor.
	 * 
	 * @param display
	 *            the display
	 */
	public BagOfWordDescriptor(boolean display) {
		super(display);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return vocabulary.size();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "BagOfWordDescriptor";
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.GlobalDescriptor#extractGlobalSignature(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public VectorSignature extractGlobalSignature(ImageEntry<T> img) throws SignatureException {
		try {
			VectorSignature bow = getEmptySignature();

			for (VectorSignature w : img.getLocalSignatures().get(descriptor)) {
				double cd = Double.MAX_VALUE;
				int cw = 0;
				for (int i = 0; i < vocabulary.size(); i++) {
					double d = vocabulary.computeDistance(i, w);
					if (d < cd) {
						cd = d;
						cw = i;
					}
				}
				bow.addTo(cw, 1);
			}
			bow.normalizeSumToOne(true);
			return bow;
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(ImageEntry<T> img) throws SignatureException {
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(ImageEntry<T> img) throws SignatureException {
	}

	/**
	 * Gets the vocabulary.
	 * 
	 * @return the vocabulary
	 */
	public VocabularyOfObjects<Integer, VectorSignature> getVocabulary() {
		return vocabulary;
	}

	/**
	 * Sets the vocabulary.
	 * 
	 * @param vocabulary
	 *            the vocabulary
	 */
	public void setVocabulary(VocabularyOfObjects<Integer, VectorSignature> vocabulary) {
		this.vocabulary = vocabulary;
	}

	/**
	 * Gets the descriptor.
	 * 
	 * @return the descriptor
	 */
	public String getDescriptor() {
		return descriptor;
	}

	/**
	 * Sets the descriptor.
	 * 
	 * @param descriptor
	 *            the new descriptor
	 */
	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return false;
	}

}
