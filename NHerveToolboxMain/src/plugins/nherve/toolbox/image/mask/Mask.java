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
package plugins.nherve.toolbox.image.mask;

import icy.image.IcyBufferedImage;
import icy.roi.ROI2D;
import icy.roi.ROI2DArea;
import icy.sequence.Sequence;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;
import plugins.nherve.toolbox.image.toolboxes.MorphologyToolbox;




/**
 * The Class Mask.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class Mask implements Serializable, Iterable<String> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8351680455776644549L;
	
	/**
	 * Gets the surface.
	 * 
	 * @param ibi
	 *            the ibi
	 * @return the surface
	 */
	public static int getSurface(BinaryIcyBufferedImage ibi) {
		int s = 0;
		byte[] raw = ibi.getRawData();
		for (int d = 0; d < raw.length; d++) {
			if (raw[d] == BinaryIcyBufferedImage.TRUE) {
				s++;
			}
		}
		return s;
	}
	
	/** The need redraw. */
	private transient boolean needRedraw;
	
	/** The raw binary data. */
	private transient byte[] rawBinaryData;
	
	/** The cache. */
	private transient BufferedImage cache;
	
	/** The draw only contours. */
	private transient boolean drawOnlyContours;
	
	/** The binary data. */
	private BinaryIcyBufferedImage binaryData;
	
	/** The color. */
	private Color color;
	
	/** The height. */
	private int height;
	
	/** The id. */
	private int id;
	
	/** The label. */
	private String label;
	
	/** The need automatic label. */
	private boolean needAutomaticLabel;
	
	/** The opacity. */
	private float opacity;
	
	/** The visible layer. */
	private boolean visibleLayer;
	
	/** The width. */
	private int width;

	/** The tags. */
	private Set<String> tags;
	
	/**
	 * Instantiates a new mask.
	 */
	private Mask() {
		super();
		visibleLayer = true;
		setColor(Color.WHITE);
		setOpacity(1.0f);
		setNeedAutomaticLabel(false);
		tags = new HashSet<String>();
		forceRedraw();
	}

	/**
	 * Instantiates a new mask.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public Mask(int width, int height) {
		this(width, height, false);
	}

	/**
	 * Instantiates a new mask.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param defaultValue
	 *            the default value
	 */
	public Mask(int width, int height, boolean defaultValue) {
		this();
		this.width = width;
		this.height = height;
		setBinaryData(new BinaryIcyBufferedImage(width, height, defaultValue));
	}

	/**
	 * Instantiates a new mask.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param label
	 *            the label
	 * @param defaultValue
	 *            the default value
	 */
	public Mask(int width, int height, String label, boolean defaultValue) {
		this(width, height, defaultValue);
		this.label = label;
	}
	
	public static Mask copy(Mask m) throws MaskException {
		Mask r = new Mask(m.getWidth(), m.getHeight());
		if (m.hasBinaryData()) {
			r.setBinaryData(m.getBinaryData().getCopy());
		} else {
			throw new MaskException("No internal mask representation available for " + m);
		}
		return r;
	}

	/**
	 * Adds the.
	 * 
	 * @param rhs
	 *            the rhs
	 * @throws MaskException
	 *             the mask exception
	 */
	public void add(Area rhs) throws MaskException {
		if (hasBinaryData()) {
			manageArea(rhs, BinaryIcyBufferedImage.TRUE);
		} else {
			throw new MaskException("No internal mask representation available");
		}
		forceRedraw();
	}
	
	/**
	 * Adds the.
	 * 
	 * @param m
	 *            the m
	 * @throws MaskException
	 *             the mask exception
	 */
	public void add(Mask m) throws MaskException {
		if (hasBinaryData() && m.hasBinaryData()) {
			binaryData.add(m.getBinaryData());
		} else {
			throw new MaskException("No internal mask representation available");
		}
		forceRedraw();
	}

	public void add(ROI2D roi) throws MaskException {
		if (hasBinaryData()) {
			manageROI(roi, BinaryIcyBufferedImage.TRUE);
		} else {
			throw new MaskException("No internal mask representation available");
		}
		forceRedraw();
	}

	/**
	 * Adds the tag.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return true, if successful
	 */
	public boolean addTag(String arg0) {
		return tags.add(arg0);
	}

	/**
	 * As ro i2 d area.
	 * 
	 * @param seq
	 *            the seq
	 * @return the rO i2 d area
	 */
	public ROI2DArea asROI2DArea(Sequence seq) {
		return binaryData.asROI2DArea(seq);
	}

	/**
	 * Clear tags.
	 */
	public void clearTags() {
		tags.clear();
	}

	/**
	 * Contains.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	public boolean contains(int x, int y) {
		return binaryData.contains(x, y);
	}

	/**
	 * Contains.
	 * 
	 * @param px
	 *            the px
	 * @return true, if successful
	 */
	public boolean contains(Pixel px) {
		return binaryData.contains(px);
	}

	/**
	 * Contains.
	 * 
	 * @param shp
	 *            the shp
	 * @return true, if successful
	 */
	public boolean contains(Shape shp) {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * Contains tag.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return true, if successful
	 */
	public boolean containsTag(String arg0) {
		return tags.contains(arg0);
	}

	/**
	 * Creates the cache.
	 * 
	 * @param c
	 *            the c
	 * @return the buffered image
	 */
	public BufferedImage createCache(Color c) {
		return createCache(c.getRGB());
	}

	/**
	 * Creates the cache.
	 * 
	 * @param rgb
	 *            the rgb
	 * @return the buffered image
	 */
	public BufferedImage createCache(int rgb) {
		BufferedImage localCache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fillCache(rgb, localCache);
		return localCache;
	}
	
	/**
	 * Dilate.
	 * 
	 * @throws MaskException
	 *             the mask exception
	 */
	public void dilate() throws MaskException {
		if (hasBinaryData()) {
			binaryData.dilate();
			forceRedraw();
		} else {
			throw new MaskException("No internal mask representation available");
		}
	}
	
	/**
	 * Erode.
	 * 
	 * @throws MaskException
	 *             the mask exception
	 */
	public void erode() throws MaskException {
		if (hasBinaryData()) {
			binaryData.erode();
			forceRedraw();
		} else {
			throw new MaskException("No internal mask representation available");
		}
	}

	private void fillCache(Color c, BufferedImage localCache) {
		fillCache(c.getRGB(), localCache);
	}

	private void fillCache(int rgb, BufferedImage localCache) {
		int[] localCacheData = ((DataBufferInt) localCache.getRaster().getDataBuffer()).getData();
		Arrays.fill(localCacheData, 0);
		
		if (hasBinaryData()) {
			BinaryIcyBufferedImage bin = binaryData;
			if (drawOnlyContours) {
				bin = MorphologyToolbox.computeBorder(binaryData);
			}

			final int limit = localCacheData.length;
			final byte[] data = bin.getRawData();
			for (int i = 0; i < limit; i++) {
				if (data[i] == BinaryIcyBufferedImage.TRUE) {
					localCacheData[i] = rgb;
				}
			}
		}
	}

	/**
	 * Fill hole.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @throws MaskException
	 *             the mask exception
	 */
	public void fillHole(int x, int y) throws MaskException {
		if (hasBinaryData()) {
			try {
				binaryData.fillHole(x, y);
			} catch (StackOverflowError e1) {
				System.err.println(e1.getClass().getName() + " : hole is too big for me !");
			}
			forceRedraw();
		} else {
			throw new MaskException("Operation only available");
		}
	}

	/**
	 * Fill holes.
	 * 
	 * @throws MaskException
	 *             the mask exception
	 */
	public void fillHoles() throws MaskException {
		if (hasBinaryData()) {
			binaryData.fillHoles();
			forceRedraw();
		} else {
			throw new MaskException("No internal mask representation available");
		}
	}
	
	public void fill(boolean value) {
		Arrays.fill(binaryData.getRawData(), value ? BinaryIcyBufferedImage.TRUE : BinaryIcyBufferedImage.FALSE);
	}

	/**
	 * Filter size.
	 * 
	 * @param size
	 *            the size
	 * @throws MaskException
	 *             the mask exception
	 */
	public void filterSize(int size) throws MaskException {
		if (hasBinaryData()) {
			binaryData.filterSize(size);
			forceRedraw();
		} else {
			throw new MaskException("Operation only available");
		}
	}

	/**
	 * Force redraw.
	 */
	public void forceRedraw() {
		needRedraw = true;
	}

	/**
	 * Gets the average color.
	 * 
	 * @param img
	 *            the img
	 * @return the average color
	 */
	public Color getAverageColor(IcyBufferedImage img) {
		double[] col = getAverageColor(img, ColorSpaceTools.RGB);

		double r = col[0] / 255d;
		double g = col[1] / 255d;
		double b = col[2] / 255d;

		return new Color((float) r, (float) g, (float) b);
	}

	/**
	 * Gets the average color.
	 * 
	 * @param img
	 *            the img
	 * @param colorSpace
	 *            the color space
	 * @return the average color
	 */
	public double[] getAverageColor(IcyBufferedImage img, int colorSpace) {
		double[] c = new double[ColorSpaceTools.NB_COLOR_CHANNELS];
		Arrays.fill(c, 0);
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (rawBinaryData[i] == BinaryIcyBufferedImage.TRUE) {
					double[] l = ColorSpaceTools.getColorComponentsD_0_255(img, colorSpace, x, y);
					for (int d = 0; d < ColorSpaceTools.NB_COLOR_CHANNELS; d++) {
						c[d] += l[d];
					}
				}
				i++;
			}
		}
		double nrm = getSurface();
		if (nrm == 0) {
			// System.out.println("Empty mask : " + getLabel());
		} else {
			for (int d = 0; d < ColorSpaceTools.NB_COLOR_CHANNELS; d++) {
				c[d] /= nrm;
			}
		}
		return c;
	}

	/**
	 * Gets the binary data.
	 * 
	 * @return the binary data
	 */
	public BinaryIcyBufferedImage getBinaryData() {
		return binaryData;
	}

	/**
	 * Gets the color.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the nb tags.
	 * 
	 * @return the nb tags
	 */
	public int getNbTags() {
		return tags.size();
	}

	/**
	 * Gets the opacity.
	 * 
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Gets the surface.
	 * 
	 * @return the surface
	 */
	public int getSurface() {
		return getSurface(getBinaryData());
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Checks for binary data.
	 * 
	 * @return true, if successful
	 */
	public boolean hasBinaryData() {
		return (binaryData != null) && (rawBinaryData != null);
	}

	/**
	 * Intersects.
	 * 
	 * @param shp
	 *            the shp
	 * @return true, if successful
	 */
	public boolean intersects(Shape shp) {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * Invert.
	 * 
	 * @throws MaskException
	 *             the mask exception
	 */
	public void invert() throws MaskException {
		if (hasBinaryData()) {
			binaryData.invert();
			forceRedraw();
		} else {
			throw new MaskException("Operation only available for binary masks");
		}
	}

	/**
	 * Checks if is draw only contours.
	 * 
	 * @return true, if is draw only contours
	 */
	public boolean isDrawOnlyContours() {
		return drawOnlyContours;
	}

	/**
	 * Checks if is need automatic label.
	 * 
	 * @return true, if is need automatic label
	 */
	public boolean isNeedAutomaticLabel() {
		return needAutomaticLabel;
	}

	/**
	 * Checks if is visible layer.
	 * 
	 * @return true, if is visible layer
	 */
	public boolean isVisibleLayer() {
		return visibleLayer;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		return tags.iterator();
	}
	
	/**
	 * Manage area.
	 * 
	 * @param rhs
	 *            the rhs
	 * @param val
	 *            the val
	 */
	private void manageArea(Area rhs, byte val) {
		Rectangle r = rhs.getBounds();
		int x1 = (int) Math.max(Math.floor(r.getMinX()), 0);
		int x2 = (int) Math.min(x1 + Math.ceil(r.getWidth()), width);
		int y1 = (int) Math.max(Math.floor(r.getMinY()), 0);
		int y2 = (int) Math.min(y1 + Math.ceil(r.getHeight()), height);
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				if (rhs.contains(x, y)) {
					rawBinaryData[x + width * y] = val;
				}
			}
		}
	}
	
	private void manageROI(ROI2D roi, byte val) {
		Rectangle r = roi.getBounds();
		int x1 = (int) Math.max(Math.floor(r.getMinX()), 0);
		int x2 = (int) Math.min(x1 + Math.ceil(r.getWidth()), width);
		int y1 = (int) Math.max(Math.floor(r.getMinY()), 0);
		int y2 = (int) Math.min(y1 + Math.ceil(r.getHeight()), height);
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				if (roi.contains(x, y)) {
					rawBinaryData[x + width * y] = val;
				}
			}
		}
	}
	
	/**
	 * Paint.
	 * 
	 * @param g
	 *            the g
	 */
	public void paint(Graphics2D g) {
		if (needRedraw) {
			if (cache == null) {
				cache = createCache(getColor());
			} else {
				fillCache(getColor(), cache);
			}
			needRedraw = false;
		}
		Composite bck = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));
		g.drawImage(cache, 0, 0, null);
		g.setComposite(bck);
	}

	/**
	 * Removes the.
	 * 
	 * @param rhs
	 *            the rhs
	 * @throws MaskException
	 *             the mask exception
	 */
	public void remove(Area rhs) throws MaskException {
		if (hasBinaryData()) {
			manageArea(rhs, BinaryIcyBufferedImage.FALSE);
		} else {
			throw new MaskException("No internal mask representation available");
		}
		forceRedraw();
	}

	/**
	 * Removes the.
	 * 
	 * @param m
	 *            the m
	 * @throws MaskException
	 *             the mask exception
	 */
	public void remove(Mask m) throws MaskException {
		if (hasBinaryData() && m.hasBinaryData()) {
			binaryData.remove(m.getBinaryData());
		} else {
			throw new MaskException("No internal mask representation available");
		}
		forceRedraw();
	}

	public void remove(ROI2D roi) throws MaskException {
		if (hasBinaryData()) {
			manageROI(roi, BinaryIcyBufferedImage.FALSE);
		} else {
			throw new MaskException("No internal mask representation available");
		}
		forceRedraw();
	}

	/**
	 * Removes the tag.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return true, if successful
	 */
	public boolean removeTag(String arg0) {
		return tags.remove(arg0);
	}

	/**
	 * Sets the binary data.
	 * 
	 * @param data
	 *            the new binary data
	 */
	public void setBinaryData(BinaryIcyBufferedImage data) {
		this.binaryData = data;
		this.rawBinaryData = data.getRawData();
		forceRedraw();
	}

	/**
	 * Sets the color.
	 * 
	 * @param color
	 *            the new color
	 */
	public void setColor(Color color) {
		this.color = color;
		forceRedraw();
	}

	/**
	 * Sets the draw only contours.
	 * 
	 * @param drawOnlyContours
	 *            the new draw only contours
	 */
	public void setDrawOnlyContours(boolean drawOnlyContours) {
		this.drawOnlyContours = drawOnlyContours;
		forceRedraw();
	}

	/**
	 * Sets the height.
	 * 
	 * @param height
	 *            the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Sets the need automatic label.
	 * 
	 * @param needAutomaticLabel
	 *            the new need automatic label
	 */
	public void setNeedAutomaticLabel(boolean needAutomaticLabel) {
		this.needAutomaticLabel = needAutomaticLabel;
	}

	/**
	 * Sets the opacity.
	 * 
	 * @param opacity
	 *            the new opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	/**
	 * Sets the visible layer.
	 * 
	 * @param visibleLayer
	 *            the new visible layer
	 */
	public void setVisibleLayer(boolean visibleLayer) {
		this.visibleLayer = visibleLayer;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String strTags = "";
		if (getNbTags() > 0) {
			strTags += " (";
			boolean first = true;
			for (String t : this) {
				if (first) {
					first = false;
				} else {
					strTags += ", ";
				}
				strTags += t;
			}
			strTags += ")";
		}
		return getId() + " - " + getLabel() + strTags;
	}

}
