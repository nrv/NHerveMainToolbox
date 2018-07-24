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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import plugins.nherve.matrix.Matrix;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;

/**
 * The Class LDA.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class LDA extends DimensionReductionAlgorithm {
	
	/** The classes. */
	private List<Integer> classes;
	
	/** The inv pooled. */
	private Matrix invPooled;
	
	/** The const stuff. */
	private Matrix constStuff;
	
	/** The classes mean. */
	private HashMap<Integer, Matrix> classesMean;
	
	/** The nb groups. */
	int nbGroups;

	/**
	 * Instantiates a new lDA.
	 * 
	 * @param signatures
	 *            the signatures
	 * @param classes
	 *            the classes
	 */
	public LDA(List<DefaultVectorSignature> signatures, List<Integer> classes) {
		super(signatures);
		this.classes = classes;
		this.invPooled = null;
		this.classesMean = null;
		this.constStuff = null;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.DimensionReductionAlgorithm#compute()
	 */
	@Override
	public void compute() throws SignatureException {
		check();
		
		Matrix m = getMatrix(signatures);
		Matrix globalMean = getMean(m);
		if (isLogEnabled()) {
			info("Global mean : ");
			globalMean.print(20, 15);
		}
		
		HashMap<Integer, Integer> classesCardinality = new HashMap<Integer, Integer>();
		for (int idx = 0; idx < m.getRowDimension(); idx++) {
			int c = classes.get(idx);
			if (classesCardinality.containsKey(c)) {
				classesCardinality.put(c, classesCardinality.get(c) + 1);
			} else {
				classesCardinality.put(c, 1);
			}
		}
		nbGroups = classesCardinality.size();
		
		HashMap<Integer, Matrix> classesMatrix = new HashMap<Integer, Matrix>();
		for (int g : classesCardinality.keySet()) {
			info("Class " + g + " has " + classesCardinality.get(g) + " members");
			classesMatrix.put(g, new Matrix(classesCardinality.get(g), dim));
		}
		
		for (int idx = 0; idx < m.getRowDimension(); idx++) {
			int c = classes.get(idx);
			Matrix mc = classesMatrix.get(c);
			int cc = classesCardinality.get(c) - 1;
			for (int d = 0; d < dim; d++) {
				mc.set(cc, d, m.get(idx, d));
			}
			classesCardinality.put(c, cc);
		}
		
		classesMean = new HashMap<Integer, Matrix>();
		for (int g : classesMatrix.keySet()) {
			Matrix lm = getMean(classesMatrix.get(g));
			if (isLogEnabled()) {
				info("Class " + g + " mean : ");
				lm.print(20, 15);
			}
			classesMean.put(g, lm);
		}
		
		for (int g : classesMatrix.keySet()) {
			Matrix cm = classesMatrix.get(g);
			Matrix ones = new Matrix(cm.getRowDimension(), 1, -1);
			cm.plusEquals(ones.times(globalMean));
		}
				
		HashMap<Integer, Matrix> varcov = new HashMap<Integer, Matrix>();
		for (int g : classesMatrix.keySet()) {
			Matrix mx = getVarCovMatrix(classesMatrix.get(g));
			if (isLogEnabled()) {
				info("C"+g+": ");
				mx.print(20, 15);
			}
			varcov.put(g, mx);
		}
		
		Matrix pooled = new Matrix(dim, dim, 0);
		for (int g : classesMatrix.keySet()) {
			Matrix vc = varcov.get(g);
			pooled.plusEquals(vc.times(classesMatrix.get(g).getRowDimension()));
		}
		pooled.timesEquals(1.0 / (double) m.getRowDimension());
		
		if (isLogEnabled()) {
			info("Pooled C: ");
			pooled.print(20, 15);
		}
		
		invPooled = pooled.inverse();
		
		Matrix p = new Matrix(1, nbGroups);
		for (int g : classesMatrix.keySet()) {
			p.set(0, g, Math.log((double)classesMatrix.get(g).getRowDimension() / (double)m.getRowDimension()));
		}
		
		constStuff = new Matrix(1, nbGroups);
		for (int g : classesMatrix.keySet()) {
			Matrix mn = classesMean.get(g);
			double c = p.get(0, g) - 0.5 * ((mn.times(invPooled)).times(mn.transpose())).get(0, 0);
			constStuff.set(0, g, c);
		}
		
		info("LDA done");
	}

	/**
	 * Project.
	 * 
	 * @param toProject
	 *            the to project
	 * @return the vector signature
	 * @throws SignatureException
	 *             the signature exception
	 */
	public DefaultVectorSignature project(DefaultVectorSignature toProject) throws SignatureException {
		DenseVectorSignature vs = new DenseVectorSignature(Math.max(nbGroups, 3));
		ArrayList<DefaultVectorSignature> a = new ArrayList<DefaultVectorSignature>();
		a.add(toProject);
		Matrix x = getMatrix(a);
		
		
		for (int g = 0; g < nbGroups; g++) {
			Matrix mn = classesMean.get(g);
			double c = ((mn.times(invPooled)).times(x.transpose())).get(0, 0);
			vs.set(g, c + constStuff.get(0, g));
		}
		
		return vs;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.DimensionReductionAlgorithm#project(java.util.List)
	 */
	@Override
	public List<DefaultVectorSignature> project(List<DefaultVectorSignature> toProject) throws SignatureException {
		ArrayList<DefaultVectorSignature> proj = new ArrayList<DefaultVectorSignature>();
		for (DefaultVectorSignature vs : toProject) {
			proj.add(project(vs));
		}
		return proj;
	}
}
