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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class DataProcessor.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class DataProcessor extends Algorithm {
	
	/**
	 * Instantiates a new data processor.
	 */
	public DataProcessor() {
		super();
	}

	/**
	 * Instantiates a new data processor.
	 * 
	 * @param display
	 *            the display
	 */
	public DataProcessor(boolean display) {
		super(display);
	}

	/**
	 * Gets the all sigs.
	 * 
	 * @param pos
	 *            the pos
	 * @param neg
	 *            the neg
	 * @return the all sigs
	 */
	private List<VectorSignature> getAllSigs(List<VectorSignature> pos, List<VectorSignature> neg) {
		ArrayList<VectorSignature> all = new ArrayList<VectorSignature>();
		all.addAll(pos);
		all.addAll(neg);
		return all;
	}
	
	/**
	 * Estimate parameters.
	 * 
	 * @param pos
	 *            the pos
	 * @param neg
	 *            the neg
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void estimateParameters(List<VectorSignature> pos, List<VectorSignature> neg)  throws SignatureException {
		estimateParameters(getAllSigs(pos, neg));
	}
	
	/**
	 * Estimate parameters.
	 * 
	 * @param pos
	 *            the pos
	 * @param neg
	 *            the neg
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void estimateParameters(VectorSignature[] pos, VectorSignature[] neg)  throws SignatureException {
		estimateParameters(getAllSigs(Arrays.asList(pos), Arrays.asList(neg)));
	}
	
	/**
	 * Apply.
	 * 
	 * @param sigs
	 *            the sigs
	 * @return the list
	 * @throws SignatureException
	 *             the signature exception
	 */
	public List<VectorSignature> apply(List<VectorSignature> sigs)  throws SignatureException {
		List<VectorSignature> res = new ArrayList<VectorSignature>();
		
		for (VectorSignature s : sigs) {
			res.add(apply(s));
		}
		
		return res;
	}
	
	/**
	 * Apply.
	 * 
	 * @param sigs
	 *            the sigs
	 * @return the vector signature[]
	 * @throws SignatureException
	 *             the signature exception
	 */
	public VectorSignature[] apply(VectorSignature[] sigs)  throws SignatureException {
		VectorSignature[] res = new VectorSignature[sigs.length];
		
		for (int i = 0; i < sigs.length; i++) {
			res[i] = apply(sigs[i]);
		}
		
		return res;
	}
	
	/**
	 * Estimate parameters.
	 * 
	 * @param sigs
	 *            the sigs
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract void estimateParameters(List<VectorSignature> sigs) throws SignatureException;
	
	/**
	 * Apply.
	 * 
	 * @param sig
	 *            the sig
	 * @return the vector signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	public abstract VectorSignature apply(VectorSignature sig) throws SignatureException;
	
}
