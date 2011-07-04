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

import icy.image.IcyBufferedImage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;


/**
 * The Class ConvolutionKernel2D.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ConvolutionKernel2D extends Algorithm {
	
	/** The Constant SOBEL_X. */
	public final static int SOBEL_X = 1;
	
	/** The Constant SOBEL_Y. */
	public final static int SOBEL_Y = 2;

	/** The Constant ONE. */
	public final static double ONE = 255;

	/** The Constant GRAYLEVEL_KD. */
	private static final int GRAYLEVEL_KD = 0;
	
	/** The Constant GRAYLEVEL_KERNEL. */
	private static final double[] GRAYLEVEL_KERNEL = { 1.0 };
	
	/** The Constant HIGHPASS_KD. */
	private static final int HIGHPASS_KD = 1;
	
	/** The Constant HIGHPASS_KERNEL. */
	private static final double[] HIGHPASS_KERNEL = { -1.0, -1.0, -1.0, -1.0, 9.0, -1.0, -1.0, -1.0, -1.0 };
	
	/** The Constant LOWPASS_KD. */
	private static final int LOWPASS_KD = 1;
	
	/** The Constant LOWPASS_KERNEL. */
	private static final double[] LOWPASS_KERNEL = { 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0 };
	
	/** The Constant LAPLACIAN_KD. */
	private static final int LAPLACIAN_KD = 1;
	
	/** The Constant LAPLACIAN_KERNEL. */
	private static final double[] LAPLACIAN_KERNEL = { 0.0, 1.0, 0.0, 1.0, -4.0, 1.0, 0.0, 1.0, 0.0 };
	
	/** The Constant SOBEL_X_KD. */
	private static final int SOBEL_X_KD = 1;
	
	/** The Constant SOBEL_X_KERNEL. */
	private static final double[] SOBEL_X_KERNEL = { 1.0, 2.0, 1.0, 0.0, 0.0, 0.0, -1.0, -2.0, -1.0 };
	
	/** The Constant SOBEL_Y_KD. */
	private static final int SOBEL_Y_KD = 1;
	
	/** The Constant SOBEL_Y_KERNEL. */
	private static final double[] SOBEL_Y_KERNEL = { 1.0, 0.0, -1.0, 2.0, 0.0, -2.0, 1.0, 0.0, -1.0 };

	/**
	 * The Interface Operator.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	private interface Operator {
		
		/**
		 * Convolve.
		 * 
		 * @param data
		 *            the data
		 * @param w
		 *            the w
		 * @param h
		 *            the h
		 * @param w2
		 *            the w2
		 * @param h2
		 *            the h2
		 * @return the double[]
		 */
		double[] convolve(final double[] data, int w, int h, int w2, int h2);

		/**
		 * Gets the kernel half width.
		 * 
		 * @return the kernel half width
		 */
		public int getKernelHalfWidth();

		/**
		 * Gets the kernel half height.
		 * 
		 * @return the kernel half height
		 */
		public int getKernelHalfHeight();
	}

	/**
	 * The Class ThresholdOperator.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	private class ThresholdOperator implements Operator {
		
		/** The min. */
		private final double min;
		
		/** The max. */
		private final double max;
		
		/** The binarize. */
		private final boolean binarize;

		/**
		 * Instantiates a new threshold operator.
		 * 
		 * @param min
		 *            the min
		 * @param max
		 *            the max
		 * @param binarize
		 *            the binarize
		 */
		public ThresholdOperator(double min, double max, boolean binarize) {
			super();
			this.min = min;
			this.max = max;
			this.binarize = binarize;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#convolve(double[], int, int, int, int)
		 */
		@Override
		public double[] convolve(final double[] data, int w, int h, int w2, int h2) {
			double[] result = new double[w * h];
			Arrays.fill(result, 0f);

			int idx = 0;
			double val = 0;
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					idx = x + y * w;
					val = data[idx];
					if ((val >= min) && (val <= max)) {
						if (binarize) {
							result[idx] = 1.0;
						} else {
							result[idx] = val;
						}
					}
				}
			}

			return result;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#getKernelHalfHeight()
		 */
		@Override
		public int getKernelHalfHeight() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#getKernelHalfWidth()
		 */
		@Override
		public int getKernelHalfWidth() {
			return 0;
		}
	}

	/**
	 * The Class MedianOperator.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	private class MedianOperator implements Operator {
		
		/** The half width. */
		private final int halfWidth;
		
		/** The half height. */
		private final int halfHeight;

		/**
		 * Instantiates a new median operator.
		 * 
		 * @param hw
		 *            the hw
		 * @param hh
		 *            the hh
		 */
		public MedianOperator(int hw, int hh) {
			super();
			halfWidth = hw;
			halfHeight = hh;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#convolve(double[], int, int, int, int)
		 */
		@Override
		public double[] convolve(final double[] data, int w, int h, int w2, int h2) {
			double[] result = new double[w * h];
			Arrays.fill(result, 0.0);

			ArrayList<Double> temp = new ArrayList<Double>();
			int idx = 0;
			int mx = w * h;
			for (int x2 = 0; x2 < w; x2++) {
				for (int y2 = 0; y2 < h; y2++) {
					temp.clear();
					for (int ky = -halfHeight; ky <= halfHeight; ky++) {
						for (int kx = -halfWidth; kx <= halfWidth; kx++) {
							idx = x2 + kx + (y2 + ky) * w;
							if ((idx >= 0) && (idx < mx)) {
								temp.add(data[idx]);
							}
						}
					}
					Collections.sort(temp);
					int mi = (int) Math.floor(temp.size() / 2.0);
					if (doBorders) {
						result[x2 - halfWidth + (y2 - halfHeight) * w] = temp.get(mi);
					} else {
						result[x2 + y2 * w] = temp.get(mi);
					}
				}
			}

			return result;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#getKernelHalfHeight()
		 */
		public int getKernelHalfHeight() {
			return halfHeight;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#getKernelHalfWidth()
		 */
		public int getKernelHalfWidth() {
			return halfWidth;
		}
	}

	/**
	 * The Class StandardOperator.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	class StandardOperator implements Operator {
		
		/** The k. */
		private final double[] k;
		
		/** The kernel half width. */
		private final int kernelHalfWidth;
		
		/** The kernel half height. */
		private final int kernelHalfHeight;
		
		/** The normalization factor. */
		private double normalizationFactor;

		/**
		 * Instantiates a new standard operator.
		 * 
		 * @param kernel
		 *            the kernel
		 * @param kdw
		 *            the kdw
		 * @param kdh
		 *            the kdh
		 */
		public StandardOperator(double[] kernel, int kdw, int kdh) {
			super();
			this.k = kernel;
			this.kernelHalfWidth = kdw;
			this.kernelHalfHeight = kdh;
			computeNormalizationFactor();
		}

		/**
		 * Compute normalization factor.
		 */
		private void computeNormalizationFactor() {
			normalizationFactor = 0;
			for (int i = 0; i < k.length; i++) {
				normalizationFactor += k[i];
			}
			if (normalizationFactor == 0) {
				normalizationFactor = 1.0;
			} else {
				normalizationFactor = 1.0 / normalizationFactor;
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			DecimalFormat df = new DecimalFormat("0.000");
			String result = "Kernel " + df.format(normalizationFactor) + "\n";
			int ki = 0;
			for (int ky = -kernelHalfHeight; ky <= kernelHalfHeight; ky++) {
				for (int kx = -kernelHalfWidth; kx <= kernelHalfWidth; kx++) {
					result += df.format(k[ki]) + " ";
					ki++;
				}
				result += "\n";
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#convolve(double[], int, int, int, int)
		 */
		@Override
		public double[] convolve(final double[] data, int w, int h, int w2, int h2) {
			double[] result = new double[w * h];
			Arrays.fill(result, 0f);

			int ki;
			double val;
			int idx;
			for (int x2 = kernelHalfWidth; x2 < w2 - kernelHalfWidth; x2++) {
				for (int y2 = kernelHalfHeight; y2 < h2 - kernelHalfHeight; y2++) {
					val = 0;
					ki = 0;
					for (int ky = -kernelHalfHeight; ky <= kernelHalfHeight; ky++) {
						for (int kx = -kernelHalfWidth; kx <= kernelHalfWidth; kx++) {
							idx = x2 + kx + (y2 + ky) * w2;
//							if (idx == 260) {
//								System.out.println("idx");
//							}
							val += data[idx] * k[ki];
							ki++;
						}
					}
					if (doBorders) {
						result[x2 - kernelHalfWidth + (y2 - kernelHalfHeight) * w] = val;
					} else {
						result[x2 + y2 * w] = val;
					}
				}
			}

			if (normalizationFactor != 1f) {
				int sz = w * h;
				for (int i = 0; i < sz; i++) {
					result[i] *= normalizationFactor;
				}
			}

			return result;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#getKernelHalfWidth()
		 */
		public int getKernelHalfWidth() {
			return kernelHalfWidth;
		}

		/* (non-Javadoc)
		 * @see plugins.nherve.toolbox.image.feature.ConvolutionKernel2D.Operator#getKernelHalfHeight()
		 */
		public int getKernelHalfHeight() {
			return kernelHalfHeight;
		}
	}

	/** The do borders. */
	private boolean doBorders;
	
	/** The op. */
	private Operator op;

	/**
	 * Instantiates a new convolution kernel2 d.
	 * 
	 * @param niceBorders
	 *            the nice borders
	 */
	private ConvolutionKernel2D(boolean niceBorders) {
		super();
		doBorders = niceBorders;
		op = null;
	}

	/**
	 * Gets the laplacian filter.
	 * 
	 * @param niceBorders
	 *            the nice borders
	 * @return the laplacian filter
	 */
	public static ConvolutionKernel2D getLaplacianFilter(boolean niceBorders) {
		return getStandardKernel(LAPLACIAN_KD, LAPLACIAN_KERNEL, niceBorders);
	}

	/**
	 * Gets the high pass filter.
	 * 
	 * @param niceBorders
	 *            the nice borders
	 * @return the high pass filter
	 */
	public static ConvolutionKernel2D getHighPassFilter(boolean niceBorders) {
		return getStandardKernel(HIGHPASS_KD, HIGHPASS_KERNEL, niceBorders);
	}

	/**
	 * Gets the low pass filter.
	 * 
	 * @param niceBorders
	 *            the nice borders
	 * @return the low pass filter
	 */
	public static ConvolutionKernel2D getLowPassFilter(boolean niceBorders) {
		return getStandardKernel(LOWPASS_KD, LOWPASS_KERNEL, niceBorders);
	}

	/**
	 * Gets the sobel operator.
	 * 
	 * @param type
	 *            the type
	 * @param niceBorders
	 *            the nice borders
	 * @return the sobel operator
	 */
	public static ConvolutionKernel2D getSobelOperator(int type, boolean niceBorders) {
		if (type == SOBEL_X) {
			return getStandardKernel(SOBEL_X_KD, SOBEL_X_KERNEL, niceBorders);
		} else if (type == SOBEL_Y) {
			return getStandardKernel(SOBEL_Y_KD, SOBEL_Y_KERNEL, niceBorders);
		} else {
			throw new IllegalArgumentException("Unknown Sobel operator type (" + type + ")");
		}
	}

	/**
	 * Gets the gaussian filter.
	 * 
	 * @param sigma
	 *            the sigma
	 * @return the gaussian filter
	 */
	public static ConvolutionKernel2D getGaussianFilter(double sigma) {
		int kd = (int) Math.ceil(sigma * 3);
		return getGaussianFilter(kd, sigma, true);
	}

	/**
	 * Gets the threshold filter.
	 * 
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param binarize
	 *            the binarize
	 * @return the threshold filter
	 */
	public static ConvolutionKernel2D getThresholdFilter(double min, double max, boolean binarize) {
		ConvolutionKernel2D g = new ConvolutionKernel2D(false);
		ThresholdOperator op = g.new ThresholdOperator(min, max, binarize);
		g.op = op;
		g.doBorders = false;
		return g;
	}

	/**
	 * Gets the median filter.
	 * 
	 * @param kd
	 *            the kd
	 * @return the median filter
	 */
	public static ConvolutionKernel2D getMedianFilter(int kd) {
		ConvolutionKernel2D g = new ConvolutionKernel2D(false);
		MedianOperator op = g.new MedianOperator(kd, kd);
		g.op = op;
		g.doBorders = false;
		return g;
	}

	/**
	 * Gets the gaussian filter.
	 * 
	 * @param kd
	 *            the kd
	 * @param sigma
	 *            the sigma
	 * @param niceBorders
	 *            the nice borders
	 * @return the gaussian filter
	 */
	public static ConvolutionKernel2D getGaussianFilter(int kd, double sigma, boolean niceBorders) {
		int ks = (2 * kd + 1) * (2 * kd + 1);
		double[] kernel = new double[ks];
		if (sigma == 0) {
			Arrays.fill(kernel, 0f);
			kernel[ks / 2] = 1f;
		} else {
			double nsg = 2.0 * sigma * sigma;
			double factor = 1.0 / (Math.PI * nsg);
			int ki = 0;
			for (int ky = -kd; ky <= kd; ky++) {
				for (int kx = -kd; kx <= kd; kx++) {
					kernel[ki] = factor * Math.exp(-(kx * kx + ky * ky) / nsg);
					ki++;
				}
			}
		}
		ConvolutionKernel2D g = new ConvolutionKernel2D(niceBorders);
		StandardOperator op = g.new StandardOperator(kernel, kd, kd);
		op.computeNormalizationFactor();
		g.op = op;
		g.doBorders = niceBorders;
		return g;
	}

	/**
	 * Gets the standard kernel.
	 * 
	 * @param kd
	 *            the kd
	 * @param k
	 *            the k
	 * @param niceBorders
	 *            the nice borders
	 * @return the standard kernel
	 */
	private static ConvolutionKernel2D getStandardKernel(int kd, double[] k, boolean niceBorders) {
		ConvolutionKernel2D kernel = new ConvolutionKernel2D(niceBorders);
		kernel.op = kernel.new StandardOperator(k, kd, kd);
		return kernel;
	}

	/**
	 * Gets the gray level converter.
	 * 
	 * @return the gray level converter
	 */
	public static ConvolutionKernel2D getGrayLevelConverter() {
		return getStandardKernel(GRAYLEVEL_KD, GRAYLEVEL_KERNEL, false);
	}

	/**
	 * Canny.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param sigma
	 *            the sigma
	 * @param hystLow
	 *            the hyst low
	 * @param hystHigh
	 *            the hyst high
	 * @return the boolean[]
	 */
	public static boolean[] canny(double[] data, int w, int h, double sigma, double hystLow, double hystHigh) {
		boolean[] edges = new boolean[w * h];
		canny(data, w, h, sigma, hystLow, hystHigh, edges, null, null);
		return edges;
	}

	/**
	 * Canny.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param sigma
	 *            the sigma
	 * @param hystLow
	 *            the hyst low
	 * @param hystHigh
	 *            the hyst high
	 * @param finalEdges
	 *            the final edges
	 * @param gradient
	 *            the gradient
	 * @param orientations
	 *            the orientations
	 */
	public static void canny(double[] data, int w, int h, double sigma, double hystLow, double hystHigh, boolean[] finalEdges, double[] gradient, double[] orientations) {
		int size = w * h;
		ConvolutionKernel2D gaussian = ConvolutionKernel2D.getGaussianFilter(sigma);
		double[] blured = gaussian.borderAndConvolve(data, w, h);
		double[][] sobel = ConvolutionKernel2D.sobel(blured, w, h, true);
		blured = null;

		if (gradient != null) {
			for (int i = 0; i < size; i++) {
				gradient[i] = sobel[0][i];
			}
		}

		if (orientations != null) {
			for (int i = 0; i < size; i++) {
				orientations[i] = sobel[1][i];
			}
		}

		// Non maximum suppression
		boolean[] edges = new boolean[size];
		Arrays.fill(edges, false);
		double step = Math.PI / 8.0;

		int idx = 0;
		double val = 0;
		double a = 0;
		for (int x = 1; x < w - 1; x++) {
			for (int y = 1; y < h - 1; y++) {
				idx = x + y * w;
				val = sobel[0][idx];
				a = sobel[1][idx];
				if (a < 0) {
					a += Math.PI;
				}
				if (a > Math.PI) {
					a -= Math.PI;
				}
				int da = (int) Math.floor(a / step);
				switch (da) {
				case 0:
				case 7:
					if ((val > sobel[0][x + (y - 1) * w]) && (val > sobel[0][x + (y + 1) * w])) {
						edges[idx] = true;
					}
					break;
				case 1:
				case 2:
					if ((val > sobel[0][x - 1 + (y - 1) * w]) && (val > sobel[0][x + 1 + (y + 1) * w])) {
						edges[idx] = true;
					}
					break;
				case 3:
				case 4:
					if ((val > sobel[0][x - 1 + y * w]) && (val > sobel[0][x + 1 + y * w])) {
						edges[idx] = true;
					}
					break;
				case 5:
				case 6:
					if ((val > sobel[0][x + 1 + (y - 1) * w]) && (val > sobel[0][x - 1 + (y + 1) * w])) {
						edges[idx] = true;
					}
					break;
				}
			}
		}

		// Hysteresis
		Arrays.fill(finalEdges, false);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				idx = x + y * w;
				if (edges[idx] && (sobel[0][idx] >= hystHigh)) {
					findHystLowContour(sobel[0], edges, finalEdges, x, y, w, size, hystLow, true);
				}
			}
		}
	}

	/**
	 * Find hyst low contour.
	 * 
	 * @param gradient
	 *            the gradient
	 * @param edges
	 *            the edges
	 * @param finalEdges
	 *            the final edges
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param w
	 *            the w
	 * @param size
	 *            the size
	 * @param thresh
	 *            the thresh
	 * @param init
	 *            the init
	 */
	private static void findHystLowContour(double[] gradient, boolean[] edges, boolean[] finalEdges, int x, int y, int w, int size, double thresh, boolean init) {
		int idx = x + y * w;
		// FIXME : a revoir avec w et h
		if ((idx >= 0) && (idx < size)) {
			if (init || (edges[idx] && (gradient[idx] >= thresh))) {
				finalEdges[idx] = true;
				edges[idx] = false;
				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						findHystLowContour(gradient, edges, finalEdges, x + dx, y + dy, w, size, thresh, false);
					}
				}
			}
		}
	}

	/**
	 * Sobel.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param niceBorders
	 *            the nice borders
	 * @return the double[][]
	 */
	public static double[][] sobel(double[] data, int w, int h, boolean niceBorders) {
		double[] d = data;
		if (niceBorders) {
			d = addBorders(data, w, h, 1, 1);
		}
		double[][] result = new double[2][w * h];

		double[] gx = getSobelOperator(SOBEL_X, niceBorders).convolve(d, w, h);
		double[] gy = getSobelOperator(SOBEL_Y, niceBorders).convolve(d, w, h);
		int idx = 0;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				idx = x + y * w;
				result[0][idx] = Math.sqrt(gx[idx] * gx[idx] + gy[idx] * gy[idx]);
				result[1][idx] = Math.atan2(gy[idx], gx[idx]);
			}
		}

		return result;
	}

	/**
	 * Adds the borders.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param kdw
	 *            the kdw
	 * @param kdh
	 *            the kdh
	 * @return the double[]
	 */
	public static double[] addBorders(double[] data, int w, int h, int kdw, int kdh) {
		int w2 = w + 2 * kdw;
		int h2 = h + 2 * kdh;

		double[] data2 = new double[w2 * h2];
		Arrays.fill(data2, 0.0);

		int idx = 0;
		int idx2 = 0;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				idx = x + w * y;
				idx2 = x + kdw + w2 * (y + kdh);
				data2[idx2] = data[idx];
			}
			for (int y2 = 0; y2 < kdh; y2++) {
				idx = x;
				idx2 = x + kdw + w2 * y2;
				data2[idx2] = data[idx];
			}
			for (int y2 = h + kdh; y2 < h2; y2++) {
				idx = x + w * (h - 1);
				idx2 = x + kdw + w2 * y2;
				data2[idx2] = data[idx];
			}
		}

		for (int y = 0; y < h; y++) {
			for (int x2 = 0; x2 < kdw; x2++) {
				idx = w * y;
				idx2 = x2 + w2 * (y + kdh);
				data2[idx2] = data[idx];
			}
			for (int x2 = w + kdw; x2 < w2; x2++) {
				idx = (w - 1) + w * y;
				idx2 = x2 + w2 * (y + kdh);
				data2[idx2] = data[idx];
			}
		}

		for (int x2 = 0; x2 < kdw; x2++) {
			idx = 0;
			for (int y2 = 0; y2 < kdh; y2++) {
				idx2 = x2 + w2 * y2;
				data2[idx2] = data[idx];
			}
			idx = w * (h - 1);
			for (int y2 = h + kdh; y2 < h2; y2++) {
				idx2 = x2 + w2 * y2;
				data2[idx2] = data[idx];
			}
		}

		for (int x2 = w + kdw; x2 < w2; x2++) {
			idx = w - 1;
			for (int y2 = 0; y2 < kdh; y2++) {
				idx2 = x2 + w2 * y2;
				data2[idx2] = data[idx];
			}
			idx = (w - 1) + w * (h - 1);
			for (int y2 = h + kdh; y2 < h2; y2++) {
				idx2 = x2 + w2 * y2;
				data2[idx2] = data[idx];
			}
		}

		return data2;
	}

	/**
	 * Not optimized yet.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the double[]
	 */
	public double[] convolve(double[] data, int w, int h) {
		int w2 = w;
		int h2 = h;

		if (doBorders) {
			w2 = w + 2 * op.getKernelHalfWidth();
			h2 = h + 2 * op.getKernelHalfHeight();
		}

		return op.convolve(data, w, h, w2, h2);
	}

	/**
	 * Border and convolve.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the double[]
	 */
	public double[] borderAndConvolve(double[] data, int w, int h) {
		double[] myData = data;

		int w2 = w;
		int h2 = h;

		if (doBorders) {
			w2 = w + 2 * op.getKernelHalfWidth();
			h2 = h + 2 * op.getKernelHalfHeight();
			myData = addBorders(data, w, h, op.getKernelHalfWidth(), op.getKernelHalfHeight());
		}

		return op.convolve(myData, w, h, w2, h2);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return op.toString();
	}

	// FIXME todo
	/**
	 * Convolve rgb.
	 * 
	 * @param img
	 *            the img
	 * @return the double[][]
	 */
	public double[][] convolveRGB(IcyBufferedImage img) {
		return null;
	}

	/**
	 * Gets the intensity.
	 * 
	 * @param img
	 *            the img
	 * @return the intensity
	 */
	public static double[] getIntensity(IcyBufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		return getIntensity(img, 0, 0, w, h);
	}

	/**
	 * Gets the intensity.
	 * 
	 * @param img
	 *            the img
	 * @param x1
	 *            the x1
	 * @param y1
	 *            the y1
	 * @param x2
	 *            the x2
	 * @param y2
	 *            the y2
	 * @return the intensity
	 */
	public static double[] getIntensity(IcyBufferedImage img, int x1, int y1, int x2, int y2) {
		int w = x2 - x1;
		int h = y2 - y1;

		IcyBufferedImage gray = SomeImageTools.computeGrayScale(img, 1, 1);
		double[] fulldata = gray.getDataXYAsDouble(0);
		int fw = img.getWidth();
		double[] data = new double[w * h];
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				data[(x - x1) + (y - y1) * w] = fulldata[x + y * fw];
			}
		}

		return data;
	}

	/**
	 * Convolve intensity.
	 * 
	 * @param img
	 *            the img
	 * @return the double[]
	 */
	public double[] convolveIntensity(IcyBufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		double[] data = getIntensity(img);

		if (doBorders) {
			data = addBorders(data, w, h, op.getKernelHalfWidth(), op.getKernelHalfHeight());
		}

		return convolve(data, w, h);
	}
}
