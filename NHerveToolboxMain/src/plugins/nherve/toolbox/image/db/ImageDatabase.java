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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignatureConcatenator;

/**
 * The Class ImageDatabase.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ImageDatabase implements Iterable<ImageEntry> {

	/** The Constant VERSION. */
	public final static String VERSION = "ImageDatabase_v1.0.0";

	/** The Constant DESC_SEPARATOR. */
	public final static String DESC_SEPARATOR = "|";

	// private final static String PFX_GLOBAL = "[Global]";
	// private final static String PFX_LOCAL = "[Local]";

	/** The name. */
	private String name;

	/** The image directory. */
	private String imageDirectory;
	
	private String signatureDirectory;

	/** The entries. */
	private List<ImageEntry> entries;

	/** The next id. */
	private int nextId;

	/** The root directory. */
	private transient String rootDirectory;

	/** The available global descriptors. */
	private transient Set<String> availableGlobalDescriptors;

	/** The available local descriptors. */
	private transient Set<String> availableLocalDescriptors;

	/** The all descriptors. */
	private transient Set<String> allDescriptors;

	/** The pos classes entries. */
	private transient Map<String, List<ImageEntry>> posClassesEntries;

	/** The neg classes entries. */
	private transient Map<String, List<ImageEntry>> negClassesEntries;

	/** The utd entries. */
	private transient boolean utdEntries;

	/**
	 * Instantiates a new image database.
	 */
	public ImageDatabase() {
		super();

		entries = new ArrayList<ImageEntry>();
		availableGlobalDescriptors = new TreeSet<String>();
		availableLocalDescriptors = new TreeSet<String>();
		allDescriptors = new TreeSet<String>();
		posClassesEntries = new HashMap<String, List<ImageEntry>>();
		negClassesEntries = new HashMap<String, List<ImageEntry>>();
		utdEntries = false;
		nextId = 0;
	}

	/**
	 * Instantiates a new image database.
	 * 
	 * @param name
	 *            the name
	 * @param rootDirectory
	 *            the root directory
	 */
	public ImageDatabase(String name, String rootDirectory) {
		this(name, rootDirectory, "images", "signatures");
	}

	public ImageDatabase(String name, String rootDirectory, String imageDirectory, String signatureDirectory) {
		this();
		this.rootDirectory = rootDirectory;
		this.name = name;
		this.imageDirectory = imageDirectory;
		this.signatureDirectory = signatureDirectory;
	}

	/**
	 * Adds the.
	 * 
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	public synchronized boolean add(ImageEntry e) {
		utdEntries = false;
		e.setId(nextId++);
		return entries.add(e);
	}

	/**
	 * Gets the available classes.
	 * 
	 * @return the available classes
	 */
	public Set<String> getAvailableClasses() {
		return posClassesEntries.keySet();
	}

	/**
	 * Gets the available global descriptors.
	 * 
	 * @return the available global descriptors
	 */
	public Set<String> getAvailableGlobalDescriptors() {
		return availableGlobalDescriptors;
	}

	/**
	 * Gets the available local descriptors.
	 * 
	 * @return the available local descriptors
	 */
	public Set<String> getAvailableLocalDescriptors() {
		return availableLocalDescriptors;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		utdEntries = false;
		allDescriptors.clear();
		entries.clear();
	}

	/**
	 * Clear descriptors.
	 */
	public void clearDescriptors() {
		allDescriptors.clear();
		for (ImageEntry e : this) {
			e.removeSignatures();
		}
		updateAvailableDescriptors();
	}

	/**
	 * Contains.
	 * 
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	public boolean contains(ImageEntry o) {
		return entries.contains(o);
	}

	/**
	 * Contains class.
	 * 
	 * @param cls
	 *            the cls
	 * @return true, if successful
	 */
	public boolean containsClass(String cls) {
		return posClassesEntries.containsKey(cls);
	}

	/**
	 * Contains descriptor.
	 * 
	 * @param desc
	 *            the desc
	 * @return true, if successful
	 */
	public boolean containsDescriptor(String desc) {
		return containsGlobalDescriptor(desc) || containsLocalDescriptor(desc);
	}

	/**
	 * Contains global descriptor.
	 * 
	 * @param desc
	 *            the desc
	 * @return true, if successful
	 */
	public boolean containsGlobalDescriptor(String desc) {
		return availableGlobalDescriptors.contains(desc);
	}

	/**
	 * Contains local descriptor.
	 * 
	 * @param desc
	 *            the desc
	 * @return true, if successful
	 */
	public boolean containsLocalDescriptor(String desc) {
		return availableLocalDescriptors.contains(desc);
	}

	/**
	 * Gets the.
	 * 
	 * @param index
	 *            the index
	 * @return the image entry
	 */
	public ImageEntry get(int index) {
		return entries.get(index);
	}

	/**
	 * Sets the.
	 * 
	 * @param index
	 *            the index
	 * @param e
	 *            the e
	 */
	public void set(int index, ImageEntry e) {
		entries.set(index, e);
	}

	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	public List<ImageEntry> getEntries() {
		return entries;
	}

	/**
	 * Gets the entries.
	 * 
	 * @param cls
	 *            the cls
	 * @param pos
	 *            the pos
	 * @return the entries
	 */
	public List<ImageEntry> getEntries(String cls, boolean pos) {
		if (containsClass(cls)) {
			if (pos) {
				return posClassesEntries.get(cls);
			} else {
				return negClassesEntries.get(cls);
			}
		} else {
			if (pos) {
				return null;
			} else {
				return entries;
			}

		}
	}

	/**
	 * Gets the global signature.
	 * 
	 * @param entry
	 *            the entry
	 * @param descs
	 *            the descs
	 * @return the global signature
	 * @throws FeatureException
	 *             the feature exception
	 */
	public VectorSignature getGlobalSignature(ImageEntry entry, String descs) throws FeatureException {
		StringTokenizer stk = new StringTokenizer(descs, DESC_SEPARATOR);
		String[] desc = new String[stk.countTokens()];
		int tk = 0;
		while (stk.hasMoreTokens()) {
			desc[tk] = stk.nextToken();
			tk++;
		}
		for (String d : desc) {
			if (!containsGlobalDescriptor(d)) {
				throw new FeatureException("Descriptor " + d + " not available for database " + getName());
			}
		}

		if (desc.length == 0) {
			throw new FeatureException("No descriptor asked for database " + getName());
		}

		VectorSignature res = null;

		if (desc.length > 1) {
			VectorSignatureConcatenator concat = new VectorSignatureConcatenator(VectorSignature.DENSE_VECTOR_SIGNATURE, false);

			for (String d : desc) {
				VectorSignature[] vsa = new VectorSignature[entries.size()];
				int idx = 0;
				for (ImageEntry e : entries) {
					vsa[idx] = e.getGlobalSignatures().get(d);
					idx++;
				}

				concat.add(vsa);
			}

			res = concat.concatenate()[0];
		} else {
			String d = desc[0];
			res = entry.getGlobalSignatures().get(d);
		}

		return res;
	}

	/**
	 * Gets the global signatures.
	 * 
	 * @param entries
	 *            the entries
	 * @param descs
	 *            the descs
	 * @return the global signatures
	 * @throws FeatureException
	 *             the feature exception
	 */
	public List<VectorSignature> getGlobalSignatures(List<ImageEntry> entries, String descs) throws FeatureException {
		StringTokenizer stk = new StringTokenizer(descs, DESC_SEPARATOR);
		String[] desc = new String[stk.countTokens()];
		int tk = 0;
		while (stk.hasMoreTokens()) {
			desc[tk] = stk.nextToken();
			tk++;
		}
		for (String d : desc) {
			if (!containsGlobalDescriptor(d)) {
				throw new FeatureException("Descriptor " + d + " not available for database " + getName());
			}
		}

		if (desc.length == 0) {
			throw new FeatureException("No descriptor asked for database " + getName());
		}

		List<VectorSignature> res = null;

		if (desc.length > 1) {
			VectorSignatureConcatenator concat = new VectorSignatureConcatenator(VectorSignature.DENSE_VECTOR_SIGNATURE, true);

			for (String d : desc) {
				VectorSignature[] vsa = new VectorSignature[entries.size()];
				int idx = 0;
				for (ImageEntry e : entries) {
					vsa[idx] = e.getGlobalSignatures().get(d);
					idx++;
				}

				concat.add(vsa);
			}

			res = Arrays.asList(concat.concatenate());
		} else {
			String d = desc[0];
			res = new ArrayList<VectorSignature>();
			for (ImageEntry e : entries) {
				res.add(e.getGlobalSignatures().get(d));
			}
		}

		return res;
	}

	/**
	 * Gets the global signatures.
	 * 
	 * @param descs
	 *            the descs
	 * @return the global signatures
	 * @throws FeatureException
	 *             the feature exception
	 */
	public List<VectorSignature> getGlobalSignatures(String descs) throws FeatureException {
		return getGlobalSignatures(entries, descs);
	}

	/**
	 * Gets the image directory.
	 * 
	 * @return the image directory
	 */
	public String getImageDirectory() {
		return imageDirectory;
	}

	/**
	 * Gets the local signature.
	 * 
	 * @param entry
	 *            the entry
	 * @param desc
	 *            the desc
	 * @return the local signature
	 * @throws FeatureException
	 *             the feature exception
	 */
	public BagOfSignatures<VectorSignature> getLocalSignature(ImageEntry entry, String desc) throws FeatureException {
		if (!availableLocalDescriptors.contains(desc)) {
			throw new FeatureException("Descriptor " + desc + " not available for database " + getName());
		}
		return entry.getLocalSignatures().get(desc);
	}

	/**
	 * Gets the local signatures.
	 * 
	 * @param desc
	 *            the desc
	 * @return the local signatures
	 * @throws FeatureException
	 *             the feature exception
	 */
	public List<VectorSignature> getLocalSignatures(String desc) throws FeatureException {
		if (!availableLocalDescriptors.contains(desc)) {
			throw new FeatureException("Descriptor " + desc + " not available for database " + getName());
		}
		List<VectorSignature> all = new ArrayList<VectorSignature>();
		for (ImageEntry entry : this) {
			all.addAll(entry.getLocalSignatures().get(desc).getSignatures());
		}
		return all;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the next id.
	 * 
	 * @return the next id
	 */
	public int getNextId() {
		return nextId;
	}

	/**
	 * Gets the root directory.
	 * 
	 * @return the root directory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Gets the root image directory.
	 * 
	 * @return the root image directory
	 */
	public String getRootImageDirectory() {
		return getRootDirectory() + "/" + getImageDirectory();
	}
	
	public String getRootSignatureDirectory() {
		return getRootDirectory() + "/" + getSignatureDirectory();
	}

	/**
	 * Index of.
	 * 
	 * @param o
	 *            the o
	 * @return the int
	 */
	public int indexOf(ImageEntry o) {
		return entries.indexOf(o);
	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ImageEntry> iterator() {
		return entries.iterator();
	}

	/**
	 * Load image.
	 * 
	 * @param e
	 *            the e
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void loadImage(ImageEntry e) throws IOException {
		e.loadImage(getRootImageDirectory());
	}

	/**
	 * Removes the.
	 * 
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	public boolean remove(ImageEntry o) {
		utdEntries = false;
		return entries.remove(o);
	}

	/**
	 * Removes the.
	 * 
	 * @param index
	 *            the index
	 * @return the image entry
	 */
	public ImageEntry remove(int index) {
		utdEntries = false;
		return entries.remove(index);
	}

	/**
	 * Sets the entries.
	 * 
	 * @param entries
	 *            the new entries
	 */
	public void setEntries(List<ImageEntry> entries) {
		utdEntries = false;
		this.entries = entries;
	}

	/**
	 * Sets the image directory.
	 * 
	 * @param imageDirectory
	 *            the new image directory
	 */
	public void setImageDirectory(String imageDirectory) {
		this.imageDirectory = imageDirectory;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the next id.
	 * 
	 * @param nextId
	 *            the new next id
	 */
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}

	/**
	 * Sets the root directory.
	 * 
	 * @param rootDirectory
	 *            the new root directory
	 */
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size() {
		return entries.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImageDatabase [entries=" + entries.size() + ", name=" + name + "]";
	}

	/**
	 * Unload image.
	 * 
	 * @param e
	 *            the e
	 */
	public void unloadImage(ImageEntry e) {
		e.unloadImage();
	}

	/**
	 * Update available descriptors.
	 */
	public void updateAvailableDescriptors() {
		availableGlobalDescriptors = new TreeSet<String>();
		availableLocalDescriptors = new TreeSet<String>();

		for (ImageEntry e : this) {
			for (String s : e.getGlobalSignatures().keySet()) {
				if (!availableGlobalDescriptors.contains(s)) {
					availableGlobalDescriptors.add(s);
				}
			}
			for (String s : e.getLocalSignatures().keySet()) {
				if (!availableLocalDescriptors.contains(s)) {
					availableLocalDescriptors.add(s);
				}
			}
		}

		allDescriptors.addAll(availableGlobalDescriptors);
		allDescriptors.addAll(availableLocalDescriptors);
	}

	/**
	 * Update classes entries.
	 */
	public void updateClassesEntries() {
		if (!utdEntries) {
			posClassesEntries = new HashMap<String, List<ImageEntry>>();
			negClassesEntries = new HashMap<String, List<ImageEntry>>();

			ArrayList<String> allPosClasses = new ArrayList<String>();

			for (ImageEntry e : this) {
				for (String c : e.getClasses().keySet()) {
					if (!posClassesEntries.containsKey(c)) {
						allPosClasses.add(c);
						posClassesEntries.put(c, new ArrayList<ImageEntry>());
						negClassesEntries.put(c, new ArrayList<ImageEntry>());
					}
				}
			}

			for (ImageEntry e : this) {
				for (String c : allPosClasses) {
					if (e.containsClass(c)) {
						posClassesEntries.get(c).add(e);
					} else {
						negClassesEntries.get(c).add(e);
					}
				}
			}

			utdEntries = true;
		}
	}

	/**
	 * Gets the all descriptors.
	 * 
	 * @return the all descriptors
	 */
	public Set<String> getAllDescriptors() {
		return allDescriptors;
	}

	public String getSignatureDirectory() {
		return signatureDirectory;
	}

	public void setSignatureDirectory(String signatureDirectory) {
		this.signatureDirectory = signatureDirectory;
	}

}
