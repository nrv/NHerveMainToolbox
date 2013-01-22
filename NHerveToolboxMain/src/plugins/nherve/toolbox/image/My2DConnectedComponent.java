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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point3i;

import plugins.adufour.connectedcomponents.ConnectedComponent;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.toolboxes.MorphologyToolbox;

/**
 * The Class My2DConnectedComponent.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class My2DConnectedComponent implements PointsEnsemble {
	
	/** The points. */
	private ArrayList<Point3i> points;
	
	/** The perimeter. */
	private ArrayList<Point3i> perimeter;
	
	/** The height. */
	private int minX, minY, width, height;
	
	/** The label. */
	private int label;

	/**
	 * Instantiates a new my2 d connected component.
	 * 
	 * @param label
	 *            the label
	 * @param internal
	 *            the internal
	 */
	public My2DConnectedComponent(int label, Point3i[] internal) {
		this(label);

		int maxX = 0;
		int maxY = 0;
		int x = 0;
		int y = 0;
		for (Point3i p : internal) {
			x = p.x;
			y = p.y;
			if (x > maxX) {
				maxX = x;
			}
			if (x < minX) {
				minX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
			if (y < minY) {
				minY = y;
			}
			this.points.add(new Point3i(x, y, 0));
		}

		this.width = maxX - minX;
		this.height = maxY - minY;
	}
	
	public My2DConnectedComponent(int label, List<Point3i> internal) {
		this(label);

		int maxX = 0;
		int maxY = 0;
		int x = 0;
		int y = 0;
		for (Point3i p : internal) {
			x = p.x;
			y = p.y;
			if (x > maxX) {
				maxX = x;
			}
			if (x < minX) {
				minX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
			if (y < minY) {
				minY = y;
			}
			this.points.add(new Point3i(x, y, 0));
		}

		this.width = maxX - minX;
		this.height = maxY - minY;
	}
	
	public My2DConnectedComponent(int label) {
		super();

		this.minX = Integer.MAX_VALUE;
		this.minY = Integer.MAX_VALUE;
		this.points = new ArrayList<Point3i>();
		this.label = label;
		this.perimeter = null;
	}

	/**
	 * Instantiates a new my2 d connected component.
	 * 
	 * @param label
	 *            the label
	 * @param internal
	 *            the internal
	 */
	public My2DConnectedComponent(int label, ConnectedComponent internal) {
		this(label, internal.getPoints());
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getId()
	 */
	@Override
	public int getId() {
		return getLabel();
	}

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public int getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getMinX()
	 */
	public int getMinX() {
		return minX;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getMinY()
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * Gets the bounds.
	 * 
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
	}

	@Override
	public List<Point3i> getPoints() {
		return points;
	}

	/**
	 * Gets the surface.
	 * 
	 * @return the surface
	 */
	public int getSurface() {
		return points.size();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getXCenter()
	 */
	@Override
	public int getXCenter() {
		return getMinX() + getWidth() / 2;
	}

	/**
	 * Contains.
	 * 
	 * @param px
	 *            the px
	 * @return true, if successful
	 */
	public boolean contains(IcyPixel px) {
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
		for (Point3i pt : points) {
			if ((pt.x == x) && (pt.y == y)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.PointsEnsemble#getYCenter()
	 */
	@Override
	public int getYCenter() {
		return getMinY() + getHeight() / 2;
	}

	/**
	 * Find perimeter.
	 */
	private void findPerimeter() {
		int xOffset = getMinX() - 1;
		int yOffset = getMinY() - 1;
		int w = getWidth() + 3;

		BinaryIcyBufferedImage bin1 = asBinaryImage();
		BinaryIcyBufferedImage bin2 = bin1.getCopy();
		MorphologyToolbox.erodeInPlace(bin2);
		bin1.remove(bin2);

		perimeter = new ArrayList<Point3i>();
		byte[] raw = bin1.getRawData();
		for (int i = 0; i < raw.length; i++) {
			if (raw[i] == BinaryIcyBufferedImage.TRUE) {
				perimeter.add(new Point3i(xOffset + i % w, yOffset + i / w , 0));
			}
		}
	}

	/**
	 * Gets the perimeter points.
	 * 
	 * @return the perimeter points
	 */
	public List<Point3i> getPerimeterPoints() {
		if (perimeter == null) {
			findPerimeter();
		}

		return perimeter;
	}

	/**
	 * As ro i2 d area.
	 * 
	 * @param seq
	 *            the seq
	 * @return the rO i2 d area
	 */
	public ROI2DArea asROI2DArea(Sequence seq) {
		ROI2DArea asa = new ROI2DArea(new Point2D.Float(0, 0));

		int bbw = getWidth() + 1;
		int bbh = getHeight() + 1;

		Rectangle bbx = new Rectangle(minX, minY, bbw, bbh);

		boolean[] data = new boolean[bbw * bbh];
		Arrays.fill(data, false);

		for (Point3i pt : points) {
			data[pt.x - minX + bbw * (pt.y - minY)] = true;
		}

		asa.setAsBooleanMask(bbx, data);
		asa.attachTo(seq);
		return asa;
	}

	/**
	 * As binary image.
	 * 
	 * @return the binary icy buffered image
	 */
	public BinaryIcyBufferedImage asBinaryImage() {
		int xOffset = getMinX() - 1;
		int yOffset = getMinY() - 1;
		int w = getWidth() + 3;
		int h = getHeight() + 3;

		return asBinaryImage(w, h, xOffset, yOffset);
	}
	
	/**
	 * As binary image.
	 * 
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param xOffset
	 *            the x offset
	 * @param yOffset
	 *            the y offset
	 * @return the binary icy buffered image
	 */
	public BinaryIcyBufferedImage asBinaryImage(int w, int h, int xOffset, int yOffset) {
		BinaryIcyBufferedImage bin = new BinaryIcyBufferedImage(w, h);
		byte[] raw = bin.getRawData();
		for (Point3i pt : points) {
			raw[pt.x - xOffset + w * (pt.y - yOffset)] = BinaryIcyBufferedImage.TRUE;
		}

		return bin;
	}

	/**
	 * As image.
	 * 
	 * @return the icy buffered image
	 */
	public IcyBufferedImage asImage() {
		int xOffset = getMinX() - 1;
		int yOffset = getMinY() - 1;
		int w = getWidth() + 3;
		int h = getHeight() + 3;

		IcyBufferedImage bin = new IcyBufferedImage(w, h, 3, TypeUtil.TYPE_BYTE);
		byte[][] raw = bin.getDataXYCAsByte();
		int idx = 0;
		for (Point3i pt : points) {
			idx = pt.x - xOffset + w * (pt.y - yOffset);
			raw[0][idx] = (byte) 255;
			raw[1][idx] = (byte) 255;
			raw[2][idx] = (byte) 255;
		}

		return bin;
	}

	/**
	 * As image.
	 * 
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the icy buffered image
	 */
	public IcyBufferedImage asImage(int w, int h) {
		return asImage(w, h, points);
	}

	/**
	 * As image.
	 * 
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param points
	 *            the points
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage asImage(int w, int h, List<Point3i> points) {
		IcyBufferedImage bin = new IcyBufferedImage(w, h, 3, TypeUtil.TYPE_BYTE);
		byte[][] raw = bin.getDataXYCAsByte();
		Arrays.fill(raw[0], (byte) 255);
		Arrays.fill(raw[1], (byte) 255);
		Arrays.fill(raw[2], (byte) 255);
		int idx = 0;
		for (Point3i pt : points) {
			idx = pt.x + w * pt.y;
			raw[0][idx] = (byte) 0;
			raw[1][idx] = (byte) 0;
			raw[2][idx] = (byte) 0;
		}

		return bin;
	}
}
