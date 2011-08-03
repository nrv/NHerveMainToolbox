package plugins.nherve.toolbox.image.feature.descriptor;

import icy.system.profile.CPUMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.concurrent.MultipleDataTask;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.descriptor.MultiThreadedSignatureExtractor.Listener;
import plugins.nherve.toolbox.image.feature.region.Pixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

public class MultiThreadedExecutionContext extends Algorithm {
	
	private final TaskManager tm;
	private final List<Listener> listeners;


	/** The image. */
	private Segmentable image;

	/** The done. */
	private int done;

	/** The done pct. */
	private int donePct;

	/** The step pct. */
	private int stepPct;

	/** The ld. */
	private LocalDescriptor<Segmentable, ? extends Signature> ld;

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
	public MultiThreadedExecutionContext(Segmentable image, SupportRegion[] regions, LocalDescriptor<Segmentable, ? extends Signature> descriptor, TaskManager tm, List<Listener> listeners) {
		super();
		
		this.tm =tm;
		this.listeners = listeners;
		
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
	public MultiThreadedExecutionContext(Segmentable image, LocalDescriptor<Segmentable, ? extends Signature> descriptor, TaskManager tm, List<Listener> listeners) {
		this(image, null, descriptor, tm, listeners);
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
	public LocalDescriptor<Segmentable, ? extends Signature> getDescriptor() {
		return ld;
	}

	/**
	 * Gets the image.
	 * 
	 * @return the image
	 */
	public Segmentable getImage() {
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

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

}
