package plugins.nherve.toolbox.genericgrid;

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

	public T get(int arg0) {
		return cells.get(arg0);
	}

	public boolean isEmpty() {
		return cells.isEmpty();
	}

	public Iterator<T> iterator() {
		return cells.iterator();
	}

	public T remove(int arg0) {
		return cells.remove(arg0);
	}

	public int size() {
		return cells.size();
	}

	public List<T> subList(int arg0, int arg1) {
		return cells.subList(arg0, arg1);
	}
	
	void notifyDisplayParametersChanged() {
		for (GridCell cell : cells) {
			cell.notifyDisplayParametersChanged();
		}
	}
	
	void setZoomOnFocus(boolean zoomOnFocus) {
		for (GridCell cell : cells) {
			cell.setZoomOnFocus(zoomOnFocus);
		}
	}

	public void clear() {
		for (GridCell cell : cells) {
			cell.removedFromGrid();
		}
		cells.clear();
		thumbnailProvider.stopCurrentWork();
	}
}
