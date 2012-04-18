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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.db.ImageDatabaseSplit;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.libsvm.svm;
import plugins.nherve.toolbox.libsvm.svm_model;
import plugins.nherve.toolbox.libsvm.svm_node;
import plugins.nherve.toolbox.libsvm.svm_parameter;
import plugins.nherve.toolbox.libsvm.svm_problem;


/**
 * The Class SVMClassifier.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SVMClassifier extends LearningAlgorithm {
	
	/** The Constant SCORE_OFFSET. */
	public final static double SCORE_OFFSET = 100d;
	
	/** The param. */
	private svm_parameter param;
	
	/** The prob. */
	private svm_problem prob;
	
	/** The model. */
	private svm_model model;
	
	/** The sig size. */
	private int sigSize;
	
	/** The balance weight. */
	private boolean balanceWeight;

	/**
	 * Instantiates a new sVM classifier.
	 */
	public SVMClassifier() {
		super();
		balanceWeight = false;
	}

	/**
	 * Creates the problem.
	 * 
	 * @param positive
	 *            the positive
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void createProblem(List<VectorSignature> positive) throws SignatureException {
		VectorSignature[] p = (VectorSignature[]) positive.toArray(new VectorSignature[positive.size()]);
		createProblem(p);
	}

	/**
	 * Creates the problem.
	 * 
	 * @param positive
	 *            the positive
	 * @param negative
	 *            the negative
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void createProblem(List<VectorSignature> positive, List<VectorSignature> negative) throws SignatureException {
		VectorSignature[] p = (VectorSignature[]) positive.toArray(new VectorSignature[positive.size()]);
		VectorSignature[] n = (VectorSignature[]) negative.toArray(new VectorSignature[negative.size()]);
		createProblem(p, n);
	}

	/**
	 * Creates the problem.
	 * 
	 * @param positive
	 *            the positive
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void createProblem(VectorSignature[] positive) throws SignatureException {

		log("SVM create problem pos(" + positive.length + ")");

		sigSize = ((VectorSignature) positive[0]).getSize();

		param = new svm_parameter();

		param.svm_type = svm_parameter.ONE_CLASS;
		param.kernel_type = svm_parameter.TRIANGULAR;
		param.degree = 3;
		param.gamma = 1.0 / sigSize;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		prob = new svm_problem();
		prob.l = positive.length;

		prob.x = new svm_node[prob.l][];
		prob.y = new double[prob.l];

		for (int i = 0; i < positive.length; i++) {
			prob.x[i] = getNode((VectorSignature) positive[i]);
			prob.y[i] = 1;
		}
	}

	/**
	 * Creates the problem.
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
	public void createProblem(ImageDatabaseSplit split, String posClass, String desc) throws ClassifierException {
		try {
			setModelInfo(posClass);
			List<VectorSignature> pos = split.getLrnSignatures(posClass, true, desc);
			List<VectorSignature> neg = split.getLrnSignatures(posClass, false, desc);
			createProblem(pos, neg);
		} catch (FeatureException e) {
			throw new ClassifierException(e);
		}
	}

	/**
	 * Creates the problem.
	 * 
	 * @param positive
	 *            the positive
	 * @param negative
	 *            the negative
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void createProblem(VectorSignature[] positive, VectorSignature[] negative) throws SignatureException {
		VectorSignature[] pa = positive;
		VectorSignature[] na = negative;

		try {
			if (hasDataProcessor() && (!isLearnDataProcessed())) {
				VectorSignature[][] data = dataProcess(positive, negative);
				pa = data[0];
				na = data[1];
			}
		} catch (ClassifierException e) {
			throw new SignatureException(e);
		}

		sigSize = pa[0].getSize();

		log(pa[0].toString());

		log("SVM create problem (" + (pa.length + na.length) + ") : pos(" + pa.length + "), neg(" + na.length + "), sig size(" + sigSize + ")");

		param = new svm_parameter();

		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.TRIANGULAR;
		param.degree = 3;
		param.gamma = 1.0 / sigSize;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;

		prob = new svm_problem();
		prob.l = pa.length + na.length;

		if (balanceWeight) {
			param.nr_weight = 2;
			param.weight_label = new int[2];
			param.weight_label[0] = +1;
			param.weight_label[1] = -1;
			param.weight = new double[2];
			param.weight[0] = (double) na.length / (double) prob.l;
			param.weight[1] = (double) pa.length / (double) prob.l;
		} else {
			param.nr_weight = 0;
			param.weight_label = null;
			param.weight = null;
		}

		prob.x = new svm_node[prob.l][];
		prob.y = new double[prob.l];

		for (int i = 0; i < positive.length; i++) {
			prob.x[i] = getNode(pa[i]);
			prob.y[i] = 1;
		}

		int idx = 0;
		for (int i = 0; i < negative.length; i++) {
			idx = pa.length + i;
			prob.x[idx] = getNode(na[i]);
			prob.y[idx] = -1;
		}
	}

	/**
	 * Cross validation.
	 */
	public void crossValidation() {
		double[] target = new double[prob.l];

		for (int pc = -10; pc < 16; pc++) {
			param.C = Math.pow(2, pc);
			// for (int pg = -5; pg < 6; pg++) {
			// param.gamma = Math.pow(2, pg);
			svm.svm_cross_validation(prob, param, 50, target);

			int total_correct = 0;
			for (int i = 0; i < prob.l; i++) {
				if (target[i] == prob.y[i]) {
					++total_correct;
				}
			}
			out("C = " + param.C + ", gamma = " + param.gamma + " - " + 100.0 * total_correct / prob.l + "%");
			// }
		}

	}
	
	/**
	 * Save model.
	 * 
	 * @param f
	 *            the f
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void saveModel(File f) throws IOException {
		svm.svm_save_model(f.getAbsolutePath(), model);
	}
	
	public void saveModel(OutputStream out) throws IOException {
		svm.svm_save_model(out, model);
	}
	
	public void loadModel(InputStream is) throws IOException {
		model = svm.svm_load_model(is);
		
		if (model == null) {
			throw new IOException("unable to load the svm model");
		}
	}
	
	/**
	 * Load model.
	 * 
	 * @param f
	 *            the f
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void loadModel(File f) throws IOException {
		model = svm.svm_load_model(f.getAbsolutePath());
	}

	/**
	 * Gets the node.
	 * 
	 * @param sig
	 *            the sig
	 * @return the node
	 * @throws SignatureException
	 *             the signature exception
	 */
	private svm_node[] getNode(VectorSignature sig) throws SignatureException {
		svm_node[] x = new svm_node[sig.getNonZeroBins()];
		int ix = 0;

		for (int d : sig) {
			x[ix] = new svm_node();
			x[ix].index = d;
			x[ix].value = sig.get(d);
			ix++;
		}

		return x;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.LearningAlgorithm#isPositiveImpl(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	protected boolean isPositiveImpl(VectorSignature sig) throws ClassifierException {
		try {
			return predictImpl(sig) > 0;
		} catch (SignatureException e) {
			throw new ClassifierException(e);
		}
	}

	/**
	 * Learn.
	 * 
	 * @param positive
	 *            the positive
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public void learn(List<VectorSignature> positive) throws ClassifierException {
		VectorSignature[] p = (VectorSignature[]) positive.toArray(new VectorSignature[positive.size()]);
		learn(p);
	}

	/**
	 * Learn.
	 * 
	 * @param positive
	 *            the positive
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public void learn(VectorSignature[] positive) throws ClassifierException {
		try {
			createProblem(positive);
			learnModel();
		} catch (SignatureException e) {
			throw new ClassifierException(e);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.LearningAlgorithm#learnImpl(plugins.nherve.toolbox.image.feature.signature.VectorSignature[], plugins.nherve.toolbox.image.feature.signature.VectorSignature[])
	 */
	@Override
	protected void learnImpl(VectorSignature[] positive, VectorSignature[] negative) throws ClassifierException {
		try {
			createProblem(positive, negative);
			learnModel();
		} catch (SignatureException e) {
			throw new ClassifierException(e);
		}
	}

	/**
	 * Learn model.
	 * 
	 * @throws SignatureException
	 *             the signature exception
	 */
	public void learnModel() throws SignatureException {
		model = svm.svm_train(prob, param);
	}
	
	public void cleanAfterLearn() {
		prob = null;
		param = null;
	}

	/**
	 * Predict impl.
	 * 
	 * @param sig
	 *            the sig
	 * @return the double
	 * @throws SignatureException
	 *             the signature exception
	 */
	private double predictImpl(VectorSignature sig) throws SignatureException {
		svm_node[] x = getNode(sig);
		return svm.svm_predict(model, x);
	}

	/**
	 * Predict.
	 * 
	 * @param sig
	 *            the sig
	 * @return the double
	 * @throws SignatureException
	 *             the signature exception
	 */
	public double predict(VectorSignature sig) throws SignatureException {
		if (hasDataProcessor()) {
			return predictImpl(getDataProcessor().apply(sig));
		} else {
			return predictImpl(sig);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.learning.LearningAlgorithm#scoreImpl(plugins.nherve.toolbox.image.feature.signature.VectorSignature)
	 */
	@Override
	protected double scoreImpl(VectorSignature sig) throws ClassifierException {
		return SCORE_OFFSET + rawScore(sig);
	}

	/**
	 * Raw score.
	 * 
	 * @param sig
	 *            the sig
	 * @return the double
	 * @throws ClassifierException
	 *             the classifier exception
	 */
	public double rawScore(VectorSignature sig) throws ClassifierException {
		try {
			double[] res = new double[1];
			svm_node[] x = getNode(sig);
			svm.svm_predict_values(model, x, res);
			return res[0];
		} catch (SignatureException e) {
			throw new ClassifierException(e);
		}
	}

	/**
	 * Sets the c.
	 * 
	 * @param c
	 *            the new c
	 */
	public void setC(double c) {
		param.C = c;
	}

	/**
	 * Sets the gamma.
	 * 
	 * @param g
	 *            the new gamma
	 */
	public void setGamma(double g) {
		param.gamma = g;
	}

	/**
	 * Gets the c.
	 * 
	 * @return the c
	 */
	public double getC() {
		return param.C;
	}

	/**
	 * Gets the gamma.
	 * 
	 * @return the gamma
	 */
	public double getGamma() {
		return param.gamma;
	}

	/**
	 * Sets the nu.
	 * 
	 * @param n
	 *            the new nu
	 */
	public void setNu(double n) {
		param.nu = n;
	}

	/**
	 * Sets the kernel.
	 * 
	 * @param k
	 *            the new kernel
	 */
	public void setKernel(int k) {
		param.kernel_type = k;
	}

	/**
	 * Gets the nb support vector.
	 * 
	 * @return the nb support vector
	 */
	public int getNbSupportVector() {
		return model.l;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + svm.kernel_type_table[param.kernel_type] + ", C = " + param.C + ", gamma = " + param.gamma + "]";
	}

	/**
	 * Checks if is balance weight.
	 * 
	 * @return true, if is balance weight
	 */
	public boolean isBalanceWeight() {
		return balanceWeight;
	}

	/**
	 * Sets the balance weight.
	 * 
	 * @param balanceWeight
	 *            the new balance weight
	 */
	public void setBalanceWeight(boolean balanceWeight) {
		this.balanceWeight = balanceWeight;
	}
}
