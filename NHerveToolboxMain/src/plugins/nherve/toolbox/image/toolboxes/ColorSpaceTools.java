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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import plugins.nherve.matrix.Matrix;
import plugins.nherve.toolbox.image.feature.PCA;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * The Class ColorSpaceTools.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ColorSpaceTools {
	
	/** The Constant VMAX. */
	public final static double VMAX = 255.0;

	/** The Constant RGB. */
	public final static int RGB = 0;
	
	/** The Constant RGB_TO_I1I2I3. */
	public final static int RGB_TO_I1I2I3 = 1;
	
	/** The Constant RGB_TO_I1H2H3. */
	public final static int RGB_TO_I1H2H3 = 2;
	
	/** The Constant RGB_TO_H1H2H3. */
	public final static int RGB_TO_H1H2H3 = 3;
	
	/** The Constant RGB_TO_HSV. */
	public final static int RGB_TO_HSV = 4;
	
	/** The Constant RGB_TO_SPECIFIC. */
	public final static int RGB_TO_SPECIFIC = 5;
	
	/** The Constant RGB_TO_LOCAL. */
	public final static int RGB_TO_LOCAL = 6;
	
	/** The Constant NONE. */
	public final static int NONE = 7;
	
	/** The Constant I1H2H3_TO_RGB. */
	public final static int I1H2H3_TO_RGB = 8;
	
	/** The Constant RGB_TO_LAB. */
	public final static int RGB_TO_LAB = 9;

	/** The LOCA l_ colorspace. */
	public static SpecificColorSpace LOCAL_COLORSPACE = null;

	/** The Constant COLOR_SPACES. */
	public final static String[] COLOR_SPACES = { "RGB", "I1I2I3", "I1H2H3", "H1H2H3", "HSV", "SPECIFIC", "LOCAL", "NONE", "I1H2H3_TO_RGB", "LAB" };
	
	/** The Constant BOUNDS_RGB. */
	private final static int[][] BOUNDS_RGB = { { 0, 255 }, { 0, 255 }, { 0, 255 } };
	
	/** The Constant BOUNDS_I1I2I3. */
	private final static int[][] BOUNDS_I1I2I3 = { { 0, 255 }, { 0, 255 }, { 0, 255 } };
	
	/** The Constant BOUNDS_I1H2H3. */
	private final static int[][] BOUNDS_I1H2H3 = { { 0, 255 }, { -255, 255 }, { -255, 255 } };
	
	/** The Constant BOUNDS_H1H2H3. */
	private final static int[][] BOUNDS_H1H2H3 = { { 0, 255 }, { 0, 255 }, { 0, 255 } };
	
	/** The Constant BOUNDS_HSV. */
	private final static int[][] BOUNDS_HSV = { { 0, 360 }, { 0, 1 }, { 0, 255 } };

	/** The Constant NB_COLOR_CHANNELS. */
	public final static int NB_COLOR_CHANNELS = 3;

	/**
	 * Gets the color components i_0_255.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param colorSpace
	 *            the color space
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color components i_0_255
	 */
	public static int[] getColorComponentsI_0_255(IcyBufferedImage icyb, int colorSpace, int x, int y) {
		int[] res = new int[NB_COLOR_CHANNELS];
		double[] dres = getColorComponentsD_0_255(icyb, colorSpace, x, y);
		for (int i = 0; i < NB_COLOR_CHANNELS; i++) {
			res[i] = (int) dres[i];
		}
		return res;
	}

	/**
	 * Gets the theoretical bounds.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param c
	 *            the c
	 * @return the theoretical bounds
	 * @throws SignatureException
	 *             the signature exception
	 */
	public static int[] getTheoreticalBounds(int colorSpace, int c) throws SignatureException {
		if (colorSpace > RGB_TO_HSV) {
			throw new SignatureException("getBoundedColorComponentsD_0_1 not available for color space " + colorSpace);
		}

		switch (colorSpace) {
		case RGB:
			return BOUNDS_RGB[c];
		case RGB_TO_I1I2I3:
			return BOUNDS_I1I2I3[c];
		case RGB_TO_I1H2H3:
			return BOUNDS_I1H2H3[c];
		case RGB_TO_H1H2H3:
			return BOUNDS_H1H2H3[c];
		case RGB_TO_HSV:
			return BOUNDS_HSV[c];
		}

		return null;
	}

	/**
	 * Gets the bounded color components d_0_1.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param r
	 *            the r
	 * @param v
	 *            the v
	 * @param b
	 *            the b
	 * @return the bounded color components d_0_1
	 * @throws SignatureException
	 *             the signature exception
	 */
	public static double[] getBoundedColorComponentsD_0_1(int colorSpace, double r, double v, double b) throws SignatureException {
		double[] res = getColorComponentsD_0_255(colorSpace, r, v, b);
		for (int c = 0; c < NB_COLOR_CHANNELS; c++) {
			int[] bds = getTheoreticalBounds(colorSpace, c);
			res[c] = (res[c] - bds[0]) / (double) (bds[1] - bds[0]);
		}
		return res;
	}

	/**
	 * Gets the color components d_0_1.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param r
	 *            the r
	 * @param v
	 *            the v
	 * @param b
	 *            the b
	 * @return the color components d_0_1
	 * @throws SignatureException
	 *             the signature exception
	 */
	public static double[] getColorComponentsD_0_1(int colorSpace, double r, double v, double b) throws SignatureException {
		double[] res = getColorComponentsD_0_255(colorSpace, r, v, b);
		for (int c = 0; c < NB_COLOR_CHANNELS; c++) {
			res[c] /= VMAX;
		}
		return res;
	}

	/**
	 * Rvb to lab.
	 * 
	 * @param r255
	 *            the r255
	 * @param g255
	 *            the g255
	 * @param b255
	 *            the b255
	 * @return the double[]
	 */
	public static double[] rvbToLab(double r255, double g255, double b255) {
		double r = r255 / 255d;
		double g = g255 / 255d;
		double b = b255 / 255d;
		double[] lab = new double[3];
		double X, Y, Z, fx, fy, fz, xr, yr, zr;
		double Ls, as, bs;
		double eps = 216d / 24389d;
		double k = 24389d / 27d;

		double Xr = 0.964221d;
		double Yr = 1d;
		double Zr = 0.825211d;

		if (r <= 0.04045d)
			r = r / 12d;
		else
			r = Math.pow((r + 0.055d) / 1.055d, 2.4d);

		if (g <= 0.04045d)
			g = g / 12d;
		else
			g = Math.pow((g + 0.055d) / 1.055d, 2.4d);

		if (b <= 0.04045d)
			b = b / 12d;
		else
			b = Math.pow((b + 0.055d) / 1.055d, 2.4d);

		X = 0.436052025d * r + 0.385081593d * g + 0.143087414d * b;
		Y = 0.222491598d * r + 0.71688606d * g + 0.060621486d * b;
		Z = 0.013929122d * r + 0.097097002d * g + 0.71418547d * b;

		xr = X / Xr;
		yr = Y / Yr;
		zr = Z / Zr;

		if (xr > eps)
			fx = Math.pow(xr, 1d / 3d);
		else
			fx = ((k * xr + 16d) / 116d);

		if (yr > eps)
			fy = Math.pow(yr, 1d / 3d);
		else
			fy = ((k * yr + 16d) / 116d);

		if (zr > eps)
			fz = Math.pow(zr, 1d / 3d);
		else
			fz = ((k * zr + 16d) / 116d);

		Ls = (116d * fy) - 16d;
		as = 500d * (fx - fy);
		bs = 200d * (fy - fz);

		lab[0] = 2.55d * Ls;
		lab[1] = as;
		lab[2] = bs;

		return lab;
	}

	/**
	 * Gets the color components d_0_255.
	 * 
	 * @param colorSpace
	 *            the color space
	 * @param r
	 *            the r
	 * @param v
	 *            the v
	 * @param b
	 *            the b
	 * @return the color components d_0_255
	 */
	public static double[] getColorComponentsD_0_255(int colorSpace, double r, double v, double b) {
		double[] res = new double[NB_COLOR_CHANNELS];

		switch (colorSpace) {
		case RGB:
		case NONE:
			res[0] = r;
			res[1] = v;
			res[2] = b;
			break;
		case RGB_TO_H1H2H3:
			res[0] = (r + v) / 2.0;
			res[1] = (VMAX + r - v) / 2.0;
			res[2] = (VMAX + b - (r + v) / 2.0) / 2.0;
			break;
		case RGB_TO_I1I2I3:
			res[0] = (r + v + b) / 3.0;
			res[1] = (VMAX + r - b) / 2.0;
			res[2] = VMAX / 2.0 + (2.0 * v - r - b) / 4.0;
			break;
		case RGB_TO_HSV:
			double max = Math.max(b, Math.max(r, v));
			double min = Math.min(b, Math.min(r, v));

			res[2] = max;
			res[1] = (max != 0.0) ? ((max - min) / max) : 0.0;
			if (res[1] == 0.0)
				res[0] = -1;
			else {
				double delta = max - min;
				if (r == max) {
					res[0] = (v - b) / delta;
				} else if (v == max) {
					res[0] = 2.0 + (b - r) / delta;
				} else if (b == max) {
					res[0] = 4.0 + (r - v) / delta;
				}
				res[0] *= 60.0;
				if (res[0] < 0.0) {
					res[0] += 360.0;
				}
			}
			break;
		case RGB_TO_SPECIFIC:
			r -= 135.007;
			v -= 140.279;
			b -= 156.021;
			res[0] = -0.033 * r - 0.900 * v + 0.433 * b;
			res[1] = -0.688 * r + 0.335 * v + 0.643 * b;
			res[2] = +0.724 * r + 0.276 * v + 0.631 * b;
			break;
		case RGB_TO_I1H2H3:
			res[0] = Conversion.private_RGB_to_I1((int) r, (int) v, (int) b);
			res[1] = Conversion.private_RGB_to_H2((int) r, (int) v, (int) b);
			res[2] = Conversion.private_RGB_to_H3((int) r, (int) v, (int) b);
			break;
		case I1H2H3_TO_RGB:
			res[0] = r + v / 2d + b / 3d;
			res[1] = r - v / 2d + b / 3d;
			res[2] = r - 2d * b / 3d;
			break;
		case RGB_TO_LAB:
			res = rvbToLab(r, v, b);
			break;
		case RGB_TO_LOCAL:
			if (LOCAL_COLORSPACE != null) {
				res = LOCAL_COLORSPACE.getColorComponents(r, v, b);
			}
			break;
		}

		return res;
	}

	/**
	 * Gets the color signature.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param colorSpace
	 *            the color space
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	public static VectorSignature getColorSignature(IcyBufferedImage icyb, int colorSpace, int x, int y) throws SignatureException {
		double[] col = getColorComponentsD_0_255(icyb, colorSpace, x, y);
		DenseVectorSignature dvs = new DenseVectorSignature(ColorSpaceTools.NB_COLOR_CHANNELS);
		for (int c = 0; c < ColorSpaceTools.NB_COLOR_CHANNELS; c++) {
			dvs.set(c, col[c]);
		}
		return dvs;
	}

	/**
	 * Gets the exact components.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the exact components
	 */
	private static double[] getExactComponents(IcyBufferedImage icyb, int x, int y) {
		double c0 = icyb.getDataAsDouble(x, y, 0);
		double c1 = icyb.getDataAsDouble(x, y, 1);
		double c2 = icyb.getDataAsDouble(x, y, 2);

		return new double[] { c0, c1, c2 };
	}

	/**
	 * Gets the color components.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color components
	 */
	private static double[] getColorComponents(IcyBufferedImage icyb, int x, int y) {
		int rgb = icyb.getRGB(x, y);

		double b = rgb & 0xFF;
		double v = (rgb >> 8) & 0xFF;
		double r = (rgb >> 16) & 0xFF;

		return new double[] { r, v, b };
	}

	/**
	 * Gets the color components d_0_255.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param colorSpace
	 *            the color space
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color components d_0_255
	 */
	public static double[] getColorComponentsD_0_255(IcyBufferedImage icyb, int colorSpace, int x, int y) {
		if (colorSpace == NONE) {
			return getExactComponents(icyb, x, y);
		}
		if (colorSpace == I1H2H3_TO_RGB) {
			double[] abc = getExactComponents(icyb, x, y);
			return getColorComponentsD_0_255(colorSpace, abc[0], abc[1], abc[2]);
		}
		double[] rvb = getColorComponents(icyb, x, y);
		return getColorComponentsD_0_255(colorSpace, rvb[0], rvb[1], rvb[2]);
	}

	/**
	 * Gets the color components d_0_1.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param colorSpace
	 *            the color space
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the color components d_0_1
	 */
	public static double[] getColorComponentsD_0_1(IcyBufferedImage icyb, int colorSpace, int x, int y) {
		if (colorSpace == NONE) {
			return getExactComponents(icyb, x, y);
		}
		double[] res = getColorComponents(icyb, x, y);
		for (int c = 0; c < NB_COLOR_CHANNELS; c++) {
			res[c] /= VMAX;
		}
		return res;
	}

	/**
	 * Gets the bounded color components d_0_1.
	 * 
	 * @param icyb
	 *            the icyb
	 * @param colorSpace
	 *            the color space
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the bounded color components d_0_1
	 * @throws SignatureException
	 *             the signature exception
	 */
	public static double[] getBoundedColorComponentsD_0_1(IcyBufferedImage icyb, int colorSpace, int x, int y) throws SignatureException {
		if (colorSpace == NONE) {
			return getExactComponents(icyb, x, y);
		}
		double[] rvb = getColorComponents(icyb, x, y);
		return getBoundedColorComponentsD_0_1(colorSpace, rvb[0], rvb[1], rvb[2]);
	}

	/**
	 * Creates the color space.
	 * 
	 * @param rgbSigs
	 *            the rgb sigs
	 * @param quantizeFirst
	 *            the quantize first
	 * @param sz
	 *            the sz
	 * @return the specific color space
	 * @throws SignatureException
	 *             the signature exception
	 */
	public static SpecificColorSpace createColorSpace(List<VectorSignature> rgbSigs, boolean quantizeFirst, int sz) throws SignatureException {
		List<VectorSignature> sigsForPCA = rgbSigs;

		if (quantizeFirst) {
			DecimalFormat df = new DecimalFormat("000000");
			double step = 255d / (double) sz;
			HashMap<String, List<VectorSignature>> qtz = new HashMap<String, List<VectorSignature>>();
			for (VectorSignature vs : rgbSigs) {
				String h = "";
				for (int d = 0; d < ColorSpaceTools.NB_COLOR_CHANNELS; d++) {
					int b = (int) Math.floor(vs.get(d) / step);
					h += df.format(b);
				}
				if (!qtz.containsKey(h)) {
					qtz.put(h, new ArrayList<VectorSignature>());
				}
				qtz.get(h).add(vs);
			}
			System.out.println("Quantizing before PCA - moving from " + rgbSigs.size() + " to " + qtz.size() + "(" + (int) Math.pow(sz, ColorSpaceTools.NB_COLOR_CHANNELS) + ") sigs");
			sigsForPCA = new ArrayList<VectorSignature>();
			for (List<VectorSignature> sigs : qtz.values()) {
				VectorSignature moy = new DenseVectorSignature(ColorSpaceTools.NB_COLOR_CHANNELS);
				for (VectorSignature vs : sigs) {
					moy.add(vs);
				}
				moy.multiply(1d / (double) sigs.size());
				sigsForPCA.add(moy);
			}
		}

		PCA pca = new PCA(sigsForPCA);
		pca.compute();

		VectorSignature mean = pca.getMean();
		Matrix proj = pca.getProjectionMatrix();

		return new SpecificColorSpace(mean.get(0), mean.get(1), mean.get(2), proj.get(0, 0), proj.get(0, 1), proj.get(0, 2), proj.get(1, 0), proj.get(1, 1), proj.get(1, 2), proj.get(2, 0), proj.get(2, 1), proj.get(2, 2));
	}

	/**
	 * Sets the local color space.
	 * 
	 * @param cs
	 *            the new local color space
	 */
	public static void setLocalColorSpace(SpecificColorSpace cs) {
		LOCAL_COLORSPACE = cs;
	}
}
