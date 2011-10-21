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
package plugins.nherve.toolbox.image.feature.signature;

import plugins.nherve.toolbox.image.feature.SignatureDistance;

/**
 * The Class L2Distance.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class L2Distance extends SignatureDistance<VectorSignature> {

	/**
	 * Instantiates a new l2 distance.
	 */
	public L2Distance() {
		super();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.SignatureDistance#computeDistance(plugins.nherve.toolbox.image.feature.Signature, plugins.nherve.toolbox.image.feature.Signature)
	 */
	@Override
	public double computeDistance(VectorSignature vs1, VectorSignature vs2) throws SignatureException {
		if ((vs1 == null) || (vs2 == null)) {
			throw new SignatureException("Null signature in L2Distance.compute()");
		}
		if (vs1.getSize() != vs2.getSize()) {
			throw new SignatureException("VectorSignature dimensions mismatch");
		}
		
		double d = 0.0;
		
		for (int dim = 0; dim < vs1.getSize(); dim++) {
			double e = vs1.get(dim) - vs2.get(dim);
			d += e * e;
		}
		
		return Math.sqrt(d);
	}

}
