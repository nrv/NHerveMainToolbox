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
package plugins.nherve.toolbox.image.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.concurrent.TaskException;
import plugins.nherve.toolbox.concurrent.TaskManager;
import plugins.nherve.toolbox.image.feature.SegmentableBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.SupportRegionFactory;
import plugins.nherve.toolbox.image.feature.descriptor.GlobalDescriptor;
import plugins.nherve.toolbox.image.feature.descriptor.LocalDescriptor;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class ImageDatabaseIndexer.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ImageDatabaseIndexer extends Algorithm {
	
	/** The db. */
	private ImageDatabase db;
	
	/** The global descriptors. */
	private Map<String, GlobalDescriptor<SegmentableBufferedImage, VectorSignature>> globalDescriptors;

	/** The region factories. */
	private Map<String, SupportRegionFactory> regionFactories;
	
	/** The local descriptors. */
	private Map<String, LocalDescriptor<SegmentableBufferedImage, VectorSignature>> localDescriptors;
	
	/** The factory for local descriptor. */
	private Map<String, String> factoryForLocalDescriptor;

	/** The entry descriptors. */
	private Map<String, GlobalDescriptor<ImageEntry, VectorSignature>> entryDescriptors;

	/** The load images. */
	private boolean loadImages;

	/**
	 * Instantiates a new image database indexer.
	 * 
	 * @param db
	 *            the db
	 */
	public ImageDatabaseIndexer(ImageDatabase db) {
		super();
		this.db = db;
		this.loadImages = true;
		this.globalDescriptors = new HashMap<String, GlobalDescriptor<SegmentableBufferedImage, VectorSignature>>();
		this.regionFactories = new HashMap<String, SupportRegionFactory>();
		this.localDescriptors = new HashMap<String, LocalDescriptor<SegmentableBufferedImage, VectorSignature>>();
		this.factoryForLocalDescriptor = new HashMap<String, String>();
		this.entryDescriptors = new HashMap<String, GlobalDescriptor<ImageEntry, VectorSignature>>();
	}

	/**
	 * The Class SingleImageWorker.
	 * 
	 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
	 */
	private class SingleImageWorker implements Callable<Integer> {
		
		/** The e. */
		private ImageEntry e;

		/**
		 * Instantiates a new single image worker.
		 * 
		 * @param e
		 *            the e
		 */
		public SingleImageWorker(ImageEntry e) {
			super();
			this.e = e;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Integer call() throws Exception {
			try {
				if (globalDescriptors.size() + localDescriptors.size() + entryDescriptors.size() > 0) {
					if (loadImages) {
						db.loadImage(e);
					}
					SegmentableBufferedImage sbi = new SegmentableBufferedImage(e.getImage());
					sbi.setName(e.getFile());

					Map<String, List<SupportRegion>> srCache = new HashMap<String, List<SupportRegion>>();
					for (String name : localDescriptors.keySet()) {
						List<SupportRegion> sr = null;
						String srn = factoryForLocalDescriptor.get(name);
						if (srn != null) {
							if (srCache.containsKey(srn)) {
								sr = srCache.get(srn);
							} else {
								sr = regionFactories.get(srn).extractRegions(sbi);
								srCache.put(srn, sr);
							}
						}
						LocalDescriptor<SegmentableBufferedImage, VectorSignature> desc = localDescriptors.get(name);
						desc.preProcess(sbi);
						BagOfSignatures<VectorSignature> bag = new BagOfSignatures<VectorSignature>();
						for (SupportRegion reg : sr) {
							VectorSignature sig = desc.extractLocalSignature(sbi, reg);
							bag.add(sig);
						}
						desc.postProcess(sbi);
						e.putSignature(name, bag);
					}
					srCache.clear();
					srCache = null;

					for (String name : globalDescriptors.keySet()) {
						GlobalDescriptor<SegmentableBufferedImage, VectorSignature> desc = globalDescriptors.get(name);
						desc.preProcess(sbi);
						VectorSignature sig = desc.extractGlobalSignature(sbi);
						desc.postProcess(sbi);
						e.putSignature(name, sig);
						
						// System.out.println(sig);
					}

					for (String name : entryDescriptors.keySet()) {
						GlobalDescriptor<ImageEntry, VectorSignature> desc = entryDescriptors.get(name);
						desc.preProcess(e);
						VectorSignature sig = desc.extractGlobalSignature(e);
						desc.postProcess(e);
						e.putSignature(name, sig);
					}

					db.unloadImage(e);
				}
				return 0;
			} catch (Exception error) {
				e.setError(error);
				return 1;
			}
		}

	}

	/**
	 * Instantiates a new image database indexer.
	 * 
	 * @param db
	 *            the db
	 * @param name
	 *            the name
	 * @param desc
	 *            the desc
	 */
	public ImageDatabaseIndexer(ImageDatabase db, String name, GlobalDescriptor<SegmentableBufferedImage, VectorSignature> desc) {
		this(db);
		addGlobalDescriptor(name, desc);
	}

	/**
	 * Adds the global descriptor.
	 * 
	 * @param name
	 *            the name
	 * @param desc
	 *            the desc
	 */
	public void addGlobalDescriptor(String name, GlobalDescriptor<SegmentableBufferedImage, VectorSignature> desc) {
		globalDescriptors.put(name, desc);
	}

	/**
	 * Adds the entry descriptor.
	 * 
	 * @param name
	 *            the name
	 * @param desc
	 *            the desc
	 */
	public void addEntryDescriptor(String name, GlobalDescriptor<ImageEntry, VectorSignature> desc) {
		entryDescriptors.put(name, desc);
	}

	/**
	 * Adds the local descriptor.
	 * 
	 * @param name
	 *            the name
	 * @param rf
	 *            the rf
	 * @param desc
	 *            the desc
	 */
	public void addLocalDescriptor(String name, String rf, LocalDescriptor<SegmentableBufferedImage, VectorSignature> desc) {
		factoryForLocalDescriptor.put(name, rf);
		localDescriptors.put(name, desc);
	}

	/**
	 * Adds the region factory.
	 * 
	 * @param name
	 *            the name
	 * @param rf
	 *            the rf
	 */
	public void addRegionFactory(String name, SupportRegionFactory rf) {
		regionFactories.put(name, rf);
	}

	/**
	 * Launch.
	 */
	public synchronized void launch() {
		TaskManager tm = TaskManager.getMainInstance();

		loadImages = !regionFactories.isEmpty();
		if (!loadImages) {
			for (LocalDescriptor<SegmentableBufferedImage, VectorSignature> ld : localDescriptors.values()) {
				if (ld.needToLoadSegmentable()) {
					loadImages = true;
					break;
				}
			}
			if (!loadImages) {
				for (GlobalDescriptor<SegmentableBufferedImage, VectorSignature> gd : globalDescriptors.values()) {
					if (gd.needToLoadSegmentable()) {
						loadImages = true;
						break;
					}
				}
				if (!loadImages) {
					for (GlobalDescriptor<ImageEntry, VectorSignature> bd : entryDescriptors.values()) {
						if (bd.needToLoadSegmentable()) {
							loadImages = true;
							break;
						}
					}
				}
			}
		}
		
		try {
			for (LocalDescriptor<SegmentableBufferedImage, VectorSignature> d : localDescriptors.values()) {
				d.initForDatabase(db);
			}
			for (GlobalDescriptor<SegmentableBufferedImage, VectorSignature> d : globalDescriptors.values()) {
				d.initForDatabase(db);
			}
			for (GlobalDescriptor<ImageEntry, VectorSignature> d : entryDescriptors.values()) {
				d.initForDatabase(db);
			}
		} catch (SignatureException e1) {
			e1.printStackTrace();
		}

		List<Future<Integer>> results = new ArrayList<Future<Integer>>();
		for (ImageEntry e : db) {
			results.add(tm.submit(new SingleImageWorker(e)));
		}
		try {
			tm.waitResults(results, "ImageDatabaseIndexer", 1000);
		} catch (TaskException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
