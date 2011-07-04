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
import plugins.nherve.toolbox.Algorithm;

/**
 * The Class AutomaticLabeler.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class AutomaticLabeler extends Algorithm {

	/**
	 * Instantiates a new automatic labeler.
	 */
	public AutomaticLabeler() {
		super();
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Gets the potential labels.
	 * 
	 * @return the potential labels
	 */
	public abstract String[] getPotentialLabels();

	/**
	 * Work.
	 * 
	 * @param img
	 *            the img
	 * @param ms
	 *            the ms
	 */
	public abstract void work(IcyBufferedImage img, MaskStack ms);

	/**
	 * Checks if is already labeled.
	 * 
	 * @param ms
	 *            the ms
	 * @return true, if is already labeled
	 */
	public boolean isAlreadyLabeled(MaskStack ms) {
		for (Mask m : ms) {
			if (isAlreadyLabeled(m)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is already labeled.
	 * 
	 * @param m
	 *            the m
	 * @return true, if is already labeled
	 */
	public boolean isAlreadyLabeled(Mask m) {
		String[] lbs = getPotentialLabels();
		for (String lb : lbs) {
			if (lb.equalsIgnoreCase(m.getLabel())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Clean all labels.
	 * 
	 * @param ms
	 *            the ms
	 */
	public void cleanAllLabels(MaskStack ms) {
		for (Mask m : ms) {
			cleanAllLabels(m);
		}
	}
	
	/**
	 * Clean all labels.
	 * 
	 * @param m
	 *            the m
	 */
	public void cleanAllLabels(Mask m) {
		String[] lbs = getPotentialLabels();
		for (String lb : lbs) {
			if (lb.equalsIgnoreCase(m.getLabel())) {
				m.setLabel(MaskStack.MASK_DEFAULT_LABEL);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
