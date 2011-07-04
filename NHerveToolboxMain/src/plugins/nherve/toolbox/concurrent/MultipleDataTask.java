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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * The Class MultipleDataTask.
 * 
 * @param <Input>
 *            the generic type
 * @param <Output>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class MultipleDataTask<Input, Output> implements Callable<Output>, Iterable<Input> {
	
	/** The all data. */
	private List<Input> allData;
	
	/** The idx1. */
	private int idx1;
	
	/** The idx2. */
	private int idx2;
	
	private Map<String, Object> contextualData;
	
	/**
	 * Instantiates a new multiple data task.
	 * 
	 * @param allData
	 *            the all data
	 * @param idx1
	 *            the idx1
	 * @param idx2
	 *            the idx2
	 */
	public MultipleDataTask(List<Input> allData, int idx1, int idx2) {
		super();
		
		this.allData = allData;
		this.idx1 = idx1;
		this.idx2 = idx2;
	}

	/**
	 * Gets the idx1.
	 * 
	 * @return the idx1
	 */
	public int getIdx1() {
		return idx1;
	}

	/**
	 * Gets the idx2.
	 * 
	 * @return the idx2
	 */
	public int getIdx2() {
		return idx2;
	}

	/**
	 * Gets the.
	 * 
	 * @param index
	 *            the index
	 * @return the input
	 */
	public Input get(int index) {
		return allData.get(index);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Input> iterator() {
		return allData.subList(idx1, idx2).iterator();
	}
	
	/**
	 * Call.
	 * 
	 * @param data
	 *            the data
	 * @param idx
	 *            the idx
	 * @throws Exception
	 *             the exception
	 */
	public abstract void call(Input data, int idx) throws Exception;
	
	/**
	 * Output call.
	 * 
	 * @return the output
	 * @throws Exception
	 *             the exception
	 */
	public abstract Output outputCall() throws Exception;
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Output call() {
		try {
			for (int i = getIdx1(); i < getIdx2(); i++) {
				if (Thread.interrupted()) {
					return null;
				}
				call(get(i), i);
			}
			
			return outputCall();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}
	}

	void setContextualData(Map<String, Object> contextualData) {
		this.contextualData = contextualData;
	}
	
	public abstract void processContextualData();

	public Object getContextualData(String key) {
		return contextualData.get(key);
	}
}
