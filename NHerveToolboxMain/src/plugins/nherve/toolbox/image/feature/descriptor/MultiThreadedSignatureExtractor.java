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

import icy.system.profile.CPUMonitor;

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
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
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
	public Signature[] extractSignatures(T img, IcySupportRegion[] regions, boolean doPreprocess) throws SignatureException {
		if (!(getDescriptor() instanceof LocalDescriptor)) {
			throw new SignatureException("Unable to extract a local signatures with this descriptor");
		}
		LocalDescriptor<T, ? extends Signature, IcyPixel> ld = (LocalDescriptor<T, ? extends Signature, IcyPixel>) getDescriptor();

		if (regions != null) {
			log("MultiThreadedSignatureExtractor() - Launching " + regions.length + " signatures extraction ...");
		} else {
			log("MultiThreadedSignatureExtractor() - Launching signatures extraction for each pixel (" + img.getHeight() * img.getWidth() + ") ...");
		}

		if (doPreprocess) {
			getDescriptor().preProcess(img);
		}

		MultiThreadedExecutionContext f = new MultiThreadedExecutionContext(img, regions, (LocalDescriptor<Segmentable, ? extends Signature, IcyPixel>) ld, tm, listeners);
		f.start();

		if (f.isInterrupted()) {
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
		return extractSignatures(img, (IcySupportRegion[]) null);
	}

	public void setTm(TaskManager tm) {
		this.tm = tm;
	}

	public boolean add(Listener e) {
		return listeners.add(e);
	}
}
