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
package plugins.nherve.toolbox.image;

import icy.image.IcyBufferedImage;
import icy.roi.ROI2DArea;
import icy.sequence.Sequence;
import icy.type.TypeUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import javax.vecmath.Point3i;

import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.segmentation.Segmentation;
import plugins.nherve.toolbox.image.toolboxes.MorphologyToolbox;
import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

/**
 * The Class BinaryIcyBufferedImage.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class BinaryIcyBufferedImage extends IcyBufferedImage {
	
	/** The Constant FALSE. */
	public static final byte FALSE = (byte) 0;
	
	/** The Constant TRUE. */
	public static final byte TRUE = (byte) 255;
	
	/** The raw binary data. */
	private transient byte[] rawBinaryData;

	/**
	 * Instantiates a new binary icy buffered image.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public BinaryIcyBufferedImage(int width, int height) {
		this(width, height, false);
	}
	
	public BinaryIcyBufferedImage(int width, int height, boolean value) {
		super(width, height, 1, TypeUtil.TYPE_BYTE);
		rawBinaryData = getRawData();
		Arrays.fill(rawBinaryData, value ? TRUE : FALSE);
	}
	
	/**
	 * Union.
	 * 
	 * @param m1
	 *            the m1
	 * @param m2
	 *            the m2
	 * @return the double
	 */
	public static double union(IcyBufferedImage m1, IcyBufferedImage m2) {
		double i = 0;
		byte[] raw1 = m1.getDataXYAsByte(0);
		byte[] raw2 = m2.getDataXYAsByte(0);

		for (int idx = 0; idx < raw1.length; idx++) {
			if ((raw1[idx] == BinaryIcyBufferedImage.TRUE) || (raw2[idx] == BinaryIcyBufferedImage.TRUE)) {
				i++;
			}
		}

		return i;
	}
	
	/**
	 * Intersection.
	 * 
	 * @param m1
	 *            the m1
	 * @param m2
	 *            the m2
	 * @return the double
	 */
	public static double intersection(BinaryIcyBufferedImage m1, BinaryIcyBufferedImage m2) {
		double i = 0;
		byte[] raw1 = m1.getRawData();
		byte[] raw2 = m2.getRawData();
		
		for (int idx = 0; idx < raw1.length; idx++) {
			if ((raw1[idx] == BinaryIcyBufferedImage.TRUE) && (raw2[idx] == BinaryIcyBufferedImage.TRUE)) {
				i++;
			}
		}

		return i;
	}
	
	/**
	 * As segmentation.
	 * 
	 * @param label
	 *            the label
	 * @return the segmentation
	 * @throws MaskException
	 *             the mask exception
	 */
	public Segmentation asSegmentation(String label) throws MaskException {
		int w = getWidth();
		int h = getHeight();
		
		Segmentation seg = new Segmentation(w, h);
		
		List<My2DConnectedComponent> ccs = SomeImageTools.findConnectedComponents(this);
		
		int idx = 1;
		for (My2DConnectedComponent cc : ccs) {
			Mask sm = seg.createNewMask(label + " " + idx, false, Color.BLACK, 1);
			sm.setBinaryData(cc.asBinaryImage(w, h, 0, 0));
			idx++;
		}
		
		DifferentColorsMap colorMap = new DifferentColorsMap(seg.size());
		for (Mask m : seg) {
			m.setColor(colorMap.get(m.getId()));
		}
		
		seg.createBackgroundMask("Background", Color.BLACK);
		
		seg.createIndex();
		
		return seg;
	}
	
	public IcyBufferedImage asIcyBufferedImage(int nbc, boolean toWhite) {
		Color bgc = Color.WHITE;
		Color fgc = Color.BLACK;
		
		if (toWhite) {
			fgc = Color.WHITE;
			bgc = Color.BLACK;
		}
		
		int w = getWidth();
		int h = getHeight();
		byte[] raw = getRawData();
		IcyBufferedImage toSave = new IcyBufferedImage(w, h, nbc, TypeUtil.TYPE_BYTE);
		Graphics2D binGraphics = toSave.createGraphics();
		binGraphics.setColor(bgc);
		binGraphics.fillRect(0, 0, w, h);
		binGraphics.setColor(fgc);
		int idx = 0;
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				if (raw[idx] == BinaryIcyBufferedImage.TRUE) {
					binGraphics.fillRect(i, j, 1, 1);
				}
				idx++;
			}
		}
		return toSave;
	}
	
	/**
	 * As ro i2 d area.
	 * 
	 * @param seq
	 *            the seq
	 * @return the rO i2 d area
	 */
	public ROI2DArea asROI2DArea(Sequence seq) {
		if (getSurface() == 0) {
			return null;
		}
		
		ROI2DArea asa = new ROI2DArea(new Point2D.Float(0, 0));
		
		int w = getWidth();
		int h = getHeight();
		int minX = w;
		int maxX = 0;
		int minY = h;
		int maxY = 0;
		
		int idx = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (rawBinaryData[idx] == TRUE) {
					if (x < minX) {
						minX = x;
					}
					if (x > maxX) {
						maxX = x;
					}
					if (y < minY) {
						minY = y;
					}
					if (y > maxY) {
						maxY = y;
					}
				}
				idx++;
			}
		}
		
		int bbw = maxX - minX + 1;
		int bbh = maxY - minY + 1;
		
		Rectangle bbx = new Rectangle(minX, minY, bbw, bbh);
		
		boolean[] data = new boolean[bbw * bbh];
		Arrays.fill(data, false);
		
		for (int yo = 0; yo < bbh; yo++) {
			for (int xo = 0; xo < bbw; xo++) {
				if (rawBinaryData[xo + minX + (yo + minY) * w] == TRUE) {
					data[xo + yo * bbw] = true;
				}
			}
		}
		
		asa.setAsBooleanMask(bbx, data);
		asa.attachTo(seq);
		return asa;
	}

	/**
	 * Gets the raw data.
	 * 
	 * @return the raw data
	 */
	public byte[] getRawData() {
		return getDataXYAsByte(0);
	}
	
	/**
	 * Sets the raw data.
	 * 
	 * @param data
	 *            the new raw data
	 */
	public void setRawData(byte[] data) {
		setDataXYAsByte(0, data);
	}

	/**
	 * Gets the surface.
	 * 
	 * @return the surface
	 */
	public int getSurface() {
		int s = 0;
		for (byte b : rawBinaryData) {
			if (b == TRUE) {
				s++;
			}
		}
		return s;
	}

	/**
	 * Adds the.
	 * 
	 * @param other
	 *            the other
	 */
	public void add(BinaryIcyBufferedImage other) {
		for (int d = 0; d < rawBinaryData.length; d++) {
			if (other.rawBinaryData[d] == TRUE) {
				rawBinaryData[d] = TRUE;
			}
		}
	}
	
	/**
	 * Adds the.
	 * 
	 * @param shape
	 *            the shape
	 */
	public void add(Shape shape) {
		manageShape(shape, TRUE);
	}
	
	/**
	 * Removes the.
	 * 
	 * @param shape
	 *            the shape
	 */
	public void remove(Shape shape) {
		manageShape(shape, FALSE);
	}
	
	/**
	 * Manage shape.
	 * 
	 * @param shape
	 *            the shape
	 * @param val
	 *            the val
	 */
	private void manageShape(Shape shape, byte val) {
		int w = getWidth();
		Rectangle r = shape.getBounds();
		int x1 = (int) Math.max(Math.floor(r.getMinX()), 0);
		int x2 = (int) Math.min(x1 + Math.ceil(r.getWidth()), w);
		int y1 = (int) Math.max(Math.floor(r.getMinY()), 0);
		int y2 = (int) Math.min(y1 + Math.ceil(r.getHeight()), getHeight());
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				if (shape.contains(x, y)) {
					rawBinaryData[x + w * y] = val;
				}
			}
		}
	}

	/**
	 * Removes the.
	 * 
	 * @param other
	 *            the other
	 */
	public void remove(BinaryIcyBufferedImage other) {
		for (int d = 0; d < rawBinaryData.length; d++) {
			if (other.rawBinaryData[d] == TRUE) {
				rawBinaryData[d] = FALSE;
			}
		}
	}

	/* (non-Javadoc)
	 * @see icy.image.IcyBufferedImage#getCopy()
	 */
	@Override
	public BinaryIcyBufferedImage getCopy() {
		BinaryIcyBufferedImage n = new BinaryIcyBufferedImage(getWidth(), getHeight());
		System.arraycopy(rawBinaryData, 0, n.rawBinaryData, 0, rawBinaryData.length);
		return n;
	}

	/* (non-Javadoc)
	 * @see icy.image.IcyBufferedImage#getDataAsByte(int, int, int)
	 */
	@Override
	@Deprecated
	public byte getDataAsByte(int x, int y, int c) {
		return super.getDataAsByte(x, y, c);
	}
	
	/**
	 * Gets the.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	public boolean get(int x, int y) {
		return rawBinaryData[x + getWidth() * y] == TRUE;
	}

	/* (non-Javadoc)
	 * @see icy.image.IcyBufferedImage#setDataAsByte(int, int, int, byte)
	 */
	@Override
	@Deprecated
	public void setDataAsByte(int x, int y, int c, byte value) {
		super.setDataAsByte(x, y, c, value);
	}
	
	/**
	 * Sets the.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param b
	 *            the b
	 */
	public void set(int x, int y, boolean b) {
		rawBinaryData[x + getWidth() * y] = b ? TRUE : FALSE;
	}

	/**
	 * Invert.
	 */
	public void invert() {
		for (int i = 0; i < rawBinaryData.length; i++) {
			if (rawBinaryData[i] == BinaryIcyBufferedImage.FALSE) {
				rawBinaryData[i] = BinaryIcyBufferedImage.TRUE;
			} else {
				rawBinaryData[i] = BinaryIcyBufferedImage.FALSE;
			}
		}
	}

	/**
	 * Dilate.
	 */
	public void dilate() {
		MorphologyToolbox.dilateInPlace(this);
	}

	/**
	 * Erode.
	 */
	public void erode() {
		MorphologyToolbox.erodeInPlace(this);
	}

	/**
	 * Fill holes.
	 */
	public void fillHoles() {
		MorphologyToolbox.fillHolesInPlace(this);
	}

	/**
	 * Fill hole not recursive.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void fillHoleNotRecursive(int x, int y) {
		int width = getWidth();
		int height = getHeight();

		Queue<Pixel> todo = new ArrayDeque<Pixel>();
		todo.offer(new Pixel(x, y));

		Pixel pix = null;
		int idx = 0;
		do {
			pix = todo.poll();
			if (pix != null) {
				x = (int) pix.x;
				y = (int) pix.y;
				idx = x + width * y;
				if ((x >= 0) && (x < width) && (y >= 0) && (y < height) && (rawBinaryData[idx] == FALSE)) {
					rawBinaryData[idx] = TRUE;
					todo.offer(new Pixel(x + 1, y));
					todo.offer(new Pixel(x - 1, y));
					todo.offer(new Pixel(x, y + 1));
					todo.offer(new Pixel(x, y - 1));
				}
			}
		} while (pix != null);
	}

	/**
	 * Fill hole.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void fillHole(int x, int y) {
		fillHoleNotRecursive(x, y);
	}

	/**
	 * Contains.
	 * 
	 * @param px
	 *            the px
	 * @return true, if successful
	 */
	public boolean contains(Pixel px) {
		return contains((int) px.x, (int) px.y);
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
		if ((x >= 0) && (x < getWidth()) && (y >= 0) && (y < getHeight())) {
			return rawBinaryData[x + getWidth() * y] == TRUE;
		} else {
			return false;
		}
	}

	/**
	 * Filter size.
	 * 
	 * @param size
	 *            the size
	 */
	public void filterSize(int size) {
		int width = getWidth();
		List<My2DConnectedComponent> ccs = SomeImageTools.findConnectedComponents(this, 0, size);
		for (My2DConnectedComponent cc : ccs) {
			for (Point3i pt : cc.getPoints()) {
				rawBinaryData[pt.x + width * pt.y] = FALSE;
			}
		}
	}
}
