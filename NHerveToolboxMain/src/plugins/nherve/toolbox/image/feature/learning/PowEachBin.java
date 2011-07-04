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
package plugins.nherve.toolbox.image.feature.learning;

import java.util.List;

import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class PowEachBin.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class PowEachBin extends DataProcessor {
	
	/** The p. */
	private double p;

	/**
	 * Instantiates a new pow each bin.
	 */
	public PowEachBin() {
		this(false);
	}

	/**
	 * Instantiates a new pow each bin.
	 * 
	 * @param display
	 *            the display
	 */
	public PowEachBin(boolean display) {
		this(1d, display);
	}
	
	/**
	 * Instantiates a new pow each bin.
	 * 
	 * @param p
	 *            the p
	 * @param display
	 *            the display
	 */
	public PowEachBin(double p, boolean display) {
		super(display);
		
		this.p = p;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.DataProcessor#apply(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	public VectorSignature apply(VectorSignature sig) throws SignatureException {
		try {
			VectorSignature res = sig.clone();
			for (int d = 0; d < sig.getSize(); d++) {
				res.set(d, Math.pow(sig.get(d), p));
			}
			return res;
		} catch (CloneNotSupportedException e) {
			throw new SignatureException(e);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.DataProcessor#estimateParameters(java.util.List)
	 */
	@Override
	public void estimateParameters(List<VectorSignature> sigs) throws SignatureException {
		// Nothing to do here
	}

}
