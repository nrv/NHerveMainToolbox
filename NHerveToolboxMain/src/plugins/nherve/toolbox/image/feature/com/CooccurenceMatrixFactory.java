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

import icy.image.IcyBufferedImage;
import icy.type.TypeUtil;

import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.descriptor.ColorPixel;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskStack;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;


/**
 * The Class CooccurenceMatrixFactory.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class CooccurenceMatrixFactory extends Algorithm {
	
	/** The kernel. */
	private List<IcyPixel> kernel;
	
	/**
	 * Instantiates a new cooccurence matrix factory.
	 */
	public CooccurenceMatrixFactory() {
		super();
		setKernel(KernelFactory.getStandardKernel(1));
	}
	
	/**
	 * Gets the index manage borders.
	 * 
	 * @param data
	 *            the data
	 * @param px
	 *            the px
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the index manage borders
	 */
	private int getIndexManageBorders(int[] data, IcyPixel px, int w, int h) {
		int x = (int)px.x;
		int y = (int)px.y;

		if (x < 0) {
			x = Math.abs(x);
		} else if (x >= w) {
			x -= 2 * (x - w + 1);
		}

		if (y < 0) {
			y = Math.abs(y);
		} else if (y >= h) {
			y -= 2 * (y - h + 1);
		}

		return data[x + y * w];
	}
	
	/**
	 * Gets the indexed image.
	 * 
	 * @param seg
	 *            the seg
	 * @return the indexed image
	 * @throws FeatureException
	 *             the feature exception
	 */
	public static IcyBufferedImage getIndexedImage(MaskStack seg) throws FeatureException {
		final int w = seg.getWidth();
		final int h = seg.getHeight();
		final int s = seg.size();

		IcyBufferedImage index = new IcyBufferedImage(w, h, 1, TypeUtil.TYPE_INT);
		int[] idxData = index.getDataXYAsInt(0);

		byte[][] segData = new byte[s][];
		int id = 0;
		for (Mask m : seg) {
			segData[id] = m.getBinaryData().getDataXYAsByte(0);
			id++;
		}

		int idx = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (id = 0; id < s; id++) {
					if (segData[id][idx] == BinaryIcyBufferedImage.TRUE) {
						idxData[idx] = id;
						break;
					}
				}
				idx++;
			}
		}

		index.dataChanged();

		return index;
	}

	/**
	 * Builds the from segmentation.
	 * 
	 * @param seg
	 *            the seg
	 * @return the cooccurence matrix
	 * @throws FeatureException
	 *             the feature exception
	 */
	public CooccurenceMatrix<Integer> buildFromSegmentation(MaskStack seg) throws FeatureException {
		return buildFromIndexedImage(getIndexedImage(seg));
	}
	
	/**
	 * Builds the from indexed image.
	 * 
	 * @param img
	 *            the img
	 * @param vocabulary
	 *            the vocabulary
	 * @return the cooccurence matrix
	 * @throws FeatureException
	 *             the feature exception
	 */
	public CooccurenceMatrix<Integer> buildFromIndexedImage(IcyBufferedImage img, Vocabulary<Integer> vocabulary) throws FeatureException {
		if (img.getDataType() != TypeUtil.TYPE_INT) {
			throw new FeatureException("Only TYPE_INT IcyBufferedImage supported in CooccurenceMatrix.buildFromIndexedImage()");
		}
		
		if (kernel == null) {
			throw new FeatureException("No kernel defined in CooccurenceMatrix.buildFromIndexedImage()");
		}

		int w = img.getWidth();
		int h = img.getHeight();

		CooccurenceMatrix<Integer> result = new CooccurenceMatrix<Integer>(vocabulary);
		int[] data = img.getDataXYAsInt(0);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				IcyPixel ct = new IcyPixel(x, y);
				int center = vocabulary.getIndex(getIndexManageBorders(data, ct, w, h));
				for (IcyPixel shift : kernel) {
					IcyPixel nb = ct.plus(shift);
					int neihbour = vocabulary.getIndex(getIndexManageBorders(data, nb, w, h));
					result.add(center, neihbour, 1);
				}
			}
		}

		return result;
	}
	
	/**
	 * Builds the from indexed image.
	 * 
	 * @param idxImg
	 *            the idx img
	 * @param oriImg
	 *            the ori img
	 * @param vocabulary
	 *            the vocabulary
	 * @return the cooccurence matrix
	 * @throws FeatureException
	 *             the feature exception
	 */
	public CooccurenceMatrix<Integer> buildFromIndexedImage(IcyBufferedImage idxImg, SegmentableIcyBufferedImage oriImg, VocabularyOfObjects<Integer, VectorSignature> vocabulary) throws FeatureException {
		if (idxImg.getDataType() != TypeUtil.TYPE_INT) {
			throw new FeatureException("Only TYPE_INT IcyBufferedImage supported in CooccurenceMatrix.buildFromIndexedImage()");
		}
		
		if (kernel == null) {
			throw new FeatureException("No kernel defined in CooccurenceMatrix.buildFromIndexedImage()");
		}

		int w = idxImg.getWidth();
		int h = idxImg.getHeight();
		
		ColorPixel colpix = new ColorPixel(ColorSpaceTools.RGB, false);

		CooccurenceMatrix<Integer> result = new CooccurenceMatrix<Integer>(vocabulary);
		int[] idxData = idxImg.getDataXYAsInt(0);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				IcyPixel ct = new IcyPixel(x, y);
				int center = vocabulary.getIndex(getIndexManageBorders(idxData, ct, w, h));
				VectorSignature vs = (VectorSignature) colpix.extractLocalSignature(oriImg, ct);
				vs.multiply(256);
				double sct = vocabulary.similarity(center, vs);
				for (IcyPixel shift : kernel) {
					IcyPixel nb = ct.plus(shift);
					VectorSignature vs2 = (VectorSignature) colpix.extractLocalSignature(oriImg, nb);
					vs2.multiply(256);
					int neihbour = vocabulary.getIndex(getIndexManageBorders(idxData, nb, w, h));
					double snb = vocabulary.similarity(neihbour, vs2);
					result.add(center, neihbour, sct * snb);
				}
			}
		}

		return result;
	}
	
	/**
	 * Gets the indexed vocabulary.
	 * 
	 * @param img
	 *            the img
	 * @return the indexed vocabulary
	 * @throws FeatureException
	 *             the feature exception
	 */
	public static StandardIntegerVocabulary getIndexedVocabulary(IcyBufferedImage img) throws FeatureException {
		if (img.getDataType() != TypeUtil.TYPE_INT) {
			throw new FeatureException("Only TYPE_INT IcyBufferedImage supported in CooccurenceMatrix.buildFromIndexedImage()");
		}

		StandardIntegerVocabulary vocabulary = new StandardIntegerVocabulary();
		int[] data = img.getDataXYAsInt(0);
		for (int d : data) {
			if (!vocabulary.contains(d)) {
				vocabulary.add(d);
			}
		}

		return vocabulary;
	}
	
	/**
	 * Builds the from indexed image.
	 * 
	 * @param img
	 *            the img
	 * @return the cooccurence matrix
	 * @throws FeatureException
	 *             the feature exception
	 */
	public CooccurenceMatrix<Integer> buildFromIndexedImage(IcyBufferedImage img) throws FeatureException {
		StandardIntegerVocabulary vocabulary = getIndexedVocabulary(img);
		return buildFromIndexedImage(img, vocabulary);
	}

	/**
	 * Gets the kernel.
	 * 
	 * @return the kernel
	 */
	public List<IcyPixel> getKernel() {
		return kernel;
	}

	/**
	 * Sets the kernel.
	 * 
	 * @param kernel
	 *            the new kernel
	 */
	public void setKernel(List<IcyPixel> kernel) {
		this.kernel = kernel;
	}
	
	/**
	 * Sets the kernel.
	 * 
	 * @param kernel
	 *            the new kernel
	 */
	public void setKernel(int kernel) {
		setKernel(KernelFactory.getStandardKernel(kernel));
	}
}
