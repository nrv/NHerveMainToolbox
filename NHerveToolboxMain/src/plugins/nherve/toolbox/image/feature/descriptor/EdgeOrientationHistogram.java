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

import icy.image.IcyBufferedImage;

import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import plugins.nherve.toolbox.image.feature.ConvolutionKernel2D;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class EdgeOrientationHistogram.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class EdgeOrientationHistogram extends GlobalAndLocalDescriptor<SegmentableIcyBufferedImage, VectorSignature> {
	
	/** The Constant DEFAULT_DIMENSION. */
	public final static int DEFAULT_DIMENSION = 7;
	
	/** The Constant DEFAULT_HYST_HIGH. */
	public final static double DEFAULT_HYST_HIGH = 0.5;
	
	/** The Constant DEFAULT_HYST_LOW. */
	public final static double DEFAULT_HYST_LOW = 0.3;
	
	/** The Constant DEFAULT_SIGMA. */
	public final static double DEFAULT_SIGMA = 1.0;

	/** The bin centers. */
	private double[] binCenters;
	
	/** The bin step. */
	private double binStep;
	
	/** The do linear smoothing. */
	private boolean doLinearSmoothing;

	/** The cache image contour. */
	private Map<SegmentableIcyBufferedImage, boolean[]> cacheImageContour;
	
	/** The cache image gradient amplitude. */
	private Map<SegmentableIcyBufferedImage, double[]> cacheImageGradientAmplitude;
	
	/** The cache image gradient orientation. */
	private Map<SegmentableIcyBufferedImage, double[]> cacheImageGradientOrientation;

	/** The p dimension. */
	private int pDimension;
	
	/** The p hyst high. */
	private double pHystHigh;
	
	/** The p hyst low. */
	private double pHystLow;
	
	/** The p sigma. */
	private double pSigma;

	/**
	 * Instantiates a new edge orientation histogram.
	 * 
	 * @param doLinearSmoothing
	 *            the do linear smoothing
	 * @param display
	 *            the display
	 */
	public EdgeOrientationHistogram(boolean doLinearSmoothing, boolean display) {
		super(display);
		cacheImageContour = new HashMap<SegmentableIcyBufferedImage, boolean[]>();
		cacheImageGradientAmplitude = new HashMap<SegmentableIcyBufferedImage, double[]>();
		cacheImageGradientOrientation = new HashMap<SegmentableIcyBufferedImage, double[]>();
		this.doLinearSmoothing = doLinearSmoothing;

		setpDimension(DEFAULT_DIMENSION);
		setpHystHigh(DEFAULT_HYST_HIGH);
		setpHystLow(DEFAULT_HYST_LOW);
		setpSigma(DEFAULT_SIGMA);

		binCenters = null;
		binStep = 0;
	}

	/**
	 * Inits the.
	 */
	private synchronized void init() {
		if (doLinearSmoothing && (binCenters == null)) {
			binCenters = new double[getpDimension()];
			binStep = Math.PI / getpDimension();
			double current = binStep / 2.0;
			for (int d = 0; d < getpDimension(); d++) {
				binCenters[d] = current;
				current += binStep;
			}
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	public int getSignatureSize() {
		return getpDimension();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public VectorSignature extractLocalSignature(SegmentableIcyBufferedImage img, Shape shp) throws SignatureException {
		throw new RuntimeException("EdgeOrientationHistogram.extractSignature(SegmentableBufferedImage img, Shape shp) not implemented");
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public VectorSignature extractLocalSignature(SegmentableIcyBufferedImage img, SupportRegion<IcyPixel> reg) throws SignatureException {
		boolean[] currentImageContour = null;
		double[] currentImageGradientAmplitude = null;
		double[] currentImageGradientOrientation = null;

		synchronized (cacheImageContour) {
			currentImageContour = cacheImageContour.get(img);
			currentImageGradientAmplitude = cacheImageGradientAmplitude.get(img);
			currentImageGradientOrientation = cacheImageGradientOrientation.get(img);
		}

		if ((currentImageContour == null) || (currentImageGradientAmplitude == null) || (currentImageGradientOrientation == null)) {
			throw new SignatureException("Canny operator not launched for current image (" + img.getName() + ")");
		}

		init();

		VectorSignature sig = getEmptySignature();
		double angle = 0;
		int idx = 0;
		int adx = 0;
		int adxNext = 0;
		double adxCoef = 0;
		double adxNextCoef = 0;
		int w = img.getWidth();
		int h = img.getHeight();

		int x, y;
		for (IcyPixel p : reg) {
			x = (int)p.x;
			y = (int)p.y;
			idx = x + y * w;
			if ((x >= 0) && (x < w) && (y >= 0) && (y < h) && currentImageContour[idx]) {
				// angle in [0, 2PI[
				angle = currentImageGradientOrientation[idx];
				while (angle < 0.0) {
					angle += 2.0 * Math.PI;
				}
				while (angle >= 2.0 * Math.PI) {
					angle -= 2.0 * Math.PI;
				}
				// angle in [0, PI[
				if (angle >= Math.PI) {
					angle -= Math.PI;
				}
				// angle in [0, 1[
				adx = (int) Math.floor(angle * getSignatureSize() / Math.PI);
				if (doLinearSmoothing) {
					adxCoef = angle - binCenters[adx];
					if (adxCoef > 0) {
						adxNext = adx + 1;
						if (adxNext >= getSignatureSize()) {
							adxNext = 0;
						}
					} else if (adxCoef < 0) {
						adxNext = adx - 1;
						if (adxNext < 0) {
							adxNext = getSignatureSize() - 1;
						}
					} else {
						sig.addTo(adx, currentImageGradientAmplitude[idx]);
						continue;
					}
					adxCoef = 1.0 - Math.abs(adxCoef / binStep);
					adxNextCoef = 1.0 - adxCoef;

					sig.addTo(adx, adxCoef * currentImageGradientAmplitude[idx]);
					sig.addTo(adxNext, adxNextCoef * currentImageGradientAmplitude[idx]);
				} else {
					sig.addTo(adx, currentImageGradientAmplitude[idx]);
				}
			}
		}

		sig.normalizeSumToOne(true);

		return sig;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(SegmentableIcyBufferedImage img) throws SignatureException {
		synchronized (cacheImageContour) {
			// log("postProcess(" + img.getName() + ") : " +
			// cacheImageContour.size());
			cacheImageContour.remove(img);
			cacheImageGradientAmplitude.remove(img);
			cacheImageGradientOrientation.remove(img);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(SegmentableIcyBufferedImage img) throws SignatureException {
		// log("preProcess(" + img.getName() + ") : " +
		// cacheImageContour.size());
		IcyBufferedImage bimg = img.getImage();
		int w = img.getWidth();
		int h = img.getHeight();
		int size = w * h;

		boolean[] currentImageContour = new boolean[size];
		double[] currentImageGradientAmplitude = new double[size];
		double[] currentImageGradientOrientation = new double[size];

		double[] data = ConvolutionKernel2D.getIntensity(bimg);

		ConvolutionKernel2D.canny(data, w, h, getpSigma(), getpHystLow(), getpHystHigh(), currentImageContour, currentImageGradientAmplitude, currentImageGradientOrientation);

		synchronized (cacheImageContour) {
			cacheImageContour.put(img, currentImageContour);
			cacheImageGradientAmplitude.put(img, currentImageGradientAmplitude);
			cacheImageGradientOrientation.put(img, currentImageGradientOrientation);
		}
	}

	/**
	 * Gets the p dimension.
	 * 
	 * @return the p dimension
	 */
	public int getpDimension() {
		return pDimension;
	}

	/**
	 * Sets the p dimension.
	 * 
	 * @param pDimension
	 *            the new p dimension
	 */
	public void setpDimension(int pDimension) {
		this.pDimension = pDimension;
	}

	/**
	 * Gets the p hyst high.
	 * 
	 * @return the p hyst high
	 */
	public double getpHystHigh() {
		return pHystHigh;
	}

	/**
	 * Sets the p hyst high.
	 * 
	 * @param pHystHigh
	 *            the new p hyst high
	 */
	public void setpHystHigh(double pHystHigh) {
		this.pHystHigh = pHystHigh;
	}

	/**
	 * Gets the p hyst low.
	 * 
	 * @return the p hyst low
	 */
	public double getpHystLow() {
		return pHystLow;
	}

	/**
	 * Sets the p hyst low.
	 * 
	 * @param pHystLow
	 *            the new p hyst low
	 */
	public void setpHystLow(double pHystLow) {
		this.pHystLow = pHystLow;
	}

	/**
	 * Gets the p sigma.
	 * 
	 * @return the p sigma
	 */
	public double getpSigma() {
		return pSigma;
	}

	/**
	 * Sets the p sigma.
	 * 
	 * @param pSigma
	 *            the new p sigma
	 */
	public void setpSigma(double pSigma) {
		this.pSigma = pSigma;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "EdgeOrientationHistogram";
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return true;
	}

}
