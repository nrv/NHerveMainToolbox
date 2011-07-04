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

import icy.sequence.Sequence;

import java.util.Arrays;
import java.util.Stack;

import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.vannary.morphomaths.MorphOp;

/**
 * The Class MorphologyToolbox.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class MorphologyToolbox {
	
	/** The Constant STD_ELEM. */
	private final static double[][] STD_ELEM = new double[][] { { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 1.0 } };
	
	/** The Constant STD_ELEM_CX. */
	private final static int STD_ELEM_CX = 1;
	
	/** The Constant STD_ELEM_CY. */
	private final static int STD_ELEM_CY = 1;

	/**
	 * Dilate.
	 * 
	 * @param in
	 *            the in
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage dilate(BinaryIcyBufferedImage in) {
		BinaryIcyBufferedImage inc = in.getCopy();
		dilateInPlace(inc);
		return inc;
	}

	/**
	 * Dilate in place.
	 * 
	 * @param in
	 *            the in
	 */
	public static void dilateInPlace(BinaryIcyBufferedImage in) {
		Sequence sin = new Sequence(in);
		MorphOp op = new MorphOp();
		op.dilateGreyScale(sin, 0, STD_ELEM, STD_ELEM_CX, STD_ELEM_CY);
	}

	/**
	 * Erode.
	 * 
	 * @param in
	 *            the in
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage erode(BinaryIcyBufferedImage in) {
		BinaryIcyBufferedImage inc = in.getCopy();
		erodeInPlace(inc);
		return inc;
	}

	/**
	 * Erode in place.
	 * 
	 * @param in
	 *            the in
	 */
	public static void erodeInPlace(BinaryIcyBufferedImage in) {
		Sequence sin = new Sequence(in);
		MorphOp op = new MorphOp();
		op.erodeGreyScale(sin, 0, STD_ELEM, STD_ELEM_CX, STD_ELEM_CY);
	}

	/**
	 * Fill holes.
	 * 
	 * @param in
	 *            the in
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage fillHoles(BinaryIcyBufferedImage in) {
		BinaryIcyBufferedImage inc = in.getCopy();
		fillHolesInPlace(inc);
		return inc;
	}

	/**
	 * Compute border.
	 * 
	 * @param in
	 *            the in
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage computeBorder(BinaryIcyBufferedImage in) {
		BinaryIcyBufferedImage out = in.getCopy();

		byte[] ind = in.getRawData();
		byte[] outd = out.getRawData();
		Arrays.fill(outd, BinaryIcyBufferedImage.FALSE);

		int i, j, offset = 0;
		int w = in.getWidth();
		int h = in.getHeight();
		int iMax = w - 1, jMax = h - 1;

		boolean isObject;

		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++, offset++) {
				isObject = (ind[offset] == BinaryIcyBufferedImage.TRUE);
				if ((i < iMax) && (isObject != (ind[offset + 1] == BinaryIcyBufferedImage.TRUE))) {
					if (isObject) {
						outd[offset] = BinaryIcyBufferedImage.TRUE;
					} else {
						outd[offset + 1] = BinaryIcyBufferedImage.TRUE;
					}
				}

				if ((j < jMax) && (isObject != (ind[offset + w] == BinaryIcyBufferedImage.TRUE))) {
					if (isObject) {
						outd[offset] = BinaryIcyBufferedImage.TRUE;
					} else {
						outd[offset + w] = BinaryIcyBufferedImage.TRUE;
					}
				}
			}
		}

		return out;
	}

	/**
	 * Fill holes in place.
	 * 
	 * @param in
	 *            the in
	 */
	public static void fillHolesInPlace(BinaryIcyBufferedImage in) {
		byte[] rawBinaryData = in.getRawData();
		int w = in.getWidth();
		int h = in.getHeight();
		int sz = w * h;
		Stack<Integer> pixelsToVisit = new Stack<Integer>();
		pixelsToVisit.ensureCapacity(sz);

		byte[] temp = new byte[sz];
		Arrays.fill(temp, BinaryIcyBufferedImage.FALSE);

		for (int i = 0; i < w; i++)
			if (rawBinaryData[i] == BinaryIcyBufferedImage.FALSE)
				pixelsToVisit.add(i);

		for (int left = 1, right = w - 1; left < sz; left += w, right += w) {
			if (rawBinaryData[left] == BinaryIcyBufferedImage.FALSE)
				pixelsToVisit.add(left);
			if (rawBinaryData[right] == BinaryIcyBufferedImage.FALSE)
				pixelsToVisit.add(right);
		}

		for (int i = sz - w; i < sz; i++)
			if (rawBinaryData[i] == BinaryIcyBufferedImage.FALSE)
				pixelsToVisit.add(i);

		if (pixelsToVisit.isEmpty())
			System.err.println("fillHoles_2D was unable to find a background pixel for flooding");

		while (!pixelsToVisit.isEmpty()) {
			int index = pixelsToVisit.pop();
			temp[index] = BinaryIcyBufferedImage.TRUE;

			int inOff = index;
			int i = index % w;
			int j = index / w;

			if (i > 0 && (temp[index - 1] == BinaryIcyBufferedImage.FALSE) && (rawBinaryData[inOff - 1] == BinaryIcyBufferedImage.FALSE)) {
				pixelsToVisit.push(index - 1);
			}
			if (i < w - 1 && (temp[index + 1] == BinaryIcyBufferedImage.FALSE) && (rawBinaryData[inOff + 1] == BinaryIcyBufferedImage.FALSE)) {
				pixelsToVisit.push(index + 1);
			}
			if (j > 0 && (temp[index - w] == BinaryIcyBufferedImage.FALSE) && (rawBinaryData[inOff - w] == BinaryIcyBufferedImage.FALSE)) {
				pixelsToVisit.push(index - w);
			}
			if (j < h - 1 && (temp[index + w] == BinaryIcyBufferedImage.FALSE) && (rawBinaryData[inOff + w] == BinaryIcyBufferedImage.FALSE)) {
				pixelsToVisit.push(index + w);
			}
		}

		for (int i = 0; i < sz; i++) {
			if (temp[i] == BinaryIcyBufferedImage.FALSE) {
				rawBinaryData[i] = BinaryIcyBufferedImage.TRUE;
			} else {
				rawBinaryData[i] = BinaryIcyBufferedImage.FALSE;
			}
		}
	}
}
