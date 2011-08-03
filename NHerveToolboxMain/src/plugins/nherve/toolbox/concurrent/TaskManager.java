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
package plugins.nherve.toolbox.concurrent;


import icy.system.SystemUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import plugins.nherve.toolbox.Algorithm;


/**
 * The Class TaskManager.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class TaskManager extends Algorithm {
	
	/** The Constant DEFAULT_NBT. */
	private final static int DEFAULT_NBT = Math.max(2, SystemUtil.getAvailableProcessors() - 1);
	
	/** The main. */
	private static TaskManager main;
	
	/** The second level. */
	private static TaskManager secondLevel;

	/** The thread pool. */
	private ThreadPoolExecutor threadPool;
	
	/** The show progress. */
	private boolean showProgress;
	
	public TaskManager() {
		this(DEFAULT_NBT);
	}

	/**
	 * Instantiates a new task manager.
	 * 
	 * @param nbt
	 *            the nbt
	 */
	public TaskManager(int nbt) {
		super();
		threadPool = (ThreadPoolExecutor)(Executors.newFixedThreadPool(nbt));
		threadPool.prestartAllCoreThreads();
		
		setShowProgress(true);
	}

	/**
	 * Gets the main instance.
	 * 
	 * @return the main instance
	 */
	public static synchronized TaskManager getMainInstance() {
		return getMainInstance(DEFAULT_NBT);
	}

	/**
	 * Gets the second level instance.
	 * 
	 * @return the second level instance
	 */
	public static synchronized TaskManager getSecondLevelInstance() {
		return getSecondLevelInstance(DEFAULT_NBT);
	}

	/**
	 * Gets the main instance.
	 * 
	 * @param preferedSize
	 *            the prefered size
	 * @return the main instance
	 */
	public static synchronized TaskManager getMainInstance(int preferedSize) {
		if (main == null) {
			main = new TaskManager(preferedSize);
		}

		return main;
	}
	
	public static synchronized TaskManager create(int preferedSize) {
		return new TaskManager(preferedSize);
	}
	
	public static synchronized TaskManager create() {
		return create(DEFAULT_NBT);
	}

	/**
	 * Gets the second level instance.
	 * 
	 * @param preferedSize
	 *            the prefered size
	 * @return the second level instance
	 */
	public static synchronized TaskManager getSecondLevelInstance(int preferedSize) {
		if (secondLevel == null) {
			secondLevel = new TaskManager(preferedSize);
		}

		return secondLevel;
	}

	/**
	 * Shutdown all.
	 */
	public static void shutdownAll() {
		if (main != null) {
			main.shutdown();
		}
		if (secondLevel != null) {
			secondLevel.shutdown();
		}
	}

	/**
	 * Inits the all.
	 * 
	 * @param preferedSize
	 *            the prefered size
	 * @param displayEnabled
	 *            the display enabled
	 * @param showProgress
	 *            the show progress
	 */
	public static void initAll(int preferedSize, boolean displayEnabled, boolean showProgress) {
		initAll(preferedSize, preferedSize, displayEnabled, showProgress);
	}

	/**
	 * Inits the all.
	 * 
	 * @param mainPreferedSize
	 *            the main prefered size
	 * @param secondLevelPreferedSize
	 *            the second level prefered size
	 * @param displayEnabled
	 *            the display enabled
	 * @param showProgress
	 *            the show progress
	 */
	public static void initAll(int mainPreferedSize, int secondLevelPreferedSize, boolean displayEnabled, boolean showProgress) {
		shutdownAll();
		getMainInstance(mainPreferedSize).setLogEnabled(displayEnabled);
		getSecondLevelInstance(secondLevelPreferedSize).setLogEnabled(displayEnabled);
		getMainInstance(mainPreferedSize).setShowProgress(showProgress);
		getSecondLevelInstance(secondLevelPreferedSize).setShowProgress(showProgress);
	}
	
	/**
	 * Inits the all.
	 */
	public static void initAll() {
		shutdownAll();
		getMainInstance();
		getSecondLevelInstance();
	}

	/**
	 * Shutdown.
	 */
	public synchronized void shutdown() {
		if (threadPool != null) {
			threadPool.shutdown();
			
			if (this == main) {
				main = null;
			}

			if (this == secondLevel) {
				secondLevel = null;
			}
		}
	}
	
	/**
	 * Submit.
	 * 
	 * @param <Output>
	 *            the generic type
	 * @param task
	 *            the task
	 * @return the future
	 */
	public <Output> Future<Output> submit(Callable<Output> task) {
		// log("Single task submitted [" + task.toString() + "] ["+threadPool.getActiveCount()+"/"+threadPool.getCorePoolSize()+"]");
		return threadPool.submit(task);
	}

	/**
	 * Submit all.
	 * 
	 * @param <Output>
	 *            the generic type
	 * @param tasks
	 *            the tasks
	 * @return the list
	 */
	public <Output> List<Future<Output>> submitAll(List<Callable<Output>> tasks) {
		List<Future<Output>> poolResults = new ArrayList<Future<Output>>();
		for (Callable<Output> task : tasks) {
			poolResults.add(threadPool.submit(task));
		}
		return poolResults;
	}

	/**
	 * Submit single for all.
	 * 
	 * @param <Input>
	 *            the generic type
	 * @param <Output>
	 *            the generic type
	 * @param allDatas
	 *            the all datas
	 * @param method
	 *            the method
	 * @param msg
	 *            the msg
	 * @param slp
	 *            the slp
	 * @return the list
	 * @throws TaskException
	 *             the task exception
	 * @throws InterruptedException 
	 */
	public <Input, Output> List<Output> submitSingleForAll(List<Input> allDatas, Class<? extends SingleDataTask<Input, Output>> method, String msg, long slp) throws TaskException, InterruptedException {
		try {
			Constructor<? extends SingleDataTask<Input, Output>> cst = method.getConstructor(new Class[] { List.class, int.class });

			List<Callable<Output>> tasks = new ArrayList<Callable<Output>>();
			for (int i = 0; i < allDatas.size(); i++) {
				SingleDataTask<Input, Output> task = cst.newInstance(new Object[] { allDatas, i });
				tasks.add(task);
			}

			List<Future<Output>> poolResults = submitAll(tasks);

			return waitResults(poolResults, msg, slp);
		} catch (SecurityException e) {
			throw new TaskException(e);
		} catch (IllegalArgumentException e) {
			throw new TaskException(e);
		} catch (NoSuchMethodException e) {
			throw new TaskException(e);
		} catch (InstantiationException e) {
			throw new TaskException(e);
		} catch (IllegalAccessException e) {
			throw new TaskException(e);
		} catch (InvocationTargetException e) {
			throw new TaskException(e);
		}
	}

	/**
	 * Submit multi for all.
	 * 
	 * @param <Input>
	 *            the generic type
	 * @param <Output>
	 *            the generic type
	 * @param allDatas
	 *            the all datas
	 * @param method
	 *            the method
	 * @param from
	 *            the from
	 * @param msg
	 *            the msg
	 * @param slp
	 *            the slp
	 * @return the list
	 * @throws TaskException
	 *             the task exception
	 * @throws InterruptedException 
	 */
	public <Input, Output> List<Output> submitMultiForAll(Input[] allDatas, Class<? extends MultipleDataTask<Input, Output>> method, Object from, String msg, long slp) throws TaskException, InterruptedException {
		return submitMultiForAll(Arrays.asList(allDatas), method, from, msg, slp);
	}
	
	public <Input, Output> List<Output> submitMultiForAll(List<Input> allDatas, Class<? extends MultipleDataTask<Input, Output>> method, Object from, String msg, long slp) throws TaskException, InterruptedException {
		return submitMultiForAll(allDatas, null, method, from, msg, slp);
	}
	
	/**
	 * Submit multi for all.
	 * 
	 * @param <Input>
	 *            the generic type
	 * @param <Output>
	 *            the generic type
	 * @param allDatas
	 *            the all datas
	 * @param method
	 *            the method
	 * @param from
	 *            the from
	 * @param msg
	 *            the msg
	 * @param slp
	 *            the slp
	 * @return the list
	 * @throws TaskException
	 *             the task exception
	 * @throws InterruptedException 
	 */
	public <Input, Output> List<Output> submitMultiForAll(List<Input> allDatas, Map<String, Object> contextualData, Class<? extends MultipleDataTask<Input, Output>> method, Object from, String msg, long slp) throws TaskException, InterruptedException {
		try {
			Constructor<? extends MultipleDataTask<Input, Output>> cst = null;
			Class<?> dc = method.getDeclaringClass();
			if (dc != null) {
				cst = method.getConstructor(new Class[] { dc, List.class, int.class, int.class });
			} else {
				cst = method.getConstructor(new Class[] { List.class, int.class, int.class });
			}

			int ws = (int) Math.ceil((double) allDatas.size() / (double) getCorePoolSize());
			int fi = 0;
			int li = 0;

			List<Callable<Output>> tasks = new ArrayList<Callable<Output>>();
			for (int w = 0; w < getCorePoolSize(); w++) {
				li = Math.min(fi + ws, allDatas.size());
				MultipleDataTask<Input, Output> task = null;
				if (dc != null) {
					task = cst.newInstance(new Object[] { from, allDatas, fi, li });
				} else {
					task = cst.newInstance(new Object[] { allDatas, fi, li });
				}
				task.setContextualData(contextualData);
				task.processContextualData();
				tasks.add(task);
				fi = li;
			}

			List<Future<Output>> poolResults = submitAll(tasks);

			return waitResults(poolResults, msg, slp);
		} catch (SecurityException e) {
			throw new TaskException(e);
		} catch (IllegalArgumentException e) {
			throw new TaskException(e);
		} catch (NoSuchMethodException e) {
			throw new TaskException(e);
		} catch (InstantiationException e) {
			throw new TaskException(e);
		} catch (IllegalAccessException e) {
			throw new TaskException(e);
		} catch (InvocationTargetException e) {
			throw new TaskException(e);
		}
	}
	
	/**
	 * Wait results.
	 * 
	 * @param <Key>
	 *            the generic type
	 * @param <Output>
	 *            the generic type
	 * @param poolResults
	 *            the pool results
	 * @param msg
	 *            the msg
	 * @param slp
	 *            the slp
	 * @return the map
	 * @throws TaskException
	 *             the task exception
	 */
	public <Key, Output> Map<Key, Output> waitResults(Map<Key, Future<Output>> poolResults, String msg, long slp) throws TaskException {
		try {
			Map<Key, Output> results = new TreeMap<Key, Output>();
			if (slp > 0) {
				if (isShowProgress()) {
					System.out.println("Launched ...");
				}
			}
			DecimalFormat cf = new DecimalFormat("00");
			boolean finished = false;
			do {
				int count = 0;
				for (Future<Output> tr : poolResults.values()) {
					if (tr.isDone()) {
						count++;
					}
				}
				if (count == poolResults.size()) {
					finished = true;
				} else {
					if (slp > 0) {
						double pct = count * 100d / poolResults.size();
						if (isShowProgress()) {
							System.out.println(" - working ("+msg+") : " + cf.format(pct) + " %");
						}
						Thread.sleep(slp);
					}
				}
			} while (!finished);

			for (Key key : poolResults.keySet()) {
				results.put(key, poolResults.get(key).get());
			}
			if (slp > 0) {
				if (isShowProgress()) {
					System.out.println("... done");
				}
			}

			return results;
		} catch (InterruptedException e) {
			throw new TaskException(e);
		} catch (ExecutionException e) {
			throw new TaskException(e);
		}
	}

	
	public <Output> Output waitResult(Future<Output> tr, long slp) throws TaskException {
		try {
			while (!tr.isDone()) {
				// System.out.println("   ... sleeping ...");
				Thread.sleep(slp);
			}
			return tr.get();
		} catch (InterruptedException e) {
			throw new TaskException(e);
		} catch (ExecutionException e) {
			throw new TaskException(e);
		}
	}
	
	/**
	 * Wait results.
	 * 
	 * @param <Output>
	 *            the generic type
	 * @param poolResults
	 *            the pool results
	 * @param msg
	 *            the msg
	 * @param slp
	 *            the slp
	 * @return the list
	 * @throws TaskException
	 *             the task exception
	 * @throws InterruptedException 
	 */
	public <Output> List<Output> waitResults(List<Future<Output>> poolResults, String msg, long slp) throws TaskException, InterruptedException {
		try {
			List<Output> results = new ArrayList<Output>();
			if (slp > 0) {
				if (isShowProgress()) {
					System.out.println("Launched ...");
				}
			}
			DecimalFormat cf = new DecimalFormat("00");
			boolean finished = false;
			do {
				int count = 0;
				for (Future<Output> tr : poolResults) {
					if (tr.isDone()) {
						count++;
					}
				}
				if (count == poolResults.size()) {
					finished = true;
				} else {
					if (slp > 0) {
						double pct = count * 100d / poolResults.size();
						if (isShowProgress()) {
							System.out.println(" - working ("+msg+") : " + cf.format(pct) + " %");
						}
						Thread.sleep(slp);
					}
				}
			} while (!finished);

			for (Future<Output> tr : poolResults) {
				results.add(tr.get());
			}
			if (slp > 0) {
				if (isShowProgress()) {
					System.out.println("... done");
				}
			}

			return results;
		} catch (ExecutionException e) {
			throw new TaskException(e);
		}
	}

	/**
	 * Wait result lists.
	 * 
	 * @param <Output>
	 *            the generic type
	 * @param poolResults
	 *            the pool results
	 * @param msg
	 *            the msg
	 * @param slp
	 *            the slp
	 * @return the list
	 * @throws TaskException
	 *             the task exception
	 */
	public <Output> List<Output> waitResultLists(List<Future<List<Output>>> poolResults, String msg, long slp) throws TaskException {
		try {
			List<Output> results = new ArrayList<Output>();
			if (slp > 0) {
				if (isShowProgress()) {
					System.out.println("Launched ...");
				}
			}
			DecimalFormat cf = new DecimalFormat("00");
			boolean finished = false;
			do {
				int count = 0;
				for (Future<List<Output>> tr : poolResults) {
					if (tr.isDone()) {
						count++;
					}
				}
				if (count == poolResults.size()) {
					finished = true;
				} else {
					if (slp > 0) {
						double pct = count * 100d / poolResults.size();
						if (isShowProgress()) {
							System.out.println(" - working ("+msg+") : " + cf.format(pct) + " %");
						}
						Thread.sleep(slp);
					}
				}
			} while (!finished);

			for (Future<List<Output>> tr : poolResults) {
				results.addAll(tr.get());
			}
			if (slp > 0) {
				if (isShowProgress()) {
					System.out.println("... done");
				}
			}

			return results;
		} catch (InterruptedException e) {
			throw new TaskException(e);
		} catch (ExecutionException e) {
			throw new TaskException(e);
		}
	}

	/**
	 * Checks if is show progress.
	 * 
	 * @return true, if is show progress
	 */
	public boolean isShowProgress() {
		return showProgress;
	}

	/**
	 * Sets the show progress.
	 * 
	 * @param showProgress
	 *            the new show progress
	 */
	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}

	/**
	 * Gets the active count.
	 * 
	 * @return the active count
	 */
	public int getActiveCount() {
		return threadPool.getActiveCount();
	}

	/**
	 * Gets the core pool size.
	 * 
	 * @return the core pool size
	 */
	public int getCorePoolSize() {
		return threadPool.getCorePoolSize();
	}
	
	public int getQueueSize() {
		return threadPool.getQueue().size();
	}

	public List<Runnable> shutdownNow() {
		return threadPool.shutdownNow();
	}

	public boolean remove(Runnable task) {
		return threadPool.remove(task);
	}
	
	public void execute(Runnable task) {
		threadPool.execute(task);
	}
}
