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
// svm_model
//
package plugins.nherve.toolbox.libsvm;

/**
 * The Class svm_model.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class svm_model implements java.io.Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -953187596080268455L;

	/** The param. */
	svm_parameter param;	// parameter
	
	/** The nr_class. */
	int nr_class;		// number of classes, = 2 in regression/one class svm
	
	/** The l. */
	public int l;			// total #SV
	
	/** The SV. */
	svm_node[][] SV;	// SVs (SV[l])
	
	/** The sv_coef. */
	double[][] sv_coef;	// coefficients for SVs in decision functions (sv_coef[k-1][l])
	
	/** The rho. */
	double[] rho;		// constants in decision functions (rho[k*(k-1)/2])
	
	/** The prob a. */
	double[] probA;         // pariwise probability information
	
	/** The prob b. */
	double[] probB;

	// for classification only

	/** The label. */
	int[] label;		// label of each class (label[k])
	
	/** The n sv. */
	int[] nSV;		// number of SVs for each class (nSV[k])
				// nSV[0] + nSV[1] + ... + nSV[k-1] = l
};
