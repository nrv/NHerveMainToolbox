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
package plugins.nherve.toolbox.image.mask;

import icy.image.IcyBufferedImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.DifferentColorsMap;
import plugins.nherve.toolbox.image.segmentation.Segmentation;

/**
 * The Class MaskStack.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class MaskStack implements Iterable<Mask>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 332436724094094775L;

	/** The Constant MASK_DEFAULT_LABEL. */
	public final static String MASK_DEFAULT_LABEL = "Default mask label";

	/** The height. */
	private int height;

	/** The width. */
	private int width;

	/** The new layer id. */
	private transient int newLayerId;

	/** The active id. */
	private int activeIndex;

	/** The masks. */
	private ArrayList<Mask> masks;

	private transient Map<MaskListener, MaskListener> listeners;

	private boolean updating;

	/**
	 * Instantiates a new mask stack.
	 */
	public MaskStack() {
		super();
		clear();
		listeners = new HashMap<MaskListener, MaskListener>();
		updating = false;
	}

	/**
	 * Instantiates a new mask stack.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public MaskStack(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	/**
	 * Adds the.
	 * 
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	public boolean add(Mask e) {
		boolean r = masks.add(e);
		fireChangeEvent();
		return r;
	}

	/**
	 * Adds the external mask.
	 * 
	 * @param m
	 *            the m
	 * @throws MaskException
	 *             the mask exception
	 */
	public void addExternalMask(Mask m) throws MaskException {
		if (newLayerId < (Integer.MAX_VALUE - 1)) {
			m.setId(newLayerId++);
			setActiveIndex(masks.size());
			add(m);
		} else {
			throw new MaskException("Maximum mask capacity reached ()");
		}
	}

	public void addExternalMask(Mask m, DifferentColorsMap colorMap) throws MaskException {
		if (newLayerId < (Integer.MAX_VALUE - 1)) {
			m.setId(newLayerId++);
			setActiveIndex(masks.size());
			m.setColor(colorMap.get(m.getId()));
			add(m);
		} else {
			throw new MaskException("Maximum mask capacity reached ()");
		}
	}

	public void addListener(MaskListener l) {
		listeners.put(l, l);
	}

	/**
	 * Adds the previous in stack.
	 * 
	 * @param m
	 *            the m
	 * @throws MaskException
	 *             the mask exception
	 */
	public void addPreviousInStack(Mask m) throws MaskException {
		int idx1 = masks.indexOf(m);
		int idx2 = idx1 - 1;
		if (idx2 >= 0) {
			Mask m2 = masks.get(idx2);
			m.add(m2);
			remove(m2);
		}
		fireChangeEvent();
	}

	/**
	 * As segmentation.
	 * 
	 * @return the segmentation
	 * @throws MaskException
	 *             the mask exception
	 */
	public Segmentation asSegmentation() throws MaskException {
		int w = getWidth();
		int h = getHeight();

		Segmentation seg = new Segmentation(w, h);

		for (Mask m : this) {
			Mask sm = seg.createNewMask(m.getLabel(), m.isNeedAutomaticLabel(), m.getColor(), m.getOpacity());
			sm.setBinaryData(m.getBinaryData().getCopy());
		}

		seg.createBackgroundMask("Background", Color.BLACK);

		seg.createIndex();

		return seg;
	}

	public void beginUpdate() {
		updating = true;
	}

	/**
	 * Check after load.
	 * 
	 * @param opacity
	 *            the opacity
	 * @param img
	 *            the img
	 */
	public void checkAfterLoad(float opacity, BufferedImage img) {
		updateNewLayerId();

		for (Mask m : masks) {
			m.setOpacity(opacity);
			m.forceRedraw();
		}
	}

	/**
	 * Clear.
	 */
	public void clear() {
		masks = new ArrayList<Mask>();
		newLayerId = 0;
		setActiveIndex(-1);
	}

	/**
	 * Copy current mask.
	 * 
	 * @return the mask
	 * @throws MaskException
	 *             the mask exception
	 */
	public Mask copyCurrentMask() throws MaskException {
		Mask m = getActiveMask();
		m = copyMask(m);
		return m;
	}

	public Mask copyCurrentMask(DifferentColorsMap colorMap) throws MaskException {
		Mask m = getActiveMask();
		m = copyMask(m, colorMap);
		return m;
	}

	/**
	 * Copy mask.
	 * 
	 * @param m
	 *            the m
	 * @return the mask
	 * @throws MaskException
	 *             the mask exception
	 */
	public Mask copyMask(Mask m) throws MaskException {
		Mask m2 = createNewMask("Copy of (" + m.getId() + ") " + m.getLabel(), m.isNeedAutomaticLabel(), m.getColor(), m.getOpacity());
		if (m.hasBinaryData()) {
			m2.setBinaryData(m.getBinaryData().getCopy());
		} else {
			throw new MaskException("No internal mask representation available for current mask");
		}

		return m2;
	}

	public Mask copyMask(Mask m, DifferentColorsMap colorMap) throws MaskException {
		Mask m2 = createNewMask("Copy of (" + m.getId() + ") " + m.getLabel(), m.isNeedAutomaticLabel(), colorMap, m.getOpacity());
		if (m.hasBinaryData()) {
			m2.setBinaryData(m.getBinaryData().getCopy());
		} else {
			throw new MaskException("No internal mask representation available for current mask");
		}

		return m2;
	}

	/**
	 * Creates the background mask.
	 * 
	 * @param label
	 *            the label
	 * @param clr
	 *            the clr
	 * @return the mask
	 * @throws MaskException
	 *             the mask exception
	 */
	public Mask createBackgroundMask(String label, Color clr) throws MaskException {
		BinaryIcyBufferedImage sum = null;
		for (Mask o : this) {
			if (sum == null) {
				sum = o.getBinaryData().getCopy();
			} else {
				sum.add(o.getBinaryData());
			}
		}
		sum.invert();

		Mask m = createNewMask(label, false, clr, 1.0f);
		m.setBinaryData(sum);

		// moveTop(m);

		return m;
	}

	/**
	 * Creates the new mask.
	 * 
	 * @param label
	 *            the label
	 * @param needAutomaticLabel
	 *            the need automatic label
	 * @param c
	 *            the c
	 * @param opacity
	 *            the opacity
	 * @return the mask
	 * @throws MaskException
	 *             the mask exception
	 */
	public Mask createNewMask(String label, boolean needAutomaticLabel, Color c, float opacity) throws MaskException {
		Mask m = new Mask(width, height, label, false);
		m.setColor(c);
		m.setOpacity(opacity);
		m.setNeedAutomaticLabel(needAutomaticLabel);
		addExternalMask(m);
		return m;
	}

	public Mask createNewMask(String label, boolean needAutomaticLabel, DifferentColorsMap colorMap, float opacity) throws MaskException {
		Mask m = new Mask(width, height, label, false);
		m.setOpacity(opacity);
		m.setNeedAutomaticLabel(needAutomaticLabel);
		addExternalMask(m, colorMap);
		return m;
	}

	public void endUpdate() {
		updating = false;
		fireChangeEvent();
	}

	public void fireChangeEvent() {
		if (!updating) {
			for (MaskListener l : listeners.keySet()) {
				l.stackChanged(this);
			}
		}
	}

	/**
	 * Gets the active id.
	 * 
	 * @return the active id
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * Gets the active mask.
	 * 
	 * @return the active mask
	 */
	public Mask getActiveMask() {
		return getByIndex(getActiveIndex());
	}

	/**
	 * Gets the mask by id.
	 * 
	 * @param id
	 *            the id
	 * @return the mask by id
	 */
	public Mask getById(int id) {
		for (Mask m : this) {
			if (m.getId() == id) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Gets the.
	 * 
	 * @param index
	 *            the index
	 * @return the mask
	 */
	public Mask getByIndex(int index) {
		if ((index < 0) || (index >= masks.size())) {
			return null;
		}
		return masks.get(index);
	}

	/**
	 * Gets the mask.
	 * 
	 * @param label
	 *            the label
	 * @return the mask
	 */
	public Mask getByLabel(String label) {
		for (Mask m : this) {
			if (label.equalsIgnoreCase(m.getLabel())) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the masks.
	 * 
	 * @return the masks
	 */
	public ArrayList<Mask> getMasks() {
		return masks;
	}

	/**
	 * Gets the masks by tag.
	 * 
	 * @param tag
	 *            the tag
	 * @return the masks by tag
	 */
	public ArrayList<Mask> getMasksByTag(String tag) {
		ArrayList<Mask> res = new ArrayList<Mask>();
		for (Mask m : this) {
			if (m.containsTag(tag)) {
				res.add(m);
			}
		}
		return res;
	}

	/**
	 * Gets the masks.
	 * 
	 * @param labelStratingWith
	 *            the label strating with
	 * @return the masks
	 */
	public ArrayList<Mask> getMasksStartingWithLabel(String labelStratingWith) {
		ArrayList<Mask> res = new ArrayList<Mask>();
		for (Mask m : this) {
			if (m.getLabel().toUpperCase().startsWith(labelStratingWith.toUpperCase())) {
				res.add(m);
			}
		}
		return res;
	}

	/**
	 * Gets the max id.
	 * 
	 * @return the max id
	 */
	public int getMaxId() {
		return newLayerId - 1;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	public int indexOf(Mask o) {
		return masks.indexOf(o);
	}

	/**
	 * Intersect previous in stack.
	 * 
	 * @param m
	 *            the m
	 * @throws MaskException
	 *             the mask exception
	 */
	public void intersectPreviousInStack(Mask m) throws MaskException {
		int idx1 = masks.indexOf(m);
		int idx2 = idx1 - 1;
		if (idx2 >= 0) {
			Mask m2 = masks.get(idx2);
			m.remove(m2);
			remove(m2);
		}
		fireChangeEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Mask> iterator() {
		return masks.iterator();
	}

	public void moveAt(Mask m, int newIdx) {
		int idx = masks.indexOf(m);
		if (idx == newIdx) {
			return;
		}
		if (idx > newIdx) {
			while (idx > newIdx) {
				swap(idx, --idx);
			}
		} else if (idx < newIdx) {
			while (idx < newIdx) {
				swap(idx, ++idx);
			}
		}
		fireChangeEvent();
	}

	public void moveBottom(Mask m) {
		moveAt(m, 0);
	}

	/**
	 * Move down.
	 * 
	 * @param m
	 *            the m
	 */
	public void moveDown(Mask m) {
		int idx1 = masks.indexOf(m);
		int idx2 = idx1 + 1;
		if (idx2 < masks.size()) {
			swap(idx1, idx2);
		}
		fireChangeEvent();
	}

	/**
	 * Move top.
	 * 
	 * @param m
	 *            the m
	 */
	public void moveTop(Mask m) {
		moveAt(m, masks.size() - 1);
	}

	/**
	 * Move up.
	 * 
	 * @param m
	 *            the m
	 */
	public void moveUp(Mask m) {
		int idx1 = masks.indexOf(m);
		int idx2 = idx1 - 1;
		if (idx2 >= 0) {
			swap(idx1, idx2);
		}
		fireChangeEvent();
	}

	public void reInitColors(DifferentColorsMap colorMap) {
		for (Mask m : masks) {
			m.setColor(colorMap.get(m.getId()));
		}
		fireChangeEvent();
	}

	/**
	 * Re init colors.
	 * 
	 * @param img
	 *            the img
	 */
	public void reInitColors(IcyBufferedImage img) {
		for (Mask m : masks) {
			m.setColor(m.getAverageColor(img));
		}
		fireChangeEvent();
	}

	/**
	 * Removes the.
	 * 
	 * @param m
	 *            the m
	 */
	public void remove(Mask m) {
		int idx1 = masks.indexOf(m);
		masks.remove(idx1);

		if (idx1 <= getActiveIndex()) {
			setActiveIndex(getActiveIndex() - 1);
		}

		if ((getActiveIndex() < 0) && (masks.size() > 0)) {
			setActiveIndex(0);
		}

		updateNewLayerId();
		fireChangeEvent();
	}

	public void removeListener(MaskListener l) {
		listeners.remove(l);
	}

	/**
	 * Removes the mask with tag.
	 * 
	 * @param tag
	 *            the tag
	 */
	public void removeMaskWithTag(String tag) {
		ArrayList<Mask> masks = getMasksByTag(tag);

		for (Mask m : masks) {
			remove(m);
		}
	}

	/**
	 * Sets the active id.
	 * 
	 * @param activeId
	 *            the new active id
	 */
	public void setActiveIndex(int active) {
		this.activeIndex = active;
	}

	/**
	 * Sets the active mask.
	 * 
	 * @param active
	 *            the new active mask
	 */
	public void setActiveMask(Mask active) {
		setActiveIndex(masks.indexOf(active));
	}

	/**
	 * Sets the height.
	 * 
	 * @param height
	 *            the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets the masks.
	 * 
	 * @param masks
	 *            the new masks
	 */
	public void setMasks(ArrayList<Mask> masks) {
		this.masks = masks;
		fireChangeEvent();
	}

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size() {
		return masks.size();
	}

	/**
	 * Swap.
	 * 
	 * @param idx1
	 *            the idx1
	 * @param idx2
	 *            the idx2
	 */
	private void swap(int idx1, int idx2) {
		Mask t = masks.get(idx2);
		masks.set(idx2, masks.get(idx1));
		masks.set(idx1, t);

		if (getActiveIndex() == idx1) {
			setActiveIndex(idx2);
		} else if (getActiveIndex() == idx2) {
			setActiveIndex(idx1);
		}
	}

	/**
	 * Update new layer id.
	 */
	private void updateNewLayerId() {
		newLayerId = 0;
		for (Mask m : masks) {
			if (m.getId() >= newLayerId) {
				newLayerId = m.getId() + 1;
			}
		}
	}

	@Override
	public String toString() {
		String str = "";
		for (Mask m : this) {
			if (str.length() > 0) {
				str += ", ";
			}
			str += m.toString();
		}
		return str;
	}

}
