package plugins.nherve.toolbox.genericgrid;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import plugins.nherve.toolbox.concurrent.TaskManager;

public abstract class DefaultThumbnailProvider<T extends GridCell> implements ThumbnailProvider<T> {
	public class CacheWorker implements Runnable {
		private T cell;

		public CacheWorker(T cell) {
			super();
			this.cell = cell;
		}

		@Override
		public void run() {
			try {
				if (cell.isOnScreen()) {
					cell.createThumbnailCache();
					cell.repaint();
				}
			} catch (Exception e) {
				cell.setThumbnail(null);
				cell.setError(true);
				e.printStackTrace();
			} finally {
				removeCacheWorker(cell);
			}
		}
	}

	public class ThumbnailWorker implements Runnable {
		private T cell;

		public ThumbnailWorker(T cell) {
			super();
			this.cell = cell;
		}

		@Override
		public void run() {
			try {
				// System.out.println(cell.getName() + " run");
				if (cell.isOnScreen()) {
					// System.out.println(cell.getName() + " onscreen");
					BufferedImage img = getThumbnail(cell);
					cell.setThumbnail(img);
				}
				// System.out.println(cell.getName() + " done");
			} catch (Exception e) {
				cell.setThumbnail(null);
				cell.setError(true);
			} finally {
				removeThumbnailWorker(cell);
			}
		}
	}

	private TaskManager cacheTM;
	private Map<T, CacheWorker> cacheWorkers;
	private TaskManager thumbTM;
	private Map<T, ThumbnailWorker> thumbWorkers;

	public DefaultThumbnailProvider() {
		super();

		thumbWorkers = Collections.synchronizedMap(new HashMap<T, ThumbnailWorker>());
		cacheWorkers = Collections.synchronizedMap(new HashMap<T, CacheWorker>());

		thumbTM = new TaskManager();
		cacheTM = new TaskManager();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.genericgrid.IThumbProv#createCacheFor(T)
	 */
	@Override
	public void createCacheFor(T cell) {
		if (!cacheWorkers.containsKey(cell)) {
			startCacheWorker(cell);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.genericgrid.IThumbProv#provideThumbnailFor(T)
	 */
	@Override
	public void provideThumbnailFor(T cell) {
		synchronized (cell) {
			if (!thumbWorkers.containsKey(cell)) {
				startThumbnailWorker(cell);
			}
		}
	}

	private void removeCacheWorker(T cell) {
		cacheTM.remove(cacheWorkers.get(cell));
		cacheWorkers.remove(cell);
	}

	private void removeThumbnailWorker(T cell) {
		synchronized (cell) {
			thumbTM.remove(thumbWorkers.get(cell));
			thumbWorkers.remove(cell);
		}
	}

	private void startCacheWorker(T cell) {
		CacheWorker w = new CacheWorker(cell);
		cacheWorkers.put(cell, w);
		cacheTM.execute(w);
	}

	private void startThumbnailWorker(T cell) {
		ThumbnailWorker w = new ThumbnailWorker(cell);
		thumbWorkers.put(cell, w);
		thumbTM.execute(w);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.genericgrid.IThumbProv#stopWorkers()
	 */
	@Override
	public void stopCurrentWork() {
		synchronized (thumbWorkers) {
			for (ThumbnailWorker w : thumbWorkers.values()) {
				thumbTM.remove(w);
			}
		}
		thumbWorkers.clear();

		synchronized (cacheWorkers) {
			for (CacheWorker w : cacheWorkers.values()) {
				cacheTM.remove(w);
			}
		}
		cacheWorkers.clear();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.genericgrid.IThumbProv#close()
	 */
	@Override
	public void close() {
		cacheTM.shutdownNow();
		thumbTM.shutdownNow();
	}

}
