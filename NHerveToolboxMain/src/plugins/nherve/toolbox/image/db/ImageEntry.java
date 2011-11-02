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

import icy.file.Loader;
import icy.image.IcyBufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import loci.formats.FormatException;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

/**
 * The Class ImageEntry.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class ImageEntry implements Segmentable {
	
	/** The id. */
	private int id;
	
	/** The file. */
	private String file;
	
	/** The width. */
	private transient int width;
	
	/** The height. */
	private transient int height;
	
	/** The image. */
	private transient IcyBufferedImage image;
	
	/** The classes. */
	private Map<String, Double> classes;
	
	/** The global signatures. */
	private Map<String, VectorSignature> globalSignatures;
	
	/** The local signatures. */
	private Map<String, BagOfSignatures<VectorSignature>> localSignatures;
	
	private transient Exception error;

	/**
	 * Instantiates a new image entry.
	 */
	public ImageEntry() {
		super();

		classes = new HashMap<String, Double>();
		globalSignatures = new HashMap<String, VectorSignature>();
		localSignatures = new HashMap<String, BagOfSignatures<VectorSignature>>();

		width = 0;
		height = 0;
		id = -1;
	}

	/**
	 * Instantiates a new image entry.
	 * 
	 * @param file
	 *            the file
	 */
	public ImageEntry(String file) {
		this();
		setFile(file);
	}

	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            the new file
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * Gets the classes.
	 * 
	 * @return the classes
	 */
	public Map<String, Double> getClasses() {
		return classes;
	}

	/**
	 * Sets the classes.
	 * 
	 * @param classes
	 *            the classes
	 */
	public void setClasses(Map<String, Double> classes) {
		this.classes = classes;
	}

	/**
	 * Contains class.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean containsClass(String key) {
		return classes.containsKey(key);
	}

	/**
	 * Put class.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putClass(String key, Double value) {
		classes.put(key, value);
	}

	/**
	 * Put signature.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putSignature(String key, VectorSignature value) {
		synchronized (globalSignatures) {
			globalSignatures.put(key, value);
		}
	}

	/**
	 * Put signature.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putSignature(String key, BagOfSignatures<VectorSignature> value) {
		synchronized (localSignatures) {
			localSignatures.put(key, value);
		}
	}

	/**
	 * Removes the signature.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSignature(String key) {
		synchronized (globalSignatures) {
			globalSignatures.remove(key);
		}
		synchronized (localSignatures) {
			localSignatures.remove(key);
		}
	}

	/**
	 * Removes the signatures.
	 */
	public void removeSignatures() {
		synchronized (globalSignatures) {
			globalSignatures.clear();
		}
		synchronized (localSignatures) {
			localSignatures.clear();
		}
	}

	/**
	 * Put class.
	 * 
	 * @param key
	 *            the key
	 */
	public void putClass(String key) {
		putClass(key, 1d);
	}
	
	/**
	 * Removes the class.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeClass(String key) {
		classes.remove(key);
	}

	/**
	 * Load image.
	 * 
	 * @param root
	 *            the root
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void loadImage(String root) throws IOException {
		loadImage(root, true);
	}
	
	public void loadImage(String root, boolean useLoci) throws IOException {
		if (useLoci) {
			loadImageLoci(root);
		} else {
			loadImageImageIO(root);
		}
	}
	
	private void loadImageLoci(String root) throws IOException {
		try {
			if (image == null) {
				image = Loader.loadImage(new File(root + "/" + file));
				width = image.getWidth();
				height = image.getHeight();
			}
		} catch (FormatException e) {
			throw new IOException(e);
		}
	}
	
	private void loadImageImageIO(String root) throws IOException {
		if (image == null) {
			image = IcyBufferedImage.createFrom(ImageIO.read(new File(root + "/" + file)));
			width = image.getWidth();
			height = image.getHeight();
		}
	}

	/**
	 * Gets the image.
	 * 
	 * @return the image
	 */
	public IcyBufferedImage getImage() {
		return image;
	}
	
	/**
	 * Sets the image.
	 * 
	 * @param i
	 *            the new image
	 */
	public void setImage(IcyBufferedImage i) {
		image = i;
	}

	/**
	 * Unload image.
	 */
	public void unloadImage() {
		image = null;
	}

	/**
	 * Gets the global signatures.
	 * 
	 * @return the global signatures
	 */
	public Map<String, VectorSignature> getGlobalSignatures() {
		return globalSignatures;
	}

	/**
	 * Sets the global signatures.
	 * 
	 * @param globalSignatures
	 *            the global signatures
	 */
	public void setGlobalSignatures(Map<String, VectorSignature> globalSignatures) {
		this.globalSignatures = globalSignatures;
	}

	/**
	 * Gets the local signatures.
	 * 
	 * @return the local signatures
	 */
	public Map<String, BagOfSignatures<VectorSignature>> getLocalSignatures() {
		return localSignatures;
	}

	/**
	 * Sets the local signatures.
	 * 
	 * @param localSignatures
	 *            the local signatures
	 */
	public void setLocalSignatures(Map<String, BagOfSignatures<VectorSignature>> localSignatures) {
		this.localSignatures = localSignatures;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Segmentable#getHeight()
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Segmentable#getWidth()
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImageEntry [file=" + file + ", id=" + id + "]";
	}

	/**
	 * Clone for split.
	 * 
	 * @return the image entry
	 */
	protected ImageEntry cloneForSplit() {
		ImageEntry e = new ImageEntry();
		
		e.id = this.id;
		e.file = this.file;
		e.width = this.width;
		e.height = this.height;
		e.image = this.image;
		e.classes = new HashMap<String, Double>(this.classes);
		e.globalSignatures = this.globalSignatures;
		e.localSignatures = this.localSignatures;
		
		return e;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

}
