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

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.feature.Descriptor;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * The Class DefaultDescriptorImpl.
 * 
 * @param <T>
 *            the generic type
 * @param <S>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class DefaultDescriptorImpl<T extends Segmentable, S extends Signature> extends Algorithm implements Descriptor<T> {
	
	/** The vector signature type. */
	private int vectorSignatureType;
	
	/**
	 * Instantiates a new default descriptor impl.
	 * 
	 * @param display
	 *            the display
	 */
	public DefaultDescriptorImpl(boolean display) {
		super(display);
		setVectorSignatureType(VectorSignature.DENSE_VECTOR_SIGNATURE);
	}

	/**
	 * Gets the signature size.
	 * 
	 * @return the signature size
	 */
	public abstract int getSignatureSize();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	/**
	 * Gets the vector signature type.
	 * 
	 * @return the vector signature type
	 */
	public int getVectorSignatureType() {
		return vectorSignatureType;
	}

	/**
	 * Sets the vector signature type.
	 * 
	 * @param vectorSignatureType
	 *            the new vector signature type
	 */
	public void setVectorSignatureType(int vectorSignatureType) {
		this.vectorSignatureType = vectorSignatureType;
	}
	
	/**
	 * Gets the empty signature.
	 * 
	 * @param size
	 *            the size
	 * @return the empty signature
	 */
	public VectorSignature getEmptySignature(int size) {
		return VectorSignature.getEmptySignature(vectorSignatureType, size);
	}
	
	/**
	 * Gets the empty signature.
	 * 
	 * @return the empty signature
	 */
	public VectorSignature getEmptySignature() {
		return getEmptySignature(getSignatureSize());
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#initForDatabase(plugins.nherve.toolbox.image.db.ImageDatabase)
	 */
	@Override
	public void initForDatabase(ImageDatabase db) throws SignatureException {
		// Nothing to do by default
	}
}
