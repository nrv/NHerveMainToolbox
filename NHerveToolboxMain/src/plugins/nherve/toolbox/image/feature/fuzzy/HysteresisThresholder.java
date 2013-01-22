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
package plugins.nherve.toolbox.image.feature.fuzzy;

import icy.image.IcyBufferedImage;
import icy.type.TypeUtil;

import java.util.Arrays;
import java.util.LinkedList;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;


/**
 * The Class HysteresisThresholder.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class HysteresisThresholder extends Algorithm {
	
	/** The high threshold. */
	private double highThreshold;
	
	/** The low threshold. */
	private double lowThreshold;
	
	/** The high value. */
	private double highValue;
	
	/** The low value. */
	private double lowValue;
	
	/**
	 * Instantiates a new hysteresis thresholder.
	 * 
	 * @param highThreshold
	 *            the high threshold
	 * @param lowThreshold
	 *            the low threshold
	 * @param highValue
	 *            the high value
	 * @param lowValue
	 *            the low value
	 */
	public HysteresisThresholder(double highThreshold, double lowThreshold, double highValue, double lowValue) {
		super();
		this.highThreshold = highThreshold;
		this.lowThreshold = lowThreshold;
		this.highValue = highValue;
		this.lowValue = lowValue;
	}
	
	/**
	 * Instantiates a new hysteresis thresholder.
	 * 
	 * @param highThreshold
	 *            the high threshold
	 * @param lowThreshold
	 *            the low threshold
	 */
	public HysteresisThresholder(double highThreshold, double lowThreshold) {
		this(highThreshold, lowThreshold, 255, 0);
	}

	/**
	 * Gets the high threshold.
	 * 
	 * @return the high threshold
	 */
	public double getHighThreshold() {
		return highThreshold;
	}

	/**
	 * Gets the low threshold.
	 * 
	 * @return the low threshold
	 */
	public double getLowThreshold() {
		return lowThreshold;
	}

	/**
	 * Gets the high value.
	 * 
	 * @return the high value
	 */
	public double getHighValue() {
		return highValue;
	}

	/**
	 * Gets the low value.
	 * 
	 * @return the low value
	 */
	public double getLowValue() {
		return lowValue;
	}
	
	/**
	 * Work.
	 * 
	 * @param gray
	 *            the gray
	 * @return the icy buffered image
	 */
	public IcyBufferedImage work(IcyBufferedImage gray) {
		LinkedList<IcyPixel> wf = new LinkedList<IcyPixel>();
		
		int w = gray.getWidth();
		int h = gray.getHeight();

		IcyBufferedImage res = new IcyBufferedImage(w, h, 1, TypeUtil.TYPE_DOUBLE);
		double[] table = gray.getDataXYAsDouble(0).clone();
		res.setDataXYAsDouble(0, table);
		table = res.getDataXYAsDouble(0);

		boolean[] done = new boolean[table.length];
		Arrays.fill(done, false);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int idx = x + w * y;
				if ((done[idx] == false) && (table[idx] >= highThreshold)) {
					table[idx] = highValue;
					done[idx] = true;
					wf.addFirst(new IcyPixel(x, y));
				}
			}
		}
		
		while (!wf.isEmpty()) {
			IcyPixel px = wf.removeLast();
			int x = (int)px.x;
			int y = (int)px.y;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					int nx = x + dx;
					int ny = y + dy;
					if ((nx >= 0) && (ny >= 0) && (nx < w) && (ny < h)) {
						int idx = nx + w * ny;
						if ((!done[idx]) && (table[idx] >= lowThreshold)) {
							table[idx] = highValue;
							done[idx] = true;
							wf.addFirst(new IcyPixel(nx, ny));
						}
					}
				}
			}
		}
		
		for (int i = 0; i < table.length; i++) {
			if (table[i] < highValue) {
				table[i] = lowValue;
			}
		}

		res.dataChanged();
		
		return res;
	}

}
