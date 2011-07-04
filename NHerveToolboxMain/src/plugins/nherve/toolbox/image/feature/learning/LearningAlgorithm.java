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
package plugins.nherve.toolbox.image.feature.learning;

import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.db.ImageDatabaseSplit;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class LearningAlgorithm.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class LearningAlgorithm extends Algorithm {
	
	/** The model info. */
	private String modelInfo = null;
	
	/** The learn data processed. */
	private boolean learnDataProcessed = false;
	
	/** The data processor. */
	private DataProcessor dataProcessor = null;

	/**
	 * Learn impl.
	 * 
	 * @param positive
	 *            the positive
	 * @param negative
	 *            the negative
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	protected abstract void learnImpl(VectorSignature[] positive, VectorSignature[] negative) throws ClassifierException;

	/**
	 * Checks if is positive impl.
	 * 
	 * @param sig
	 *            the sig
	 * @return true, if is positive impl
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	protected abstract boolean isPositiveImpl(VectorSignature sig) throws ClassifierException;

	/**
	 * Score impl.
	 * 
	 * @param sig
	 *            the sig
	 * @return the double
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	protected abstract double scoreImpl(VectorSignature sig) throws ClassifierException;

	/**
	 * Data process.
	 * 
	 * @param positive
	 *            the positive
	 * @param negative
	 *            the negative
	 * @return the vector signature[][]
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	protected VectorSignature[][] dataProcess(VectorSignature[] positive, VectorSignature[] negative) throws ClassifierException {
		log("Launching DataProcessor for learn data");
		VectorSignature[][] data = new VectorSignature[2][];
		
		try {
			dataProcessor.estimateParameters(positive, negative);
			data[0] = dataProcessor.apply(positive);
			data[1] = dataProcessor.apply(negative);
			learnDataProcessed = true;
		} catch (SignatureException e) {
			throw new ClassifierException(e);
		}

		return data;
	}

	/**
	 * Learn.
	 * 
	 * @param positive
	 *            the positive
	 * @param negative
	 *            the negative
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public void learn(VectorSignature[] positive, VectorSignature[] negative) throws ClassifierException {
		VectorSignature[] pa = positive;
		VectorSignature[] na = negative;

		if (hasDataProcessor() && (!isLearnDataProcessed())) {
			VectorSignature[][] data = dataProcess(positive, negative);
			pa = data[0];
			na = data[1];
		}

		learnImpl(pa, na);
	}

	/**
	 * Checks if is positive.
	 * 
	 * @param sig
	 *            the sig
	 * @return true, if is positive
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public boolean isPositive(VectorSignature sig) throws ClassifierException {
		if (hasDataProcessor()) {
			try {
				return isPositiveImpl(dataProcessor.apply(sig));
			} catch (SignatureException e) {
				throw new ClassifierException(e);
			}
		} else {
			return isPositiveImpl(sig);
		}
	}

	/**
	 * Score.
	 * 
	 * @param sig
	 *            the sig
	 * @return the double
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public double score(VectorSignature sig) throws ClassifierException {
		if (hasDataProcessor()) {
			try {
				return scoreImpl(dataProcessor.apply(sig));
			} catch (SignatureException e) {
				throw new ClassifierException(e);
			}
		} else {
			return scoreImpl(sig);
		}
	}

	/**
	 * Learn.
	 * 
	 * @param positive
	 *            the positive
	 * @param negative
	 *            the negative
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public void learn(List<VectorSignature> positive, List<VectorSignature> negative) throws ClassifierException {
		VectorSignature[] p = (VectorSignature[]) positive.toArray(new VectorSignature[positive.size()]);
		VectorSignature[] n = (VectorSignature[]) negative.toArray(new VectorSignature[negative.size()]);
		learn(p, n);
	}

	/**
	 * Learn.
	 * 
	 * @param split
	 *            the split
	 * @param posClass
	 *            the pos class
	 * @param desc
	 *            the desc
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public void learn(ImageDatabaseSplit split, String posClass, String desc) throws ClassifierException {
		try {
			setModelInfo(posClass);
			List<VectorSignature> pos = split.getLrnSignatures(posClass, true, desc);
			List<VectorSignature> neg = split.getLrnSignatures(posClass, false, desc);
			learn(pos, neg);
		} catch (FeatureException e) {
			throw new ClassifierException(e);
		}
	}

	/**
	 * Gets the model info.
	 * 
	 * @return the model info
	 */
	public String getModelInfo() {
		return modelInfo;
	}

	/**
	 * Sets the model info.
	 * 
	 * @param modelInfo
	 *            the new model info
	 */
	public void setModelInfo(String modelInfo) {
		this.modelInfo = modelInfo;
	}

	/**
	 * Gets the data processor.
	 * 
	 * @return the data processor
	 */
	protected DataProcessor getDataProcessor() {
		return dataProcessor;
	}

	/**
	 * Sets the data processor.
	 * 
	 * @param dataProcessor
	 *            the new data processor
	 */
	public void setDataProcessor(DataProcessor dataProcessor) {
		this.dataProcessor = dataProcessor;
	}

	/**
	 * Checks if is learn data processed.
	 * 
	 * @return true, if is learn data processed
	 */
	protected boolean isLearnDataProcessed() {
		return learnDataProcessed;
	}
	
	/**
	 * Checks for data processor.
	 * 
	 * @return true, if successful
	 */
	protected boolean hasDataProcessor() {
		return dataProcessor != null;
	}
}
