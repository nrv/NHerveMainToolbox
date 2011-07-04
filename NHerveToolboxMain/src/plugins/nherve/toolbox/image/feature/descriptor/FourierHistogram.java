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

import java.awt.Rectangle;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import plugins.nherve.toolbox.image.feature.SegmentableBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.FullImageSupportRegion;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.region.RectangleSupportRegion;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

/**
 * The Class FourierHistogram.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class FourierHistogram extends GlobalAndLocalDescriptor<SegmentableBufferedImage, VectorSignature> {
	
	/** The Constant df. */
	private final static DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	/** The wdw new window size. */
	private int wdwNewWindowSize;
	
	/** The wdw new half window size. */
	private int wdwNewHalfWindowSize;
	
	/** The wdw radius. */
	private double[] wdwRadius;

	/** The nb disks. */
	private int nbDisks;
	
	/** The nb wedges. */
	private int nbWedges;
	
	/** The disks have same surface. */
	private boolean disksHaveSameSurface;
	
	/** The canal. */
	private int canal;

	/** The cache gray. */
	private Map<SegmentableBufferedImage, IcyBufferedImage> cacheGray;

	/**
	 * Instantiates a new fourier histogram.
	 * 
	 * @param nbDisks
	 *            the nb disks
	 * @param nbWedges
	 *            the nb wedges
	 * @param disksHaveSameSurface
	 *            the disks have same surface
	 * @param display
	 *            the display
	 * @param canal
	 *            the canal
	 */
	public FourierHistogram(int nbDisks, int nbWedges, boolean disksHaveSameSurface, boolean display, int canal) {
		super(display);

		cacheGray = new HashMap<SegmentableBufferedImage, IcyBufferedImage>();

		this.nbDisks = nbDisks;
		this.nbWedges = nbWedges;
		this.canal = canal;
		this.disksHaveSameSurface = disksHaveSameSurface;
	}

	/**
	 * Instantiates a new fourier histogram.
	 * 
	 * @param windowSize
	 *            the window size
	 * @param nbDisks
	 *            the nb disks
	 * @param nbWedges
	 *            the nb wedges
	 * @param disksHaveSameSurface
	 *            the disks have same surface
	 * @param display
	 *            the display
	 * @param canal
	 *            the canal
	 */
	public FourierHistogram(int windowSize, int nbDisks, int nbWedges, boolean disksHaveSameSurface, boolean display, int canal) {
		this(nbDisks, nbWedges, disksHaveSameSurface, display, canal);

		wdwNewWindowSize = closestBiggerPow2(windowSize);
		wdwNewHalfWindowSize = wdwNewWindowSize / 2;

		wdwRadius = getRadiuses(wdwNewHalfWindowSize);

		log("FourierHistogram(w = " + windowSize + ", s = " + wdwNewWindowSize + ", nw = " + wdwNewHalfWindowSize + ") " + asString(wdwRadius));
	}

	/**
	 * As string.
	 * 
	 * @param radius
	 *            the radius
	 * @return the string
	 */
	private String asString(double[] radius) {
		String radiusString = "[" + df.format(radius[0]);
		for (int i = 1; i <= nbDisks; i++) {
			radiusString += " | " + df.format(radius[i]);
		}
		return radiusString;
	}

	/**
	 * Closest bigger pow2.
	 * 
	 * @param ws
	 *            the ws
	 * @return the int
	 */
	private int closestBiggerPow2(int ws) {
		return (int) Math.pow(2, Math.ceil(Math.log(ws) / Math.log(2)));
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, java.awt.Shape)
	 */
	@Override
	public VectorSignature extractLocalSignature(SegmentableBufferedImage img, Shape shp) throws SignatureException {
		throw new SignatureException("Not implemented yet");
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor#extractLocalSignature(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.feature.SupportRegion)
	 */
	@Override
	public VectorSignature extractLocalSignature(SegmentableBufferedImage img, SupportRegion reg) throws SignatureException {
		IcyBufferedImage gray = null;

		synchronized (cacheGray) {
			gray = cacheGray.get(img);
		}

		if (gray == null) {
			throw new SignatureException("PreProcess not launched for current image (" + img.getName() + ")");
		}

		int grayW = gray.getWidth();
		int grayH = gray.getHeight();

		double[] gd = gray.getDataXYAsDouble(0);
		VectorSignature sig = getEmptySignature();

		int nws = 0;
		int hws = 0;
		double[] rds;

		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;

		if (reg instanceof FullImageSupportRegion) {
			int ws = (int) Math.max(grayW, grayH);
			nws = closestBiggerPow2(ws);
			hws = nws / 2;
			rds = getRadiuses(hws);

			x1 = 0;
			x2 = nws;
			y1 = 0;
			y2 = nws;

			// log("FourierHistogram(ws = " + ws + ", nws = " + nws + ", hws = "
			// + hws + ") " + asString(rds));
		} else if (reg instanceof RectangleSupportRegion) {
			RectangleSupportRegion r = (RectangleSupportRegion) reg;
			Rectangle rect = r.getBounds();
			nws = Math.max(rect.width, rect.height);
			hws = nws / 2;
			rds = getRadiuses(hws);
			x1 = rect.x;
			x2 = x1 + nws;
			y1 = rect.y;
			y2 = y1 + nws;
		} else {
			Pixel px = reg.getCenter();
			int cx = (int) px.x;
			int cy = (int) px.y;

			nws = wdwNewWindowSize;
			hws = wdwNewHalfWindowSize;
			rds = wdwRadius;

			x1 = cx - hws;
			x2 = cx + hws;
			y1 = cy - hws;
			y2 = cy + hws;
		}

		int nbCols = 2 * nws;
		int nbRows = nws;

		double[][] data = new double[nbRows][nbCols];

		if (reg instanceof FullImageSupportRegion) {
			for (int i = 0; i < nbRows; i++) {
				Arrays.fill(data[i], 0);
			}
			for (int x = 0; x < grayW; x++) {
				for (int y = 0; y < grayH; y++) {
					data[y][2 * x] = gd[x + grayW * y];
					data[y][2 * x + 1] = 0;
				}
			}
		} else {
			int j = 0;
			for (int x = x1; x < x2; x++) {
				int lx = x;
				if (lx < 0) {
					lx = Math.abs(lx);
				} else if (lx >= grayW) {
					lx -= 2 * (lx - grayW + 1);
				}
				int i = 0;
				for (int y = y1; y < y2; y++) {
					int ly = y;
					if (ly < 0) {
						ly = Math.abs(ly);
					} else if (ly >= grayH) {
						ly -= 2 * (ly - grayH + 1);
					}
					data[i][2 * j] = gd[lx + grayW * ly];
					data[i][2 * j + 1] = 0;
					i++;
				}
				j++;
			}
		}

		DoubleFFT_2D fft = new DoubleFFT_2D(nws, nws);
		fft.complexForward(data);

		double[][] amp = new double[nws][nws];
		for (int i = 0; i < nws; i++) {
			for (int j = 0; j < nws; j++) {
				amp[i][j] = Math.sqrt(data[i][2 * j] * data[i][2 * j] + data[i][2 * j + 1] * data[i][2 * j + 1]);
			}
		}

		if (nbDisks > 0) {
			for (int i = 0; i <= hws; i++) {
				for (int j = 0; j <= hws; j++) {
					double distToOrigin = Math.sqrt(i * i + j * j);
					for (int nd = 0; nd < nbDisks; nd++) {
						if ((rds[nd] <= distToOrigin) && (distToOrigin < rds[nd + 1])) {
							sig.addTo(nd, amp[i][j]);
							if ((i > 0) && (i < hws)) {
								sig.addTo(nd, amp[nws - i][j]);
							}
						}
					}
				}
			}
			double nrm = 0;
			for (int nd = 0; nd < nbDisks; nd++) {
				nrm += sig.get(nd);
			}
			if (nbWedges > 0) {
				nrm *= 2;
			}
			if (nrm != 0) {
				nrm = 1 / nrm;
				for (int nd = 0; nd < nbDisks; nd++) {
					sig.multiply(nd, nrm);
				}
			}
		}

		if (nbWedges > 0) {
			double x, y, a;
			int idx;
			for (int i = 0; i < nws; i++) {
				if (i > hws) {
					x = i - nws;
				} else {
					x = i;
				}
				for (int j = 0; j <= hws; j++) {
					if ((i != 0) && (j != 0)) {
						y = j;
						a = Math.atan2(y, x);
						if (a == Math.PI) {
							a -= 1e-6;
						}
						idx = nbDisks + (int) Math.floor(a * nbWedges / Math.PI);
						sig.addTo(idx, amp[i][j]);
					}
				}
			}
			double nrm = 0;
			for (int nd = nbDisks; nd < getSignatureSize(); nd++) {
				nrm += sig.get(nd);
			}
			if (nbDisks > 0) {
				nrm *= 2;
			}
			if (nrm != 0) {
				nrm = 1 / nrm;
				for (int nd = nbDisks; nd < getSignatureSize(); nd++) {
					sig.multiply(nd, nrm);
				}
			}
		}

		// log(sig.toString());
		
		return sig;
	}

	/**
	 * Gets the radiuses.
	 * 
	 * @param hws
	 *            the hws
	 * @return the radiuses
	 */
	private double[] getRadiuses(int hws) {
		double[] radius = new double[nbDisks + 1];
		radius[0] = 0;

		if (disksHaveSameSurface) {
			double radius1 = (double) hws / Math.sqrt(nbDisks);
			for (int i = 1; i <= nbDisks; i++) {
				radius[i] = Math.sqrt(i) * radius1;
			}
		} else {
			double radius1 = (double) hws / (double) nbDisks;
			for (int i = 1; i <= nbDisks; i++) {
				radius[i] = i * radius1;
			}
		}

		return radius;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return nbDisks + nbWedges;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(SegmentableBufferedImage img) throws SignatureException {
		synchronized (cacheGray) {
			cacheGray.remove(img);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(SegmentableBufferedImage img) throws SignatureException {
		IcyBufferedImage gray = SomeImageTools.computeGrayScale(img.getImage(), canal, 1);
		synchronized (cacheGray) {
			cacheGray.put(img, gray);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "FourierHistogram";
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return true;
	}

}
