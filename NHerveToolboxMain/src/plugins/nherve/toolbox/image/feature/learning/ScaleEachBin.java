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

import java.util.Arrays;
import java.util.List;

import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class ScaleEachBin.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ScaleEachBin extends DataProcessor {
	
	/** The MIN. */
	private double MIN;
	
	/** The DIFF. */
	private double DIFF;

	/** The sz. */
	private int sz;
	
	/** The min. */
	private double[] min;
	
	/** The max. */
	private double[] max;
	
	/** The diff. */
	private double[] diff;

	/**
	 * Instantiates a new scale each bin.
	 */
	public ScaleEachBin() {
		this(false);
	}

	/**
	 * Instantiates a new scale each bin.
	 * 
	 * @param display
	 *            the display
	 */
	public ScaleEachBin(boolean display) {
		this(0, 1, display);
	}

	/**
	 * Instantiates a new scale each bin.
	 * 
	 * @param bmin
	 *            the bmin
	 * @param bmax
	 *            the bmax
	 * @param display
	 *            the display
	 */
	public ScaleEachBin(double bmin, double bmax, boolean display) {
		super(display);

		MIN = bmin;
		DIFF = bmax - bmin;

		min = null;
		max = null;
		diff = null;
		sz = 0;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.DataProcessor#apply(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	public VectorSignature apply(VectorSignature sig) throws SignatureException {
		try {
			VectorSignature res = sig.clone();
			for (int d = 0; d < sz; d++) {
				if (diff[d] != 0) {
					double v = sig.get(d);
					v = MIN + DIFF * (v - min[d]) / diff[d];
					res.set(d, v);
				}
			}
//		if (isDisplayEnabled()) {
//			log("Before : " + sig.toString());
//			log("After  : " + res.toString());
//		}
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
		if ((sigs == null) || (sigs.isEmpty())) {
			throw new SignatureException("Not enough signatures to estimate parameters");
		}

		sz = sigs.get(0).getSize();

		min = new double[sz];
		Arrays.fill(min, Double.MAX_VALUE);
		max = new double[sz];
		Arrays.fill(max, -Double.MAX_VALUE);

		for (VectorSignature s : sigs) {
			if (s.getSize() != sz) {
				throw new SignatureException("Signature size mismatch (" + s.getSize() + "/" + sz + ")");
			}
			for (int d = 0; d < sz; d++) {
				double v = s.get(d);
				if (v < min[d]) {
					min[d] = v;
				}
				if (v > max[d]) {
					max[d] = v;
				}
			}
		}

		diff = new double[sz];
		for (int d = 0; d < sz; d++) {
			diff[d] = max[d] - min[d];
		}
	}

}
