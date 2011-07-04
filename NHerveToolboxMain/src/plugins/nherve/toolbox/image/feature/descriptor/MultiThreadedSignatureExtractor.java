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
package plugins.nherve.toolbox.image.feature.descriptor;

import icy.system.CPUMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.SignatureExtractor;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

/**
 * The Class MultiThreadedSignatureExtractor.
 * 
 * @param <T>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class MultiThreadedSignatureExtractor<T extends Segmentable> extends SignatureExtractor<T> {
	public interface Listener {
		void notifyProgress(int nb, int total);
	}

	private TaskManager tm;
	private List<Listener> listeners;

	/**
	 * The Class ExecutionContext.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	public class ExecutionContext {

		/** The image. */
		private T image;

		/** The done. */
		private int done;

		/** The done pct. */
		private int donePct;

		/** The step pct. */
		private int stepPct;

		/** The ld. */
		private LocalDescriptor<T, ? extends Signature> ld;

		/** The regions. */
		private SupportRegion[] regions;

		/** The result. */
		private Signature[] result;

		/** The errors. */
		private Map<Integer, SignatureException> errors;

		/** The cpu. */
		private CPUMonitor cpu;

		/** The nbr. */
		private int nbr;

		/** The do all pixels. */
		private boolean doAllPixels;

		private boolean interrupted;

		/**
		 * The Class SignatureExtractionWorker.
		 * 
		 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
		 */
		public class SignatureExtractionWorker extends MultipleDataTask<SupportRegion, Integer> {

			/**
			 * Instantiates a new signature extraction worker.
			 * 
			 * @param allData
			 *            the all data
			 * @param idx1
			 *            the idx1
			 * @param idx2
			 *            the idx2
			 */
			public SignatureExtractionWorker(List<SupportRegion> allData, int idx1, int idx2) {
				super(allData, idx1, idx2);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * plugins.nherve.toolbox.concurrent.MultipleDataTask#call(java.
			 * lang.Object, int)
			 */
			@Override
			public void call(SupportRegion data, int idx) throws Exception {
				try {
					Signature sig = null;
					sig = ld.extractLocalSignature(getImage(), data);
					setResult(idx, sig);
				} catch (SignatureException e) {
					logError("On region " + data + " : " + e.getMessage());
					addError(idx, e);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * plugins.nherve.toolbox.concurrent.MultipleDataTask#outputCall()
			 */
			@Override
			public Integer outputCall() throws Exception {
				return 0;
			}

			@Override
			public void processContextualData() {
			}

		}

		/**
		 * The Class PixelLineSignatureExtractionWorker.
		 * 
		 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
		 */
		public class PixelLineSignatureExtractionWorker implements Callable<Integer> {

			/** The line. */
			private int line;

			/** The width. */
			private int width;

			/**
			 * Instantiates a new pixel line signature extraction worker.
			 * 
			 * @param line
			 *            the line
			 * @param width
			 *            the width
			 */
			public PixelLineSignatureExtractionWorker(int line, int width) {
				super();
				this.line = line;
				this.width = width;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Integer call() {
				int idx = width * line;

				Signature sig = null;

				for (int x = 0; x < width; x++) {
					try {
						sig = ld.extractLocalSignature(getImage(), new Pixel(x, line));
						setResult(idx, sig);
					} catch (SignatureException e) {
						logError("On pixel " + x + "x" + line + " : " + e.getMessage());
						addError(idx, e);
					}
					idx++;
				}

				return 0;
			}
		}

		/**
		 * Instantiates a new execution context.
		 * 
		 * @param image
		 *            the image
		 * @param regions
		 *            the regions
		 * @param descriptor
		 *            the descriptor
		 */
		public ExecutionContext(T image, SupportRegion[] regions, LocalDescriptor<T, ? extends Signature> descriptor) {
			super();
			this.image = image;
			this.regions = regions;
			this.ld = descriptor;
			cpu = new CPUMonitor();
			doAllPixels = (regions == null);
			nbr = 0;
		}

		/**
		 * Instantiates a new execution context.
		 * 
		 * @param image
		 *            the image
		 * @param descriptor
		 *            the descriptor
		 */
		public ExecutionContext(T image, LocalDescriptor<T, ? extends Signature> descriptor) {
			this(image, null, descriptor);
		}

		/**
		 * Sets the result.
		 * 
		 * @param idx
		 *            the idx
		 * @param sig
		 *            the sig
		 */
		public synchronized void setResult(int idx, Signature sig) {
			result[idx] = sig;
			done++;
			if (isLogEnabled() || !listeners.isEmpty()) {
				while (done >= donePct) {

					for (Listener l : listeners) {
						l.notifyProgress(done, result.length);
					}

					if (isLogEnabled()) {
						cpu.stop();
						log(" ~ " + done * 100.0 / nbr + " % ~ " + cpu.getElapsedTimeMilli() / 1000.0 + " s");
						cpu.start();
					}
					donePct += stepPct;
				}
			}
		}

		/**
		 * Start.
		 */
		public void start() {
			if (isLogEnabled()) {
				cpu.start();
			}

			interrupted = false;

			if (doAllPixels) {
				nbr = image.getHeight() * image.getWidth();
			} else {
				nbr = regions.length;
			}

			result = new Signature[nbr];
			errors = new HashMap<Integer, SignatureException>();
			done = 0;
			stepPct = nbr / 100;
			if (stepPct == 0) {
				stepPct = 1;
			}
			donePct = stepPct;

			if (doAllPixels) {
				List<Future<Integer>> poolResults = new ArrayList<Future<Integer>>();
				for (int y = 0; y < image.getHeight(); y++) {
					poolResults.add(tm.submit(new PixelLineSignatureExtractionWorker(y, image.getWidth())));
				}
				try {
					tm.waitResults(poolResults, "Signatures extraction", 0);
				} catch (TaskException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					interrupted = true;
					log("MultiThreadedSignatureExtractor interrupted 1");
				}
			} else {
				try {
					tm.submitMultiForAll(Arrays.asList(regions), SignatureExtractionWorker.class, this, "Signatures extraction", 0);
				} catch (TaskException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					interrupted = true;
					log("MultiThreadedSignatureExtractor interrupted 2");
				}
			}

			if (isLogEnabled()) {
				cpu.stop();
			}
		}

		/**
		 * Gets the result.
		 * 
		 * @return the result
		 */
		public Signature[] getResult() {
			return result;
		}

		/**
		 * Gets the descriptor.
		 * 
		 * @return the descriptor
		 */
		public LocalDescriptor<T, ? extends Signature> getDescriptor() {
			return ld;
		}

		/**
		 * Gets the image.
		 * 
		 * @return the image
		 */
		public T getImage() {
			return image;
		}

		/**
		 * Adds the error.
		 * 
		 * @param idx
		 *            the idx
		 * @param e
		 *            the e
		 */
		public void addError(int idx, SignatureException e) {
			errors.put(idx, e);
		}

		/**
		 * Checks for errors.
		 * 
		 * @return true, if successful
		 */
		public boolean hasErrors() {
			return errors.size() > 0;
		}

		/**
		 * Gets the errors.
		 * 
		 * @return the errors
		 */
		public Map<Integer, SignatureException> getErrors() {
			return errors;
		}
	}

	/**
	 * Instantiates a new multi threaded signature extractor.
	 * 
	 * @param descriptor
	 *            the descriptor
	 */
	public MultiThreadedSignatureExtractor(DefaultDescriptorImpl<T, ? extends Signature> descriptor) {
		super(descriptor);

		tm = TaskManager.getSecondLevelInstance();

		listeners = new ArrayList<MultiThreadedSignatureExtractor.Listener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.nherve.toolbox.image.feature.SignatureExtractor#extractSignatures
	 * (plugins.nherve.toolbox.image.feature.Segmentable,
	 * plugins.nherve.toolbox.image.feature.SupportRegion[], boolean)
	 */
	@SuppressWarnings("unchecked")
	public Signature[] extractSignatures(T img, SupportRegion[] regions, boolean doPreprocess) throws SignatureException {
		if (!(getDescriptor() instanceof LocalDescriptor)) {
			throw new SignatureException("Unable to extract a local signatures with this descriptor");
		}
		LocalDescriptor<T, ? extends Signature> ld = (LocalDescriptor<T, ? extends Signature>) getDescriptor();

		if (regions != null) {
			log("MultiThreadedSignatureExtractor() - Launching " + regions.length + " signatures extraction ...");
		} else {
			log("MultiThreadedSignatureExtractor() - Launching signatures extraction for each pixel (" + img.getHeight() * img.getWidth() + ") ...");
		}

		if (doPreprocess) {
			getDescriptor().preProcess(img);
		}

		ExecutionContext f = new ExecutionContext(img, regions, ld);
		f.start();

		if (f.interrupted) {
			return null;
		}

		if (f.hasErrors()) {
			throw new SignatureException(f.getErrors().size() + " errors found");
		}

		if (doPreprocess) {
			getDescriptor().postProcess(img);
		}

		return f.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.nherve.toolbox.image.feature.SignatureExtractor#extractSignatures
	 * (plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public Signature[] extractSignatures(T img) throws SignatureException {
		return extractSignatures(img, (SupportRegion[]) null);
	}

	public void setTm(TaskManager tm) {
		this.tm = tm;
	}

	public boolean add(Listener e) {
		return listeners.add(e);
	}
}
