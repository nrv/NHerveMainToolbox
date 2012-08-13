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

package plugins.nherve.toolbox.genericgrid;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GridCellCollection<T extends GridCell> implements Iterable<T>{
	private List<T> cells;
	private ThumbnailProvider<T> thumbnailProvider;

	public GridCellCollection(ThumbnailProvider<T> thumbnailProvider) {
		super();
		
		cells = Collections.synchronizedList(new ArrayList<T>());
		this.thumbnailProvider = thumbnailProvider;
	}

	public boolean add(T c) {
		c.setThumbnailProvider(thumbnailProvider);
		return cells.add(c);
	}
	
	public void addAll(List<T> cl) {
		for (T t : cl) {
			add(t);
		}
	}

	public void clear() {
		for (GridCell cell : cells) {
			cell.removedFromGrid();
		}
		cells.clear();
		thumbnailProvider.stopCurrentWork();
	}

	public T get(int arg0) {
		return cells.get(arg0);
	}

	public int indexOf(T arg0) {
		return cells.indexOf(arg0);
	}

	public boolean isEmpty() {
		return cells.isEmpty();
	}

	public Iterator<T> iterator() {
		return cells.iterator();
	}

	void notifyDisplayParametersChanged() {
		for (GridCell cell : cells) {
			cell.notifyDisplayParametersChanged();
		}
	}
	
	public T remove(int arg0) {
		return cells.remove(arg0);
	}
	
	void setDisplayName(boolean displayName) {
		for (GridCell cell : cells) {
			cell.setDisplayName(displayName);
		}
	}
	
	void setNameFont(Font font) {
		for (GridCell cell : cells) {
			cell.setNameFont(font);
		}
	}
	
	void setZoomOnFocus(boolean zoomOnFocus) {
		for (GridCell cell : cells) {
			cell.setZoomOnFocus(zoomOnFocus);
		}
	}

	public int size() {
		return cells.size();
	}

	public List<T> subList(int arg0, int arg1) {
		return cells.subList(arg0, arg1);
	}
}
