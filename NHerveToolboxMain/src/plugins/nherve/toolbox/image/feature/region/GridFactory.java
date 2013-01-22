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
package plugins.nherve.toolbox.image.feature.region;

import java.util.ArrayList;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.IcySupportRegionFactory;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.mask.Mask;


/**
 * The Class GridFactory.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class GridFactory extends Algorithm implements IcySupportRegionFactory {
	
	/** The Constant ALGO_FIXED_SIZE. */
	public final static int ALGO_FIXED_SIZE = 1;
	
	/** The Constant ALGO_FIXED_NB_SQUARE. */
	public final static int ALGO_FIXED_NB_SQUARE = 2;
	
	/** The Constant ALGO_ON_ALL_PIXELS. */
	public final static int ALGO_ON_ALL_PIXELS = 3;
	
	/** The Constant ALGO_ONLY_PIXELS. */
	public final static int ALGO_ONLY_PIXELS = 4;

	/** The algorithm. */
	private int algorithm;
	
	/** The length. */
	private int length;
	
	/** The overlap. */
	private int overlap;
	
	/** The center. */
	private boolean center;

	/**
	 * Instantiates a new grid factory.
	 * 
	 * @param algo
	 *            the algo
	 */
	public GridFactory(int algo) {
		super();
		this.algorithm = algo;
		setCenter(false);
		setLength(1);
		setOverlap(0);
	}

	/**
	 * Extract regions all pixels.
	 * 
	 * @param img
	 *            the img
	 * @return the list
	 * @throws SupportRegionException
	 *             the support region exception
	 */
	private List<IcySupportRegion> extractRegionsAllPixels(Segmentable img) throws SupportRegionException {
		if (length <= 0) {
			throw new SupportRegionException("Invalid region length (" + length + ")");
		}

		int nbCol = img.getWidth();
		int nbRow = img.getHeight();

		ArrayList<IcySupportRegion> result = new ArrayList<IcySupportRegion>();

		int x = 0;
		int y = 0;
		for (int i = 0; i < nbCol; i++) {
			y = 0;
			for (int j = 0; j < nbRow; j++) {
				result.add(new RectangleSupportRegion(img, x, y, length));
				y += 1;
			}
			x += 1;
		}

		return result;
	}
	
	/**
	 * Extract all pixels.
	 * 
	 * @param img
	 *            the img
	 * @return the list
	 * @throws SupportRegionException
	 *             the support region exception
	 */
	private List<IcySupportRegion> extractAllPixels(Segmentable img) throws SupportRegionException {
		int nbCol = img.getWidth();
		int nbRow = img.getHeight();

		ArrayList<IcySupportRegion> result = new ArrayList<IcySupportRegion>();

		for (int i = 0; i < nbCol; i+=length) {
			for (int j = 0; j < nbRow; j+=length) {
				result.add(new IcyPixel(i, j));
			}
		}

		return result;
	}

	/**
	 * Extract regions fixed size.
	 * 
	 * @param img
	 *            the img
	 * @return the list
	 * @throws SupportRegionException
	 *             the support region exception
	 */
	private List<IcySupportRegion> extractRegionsFixedSize(Segmentable img) throws SupportRegionException {
		if (length <= 0) {
			throw new SupportRegionException("Invalid region length (" + length + ")");
		}
		
		if (overlap >= length) {
			throw new SupportRegionException("Invalid overlap (" + overlap + " / " + length + ")");
		}

		int uniqueLength = length - overlap;
		
		int nbCol = (int) Math.floor((float) img.getWidth() / (float) uniqueLength);
		int nbRow = (int) Math.floor((float) img.getHeight() / (float) uniqueLength);

		int xOffset = length / 2;
		int yOffset = length / 2;

		if (center) {
			xOffset += (img.getWidth() - nbCol * uniqueLength) / 2;
			yOffset += (img.getHeight() - nbRow * uniqueLength) / 2;
		}

		ArrayList<IcySupportRegion> result = new ArrayList<IcySupportRegion>();

		int x = xOffset;
		int y = yOffset;
		for (int i = 0; i < nbCol; i++) {
			y = yOffset;
			for (int j = 0; j < nbRow; j++) {
				result.add(new RectangleSupportRegion(img, x, y, length));
				y += uniqueLength;
			}
			x += uniqueLength;
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegionFactory#extractRegions(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public List<IcySupportRegion> extractRegions(Segmentable img) throws SupportRegionException {
		log("Launching regions extraction ...");
		List<IcySupportRegion> res = null;
		switch (algorithm) {
		case ALGO_FIXED_SIZE:
			res = extractRegionsFixedSize(img);
			break;
		case ALGO_ON_ALL_PIXELS:
			res = extractRegionsAllPixels(img);
			break;
		case ALGO_ONLY_PIXELS:
			res = extractAllPixels(img);
			break;
		case ALGO_FIXED_NB_SQUARE:
		default:
			throw new SupportRegionException("Algorithm(" + algorithm + ") not yet implemented");
		}
		log("... " + res.size() + " regions found");
		return res;
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public float getLength() {
		return length;
	}

	/**
	 * Sets the length.
	 * 
	 * @param length
	 *            the new length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Checks if is center.
	 * 
	 * @return true, if is center
	 */
	public boolean isCenter() {
		return center;
	}

	/**
	 * Sets the center.
	 * 
	 * @param center
	 *            the new center
	 */
	public void setCenter(boolean center) {
		this.center = center;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SupportRegionFactory#extractRegions(plugins.nherve.toolbox.image.feature.Segmentable, plugins.nherve.toolbox.image.mask.Mask)
	 */
	@Override
	public List<IcySupportRegion> extractRegions(Segmentable img, Mask mask) throws SupportRegionException {
		log("Launching regions extraction ...");
		List<IcySupportRegion> allRegions = extractRegions(img);
		List<IcySupportRegion> filteredRegions = new ArrayList<IcySupportRegion>();
		for (IcySupportRegion sr : allRegions) {
			if (sr.intersects(mask)) {
				filteredRegions.add(sr);
			}
		}
		log("... " + filteredRegions.size() + " regions found");
		return filteredRegions;
	}
	
	/**
	 * Gets the mask as pixels.
	 * 
	 * @param m
	 *            the m
	 * @return the mask as pixels
	 */
	public static List<IcyPixel> getMaskAsPixels(Mask m) {
		byte[] b = m.getBinaryData().getRawData();
		int w = m.getWidth();
		int h = m.getHeight();

		ArrayList<IcyPixel> result = new ArrayList<IcyPixel>();

		int idx = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (b[idx] == BinaryIcyBufferedImage.TRUE) {
					result.add(new IcyPixel(x, y));
				}
				idx++;
			}
		}

		return result;
	}
	
	public static List<IcyPixel> getAllPixels(Segmentable img) {
		int w = img.getWidth();
		int h = img.getHeight();

		ArrayList<IcyPixel> result = new ArrayList<IcyPixel>();

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				result.add(new IcyPixel(x, y));
			}
		}

		return result;
	}

	/**
	 * Gets the overlap.
	 * 
	 * @return the overlap
	 */
	public int getOverlap() {
		return overlap;
	}

	/**
	 * Sets the overlap.
	 * 
	 * @param overlap
	 *            the new overlap
	 */
	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}
}
