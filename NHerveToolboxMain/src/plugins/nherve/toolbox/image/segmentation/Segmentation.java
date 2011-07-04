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
package plugins.nherve.toolbox.image.segmentation;

import icy.image.IcyBufferedImage;
import icy.type.TypeUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.mask.MaskStack;

/**
 * The Class Segmentation.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class Segmentation extends MaskStack implements Segmentable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5544766662934765236L;

	/** The index. */
	private IcyBufferedImage index;

	/**
	 * Instantiates a new segmentation.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public Segmentation(int width, int height) {
		super(width, height);
		index = null;
	}

	/**
	 * Check.
	 * 
	 * @throws MaskException
	 *             the mask exception
	 */
	public void check() throws MaskException {
		int count = 0;
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				count = 0;
				for (Mask o : this) {
					if (o.getBinaryData().get(x, y)) {
						count++;
					}
				}
				if (count != 1) {
					throw new MaskException("Segmentation problem at " + x + "x" + y);
				}
			}
		}
	}

	/**
	 * Gets the mask id.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the mask id
	 * @throws MaskException
	 *             the mask exception
	 */
	public int getMaskId(int x, int y) throws MaskException {
		if (index == null) {
			throw new MaskException("Index not created, call createIndex() first");
		}
		if ((x < 0) || (x >= getWidth())) {
			throw new MaskException("Bad X coordinate");
		}
		if ((y < 0) || (y >= getHeight())) {
			throw new MaskException("Bad Y coordinate");
		}
		return index.getDataAsInt(x, y, 0);
	}

	/**
	 * Gets the mask.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the mask
	 * @throws MaskException
	 *             the mask exception
	 */
	public Mask getMask(int x, int y) throws MaskException {
		return getById(getMaskId(x, y));
	}

	/**
	 * Creates the index.
	 * 
	 * @throws MaskException
	 *             the mask exception
	 */
	public void createIndex() throws MaskException {
		index = new IcyBufferedImage(getWidth(), getHeight(), 1, TypeUtil.TYPE_INT);
		int[] indexData = index.getDataXYAsInt(0);
		
		List<byte[]> mbin = new ArrayList<byte[]>();
		List<Integer> mid = new ArrayList<Integer>();
		for (Mask m : this) {
			mbin.add(m.getBinaryData().getRawData());
			mid.add(m.getId());
		}

		int maskId = -1;
		int idx = 0;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				maskId = -1;
				Iterator<Integer> itid = mid.iterator();
				for (byte[] m : mbin) {
					maskId = itid.next();
					if (m[idx] == BinaryIcyBufferedImage.TRUE) {
						break;
					}
				}
				indexData[idx] = maskId;
				idx++;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Segmentation clone() throws CloneNotSupportedException {
		Segmentation clone = new Segmentation(getWidth(), getHeight());

		for (Mask m : this) {
			try {
				Mask mc = clone.createNewMask(m.getLabel(), m.isNeedAutomaticLabel(), new Color(m.getColor().getRGB()), m.getOpacity());
				mc.setBinaryData(m.getBinaryData().getCopy());
			} catch (MaskException e) {
				throw new CloneNotSupportedException(e.getMessage());
			}
		}

		return clone;
	}

}
