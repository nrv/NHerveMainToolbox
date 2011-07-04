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

import plugins.nherve.toolbox.image.feature.SignatureDistance;
import plugins.nherve.toolbox.image.feature.signature.L2Distance;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * The Class KNNClassifier.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class KNNClassifier extends LearningAlgorithm {
	
	/** The positive. */
	private VectorSignature[] positive;
	
	/** The negative. */
	private VectorSignature[] negative;
	
	/** The dist. */
	private SignatureDistance dist;

	/**
	 * Instantiates a new kNN classifier.
	 */
	public KNNClassifier() {
		super();
		this.positive = null;
		this.negative = null;
		this.dist = new L2Distance();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.LearningAlgorithm#isPositiveImpl(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	protected boolean isPositiveImpl(VectorSignature sig) throws ClassifierException {
		double minDist = Double.MAX_VALUE;
		
		try {
			double d = 0.0;
			for (VectorSignature ps : positive) {
				d = dist.computeDistance(sig, ps);
				if (d < minDist) {
					minDist = d;
				}
			}
			
			for (VectorSignature ps : negative) {
				d = dist.computeDistance(sig, ps);
				if (d < minDist) {
					return false;
				}
			}
			
		} catch (SignatureException e) {
			throw new ClassifierException(e);
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.LearningAlgorithm#learnImpl(plugins.nherve.toolbox.image.feature.signature.VectorSignature[], plugins.nherve.toolbox.image.feature.signature.VectorSignature[])
	 */
	@Override
	protected void learnImpl(VectorSignature[] positive, VectorSignature[] negative) throws ClassifierException {
		this.positive = positive;
		this.negative = negative;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.LearningAlgorithm#scoreImpl(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	protected double scoreImpl(VectorSignature sig) throws ClassifierException {
		throw new ClassifierException("KNNClassifier.score(VectorSignature sig) not yet implemented");
	}

}
