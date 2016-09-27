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
package plugins.nherve.toolbox.image.toolboxes;

import icy.image.IcyBufferedImage;

import java.text.DecimalFormat;

import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;


/**
 * The Class SpecificColorSpace.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SpecificColorSpace {
	
	/** The mb. */
	private double mr, mv, mb;
	
	/** The b2. */
	private double r0, v0, b0, r1, v1, b1, r2, v2, b2;

	/**
	 * Instantiates a new specific color space.
	 * 
	 * @param mr
	 *            the mr
	 * @param mv
	 *            the mv
	 * @param mb
	 *            the mb
	 * @param r0
	 *            the r0
	 * @param v0
	 *            the v0
	 * @param b0
	 *            the b0
	 * @param r1
	 *            the r1
	 * @param v1
	 *            the v1
	 * @param b1
	 *            the b1
	 * @param r2
	 *            the r2
	 * @param v2
	 *            the v2
	 * @param b2
	 *            the b2
	 */
	public SpecificColorSpace(double mr, double mv, double mb, double r0, double v0, double b0, double r1, double v1, double b1, double r2, double v2, double b2) {
		super();
		this.mr = mr;
		this.mv = mv;
		this.mb = mb;
		this.r0 = r0;
		this.v0 = v0;
		this.b0 = b0;
		this.r1 = r1;
		this.v1 = v1;
		this.b1 = b1;
		this.r2 = r2;
		this.v2 = v2;
		this.b2 = b2;
	}
	
	/**
	 * Gets the color signature.
	 * 
	 * @param rvbSig
	 *            the rvb sig
	 * @return the color signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	public DefaultVectorSignature getColorSignature(DefaultVectorSignature rvbSig) throws SignatureException {
		DenseVectorSignature dvs = new DenseVectorSignature(ColorSpaceTools.NB_COLOR_CHANNELS);
		double[] col = getColorComponents(rvbSig.get(0), rvbSig.get(1), rvbSig.get(2));
		for (int c = 0; c < ColorSpaceTools.NB_COLOR_CHANNELS; c++) {
			dvs.set(c, col[c]);
		}
		return dvs;
	}
	
	/**
	 * Gets the color signature.
	 * 
	 * @param img
	 *            the img
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	public DefaultVectorSignature getColorSignature(IcyBufferedImage img, int x, int y) throws SignatureException {
		double[] col = ColorSpaceTools.getColorComponentsD_0_255(img, ColorSpaceTools.RGB, x, y);
		
		col = getColorComponents(col[0], col[1], col[2]);
			
		DenseVectorSignature dvs = new DenseVectorSignature(ColorSpaceTools.NB_COLOR_CHANNELS);
		for (int c = 0; c < ColorSpaceTools.NB_COLOR_CHANNELS; c++) {
			dvs.set(c, col[c]);
		}
		return dvs;
	}

	/**
	 * Gets the color components.
	 * 
	 * @param r
	 *            the r
	 * @param v
	 *            the v
	 * @param b
	 *            the b
	 * @return the color components
	 */
	public double[] getColorComponents(double r, double v, double b) {
		double[] res = new double[ColorSpaceTools.NB_COLOR_CHANNELS];

		r -= mr;
		v -= mv;
		b -= mb;
		res[0] = r0 * r + v0 * v + b0 * b;
		res[1] = r1 * r + v1 * v + b1 * b;
		res[2] = r2 * r + v2 * v + b2 * b;

		return res;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.000");
		String ts = "mean ["+df.format(mr)+" "+df.format(mv)+" "+df.format(mb)+"]\n";
		ts +="     ["+df.format(r0)+" "+df.format(v0)+" "+df.format(b0)+"]\n";
		ts +="     ["+df.format(r1)+" "+df.format(v1)+" "+df.format(b1)+"]\n";
		ts +="     ["+df.format(r2)+" "+df.format(v2)+" "+df.format(b2)+"]";
		return ts;
	}
}
