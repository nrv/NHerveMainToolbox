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

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.segmentation.Segmentation;
import plugins.nherve.toolbox.image.segmentation.SegmentationException;

/**
 * The Class SegmentationAlgorithm.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class SegmentationAlgorithm<T extends Segmentable> extends Algorithm {
	
	/**
	 * Segment.
	 * 
	 * @param img
	 *            the img
	 * @return the segmentation
	 * @throws SegmentationException
	 *             the segmentation exception
	 */
	public abstract Segmentation segment(T img) throws SegmentationException;
}
