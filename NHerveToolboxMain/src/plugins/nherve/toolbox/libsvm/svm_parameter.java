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
package plugins.nherve.toolbox.libsvm;

/**
 * The Class svm_parameter.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class svm_parameter implements Cloneable,java.io.Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5828548312895209767L;

	/* svm_type */
	/** The Constant C_SVC. */
	public static final int C_SVC = 0;
	
	/** The Constant NU_SVC. */
	public static final int NU_SVC = 1;
	
	/** The Constant ONE_CLASS. */
	public static final int ONE_CLASS = 2;
	
	/** The Constant EPSILON_SVR. */
	public static final int EPSILON_SVR = 3;
	
	/** The Constant NU_SVR. */
	public static final int NU_SVR = 4;

	/* kernel_type */
	/** The Constant LINEAR. */
	public static final int LINEAR = 0;
	
	/** The Constant POLY. */
	public static final int POLY = 1;
	
	/** The Constant RBF. */
	public static final int RBF = 2;
	
	/** The Constant SIGMOID. */
	public static final int SIGMOID = 3;
	
	/** The Constant PRECOMPUTED. */
	public static final int PRECOMPUTED = 4;
	
	/** The Constant TRIANGULAR. */
	public static final int TRIANGULAR = 5;
	
	/** The Constant LAPLACE. */
	public static final int LAPLACE = 6;
	
	/** The Constant CHI2. */
	public static final int CHI2 = 7;

	/** The svm_type. */
	public int svm_type;
	
	/** The kernel_type. */
	public int kernel_type;
	
	/** The degree. */
	public int degree;	// for poly
	
	/** The gamma. */
	public double gamma;	// for poly/rbf/sigmoid
	
	/** The coef0. */
	public double coef0;	// for poly/sigmoid

	// these are for training only
	/** The cache_size. */
	public double cache_size; // in MB
	
	/** The eps. */
	public double eps;	// stopping criteria
	
	/** The C. */
	public double C;	// for C_SVC, EPSILON_SVR and NU_SVR
	
	/** The nr_weight. */
	public int nr_weight;		// for C_SVC
	
	/** The weight_label. */
	public int[] weight_label;	// for C_SVC
	
	/** The weight. */
	public double[] weight;		// for C_SVC
	
	/** The nu. */
	public double nu;	// for NU_SVC, ONE_CLASS, and NU_SVR
	
	/** The p. */
	public double p;	// for EPSILON_SVR
	
	/** The shrinking. */
	public int shrinking;	// use the shrinking heuristics
	
	/** The probability. */
	public int probability; // do probability estimates

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() 
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			return null;
		}
	}

}
