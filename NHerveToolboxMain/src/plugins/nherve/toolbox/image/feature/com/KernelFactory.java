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
package plugins.nherve.toolbox.image.feature.com;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;


/**
 * The Class KernelFactory.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class KernelFactory {
	
	/** The Constant kdf. */
	private final static DecimalFormat kdf = new DecimalFormat("+0.00;-0.00");

	/** The Constant KERNEL_2. */
	private static final List<IcyPixel> KERNEL_2;
	static {
		KERNEL_2 = new ArrayList<IcyPixel>();
		KERNEL_2.add(new IcyPixel(-2, 0));
		KERNEL_2.add(new IcyPixel(+2, 0));
		KERNEL_2.add(new IcyPixel(0, -2));
		KERNEL_2.add(new IcyPixel(0, +2));
		KERNEL_2.add(new IcyPixel(-2, -2));
		KERNEL_2.add(new IcyPixel(+2, +2));
		KERNEL_2.add(new IcyPixel(+2, -2));
		KERNEL_2.add(new IcyPixel(-2, +2));
	}
	
	/**
	 * Gets the standard kernel.
	 * 
	 * @param id
	 *            the id
	 * @return the standard kernel
	 */
	public static List<IcyPixel> getStandardKernel(int id) {
		return getStandardKernel(id, false);
	}

	/**
	 * Gets the standard kernel.
	 * 
	 * @param id
	 *            the id
	 * @param precise
	 *            the precise
	 * @return the standard kernel
	 */
	public static List<IcyPixel> getStandardKernel(int id, boolean precise) {
		switch (id) {
		case 0:
			return getKernel(1, 0d, precise);
		case 1:
			return getKernel(8, 1.5d, precise);
		case 2:
			return KERNEL_2;
		case 3:
			return getKernel(4, 1d, precise);
		case 4:
			return getKernel(4, 2d, precise);
		case 5:
			return getKernel(8, 3d, precise);
		case 6:
			return getKernel(16, 3.5d, precise);
		case 7:
			return getKernel(24, 5d, precise);
		case 8:
			return getKernel(36, 8d, precise);
		case 9:
			return getKernel(4, 2d, precise);
		case 10:
			return getKernel(8, 2d, precise);
		case 11:
			return getKernel(16, 2d, precise);
		case 12:
			return getKernel(3, 2d, precise);
		case 13:
			return getKernel(8, 1d, precise);
		case 14:
			return getKernel(8, 4d, precise);
		}

		return null;
	}

	/**
	 * Gets the kernel.
	 * 
	 * @param nbn
	 *            the nbn
	 * @param r
	 *            the r
	 * @param precise
	 *            the precise
	 * @return the kernel
	 */
	public static List<IcyPixel> getKernel(final int nbn, final double r, boolean precise) {
		List<IcyPixel> kernel = new ArrayList<IcyPixel>();

		HashMap<String, IcyPixel> avoidDuplicates = null;
		if (!precise) {
			avoidDuplicates = new HashMap<String, IcyPixel>();
		}
		double x, y, c;
		final double cst = 2 * Math.PI / (double)nbn;

		for (int p = 0; p < nbn; p++) {
			c = (double)p * cst;
			x = -r * Math.sin(c);
			y = r * Math.cos(c);
			if (Math.abs(x) < 0.0000001) {
				x = 0;
			}
			if (Math.abs(y) < 0.0000001) {
				y = 0;
			}

			if (precise) {
				kernel.add(new IcyPixel(x, y));
			} else {
				IcyPixel px = new IcyPixel((int) x, (int) y);
				String k = px.toString();
				if (!avoidDuplicates.containsKey(k)) {
					avoidDuplicates.put(k, px);
					kernel.add(px);
				}
			}
		}

		return kernel;
	}

	/**
	 * Log.
	 * 
	 * @param name
	 *            the name
	 * @param kernel
	 *            the kernel
	 */
	public static void log(String name, List<IcyPixel> kernel) {
		Algorithm.out("------ Kernel " + name + " ------");
		for (IcyPixel shift : kernel) {
			Algorithm.out("    (" + kdf.format(shift.x) + ", " + kdf.format(shift.y) + ")");
		}
		Algorithm.out("----------------------");
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		for (int k = 0; k <= 12; k++) {
			log("" + k, getStandardKernel(k));
			log("precise " + k, getStandardKernel(k, true));
			Algorithm.out("");
		}
	}
}
