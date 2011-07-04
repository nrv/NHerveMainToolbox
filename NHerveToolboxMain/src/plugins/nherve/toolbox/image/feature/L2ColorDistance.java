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
package plugins.nherve.toolbox.image.feature;

/**
 * The Class L2ColorDistance.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class L2ColorDistance extends ColorDistance {

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.ColorDistance#computeDistance(double[], double[])
	 */
	@Override
	public double computeDistance(double[] c1, double[] c2) {
		double dr = c1[0] - c2[0];
		double dg = c1[1] - c2[1];
		double db = c1[2] - c2[2];

		return Math.sqrt(dr * dr + dg * dg + db * db);
	}

}
