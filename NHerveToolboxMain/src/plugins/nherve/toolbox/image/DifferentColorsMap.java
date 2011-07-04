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

import java.awt.Color;

// FIXME : faire hériter de icy.image.ColorMap et remonter les méthodes suivantes à la classe mère :
// public Color get(int idx)
// public int getNbColors()

/**
 * The Class DifferentColorsMap.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class DifferentColorsMap {
	
	/** The Constant COLORMAP_SIZE. */
	public final static int COLORMAP_SIZE = 256;

	/** The nb colors. */
	private int nbColors;
	
	/** The r. */
	private float[] r;
	
	/** The g. */
	private float[] g;
	
	/** The b. */
	private float[] b;
	
	/**
	 * To rgb.
	 * 
	 * @param idx
	 *            the idx
	 * @param h
	 *            the h
	 * @param s
	 *            the s
	 * @param v
	 *            the v
	 */
	private void toRGB(int idx, float h, float s, float v) {
//		System.out.println(idx + " - " + h);
		int region = (int) Math.floor(h);
		float c1 = v * (1 - s);
		float c2 = v * (1 - s * (h - region));
		float c3 = v * (1 - s * (1 - (h - region)));

		switch (region) {
		case 0: {
			r[idx] = v;
			g[idx] = c3;
			b[idx] = c1;
			break;
		}
		case 1: {
			r[idx] = c2;
			g[idx] = v;
			b[idx] = c1;
			break;
		}
		case 2: {
			r[idx] = c1;
			g[idx] = v;
			b[idx] = c3;
			break;
		}
		case 3: {
			r[idx] = c1;
			g[idx] = c2;
			b[idx] = v;
			break;
		}
		case 4: {
			r[idx] = c3;
			g[idx] = c1;
			b[idx] = v;
			break;
		}
		default: {
			r[idx] = v;
			g[idx] = c1;
			b[idx] = c2;
		}
		}
	}

	/**
	 * Instantiates a new different colors map.
	 */
	public DifferentColorsMap() {
		this(COLORMAP_SIZE);
	}
	
	/**
	 * Instantiates a new different colors map.
	 * 
	 * @param nbColors
	 *            the nb colors
	 */
	public DifferentColorsMap(int nbColors) {
		this(nbColors, 3);
	}
	
	/**
	 * Instantiates a new different colors map.
	 * 
	 * @param nbColors
	 *            the nb colors
	 * @param nbCycles
	 *            the nb cycles
	 */
	public DifferentColorsMap(int nbColors, int nbCycles) {
		this.nbColors = nbColors;
		r = new float[nbColors];
		g = new float[nbColors];
		b = new float[nbColors];
		
		float hue = 0;
		float dhue = 6f / nbColors;
		float sat = 1f;
		float val = 1f;
		
		for(int cycle = 0; cycle < nbCycles; cycle++) {
			if (cycle % 2 == 0) {
				val = 1f;
			} else {
				val = 0.7f;
			}
			if (cycle % 3 == 0) {
				sat = 1f;
			} else if (cycle % 3 == 1) {
				sat = 0.8f;
			} else {
				sat = 0.6f;
			}
			for (int i = cycle * nbColors / nbCycles; i < (cycle + 1) * nbColors / nbCycles; i++) {
				hue = cycle * dhue + (i - cycle * nbColors / nbCycles) * nbCycles * dhue;
				toRGB(i, hue, sat, val);
			}
		}
	}

	/**
	 * Gets the.
	 * 
	 * @param idx
	 *            the idx
	 * @return the color
	 */
	public Color get(int idx) {
		// System.out.println(idx + " - r:" + r[idx] + ", g:" + g[idx] + ", b:" + b[idx]);
		return new Color(r[idx], g[idx], b[idx]);
	}

	/**
	 * Gets the nb colors.
	 * 
	 * @return the nb colors
	 */
	public int getNbColors() {
		return nbColors;
	}
}
